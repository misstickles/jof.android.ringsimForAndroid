package uk.co.jofaircloth.ringsimForAndroid;

import java.util.List;

import uk.co.jofaircloth.ringsim.SearchResults;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultsViewAdapter123 extends ArrayAdapter<SearchResults> {

	private final Context context;
	private final List<SearchResults> res;
	
	public ResultsViewAdapter123(Context ctx, List<SearchResults> res) {
		super(ctx, R.layout.results_list, res);
		this.context = ctx;
		this.res = res;
	}
	
	@Override
	public View getView(int pos, View concertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.results_list, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		textView.setText(res.get(pos).toString());
		imageView.setImageResource(R.drawable.bell_icon);
		
		return rowView;		
	}
	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//	}
//	
//	private class OnItemClickWatcher implements OnItemClickListener {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int pos,
//				long id) {
//			Toast.makeText(getApplicationContext(), ((TextView)view).getText(), Toast.LENGTH_LONG).show();			
//		}
//	}
//	
//	public List<SearchResults> searchMethods() {
//		ReadMSLIB lib = new ReadMSLIB(stage, cls, search);
//		List<SearchResults> res = lib.searchMethod();
//		return res;
//	}
	
}

