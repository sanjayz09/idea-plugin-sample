package com.sanjay.idea.plugin.sample

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.search.GlobalSearchScope


/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/21.
 */
object MVPUtils {

    fun handle(project: Project, psiDirectory: PsiDirectory, prefix: String) {
        val classWorkspace = "com.sanjay.application.mvp."
        val iMVPView =
            findClass(project, classWorkspace + "IMVPView") ?: return
        val baseMVPView =
            findClass(project, classWorkspace + "impl.BaseMVPViewConstraintLayout") ?: return
        val iMVPPresenter =
            findClass(project, classWorkspace + "IMVPPresenter") ?: return
        val baseMVPPresenter =
            findClass(project, classWorkspace + "impl.BaseMVPPresenter") ?: return
        val baseMVPComponent =
            findClass(project, classWorkspace + "impl.BaseMVPComponentFragment") ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            val contractStruct = generateContract(
                project, psiDirectory, prefix + STRING_CONTRACT,
                iMVPView, baseMVPPresenter
            )
            // View
            generateView(
                project, psiDirectory, prefix + STRING_VIEW,
                contractStruct.view, contractStruct.presenter,
                baseMVPView
            )
            // Presenter
            generatePresenter(
                project, psiDirectory, prefix + STRING_PRESENTER,
                contractStruct.view, contractStruct.presenter
            )
            // Component
            generateComponent(
                project, psiDirectory, prefix + STRING_COMPONENT,
                contractStruct.view, contractStruct.presenter,
                baseMVPComponent
            )
            //
            openEditor(project, psiDirectory.virtualFile)
        }
    }

    private fun openEditor(project: Project, file: VirtualFile) {
        FileEditorManager.getInstance(project).openEditor(
            OpenFileDescriptor(project, file), true
        )
    }

    private fun findClass(project: Project, className: String): PsiClass? {
        return JavaPsiFacade.getInstance(project)
            .findClass(className, GlobalSearchScope.projectScope(project))
    }

    private fun getSubstitutorClassType(
        factory: PsiElementFactory,
        psiClassMain: PsiClass,
        vararg psiClasses: PsiClass
    ): PsiClassType {
        var rawSubstitutor = factory.createRawSubstitutor(psiClassMain)
        psiClasses.asSequence()
            .forEachIndexed { index, psiClass ->
                rawSubstitutor = rawSubstitutor.put(
                    psiClassMain.typeParameters[index],
                    factory.createType(psiClass)
                )
            }
        return factory.createType(psiClassMain, rawSubstitutor)
    }

    private fun generateView(
        project: Project,
        psiDirectory: PsiDirectory,
        className: String,
        iView: PsiClass,
        iPresenter: PsiClass,
        baseMVPView: PsiClass
    ) {
        val factory = JavaPsiFacade.getElementFactory(project)
        val psiClass = JavaDirectoryService.getInstance()
            .createClass(psiDirectory, className)
            .apply {
                modifierList?.setModifierProperty(PsiModifier.PUBLIC, true)
                extendsList?.add(
                    factory.createReferenceElementByType(
                        getSubstitutorClassType(factory, baseMVPView, iPresenter)
                    )
                )
                implementsList?.add(
                    factory.createReferenceElementByType(
                        factory.createType(iView)
                    )
                )
            }

        GenerateConstructorHelper.generateAllSuperConstructor(project, psiClass)

        JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiClass)
    }


    private fun generatePresenter(
        project: Project,
        psiDirectory: PsiDirectory,
        className: String,
        iView: PsiClass,
        iPresenter: PsiClass
    ) {
        val factory = JavaPsiFacade.getElementFactory(project)
        val psiClass = JavaDirectoryService.getInstance()
            .createClass(psiDirectory, className)
            .apply {
                modifierList?.setModifierProperty(PsiModifier.PUBLIC, true)
                extendsList?.add(
                    factory.createReferenceElementByType(
                        factory.createType(iPresenter)
                    )
                )
            }

        JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiClass)
    }

    private fun generateComponent(
        project: Project,
        psiDirectory: PsiDirectory,
        className: String,
        iView: PsiClass,
        iPresenter: PsiClass,
        baseMVPComponent: PsiClass
    ) {
        val factory = JavaPsiFacade.getElementFactory(project)
        val psiClass = JavaDirectoryService.getInstance()
            .createClass(psiDirectory, className)
            .apply {
                modifierList?.setModifierProperty(PsiModifier.PUBLIC, true)
                extendsList?.add(
                    factory.createReferenceElementByType(
                        getSubstitutorClassType(factory, baseMVPComponent, iView, iPresenter)
                    )
                )
            }

        JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiClass)
    }

    private fun generateContract(
        project: Project,
        psiDirectory: PsiDirectory,
        className: String,
        iView: PsiClass,
        iPresenter: PsiClass
    ): ContractStruct {
        val factory = JavaPsiFacade.getElementFactory(project)
        val view = factory.createInterface(STRING_VIEW)
            .apply {
                modifierList?.setModifierProperty(PsiModifier.PUBLIC, false)
            }
        val presenter = factory.createClass(STRING_PRESENTER)
            .apply {
                modifierList?.apply {
                    setModifierProperty(PsiModifier.PUBLIC, false)
                    setModifierProperty(PsiModifier.ABSTRACT, true)
                }
                extendsList?.apply {
                    add(
                        factory.createReferenceElementByType(
                            getSubstitutorClassType(factory, iPresenter, view)
                        )
                    )
                }
            }

        view.extendsList?.add(
            factory.createReferenceElementByType(
                getSubstitutorClassType(factory, iView, presenter)
            )
        )
        // Contract
        val contract = JavaDirectoryService.getInstance()
            .createInterface(psiDirectory, className)
            .apply {
                modifierList?.setModifierProperty(PsiModifier.PUBLIC, true)
                add(view)
                add(presenter)
            }

        JavaCodeStyleManager.getInstance(project).shortenClassReferences(contract)

        return ContractStruct(contract.allInnerClasses[0], contract.allInnerClasses[1])
    }

    private class ContractStruct(val view: PsiClass, val presenter: PsiClass)

    private const val STRING_CONTRACT = "Contract"
    private const val STRING_VIEW = "View"
    private const val STRING_PRESENTER = "Presenter"
    private const val STRING_COMPONENT = "Component"

}