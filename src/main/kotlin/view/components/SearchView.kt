package view.components

import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

interface SearchView {
    fun setOnSearchAction(listener: (query: String) -> Unit)
    fun clearSearch()
}

class SearchViewImpl(private val searchField: JTextField) : SearchView {
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


    override fun clearSearch() {
        SwingUtilities.invokeLater {
            searchField.text = ""
        }
    }
}