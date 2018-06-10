package igrek.forceawaken.domain.ringtone;

import android.net.Uri;

import java.io.File;

public class Ringtone {
	
	private File file;
	private String name;
	
	public Ringtone(File file, String name) {
		this.file = file;
		this.name = name;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getName() {
		return name;
	}
	
	public Uri getUri() {
		return Uri.fromFile(file);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj != null) && (obj instanceof Ringtone) && file.equals(((Ringtone) obj).file);
	}
}
