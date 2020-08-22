package com.sanjay.idea.plugin.sample

import com.intellij.ide.util.DirectoryChooserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.impl.file.PsiDirectoryFactory

/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/21.
 */
class SampleAnAction : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)
        val presentation = e.presentation
        val project = e.project
        if (project == null) {
            presentation.isEnabledAndVisible = false
            return
        }

        val view = e.getData(LangDataKeys.IDE_VIEW)
        if (view == null) {
            presentation.isEnabledAndVisible = false
            return
        }

        val directories = view.directories
        if (directories.isEmpty()) {
            presentation.isEnabledAndVisible = false
            return
        }

        var isPackage = false
        val factory = PsiDirectoryFactory.getInstance(project)
        for (directory in directories) {
            if (factory.isPackage(directory)) {
                isPackage = true
                break
            }
        }
        presentation.isEnabledAndVisible = isPackage
    }

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val project = anActionEvent.project ?: return
        val view = anActionEvent.getData(LangDataKeys.IDE_VIEW) ?: return
        val prefix = Messages.showInputDialog(
            project,
            CLASS_NAME_INPUT_TIP,
            CLASS_NAME_INPUT_TITLE,
            Messages.getQuestionIcon()
        )
        if (prefix.isNullOrEmpty()) {
            Messages.showErrorDialog(CLASS_NAME_INPUT_NO, ERROR_TITLE)
            return
        }

        val psiDirectory = DirectoryChooserUtil.getOrChooseDirectory(view) ?: return
        MVPUtils.handle(project, psiDirectory, prefix)
    }

    companion object {
        private const val ERROR_TITLE = "Error"

        private const val CLASS_NAME_INPUT_TITLE = "Create New MVP Component"
        private const val CLASS_NAME_INPUT_TIP = "Name(**Component):"
        private const val CLASS_NAME_INPUT_NO = "Please input class name!"
    }
}