package com.laomou.view.components

import com.laomou.presenter.SearchPresenter
import com.laomou.view.SearchField
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

interface SearchView {
    fun setPresenter(presenter: SearchPresenter)
    fun setOnSearchAction(listener: (query: String) -> Unit)
    fun setProgress(percent: Int);
    fun clearSearch();
}

class SearchViewImpl(private val searchField: SearchField) : SearchView {
    private lateinit var presenter: SearchPresenter

    override fun setPresenter(presenter: SearchPresenter) {
        this.presenter = presenter
    }

    override fun setOnSearchAction(listener: (query: String) -> Unit) {
        searchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = onSearch()
            override fun removeUpdate(e: DocumentEvent) = onSearch()
            override fun changedUpdate(e: DocumentEvent) = onSearch()

            private fun onSearch() {
                listener(searchField.text)
            }
        })
    }

    override fun setProgress(percent: Int) {
        SwingUtilities.invokeLater {
            searchField.setProgress(percent)
        }
    }

    override fun clearSearch() {
        SwingUtilities.invokeLater {
            searchField.text = ""
            searchField.setProgress(-1)
        }
    }
}