package igrek.forceawaken.ui.errorcheck;

import android.view.View;

public abstract class SafeClickListener implements View.OnClickListener {
	
	@Override
	public void onClick(View var1) {
		try {
			onClick();
		} catch (Throwable t) {
			UIErrorHandler.showError(t);
		}
	}
	
	protected abstract void onClick() throws Throwable;
	
}
