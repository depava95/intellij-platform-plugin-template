package org.jetbrains.plugins.template.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import org.jetbrains.plugins.template.ui.CommentDialog

class AddMarkdownCommentAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)

        // Only show the action if we have a project, an editor, and we are in a markdown file
        val isMarkdownFile = file?.extension?.lowercase() == "md"
        val hasSelection = editor?.selectionModel?.hasSelection() ?: false
        
        e.presentation.isEnabledAndVisible = project != null && isMarkdownFile && hasSelection
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        // This action relies entirely on having a standard text Editor with a selection
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        
        val document = editor.document
        val virtualFile = FileDocumentManager.getInstance().getFile(document) ?: return

        val selectionModel = editor.selectionModel
        
        if (!selectionModel.hasSelection()) {
            return
        }

        val startOffset = selectionModel.selectionStart
        val endOffset = selectionModel.selectionEnd
        val startLine = document.getLineNumber(startOffset)
        val endLine = document.getLineNumber(endOffset)
        
        showCommentDialog(project, document, virtualFile, startLine, endLine)
    }

    private fun showCommentDialog(project: Project, document: Document, virtualFile: VirtualFile, startLine: Int, endLine: Int) {
        val dialog = CommentDialog(project)
        if (dialog.showAndGet()) {
            val commentText = dialog.getCommentText()
            if (commentText.isNotEmpty()) {
                val insertOffset = document.getLineEndOffset(endLine)
                
                val linesText = if (startLine == endLine) "line ${startLine + 1}" else "lines ${startLine + 1}-${endLine + 1}"
                val formattedComment = "\n\n> **Comment for $linesText:**\n> $commentText\n"

                val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
                if (psiFile != null) {
                    WriteCommandAction.runWriteCommandAction(project, "Add Markdown Comment", "Markdown", {
                        document.insertString(insertOffset, formattedComment)
                    }, psiFile)
                }
            }
        }
    }
}
