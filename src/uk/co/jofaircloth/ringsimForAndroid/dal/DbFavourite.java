package uk.co.jofaircloth.ringsimForAndroid.dal;

import uk.co.jofaircloth.ringsim.SearchResults;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DbFavourite extends DbAdapter implements DbConstants {
	public static final String TABLE_NAME = "favourite";
	
	public static final String METHOD_NAME = "method_name";
	// need library as names may be in different format (NOTE: this means we can't currently copy across libraries)
	public static final String LIBRARY = "library";
	public static final String METHOD_STAGE = "method_stage";
	public static final String METHOD_CLASS = "method_class";
	public static final String NOTATION = "notation";
	public static final String LEAD_END = "lead_end";
	
	public static final int METHOD_NAME_INDEX = 1;
	public static final int METHOD_STAGE_INDEX = 3;
	public static final int METHOD_CLASS_INDEX = 4;
	public static final int NOTATION_INDEX = 5;
	public static final int LEAD_END_INDEX = 6;
	
	public static final String CREATE_FAVOURITE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " (" + 
			"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			METHOD_NAME + " TEXT NOT NULL, " +
			LIBRARY + " TEXT NOT NULL, " +
			METHOD_STAGE + " TEXT NOT NULL, " +
			METHOD_CLASS + " TEXT NOT NULL, " +
			NOTATION + " TEXT NOT NULL, " +
			LEAD_END + " TEXT NOT NULL" +
			");";
	
	public static final String ALTER_FAVOURITE_TABLE = 
		"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	private final Context context;
	private DbAdapter dbAdapter;
	private SQLiteDatabase db;
	
	public DbFavourite(Context context) {
		super(context);
		this.context = context;
	}
	
	public DbFavourite open() throws SQLException {
		dbAdapter = new DbAdapter(context);
		db = dbAdapter.getWritableDatabase();
		return this;
	}
	
	/**
	 * Insert a new record into the ringsim database
	 */
	public boolean addFavourite(SearchResults res) {
		ContentValues values = new ContentValues();
		values.put(DbFavourite.METHOD_NAME, res.getMethodName());
		values.put(DbFavourite.METHOD_CLASS, res.getMethodClass());
		values.put(DbFavourite.METHOD_STAGE, res.getMethodStage());
		values.put(DbFavourite.LIBRARY, "MSLIB");
		values.put(DbFavourite.NOTATION, res.getNotation());
		values.put(DbFavourite.LEAD_END, res.getLeadEndGroup());
		long execResult = db.insert(TABLE_NAME, null, values);
		if (execResult > -1) {
			return true;
		}
		else
			return false;
	}
	
	public boolean deleteFavourite(SearchResults res) {
		ContentValues values = new ContentValues();
		values.put(DbFavourite.METHOD_NAME, res.toString());
		long execResult = db.delete(DbFavourite.TABLE_NAME, DbFavourite.METHOD_NAME + "='" + res.toString() + "'", null);
		if (execResult > -1)
			return true;
		else
			return false;
	}
	

	private String[] FROM = { _ID, METHOD_NAME, METHOD_CLASS, METHOD_STAGE, LIBRARY, NOTATION, LEAD_END, };
	private String ORDER = METHOD_NAME;
	public Cursor getFavourites() throws SQLException {
		if (db == null) {
			open();
		}
		Cursor c = db.query(true, TABLE_NAME, FROM, null, null, null, null, ORDER, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	
	
}
