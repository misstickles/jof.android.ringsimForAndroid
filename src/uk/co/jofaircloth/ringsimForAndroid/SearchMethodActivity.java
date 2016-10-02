package uk.co.jofaircloth.ringsimForAndroid;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.jofaircloth.ringsim.Notation;
import uk.co.jofaircloth.ringsim.SearchResults;
import uk.co.jofaircloth.ringsim.enums.Calls;
import uk.co.jofaircloth.ringsim.enums.Libraries;
import uk.co.jofaircloth.ringsim.libraries.ReadMSLIB;
import uk.co.jofaircloth.ringsimForAndroid.dal.DbFavourite;
import uk.co.jofaircloth.ringsimForAndroid.settings.AndroidSettings;
import uk.co.jofaircloth.ringsimForAndroid.utils.SdCard;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class SearchMethodActivity extends ListActivity {
	
	private SearchResultsAdapter searchResultsAdapter;
	private ListView lv;
	private DbFavourite dbFavourite;
	private Button btnSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_method);
		
	    // TODO: un-hardcode library (add options panel...!!)
	    AndroidSettings.setLibraryType(Libraries.MSLIB);
	    dbFavourite = new DbFavourite(this);

		btnSearch = (Button) findViewById(R.id.search_button);
		btnSearch.setOnClickListener(new onClickListener());
	}
	
	private class onClickListener implements OnClickListener {

    	private List<SearchResults> res = null;

		@Override
		public void onClick(View view) {
			// TODO: search and update results grid
			res = getSearchResults();					
		    setupResultAdapter();
	    	Toast.makeText(getApplicationContext(), String.format("Found %s results", res.size()), Toast.LENGTH_SHORT).show();			
		}
		
		private List<SearchResults> getSearchResults() {
			List<SearchResults> res = null;
			if (SdCard.checkMedia()) {
				// TODO: try to get switch with enums working...
				Libraries lib = AndroidSettings.getLibraryType();
				if (lib == Libraries.MSLIB) {
					AndroidSettings.setMSLibFileName();
					ReadMSLIB r = new ReadMSLIB( ((EditText) findViewById(R.id.search_text)).getText().toString());
					res = r.searchMethod();
				} else {
					res = null;
				}
			}
			return res;
		}
		
		private void setupResultAdapter() {
			Collections.sort(res);
			//adapter = new ArrayAdapter<SearchResults>(RingsimActivity.this, android.R.layout.simple_list_item_1, res);
			//adapter = new ResultsListIndexAdapter<SearchResults>(RingsimActivity.this, android.R.layout.simple_list_item_1, res);
			
			// add the favourites to a list, so we can use .contains
			Set<String> favourites = new HashSet<String>();

			Cursor c = dbFavourite.getFavourites();
			while (c.moveToNext()) {
				favourites.add(c.getString(DbFavourite.METHOD_NAME_INDEX));
			}

			searchResultsAdapter = new SearchResultsAdapter(SearchMethodActivity.this, res, favourites);
			//adapter = new SearchResultsAdapter(RingsimActivity.this, android.R.layout.simple_list_item_1, res);
			// here we are using the default named list			
			setListAdapter(searchResultsAdapter);
			lv = getListView();
			registerForContextMenu(lv);
			lv.setOnItemClickListener(new OnItemClickWatcher());			
		}
	}
	
    private class OnItemClickWatcher implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			SearchResults r = (SearchResults) getListView().getItemAtPosition(position);
			Intent intent = new Intent(SearchMethodActivity.this, BlueLineViewActivity.class);
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
