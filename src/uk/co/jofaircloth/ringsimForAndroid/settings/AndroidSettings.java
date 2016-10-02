package uk.co.jofaircloth.ringsimForAndroid.settings;

import android.os.Environment;
import uk.co.jofaircloth.ringsim.enums.Libraries;
import uk.co.jofaircloth.ringsim.settings.Settings;

public class AndroidSettings extends Settings {
	
	private static Libraries libraryType;
	
	// these are defined in the enum in the ringsim library (ringsim.enum.Libraries)
	private static final String MSLIB_FILENAME = Environment.getExternalStorageDirectory() + "/ringsim/MSLIB/";
	private static final String PDF_FILE_LOCATION = Environment.getExternalStorageDirectory() + "/ringsim/Pdf/";

	public static Libraries getLibraryType() {
		return libraryType;
	}
	public static void setLibraryType(Libraries library) {
		libraryType = library;
	}
	
	/**
	 * Define the MSLIB file location on the sdcard
	 */
	public static void setMSLibFileName() {
		Settings.setMSLibFileName(MSLIB_FILENAME);
	}
	public static void setPdfLocation() {
		Settings.setPdfFileLocation(PDF_FILE_LOCATION);
	}
}
