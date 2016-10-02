package uk.co.jofaircloth.ringsimForAndroid;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.jofaircloth.ringsim.Notation;
import uk.co.jofaircloth.ringsim.SearchResults;
import uk.co.jofaircloth.ringsim.enums.Calls;
import uk.co.jofaircloth.ringsim.enums.Libraries;
import uk.co.jofaircloth.ringsim.enums.MethodClass;
import uk.co.jofaircloth.ringsim.enums.MethodStage;
import uk.co.jofaircloth.ringsim.libraries.ReadMSLIB;
import uk.co.jofaircloth.ringsimForAndroid.dal.DbFavourite;
import uk.co.jofaircloth.ringsimForAndroid.settings.AndroidSettings;
import uk.co.jofaircloth.ringsimForAndroid.utils.SdCard;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RingsimActivity extends ListActivity {
	
	private Spinner spnStage, spnClass;
	private ProgressDialog progress;
	SearchResultsAdapter searchResultsAdapter;
	private ListView lv;
	DbFavourite dbFavourite;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // TODO: un-hardcode library (add options panel...!!)
	    AndroidSettings.setLibraryType(Libraries.MSLIB);
	    dbFavourite = new DbFavourite(this);
		
	    setContentView(R.layout.main);
	    
	    spnClass = (Spinner) findViewById(R.id.methodClass); 
		ArrayAdapter<MethodClass> adClass = new ArrayAdapter<MethodClass>(this,
					android.R.layout.simple_spinner_item, MethodClass.values());
		adClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnClass.setAdapter(adClass);
		spnClass.setSelection(7);
		spnClass.setOnItemSelectedListener(new ItemSelectedListener());
		  
		spnStage = (Spinner) findViewById(R.id.methodStage);
		ArrayAdapter<MethodStage> adStage = new ArrayAdapter<MethodStage>(this,
					android.R.layout.simple_spinner_item, MethodStage.values());
		adStage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnStage.setAdapter(adStage);
		spnStage.setSelection(3);
		spnStage.setOnItemSelectedListener(new ItemSelectedListener());
		
		setAlphabetView();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	// save my settings...
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	dbFavourite.close();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_show_favourites:
				Intent intent = new Intent(this, FavouritesActivity.class);
				startActivity(intent);
				return true;
			case R.id.menu_search_method:
				Intent searchIntent = new Intent(this, SearchMethodActivity.class);
				startActivity(searchIntent);
				return true;
			case R.id.menu_preferences:
				Intent preferencesIntent = new Intent(this, PreferencesActivity.class);
				startActivity(preferencesIntent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		SearchResults itm = (SearchResults) lv.getItemAtPosition(info.position);

		switch (item.getItemId()) {
		case R.id.favourite:
			if (dbFavourite.addFavourite(itm)) {
				ImageView iv = (ImageView) info.targetView.findViewById(R.id.row_method_favourite_icon);				
				iv.setVisibility(View.VISIBLE);

				Toast.makeText(getApplicationContext(), itm.toString() + " added to favourites", Toast.LENGTH_LONG).show();
				searchResultsAdapter.notifyDataSetChanged();
				lv.invalidateViews(); //.invalidate();
			}
			return true;
		case R.id.remove_favourite:
			if (dbFavourite.deleteFavourite(itm)) {				
				Toast.makeText(getApplicationContext(), itm.toString() + " removed from favourites", Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.draw_on_screen:
			Intent intent = new Intent(this, BlueLineViewActivity.class);
			Notation n = new Notation(itm);        
			intent.putStringArrayListExtra("notationPlain", n.format(Calls.PLAIN));
			intent.putStringArrayListExtra("notationBob", n.format(Calls.BOB));
			intent.putStringArrayListExtra("notationSingle", n.format(Calls.SINGLE));
			intent.putExtra("stage", itm.getMethodStage());
			intent.putExtra("name", itm.toString());
			startActivity(intent);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	public void alphabetClick(View view) {
		try {
			lv.setSelection(searchResultsAdapter.getAlphabetIndexer().get(view.getTag()));
		} catch (Exception ex) {
			
		}
	}
	
	private void setAlphabetView() {
//		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//		HashMap<String, Integer> available = searchResultsAdapter.getAlphabetIndexer();
//		TextView tv;
//		View vwAlphabet = this.findViewById(R.id.alphabet);
//		for (char a : alphabet.toCharArray()) {
//			if (!available.containsKey(Character.toString(a))) {
//				tv = (TextView) vwAlphabet.findViewWithTag(Character.toString(a));
//				tv.setVisibility(View.INVISIBLE);
//			}
//		}
	}
	
    private class ItemSelectedListener implements OnItemSelectedListener {

    	private List<SearchResults> res = null;

    	@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			// TODO: progress bar
		    progress = ProgressDialog.show(parent.getContext(), "", "Searching. Please wait...", true);
			
		    // run the search in a new thread...
//		    SearchResultsThread searchThread = new SearchResultsThread();
//		    searchThread.start();
		    
			res = getMethodResults();					
		    setupResultAdapter();
	    	Toast.makeText(getApplicationContext(), String.format("Found %s results", res.size()), Toast.LENGTH_SHORT).show();

			// dismiss seems to not show it at all - but it does popup!
			// TODO pursue the threading...
			progress.dismiss();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub			
		}
		
		private List<SearchResults> getMethodResults() {
			List<SearchResults> res = null;
			if (SdCard.checkMedia()) {
				// TODO: try to get switch with enums working...
				Libraries lib = AndroidSettings.getLibraryType();
				if (lib == Libraries.MSLIB) {
					AndroidSettings.setMSLibFileName();
					ReadMSLIB r = new ReadMSLIB((MethodClass)spnClass.getSelectedItem(), (MethodStage)spnStage.getSelectedItem());
					res = r.searchFiles();
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

			searchResultsAdapter = new SearchResultsAdapter(RingsimActivity.this, res, favourites);

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
			SearchResults r = (SearchResults) lv.getItemAtPosition(position);
			Intent intent = new Intent(RingsimActivity.this, BlueLineViewActivity.class);
			Notation n = new Notation(r);        
			intent.putStringArrayListExtra("notationPlain", n.format(Calls.PLAIN));
			intent.putStringArrayListExtra("notationBob", n.format(Calls.BOB));
			intent.putStringArrayListExtra("notationSingle", n.format(Calls.SINGLE));
			intent.putExtra("stage", r.getMethodStage());
			intent.putExtra("name", r.toString());
			startActivity(intent);

			// create PDF
//			SearchResults r = (SearchResults) lv.getItemAtPosition(position);
//			AndroidSettings.setPdfLocation();
//			AndroidSettings.setIsTitleFileName(true);
//			
//			Notation n = new Notation(r);
//			ArrayList<String> notation = n.format();
//			Method m = new Method().getBlueline(notation, MethodStage.getStage(r.getMethodStage()));
//	        WritePdf pdf = new WritePdf(m, notation, r.toString());
//	        String pdfFile = pdf.createPdf();
//	        
//	        File f = new File(pdfFile);
//	        if (f.exists()) {
//	        	Uri path = Uri.fromFile(f);
//	        	Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//	        	pdfIntent.setDataAndType(path, "application/pdf");
//	        	pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	        	
//	        	try {
//	        		Intent chooser = Intent.createChooser(pdfIntent, "Choose PDF viewer");
//	        		startActivity(chooser);
////	        		startActivity(pdfIntent);
//	        	} catch (ActivityNotFoundException anfEx) {
//	        		Toast.makeText(parent.getContext(), "No application available to open the PDF", Toast.LENGTH_LONG).show();
//	        	}
//	        }
//
//	        Toast.makeText(parent.getContext(), r.toString(), Toast.LENGTH_LONG).show();
//			Toast.makeText(parent.getContext(), notation.toString(), Toast.LENGTH_LONG).show();
		}
    	
    }
        
}