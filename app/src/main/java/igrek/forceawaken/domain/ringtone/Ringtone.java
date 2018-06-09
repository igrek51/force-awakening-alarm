package igrek.forceawaken.domain.ringtone;

import android.net.Uri;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ringtone {
	
	private File file;
	private String name;
	
	public Uri getUri() {
		return Uri.fromFile(file);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
