package uk.co.jofaircloth.ringsimForAndroid.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "ringsim.db";
	public static final int DATABASE_VERSION = 6;

	public DbAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbFavourite.CREATE_FAVOURITE_TABLE);				
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DbFavourite.ALTER_FAVOURITE_TABLE);
		onCreate(db);
	}

}
