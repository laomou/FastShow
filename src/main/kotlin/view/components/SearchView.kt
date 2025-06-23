package view.components

import presenter.SearchPresenter
import view.JSearchField
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

interface SearchView {
    fun setPresenter(presenter: SearchPresenter)
    fun setOnSearchAction(listener: (query: String) -> Unit)
    fun setProgress(percent: Int);
    fun clearSearch();
}

class SearchViewImpl(private val searchField: JSearchField) : SearchView {
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