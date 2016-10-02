package uk.co.jofaircloth.ringsimForAndroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.co.jofaircloth.ringsim.SearchResults;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SearchResultsAdapter extends ArrayAdapter<SearchResults> implements Filterable, SectionIndexer {

	private final Context context;
	private final List<SearchResults> values;
	private final Set<String> favourites;

	private HashMap<String, Integer> indexer = null;			
	private String[] sections = null;

	public SearchResultsAdapter(Context context, List<SearchResults> res, Set<String> favourites) {
		super(context, R.layout.row_method, res);
		this.context = context;
		this.values = res;
		this.favourites = favourites;

		// set up scrolling
		indexer = new HashMap<String, Integer>();
		
		int size = res.size();
		for (int i = size - 1; i >= 0; i--) {
			String r = res.get(i).toString();
			indexer.put(r.substring(0, 1), i);
			Log.d("indexer", r.substring(0, 1) + Integer.toString(i));
		}
		
		Set<String> keys = indexer.keySet();
		
		Iterator<String> it = keys.iterator();
		ArrayList<String> keyList = new ArrayList<String>();
		
		while (it.hasNext()) {
			String key = it.next();
			keyList.add(key);
		}
		
		Collections.sort(keyList);
		sections = new String[keyList.size()];
		keyList.toArray(sections);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_method, parent, false);
		
		ImageView iv = (ImageView) rowView.findViewById(R.id.row_method_favourite_icon);
		TextView tv = (TextView) rowView.findViewById(R.id.row_method_label);
		SearchResults res = values.get(position);
		tv.setText(res.toString());
		
		if (favourites.contains(res.getMethodName())) {
			iv.setVisibility(View.VISIBLE);
		} else {
			iv.setVisibility(View.INVISIBLE);
		}
		
		return rowView;		
	}

	@Override
	public int getPositionForSection(int section) {
		String letter = sections[section];
		Log.d("posn", Integer.toString(indexer.get(letter)));
		return indexer.get(letter);
	}
	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}
	@Override
	public Object[] getSections() {
		return sections;
	}

	public HashMap<String, Integer> getAlphabetIndexer() {
		return indexer;
	}

}
