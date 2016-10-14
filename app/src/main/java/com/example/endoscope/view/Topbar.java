package com.example.endoscope.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.endoscope.R;


//自定义Topbar
public class Topbar extends RelativeLayout {

	private ImageButton leftBT, rightBT;
	private TextView titleTV;

	private Drawable leftBackground;
	private Drawable leftSrc;

	private Drawable rightBackground;
	private Drawable rightSrc;

	private float titleTextSize;
	private int titleTextColor;
	private String toptitle;

	
	private LayoutParams leftParams, rightParams, titleParams;

	public interface topbarClickListener {
		public void leftClick();

		public void rightClick();
	}

	public topbarClickListener listener;

	public void setOnTopbarClickListener(topbarClickListener listener) {
		this.listener = listener;
	}

	@SuppressLint("NewApi") 
	public Topbar(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.Topbar);

		leftBackground = ta.getDrawable(R.styleable.Topbar_leftBackground);
		leftSrc = ta.getDrawable(R.styleable.Topbar_leftSrc);

		rightBackground = ta.getDrawable(R.styleable.Topbar_rightBackground);
		rightSrc = ta.getDrawable(R.styleable.Topbar_rightSrc);

		titleTextSize = ta.getDimension(R.styleable.Topbar_titleTextSize, 0);
		titleTextColor = ta.getColor(R.styleable.Topbar_titleTextColor, 0);
		toptitle = ta.getString(R.styleable.Topbar_toptitle);

		ta.recycle();

		leftBT = new ImageButton(context);
		rightBT = new ImageButton(context);
		titleTV = new TextView(context);

		leftBT.setBackground(leftBackground);
		leftBT.setImageDrawable(leftSrc);

		rightBT.setBackground(rightBackground);
		rightBT.setImageDrawable(rightSrc);

		titleTV.setTextColor(titleTextColor);
		titleTV.setTextSize(titleTextSize);
		titleTV.setText(toptitle);
		titleTV.setGravity(Gravity.CENTER);

		setBackgroundColor(0xFF77C0FE);

		leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
		addView(leftBT, leftParams);

		rightParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
		addView(rightBT, rightParams);

		titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		titleParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
		addView(titleTV, titleParams);

		leftBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.leftClick();
			}
		});

		rightBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.rightClick();
			}
		});

	}


	public void setLeftButtonIsVisiable(boolean flag) {
		if (flag) {
			leftBT.setVisibility(View.VISIBLE);
		} else {
			leftBT.setVisibility(View.GONE);
		}
	}


	public void setRightButtonIsVisiable(boolean flag) {
		if (flag) {
			rightBT.setVisibility(View.VISIBLE);
		} else {
			rightBT.setVisibility(View.GONE);
		}
	}


	public void setToptitle(String s) {
		titleTV.setText(s);
	}


	public void setLeftBT(int i) {
		leftBT.setImageResource(i);
	}


	public void setRightBT(int i) {
		rightBT.setImageResource(i);
	}

}
