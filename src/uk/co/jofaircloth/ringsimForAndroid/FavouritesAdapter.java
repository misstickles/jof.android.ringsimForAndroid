package uk.co.jofaircloth.ringsimForAndroid;

import uk.co.jofaircloth.ringsim.SearchResults;
import uk.co.jofaircloth.ringsimForAndroid.dal.DbFavourite;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class FavouritesAdapter extends SimpleCursorAdapter {
	
	private static final String[] FROM = new String[] {DbFavourite._ID, DbFavourite.METHOD_NAME, DbFavourite.LEAD_END, DbFavourite.NOTATION};
	private static final int[] TO = new int[] {R.id.row_method_label};

	public FavouritesAdapter(Context context, Cursor c) {
		super(context, R.layout.row_method, c, FROM, TO);
	}
	
	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);
		
		SearchResults res = new SearchResults(cursor.getString(DbFavourite.METHOD_NAME_INDEX)
				, cursor.getString(DbFavourite.LEAD_END_INDEX)
				, cursor.getString(DbFavourite.NOTATION_INDEX)
				, cursor.getString(DbFavourite.METHOD_CLASS_INDEX)
				, cursor.getString(DbFavourite.METHOD_STAGE_INDEX));
		
		TextView text = (TextView) row.findViewById(R.id.row_method_label);
		text.setText(res.toString()); //cursor.getString(DbFavourite.METHOD_NAME_INDEX) + cursor.getString(DbFavourite.LEAD_END_INDEX));
	}
	

}
