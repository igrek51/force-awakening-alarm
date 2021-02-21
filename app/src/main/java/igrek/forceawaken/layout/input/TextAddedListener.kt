package igrek.forceawaken.layout.input

import android.text.Editable
import android.text.TextWatcher

abstract class TextAddedListener : TextWatcher {
    private var beforeLength = 0
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        beforeLength = s.length
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        if (s.length > beforeLength) {
            onTextAdded(s.toString())
        }
    }

    protected abstract fun onTextAdded(newValue: String)
}