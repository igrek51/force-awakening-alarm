package igrek.forceawaken.service.filesystem;

import android.content.Context;

import java.io.File;

public class InternalDataService {
	
	Context context;
	
	public InternalDataService(Context context) {
		this.context = context;
	}
	
	public File getInternalDataDir() {
		// /data/data/igrek.forceawaken/app_data
		File dataDir = context.getDir("data", Context.MODE_PRIVATE);
		// chmod 777 on directory
		dataDir.setReadable(true, false);
		dataDir.setWritable(true, false);
		dataDir.setExecutable(true, false);
		return dataDir;
	}
	
}
