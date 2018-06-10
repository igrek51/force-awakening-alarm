package igrek.forceawaken.service.ui.info;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

import javax.inject.Inject;

import igrek.forceawaken.R;
import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.ui.errorcheck.SafeClickListener;

public class UserInfoService {
	
	@Inject
	Logger logger;
	
	@Inject
	Activity activity;
	
	private HashMap<View, Snackbar> infobars = new HashMap<>();
	
	public UserInfoService() {
		DaggerIOC.getAppComponent().inject(this);
	}
	
	private void showActionInfo(String info, String actionName, InfoBarClickAction action, Integer color) {
		
		View view = activity.findViewById(android.R.id.content);
		
		Snackbar snackbar = infobars.get(view);
		if (snackbar == null || !snackbar.isShown()) { // new info bar
			snackbar = Snackbar.make(view, info, Snackbar.LENGTH_SHORT);
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
	
	
	public void showInfo(String info) {
		showActionInfo(info, "OK", null, null);
	}
	
	public void showInfoCancellable(String info, InfoBarClickAction cancelCallback) {
		showActionInfo(info, "Undo", cancelCallback, ContextCompat.getColor(activity, R.color.colorPrimary));
	}
	
	public void showToast(String message) {
		Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
}
