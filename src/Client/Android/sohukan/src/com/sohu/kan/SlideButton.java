/**
 * 
 */
package com.sohu.kan;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


/**
 * @author Leon
 *
 */
public class SlideButton extends View implements OnTouchListener {
	
	private boolean bSwitch;
	private int mode, mode_size;
	private float currentX, innerX, innerMax;
	private Bitmap outter, inner;
	private boolean bClicked, bMoved;
	private float innerhalf, outterhalf;
	private OnClickListener clickListener = null;
	
	public SlideButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		bSwitch 	= true;
		currentX	= 0; 
		mode		= 0;
		mode_size	= 2;
		bClicked	= false;
		bMoved		= false;
		this.setClickable(true);
		
		outter = BitmapFactory.decodeResource(getResources(), R.drawable.outter_white);
		inner  = BitmapFactory.decodeResource(getResources(), R.drawable.inner_white);
		
		innerX = 0;
		innerMax = outter.getWidth() - inner.getWidth();
		innerhalf= inner.getWidth() / 2;
		outterhalf = outter.getWidth() / 2;
		this.setOnTouchListener(this);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint 	= new Paint();
		Matrix matrix 	= new Matrix();
		
		// draw background
		canvas.drawBitmap(outter, 0, 0, paint);
		// draw the inner according to the current x.
		canvas.drawBitmap(inner, innerX, 0, paint);
	}
	
	
	public boolean getSwitch() { return this.bSwitch; }
	public void setSwitch(boolean flag) { this.bSwitch = flag; }
	public boolean changeSwitch() { bSwitch = !bSwitch; return bSwitch; }
	
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if (v != this) return false;
		
		float x = e.getX();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			bClicked = true;
			currentX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			if (bClicked) {
				float delta = x - currentX;
				currentX = x;
				move(delta);
				bMoved = true;
				
			}
			break;
		case MotionEvent.ACTION_UP:
			if (bClicked) {
				bClicked = false;
				judge();
			}
			break;
			
		default:
			break;
		}
		
		invalidate();
		return false;
	}
	
	protected void move(float __delta) {
		if (__delta == 0) return;
		else if (__delta > 0) {
			innerX += __delta;
			if (innerX > innerMax) {
				innerX = innerMax;
			}
		} else {
			innerX += __delta;
			if (innerX < 0) {
				innerX = 0;
			}
		}
	}
	
	protected void judge() {
		if (bMoved) {
			boolean flag = false;
			if (innerX + innerhalf < outterhalf) {
				// left
				innerX = 0;
				if (!bSwitch) {
					bSwitch = true;
					flag = true;
				}
			} else {
				// right
				innerX = innerMax;
				if (bSwitch) {
					bSwitch = false;
					flag = true;
				}
			}
			bMoved   = false;
			
			if (null != clickListener && flag)	{
				clickListener.onClick(this);
			}
		}
	}
	
	@Override
	public void setOnClickListener(OnClickListener click) {
		this.clickListener = click;
	}
	
	
	/**
	 * 
	 * change to the opposite mode
	 * 
	 * __mode:		0: white
	 * 				1: black
	 * 
	 */
	public void changeMode(int __mode) {
		if (__mode == this.mode) {
			return;
		}
		
		this.mode = __mode;
		
		switch (mode) {
		case 0:
			outter = BitmapFactory.decodeResource(getResources(), R.drawable.outter_white);
			inner  = BitmapFactory.decodeResource(getResources(), R.drawable.inner_white);
			break;
		case 1:
			outter = BitmapFactory.decodeResource(getResources(), R.drawable.outter_black);
			inner  = BitmapFactory.decodeResource(getResources(), R.drawable.inner_black);
			break;
		
		}
		this.invalidate();
	}
	
	/**
	 * 
	 * auto change when you click, turn black to white or else.
	 * 
	 */
	public void changeAuto() {
		int tmp_mode = (this.mode + 1) % this.mode_size;
		changeMode(tmp_mode);
	 }

}
