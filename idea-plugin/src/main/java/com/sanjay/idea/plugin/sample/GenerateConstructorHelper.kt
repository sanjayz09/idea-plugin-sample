package com.sanjay.idea.plugin.sample

import com.intellij.codeInsight.generation.ConstructorBodyGenerator
import com.intellij.codeInsight.generation.GenerateMembersUtil
import com.intellij.codeInsight.generation.OverrideImplementUtil
import com.intellij.codeInsight.generation.PsiMethodMember
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.undo.UndoUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.TypeConversionUtil
import com.intellij.util.IncorrectOperationException


/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/21.
 *
 * @see "https://github.com/JetBrains/intellij-community/blob/8262ac4c0bb38de4e185c4cd9c6470de0280e776/java/java-impl/src/com/intellij/codeInsight/daemon/impl/quickfix/CreateConstructorMatchingSuperFix.java"
 *
 */
object GenerateConstructorHelper {

    fun generateAllSuperConstructor(
        project: Project,
        myClass: PsiClass
    ) {
        val baseClass = myClass.superClass ?: return
        val substitutor =
            TypeConversionUtil.getSuperClassSubstitutor(baseClass, myClass, PsiSubstitutor.EMPTY)
        val baseConstructors: MutableList<PsiMethodMember> = ArrayList()
        val baseConstrs = baseClass.constructors
        for (baseConstr in baseConstrs) {
            if (PsiUtil.isAccessible(baseConstr, myClass, myClass)) {
                baseConstructors.add(PsiMethodMember(baseConstr, substitutor))
            }
        }

        constructor2Delegate(
            project,
            substitutor,
            baseConstructors,
            baseConstrs,
            myClass
        )
        return
    }

    fun constructor2Delegate(
        project: Project,
        substitutor: PsiSubstitutor,
        baseConstructors: List<PsiMethodMember>,
        baseConstrs: Array<PsiMethod>,
        targetClass: PsiClass
    ) {
        var constructors = baseConstructors.toTypedArray()
        if (constructors.isEmpty()) {
            constructors = Array(baseConstrs.size) { i ->
                PsiMethodMember(baseConstrs[i], substitutor)
            }
        }

        if (constructors.isEmpty()) return // Otherwise we won't have been messing with all this stuff

        val isCopyJavadoc = false
        ApplicationManager.getApplication().runWriteAction {
            try {
                if (targetClass.lBrace == null) {
                    val psiClass =
                        JavaPsiFacade.getElementFactory(targetClass.project)
                            .createClass("X")
                    targetClass.addRangeAfter(
                        psiClass.lBrace,
                        psiClass.rBrace,
                        targetClass.lastChild
                    )
                }
                val factory =
                    JVMElementFactories.getFactory(targetClass.language, project)
                        ?: return@runWriteAction
                val formatter: CodeStyleManager = CodeStyleManager.getInstance(project)
                for (candidate in constructors) {
                    val base = candidate.element
                    var derived = GenerateMembersUtil.substituteGenericMethod(
                        base,
                        candidate.substitutor,
                        targetClass
                    )
                    if (!isCopyJavadoc) {
                        val docComment = derived.docComment
                        docComment?.delete()
                    }
                    val targetClassName = targetClass.name ?: continue

                    derived.name = targetClassName
                    val generator =
                        ConstructorBodyGenerator.INSTANCE.forLanguage(derived.language)
                    if (generator != null) {
                        val buffer = StringBuilder()
                        generator.start(buffer, derived.name, PsiParameter.EMPTY_ARRAY)
                        generator.generateSuperCallIfNeeded(
                            buffer,
                            derived.parameterList.parameters
                        )
                        generator.finish(buffer)

                        factory.createMethodFromText(buffer.toString(), targetClass)
                            .body?.let { _body ->
                                derived.body?.replace(_body)
                            }
                    }
                    derived = formatter.reformat(derived) as PsiMethod
                    derived = JavaCodeStyleManager.getInstance(project)
                        .shortenClassReferences(derived) as PsiMethod

                    OverrideImplementUtil.createGenerationInfo(derived)
                        .insert(targetClass, null, true)
                }
            } catch (e: IncorrectOperationException) {
                // ignore
            }
            UndoUtil.markPsiFileForUndo(targetClass.containingFile)
        }
    }

}