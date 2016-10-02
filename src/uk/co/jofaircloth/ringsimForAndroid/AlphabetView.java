package uk.co.jofaircloth.ringsimForAndroid;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class AlphabetView extends View {

	private final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public AlphabetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
//	@Override
//	protected void onDraw(Canvas canvas) {
//		TextView t;
//		for (char c : ALPHABET.toCharArray()) {
//			t = new TextView(this.getContext());
//			t.setText(c);
//			t.setId(c);
//			t.setTag(c);
//			canvas.
//		}
//	}
	
	private class OnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			
		}
		
	}
	

}
