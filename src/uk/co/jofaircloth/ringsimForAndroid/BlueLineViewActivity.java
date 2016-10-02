package uk.co.jofaircloth.ringsimForAndroid;

import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import uk.co.jofaircloth.ringsim.Method;
import uk.co.jofaircloth.ringsim.enums.Calls;
import uk.co.jofaircloth.ringsim.enums.MethodStage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class BlueLineViewActivity extends Activity {

	private BlueLineView blueLineView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = getIntent().getExtras();
		if (b != null) {
			ArrayList<String> nPlain = b.getStringArrayList("notationPlain");
			ArrayList<String> nBob = b.getStringArrayList("notationBob");
			ArrayList<String> nSingle = b.getStringArrayList("notationSingle");
			
			int stage = b.getInt("stage");
			String methodName = b.getString("name");
			
			Method mPlain = new Method().getBlueline(nPlain, MethodStage.getStage(stage));
			Method mAllBob = new Method().getBlueline(nBob, MethodStage.getStage(stage));
			Method mAllSingle = new Method().getBlueline(nSingle, MethodStage.getStage(stage));

			// get select range of rows for bobs and singles, and write as per plain
			// method
			int startRow = nPlain.size() - 4;
			int endRow = nPlain.size() + 5;
			Method mBob = new Method(), mSingle = new Method();
			for (int i = startRow; i < endRow; i++) {
				mBob.add(mAllBob.get(i));
				mSingle.add(mAllSingle.get(i));
			}

			setContentView(R.layout.blue_line_view);
			
			TextView t = (TextView) findViewById(R.id.blue_line_label);
			t.setText(methodName);
			
			blueLineView = (BlueLineView) findViewById(R.id.blue_line);
			blueLineView.setMethod(mPlain);
			blueLineView.setNotation(nPlain);
			blueLineView.setNoBells(stage);
			blueLineView.setCalls(Calls.PLAIN);

			blueLineView = (BlueLineView) findViewById(R.id.bob_line);
			blueLineView.setMethod(mBob);
			blueLineView.setNotation(nBob);
			blueLineView.setNoBells(stage);
			blueLineView.setCalls(Calls.BOB);
			blueLineView = (BlueLineView) findViewById(R.id.single_line);
			blueLineView.setMethod(mSingle);
			blueLineView.setNotation(nSingle);
			blueLineView.setNoBells(stage);
			blueLineView.setCalls(Calls.SINGLE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.blue_line_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_preferences:
			Intent intent = new Intent(this, PreferencesActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
