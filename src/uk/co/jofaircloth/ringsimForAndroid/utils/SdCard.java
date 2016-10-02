package uk.co.jofaircloth.ringsimForAndroid.utils;

import android.os.Environment;

public class SdCard {
	
	private static boolean externalStorageAvailable = false;
	private static boolean externalStorageWriteable = false;

	private SdCard() {}
	
	public static boolean checkMedia() {
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			externalStorageAvailable = externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			externalStorageAvailable = true;
			externalStorageWriteable = false;
			// TODO: check other states...
		} else {
			externalStorageAvailable = externalStorageWriteable = false;
		}
		return externalStorageAvailable & externalStorageWriteable;
	}
	

}
