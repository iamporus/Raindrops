/*
 * Copyright (C) 2014 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.room.raindrops.components;

/**
 * @author Purushottam Pawar
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.room.raindrops.R;

import java.util.ArrayList;

/**
 * Extends the default HorizontalScrollView of android to provide snapping
 * ability to child items. When scrolled to either of the directions, child
 * items snap to the fixed positions and the item at center position is
 * displayed as selected. Clicking on child items works in same fashion.
 */
public class SnappingScrollView extends ScrollView {

    /**
     * Callback when snapping is finished and one of the child item is selected.
     */
    public interface OnScrollerItemSelectedListener {
        public void onScrollerItemSelected(int selectedItem);
    }

    /**
     * Custom layout to lay out the children at equal distances
     */
    private EquiSpacerLayout internalWrapper;

    /**
     * currently selected child item
     */
    private int selectedItem;

    /**
     * listener to provide callback on item selected upon scroll or click
     */
    private OnScrollerItemSelectedListener mItemSelectedListener;

    /**
     * minimum allowed child width
     */

    protected int childWidth = EquiSpacerLayout.MIN_CHILD_WIDTH;

    protected int childHeight;

    private boolean scrollingDown;

    public SnappingScrollView(Context context) {
        super(context);
        init(context, null);
    }

    public SnappingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SnappingScrollView(Context context, AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        internalWrapper = new EquiSpacerLayout(getContext());
        internalWrapper.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        internalWrapper.setOrientation(LinearLayout.VERTICAL);
        addView(internalWrapper);

        setOnTouchListener(mTouchListener);
    }

    /**
     * Get index of currently selected item
     *
     * @return index of currently selected item
     */
    public int getSelectedItemIndex() {
        return selectedItem;
    }

    public View getSelectedItem(){
        return internalWrapper.getChildAt(selectedItem);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Set an item as selected. The list is scrolled to the index of selected
     * item and the item is highlighted.
     *
     * @param isClicked    pass in true if the item is clicked else false
     * @param selectedItem the index of item that should be selected
     */
    private void setSelectedItem(boolean isClicked, int selectedItem) {

        int currentSelection = selectedItem;
        int visibleItems = internalWrapper.getVisibleChildrenCount();
        if (!isClicked)
            currentSelection = selectedItem + visibleItems / 2;

        if (internalWrapper.getChildAt(currentSelection) instanceof TextView) {

            ((TextView) internalWrapper.getChildAt(currentSelection))
                    .setTextColor(Color.RED);
            for (int i = 0; i < internalWrapper.getChildCount(); i++) {
                if (i != currentSelection)
                    ((TextView) internalWrapper.getChildAt(i))
                            .setTextColor(Color.GRAY);
            }
        }

        if (isClicked) {
//			smoothScrollTo(
//					(selectedItem - visibleItems / 2) * (int) childHeight, 0);
//			this.selectedItem = selectedItem - visibleItems / 2;
        } else {
            smoothScrollTo(0, selectedItem * (int) childHeight);
            this.selectedItem = selectedItem;
        }

        internalWrapper.invalidate();

        if (mItemSelectedListener != null)
            mItemSelectedListener.onScrollerItemSelected(getSelectedItemIndex());
    }

    /**
     * Set an item as selected. The list is scrolled to the index of selected
     * item and the item is highlighted.
     * <p/>
     * <p/>
     * the index of item that should be selected
     */
    public void setSelectedItem(int index) {
        setSelectedItem(false, index);
    }

    /**
     * Register a callback to be invoked when an item is selected from the
     * horizontal list
     *
     * @param mListener The callback that will run
     */
    public void setOnScrollerItemSelectedListener(
            OnScrollerItemSelectedListener mListener) {
        this.mItemSelectedListener = mListener;
    }

    /**
     * Set String items as child views to the SnappingHorizontalScrollView.
     *
     * @param items Array of items to be displayed as child inside
     *              SnappingHorizontalScrollView
     */
    public void setFeatureItems(ArrayList<String> items) {

        for (int i = 0; i < items.size(); i++) {

            TextView tx = (TextView) View.inflate(getContext(),
                    R.layout.list_item, null);
            tx.setText(items.get(i));
            tx.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    for (int i = 0; i < internalWrapper.getChildCount(); i++) {
                        if (internalWrapper.getChildAt(i) == v)
                            setSelectedItem(true, i);
                    }
                }
            });
            internalWrapper.addView(tx);
        }


    }

    /**
     * Set inflated custom layouts as children to the
     * SnappingHorizontalScrollView.
     *
     * @param items Array of items to be displayed as child inside
     *              SnappingHorizontalScrollView
     */
    public void setFeatureItems(View[] items) {

        try {
            for (int i = 0; i < items.length; i++) {
                View item = items[i];
                if (item != null) {
                    item.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            for (int i = 0; i < internalWrapper.getChildCount(); i++) {
                                if (internalWrapper.getChildAt(i) == v)
                                    setSelectedItem(true, i);
                            }
                        }
                    });
                    internalWrapper.addView(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Add a single item at the end of the list
     *
     * @param item view to be added in the list
     */
    public void addFeatureItem(View item) {

        item.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                for (int i = 0; i < internalWrapper.getChildCount(); i++) {
                    if (internalWrapper.getChildAt(i) == v)
                        setSelectedItem(true, i);
                }
            }
        });

        internalWrapper.addView(item);


    }

    public void clearFeatureItems(){
        internalWrapper.removeAllViews();
        invalidate();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (t > oldt)
            scrollingDown = true;
        else
            scrollingDown = false;
    }

    private OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            childHeight = internalWrapper.getChildAt(0).getHeight();
            if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                int scrollY = getScrollY();
                System.out.println("Scroll Y : " + scrollY);
                int factor = scrollY / (int) childHeight;
                System.out.println("Factor: " + factor);

                if (scrollY < childHeight / 4) {
                    factor = 0;
                    System.out.println("Setting factor to 0 ");
                } else {
                    if (scrollingDown && scrollY > (factor * childHeight) + childHeight / 4) {
                        System.out.println("Incrementing factor ");
                        factor++;
                    } else if (factor != 0 && !scrollingDown && scrollY > (factor * childHeight) + childHeight / 4) {
                        //do nothing
                    }
                }

                setSelectedItem(false, factor);

                return true;
            } else {

                return false;
            }
        }
    };

    /**
     * Aim of this layout is to lay out its children with fixed heights and
     * widths
     *
     * @author Purushottam Pawar
     */
    private class EquiSpacerLayout extends LinearLayout {

        private static final int MIN_CHILD_WIDTH = 150;
        private int maxWidth;
        private int calculatedChildWidth = MIN_CHILD_WIDTH;
        private int maxHeight;
        private int calculatedChildHeight;

        public EquiSpacerLayout(Context context) {
            super(context);

            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay()
                    .getMetrics(displaymetrics);
            maxWidth = displaymetrics.widthPixels;
            maxHeight = displaymetrics.heightPixels;
        }

        public int getVisibleChildrenCount() {

            return 1;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

			/*-

			1. Get proposed width for the parent using received measure specs
			2. Calculate child width which shall be	totalParentWidth/noOfChildren
			3. Create Measure Specs for child using derived child width
			4. Get height of child with max height
			5. Create Measure Specs for the height using derived child height
			6. Pass the specs to every child by calling measure() on every child
			7. call setMeasuredDimensions on parent with original received width
			and max height of child
			
			-*/

            int numberOfChilds = getChildCount();

            int width = MeasureSpec.getSize(widthMeasureSpec)
                    - getPaddingLeft() - getPaddingRight();
            int height = MeasureSpec.getSize(heightMeasureSpec)
                    - getPaddingTop() - getPaddingBottom();


            calculatedChildWidth = maxWidth;
            calculatedChildHeight = maxHeight;


            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    calculatedChildWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    calculatedChildHeight, MeasureSpec.EXACTLY);

            int maxChildHeight = maxHeight;

            for (int i = 0; i < numberOfChilds; i++) {

                View child = getChildAt(i);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                height += child.getMeasuredHeight();

            }

            setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(),
                    height);

        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right,
                                int bottom) {
            int childCount = getChildCount();

            int childLeft = 0;
            for (int i = 0; i < childCount; i++) {

                View child = getChildAt(i);
                final int childMeasuredWidth = child.getMeasuredWidth();
                final int childMeasuredHeight = child.getMeasuredHeight();
//
                int childTop = (i * childMeasuredHeight);
                child.layout(childLeft, childTop, childLeft
                        + childMeasuredWidth, childTop + childMeasuredHeight);
            }
        }

    }

}
