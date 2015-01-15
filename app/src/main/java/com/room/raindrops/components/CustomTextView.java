package com.room.raindrops.components;

/**
 * @author Purushottam Pawar
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

	private String customFont;

	public CustomTextView(Context context) {
		super(context);
		init(context, null);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

		if (!isInEditMode()) {
			if (attrs != null) {
				this.customFont = attrs.getAttributeValue(
						"http://schemas.android.com/apk/res-auto",
						"ttf_name");

				Typeface font = Typeface.createFromAsset(context.getAssets(),
						customFont);
				setTypeface(font);
			}
		}

	}

	@Override
	public void setTypeface(Typeface tf) {
		super.setTypeface(tf);
	}

}
