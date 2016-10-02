package uk.co.jofaircloth.ringsimForAndroid;

import uk.co.jofaircloth.ringsim.Notation;
import uk.co.jofaircloth.ringsim.SearchResults;
import uk.co.jofaircloth.ringsim.enums.Calls;
import uk.co.jofaircloth.ringsimForAndroid.dal.DbFavourite;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class FavouritesActivity extends Activity {

	private ListView lvFavourites;	
	private SimpleCursorAdapter adapter;
	private	DbFavourite dbFavourite;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favourites_list);
		
		lvFavourites = (ListView) findViewById(R.id.favourites_list_view);
		
		dbFavourite = new DbFavourite(this);
		dbFavourite.open();
	}
	
	@Override
		protected void onResume() {
			super.onResume();

			fillFavourites();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		dbFavourite.close();
	}

	private void fillFavourites() {
		// get the favourites
		Cursor c = dbFavourite.getFavourites();
		startManagingCursor(c);
		
		adapter = new FavouritesAdapter(this, c);
		lvFavourites.setAdapter(adapter);
		lvFavourites.setOnItemClickListener(new OnItemClickWatcher());
	}
	
    private class OnItemClickWatcher implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			SQLiteCursor cur = (SQLiteCursor) lvFavourites.getItemAtPosition(position);
			//SearchResults r = (SearchResults) lvFavourites.getItemAtPosition(position);
			SearchResults r = new SearchResults(cur.getString(DbFavourite.METHOD_NAME_INDEX)
					, cur.getString(DbFavourite.LEAD_END_INDEX)
					, cur.getString(DbFavourite.NOTATION_INDEX)
					, cur.getString(DbFavourite.METHOD_CLASS_INDEX)
					, cur.getString(DbFavourite.METHOD_STAGE_INDEX));
			Intent intent = new Intent(FavouritesActivity.this, BlueLineViewActivity.class);
			Notation n = new Notation(r);        
			intent.putStringArrayListExtra("notationPlain", n.format(Calls.PLAIN));
			intent.putStringArrayListExtra("notationBob", n.format(Calls.BOB));
			intent.putStringArrayListExtra("notationSingle", n.format(Calls.SINGLE));
			intent.putExtra("stage", r.getMethodStage());
			intent.putExtra("name", r.toString());
			startActivity(intent);
		}
    	
    }

}
