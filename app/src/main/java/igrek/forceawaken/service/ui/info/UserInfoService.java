package igrek.forceawaken.service.ui.info;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

import igrek.forceawaken.R;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.ui.errorcheck.SafeClickListener;

public class UserInfoService {
	
	private Logger logger;
	private Activity activity;
	
	private HashMap<View, Snackbar> infobars = new HashMap<>();
	
	public UserInfoService(Logger logger, Activity activity) {
		this.logger = logger;
		this.activity = activity;
	}
	
	private void showActionInfo(String info, String actionName, InfoBarClickAction action, Integer color) {
		
		View view = activity.findViewById(android.R.id.content);
		
		Snackbar snackbar = infobars.get(view);
		if (snackbar == null || !snackbar.isShown()) { // new info bar
			snackbar = Snackbar.make(view, info, Snackbar.LENGTH_LONG);
			snackbar.setActionTextColor(Color.WHITE);
		} else { // visible - used once again
			snackbar.setText(info);
		}
		
		if (actionName != null) {
			if (action == null) {
				action = snackbar::dismiss;
			}
			
			final InfoBarClickAction finalAction = action;
			snackbar.setAction(actionName, new SafeClickListener() {
				@Override
				public void onClick() {
					finalAction.onClick();
				}
			});
			if (color != null) {
				snackbar.setActionTextColor(color);
			}
		}
		
		snackbar.show();
		infobars.put(view, snackbar);
		logger.info(info);
	}
	
	
	public void showInfoBar(String info) {
		showActionInfo(info, "OK", null, null);
	}
	
	public void showInfoBarCancellable(String info, InfoBarClickAction cancelCallback) {
		showActionInfo(info, "Undo", cancelCallback, ContextCompat.getColor(activity, R.color.colorPrimary));
	}
	
	public void showToast(String message) {
		Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
		logger.info("TOAST: " + message);
	}
}
