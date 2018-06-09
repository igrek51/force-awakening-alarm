package igrek.forceawaken.ui.input;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class TextAddedListener implements TextWatcher {
	
	private int beforeLength;
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		beforeLength = s.length();
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() > beforeLength) {
			onTextAdded(s.toString());
		}
	}
	
	protected abstract void onTextAdded(String newValue);
}
