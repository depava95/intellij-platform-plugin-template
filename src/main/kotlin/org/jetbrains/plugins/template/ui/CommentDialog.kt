package org.jetbrains.plugins.template.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class CommentDialog(project: Project) : DialogWrapper(project) {

    private val commentTextArea = JBTextArea().apply {
        emptyText.text = "Enter your comment here..."
        lineWrap = true
        wrapStyleWord = true
        rows = 5
    }

    init {
        title = "Leave a Comment"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row {
                cell(commentTextArea)
                    .align(Align.FILL)
                    .focused()
            }
        }
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return commentTextArea
    }

    fun getCommentText(): String {
        return commentTextArea.text.trim()
    }
}
