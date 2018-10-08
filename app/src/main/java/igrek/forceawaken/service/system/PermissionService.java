package igrek.forceawaken.service.system;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;

public class PermissionService {
	
	private Logger logger = LoggerFactory.getLogger();
	
	@Inject
	Activity activity;
	
	public PermissionService() {
		DaggerIOC.getFactoryComponent().inject(this);
	}
	
	public boolean isStoragePermissionGranted() {
		if (Build.VERSION.SDK_INT >= 23) {
			if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				// Permission is granted
				return true;
			} else {
				// Permission is revoked
				ActivityCompat.requestPermissions(activity, new String[]{
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				}, 1);
				return false;
			}
		} else { //permission is automatically granted on sdk<23 upon installation
			// Permission is granted
			return true;
		}
	}
	
	private void onPermissionGranted(String permission) {
		logger.info("permission " + permission + " has been granted");
	}
	
	private void onPermissionDenied(String permission) {
		logger.warn("permission " + permission + " has been denied");
	}
	
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (grantResults.length > 0) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				onPermissionGranted(permissions[0]);
			} else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
				onPermissionDenied(permissions[0]);
			}
		}
	}
}
