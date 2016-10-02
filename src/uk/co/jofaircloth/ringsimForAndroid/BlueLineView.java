package uk.co.jofaircloth.ringsimForAndroid;

import java.util.ArrayList;

import uk.co.jofaircloth.ringsim.Method;
import uk.co.jofaircloth.ringsim.Row;
import uk.co.jofaircloth.ringsim.enums.Calls;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class BlueLineView extends View {

	// **** SETTINGS...
	private SharedPreferences prefs;
	private char highlightBell1;
	private boolean showNumbers;
	private boolean showAsSingleColumn;

	private Paint p = new Paint();
	// TODO: use Calls enum to iterate method 'types'...??
	// TODO: change ArrayList<String> to Notation
	private Method method;
	private ArrayList<String> notation;
	private int noBells;

	private int parentWidth;
	private int parentHeight;
	private PointF lastTouch = new PointF();
	private float posX;
	private float posY;
	private Canvas c;
	private Context context;

	// for grid display
	private int currentColumn = 0;
	private int startX = 0, startY = 0, endX = 0, endY = 0;
	private int offsetTextInitialX = 40, offsetTextInitialY = 23;
	private int offsetLineInitialX = 45, offsetLineInitialY = 18; // difference
																	// is 6
	private int offsetNotationInitialX = 1, offsetNotationInitialY = 28;

	private int textSize = 15;
	private float dotRadius = 3f;
	private int columnWidth = 0;
	private Calls call;

	private static final int INVALID_POINTER_ID = -1;
	private int activePointerId = INVALID_POINTER_ID;

	public void setCalls(Calls c) {
		call = c;
	}
	
	public void setMethod(Method m) {
		method = m;
	}

	public void setNotation(ArrayList<String> n) {
		notation= n;
	}

	public void setNoBells(int b) {
		noBells = b;
	}

	public BlueLineView(Context context) {
		super(context, null, 0);
	}

	public BlueLineView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);

		this.context = context;

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangedWatcher());

		p.setAntiAlias(true);
		p.setStrokeWidth(0);
		p.setTextSize(textSize);
		setBackgroundColor(Color.WHITE);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		c = canvas;

		highlightBell1 = prefs.getString(context.getString(R.string.pref_highlight_bell_1), "4").charAt(0);
		showAsSingleColumn = prefs.getBoolean(context.getString(R.string.pref_show_as_single_line), true);
		showNumbers = prefs.getBoolean(context.getString(R.string.pref_show_bell_numbers), true);

		// canvas.save();
		// canvas.translate(posX, posY);
		// canvas.scale(scaleFactor, scaleFactor);
		
		writeMethod(canvas, method, notation);

		LinearLayout.LayoutParams params = (LayoutParams) getLayoutParams();
		params.height = (method.size() * textSize) + offsetTextInitialY;
		params.width = method.get(0).length() * textSize + 100;
		setLayoutParams(params);
		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		this.setMeasuredDimension(parentWidth, parentHeight);
		this.setLayoutParams(new LinearLayout.LayoutParams(parentWidth,
				parentHeight));
		// this.setLayoutParams(new View.LayoutParams(parentWidth,
		// parentHeight));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private void writeMethod(Canvas canvas, Method m, ArrayList<String> n) {
		if (m != null) {
			int nextIdx = 0;
			String c;
			int i = 0;
	
			int offsetTextX = 40, offsetTextY = 23;
			int offsetLineX = 46, offsetLineY = 19; // difference is 6
			int offsetNotationX = 1, offsetNotationY = 28;
	
			if (isACall(call)) {
				offsetTextX = 5;
				offsetLineX = offsetTextInitialX - offsetLineInitialX + 5;
				offsetNotationX = 0;
				offsetTextY = 0;
				offsetLineY = offsetTextInitialY - offsetLineInitialY;
				offsetNotationY = 0;
			} else {
				offsetTextX = offsetTextInitialX;
				offsetLineX = offsetLineInitialX;
			}
	
			currentColumn = 1;
	
			// TODO: as this method is called 00s of times, re-initiate... (FIX!)
			for (Row r : m) {
	
				if (!showAsSingleColumn) {
	
					if (i != 0 && (i % n.size() == 0)) {
						// newColumn();
						offsetLineX = offsetLineInitialX + ((parentWidth / 6) * currentColumn);
						offsetTextX = offsetTextInitialX + ((parentWidth / 6) * currentColumn);
						offsetLineY = offsetLineInitialY;
						offsetTextY = offsetTextInitialY;
						currentColumn++;
						Log.d("offset", Integer.toString(offsetLineX));
					}
				}
	
				for (int j = 0; j < r.length(); j++) {
					c = Character.toString(r.charAt(j));
	
					// find the index of current char in the next row
					if (i < m.size() - 1) {
						nextIdx = m.get(i + 1).indexOf(r.charAt(j));
						if (nextIdx > j) {
							startX = (j * textSize) + offsetLineX;
							startY = (i * textSize) + offsetLineY;
							endX = (nextIdx * textSize) + offsetLineX;
							endY = ((i + 1) * textSize) + offsetLineY;
						} else if (nextIdx < j) {
							startX = (j * textSize) + offsetLineX;
							startY = (i * textSize) + offsetLineY;
							endX = (nextIdx * textSize) + offsetLineX;
							endY = ((i + 1) * textSize) + offsetLineY;
						} else {
							startX = (j * textSize) + offsetLineX;
							startY = (i * textSize) + offsetLineY;
							endX = (j * textSize) + offsetLineX;
							endY = ((i + 1) * textSize) + offsetLineY;
						}
					}
	
					if (c.equals("1")) {
						p.setColor(Color.RED);
						canvas.drawLine(startX, startY, endX, endY, p);
					} else if (c.equals(Character.toString(highlightBell1))) {
						p.setColor(Color.BLUE);
						canvas.drawLine(startX, startY, endX, endY, p);
					} else if (isACall(call)
							&& (new Row(noBells)).contains(c.charAt(0))) {
						p.setColor(Color.BLUE);
						canvas.drawLine(startX, startY, endX, endY, p);
					} else {
						if (showNumbers) {
							p.setColor(Color.BLACK);
							canvas.drawText(c, (textSize * j) + offsetTextX,
									(textSize * i) + offsetTextY, p);
						}
					}
	
					if (!isACall(call)) {
						if (showNumbers) {
							if ((isACall(call) && i == 4)
									|| (i != 0 && i % n.size() == 0)) {
								canvas.drawLine(offsetTextX, (textSize * (i - 1))
										+ offsetTextY, (noBells * textSize)
										+ offsetTextX, (textSize * (i - 1))
										+ offsetTextY, p);
							}
						} else {
							// just draw a blob on the line...
							if (i % n.size() == 0 && i != m.size() - 1) {
								p.setColor(Color.BLUE);
								canvas.drawCircle(
										(r.indexOf(highlightBell1) * textSize)
												+ offsetTextX + (dotRadius * 2),
										(textSize * (i - 1)) + offsetTextY
												+ (textSize) - dotRadius - 1,
										dotRadius, p);
							}
						}
					}
	
					if (!isACall(call)) {
						if (i % n.size() == 0 && i != m.size() - 1) {
							drawPlaceBell(r, i);
						}
					}
					if (i == 0) {
						if (showNumbers && !isACall(call)) {
							drawPlaceNotation(n);
						}
					}
	
				}
	
				i++;
			}
		}
	}

	private boolean isACall(Calls call) {
		if (call.equals(Calls.BOB) || call.equals(Calls.SINGLE)) {
			return true;
		} else {
			return false;
		}
	}

	private void drawPlaceBell(Row r, int row) {
		// get position of highlight bell - this is the place bell
		int place = r.indexOf(highlightBell1) + 1;
		p.setColor(Color.BLACK);
		c.drawText(Integer.toString(place), (noBells * textSize) + offsetTextInitialX, (row * textSize) + offsetTextInitialY, p);
	}

	private void drawPlaceNotation(ArrayList<String> not) {
		int i = 0;
		for (String n : not) {
			c.drawText(n, offsetNotationInitialX + 8, (textSize * i) + offsetNotationInitialY, p);
			i++;
		}
	}

	private void newColumn() {
		int colWidth = parentWidth / 3;
		columnWidth += colWidth;
	}

	private class OnSharedPreferenceChangedWatcher implements
			OnSharedPreferenceChangeListener {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			invalidate();
		}

	}

	/**
	 * this doesn't work with scroll view anyway...
	 */
	// @Override
	// public boolean onTouchEvent(MotionEvent ev) {
	// // dumpEvent(ev);
	// final int action = ev.getAction();
	//
	// switch (action & MotionEvent.ACTION_MASK) {
	// case MotionEvent.ACTION_DOWN: {
	// // remember where we started
	// lastTouch.set(ev.getX(), ev.getY());
	//
	// // save the id of this pointer
	// activePointerId = ev.getPointerId(0);
	// break;
	// }
	// case MotionEvent.ACTION_MOVE: {
	// // find the index of the active pointer and fetch its position
	// final int pointerIndex = ev.findPointerIndex(activePointerId);
	// final float x = ev.getX(pointerIndex);
	// final float y = ev.getY(pointerIndex);
	//
	// // calculate the distance moved
	// final float dx = x - lastTouch.x;
	// final float dy = y - lastTouch.y;
	//
	// posX += dx/10;
	// posY += dy/10;
	//
	// // invalidate to request a redraw
	// invalidate();
	// break;
	// }
	// case MotionEvent.ACTION_UP: {
	// activePointerId = INVALID_POINTER_ID;
	// break;
	// }
	// case MotionEvent.ACTION_CANCEL: {
	// activePointerId = INVALID_POINTER_ID;
	// break;
	// }
	// case MotionEvent.ACTION_POINTER_UP: {
	// // extract the index of the pointer that left the touch sensor
	// final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
	// >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	// final int pointerId = ev.getPointerId(pointerIndex);
	// if (pointerId == activePointerId) {
	// //action this was our active pointer going up. Choose a new active
	// pointer and adjust accordingly
	// final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
	// lastTouch.set(ev.getX(newPointerIndex), ev.getY(newPointerIndex));
	// activePointerId = ev.getPointerId(newPointerIndex);
	// }
	// break;
	// }
	// }
	// return true;
	// }

}
