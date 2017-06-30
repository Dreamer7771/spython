package com.anatoly1410.editorapp.Presentation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.anatoly1410.editorapp.Domain.Pair;
import com.anatoly1410.editorapp.Domain.Snippet;
import com.anatoly1410.editorapp.R;
import com.anatoly1410.editorapp.UtilityMethods;
import com.anatoly1410.editorapp.Data.VisualStylesManager;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by 1 on 21.12.2016.
 */
/*Menu for autocompletion*/
public class AutocompletionMenu extends ListView {
    /*Height and width of component*/
    private int mHeight;
    private int mWidth;

    /*Menu item height*/
    private int mItemHeight;

    /*Parameters for distinguishing click from move action
    * - coordinates of the last tap
    * - max distance between two taps to consider it as a click*/
    private Point mLastActionDownCoords;
    private float maxClickDist = 5;

    /*Paints and colors*/
    private int mAutocompMenuTextColor;

    private MainActivity parentActivity;
    /*Sets height and width of the component*/
    public void setHeight(int height)
    {
        this.mHeight = height;
    }
    public void setWidth(int width) { this.mWidth = width;}

    /*Constructor:
    * - sets concrete height and width of component
    * - set initial style settings
    * - sets basic listeners*/

    /*Autocompletion ListView instance*/
    ListView mAutocompListView;

    public ListView getmAutocompListView(){
        return this;
    }

    public void setParentActivity(MainActivity activity){
        parentActivity = activity;
    }

    public AutocompletionMenu(Context context,AttributeSet attrs) {
        super(context, attrs);

        parentActivity = (MainActivity)context;
        mWidth = 300;
        mHeight = 200;
        mItemHeight = 0;
        setDividerHeight(0);
        mLastActionDownCoords = null;
        setStyleSettings();
        parentActivity.visualStylesManager.addStyleChangedListener(new VisualStylesManager.OnStyleChangedListener() {
            @Override
            public void onStyleChanged() {
                setStyleSettings();
            }
        });
    }
    /*Sets style settings for component*/
    private void setStyleSettings() {
        mAutocompMenuTextColor = parentActivity.visualStylesManager.getAutocompMenuTextColor();

        mAutocompMenuTextColor = parentActivity.visualStylesManager.getAutocompMenuTextColor();
    }
    /*Sets parent activity and other components references
    * Sets menu content*/
    /*Draw selection for selected item*/

    /*Sets concrete mWidth and mHeight*/
    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(mWidth,mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }
    /*Sets position of the left-top corner of the component*/
    public void setPosition(PointF pos, int lineHeight, int cScrollViewHeight, int scrollY)
    {
        if(pos == null)
            return;
        int halfOfCSViewHeight = cScrollViewHeight/2;
        setX((int) pos.x);
        if(pos.y - scrollY < halfOfCSViewHeight) {
            setY((int) pos.y);
        }else{

            int height = getTotalHeightofListView();
            if(height > halfOfCSViewHeight){
                height = halfOfCSViewHeight;
            }
            setY((int)pos.y - lineHeight - height);
        }
    }

    private int getTotalHeightofListView() {

        ListAdapter lvAdapter = getAdapter();
        int listviewElementsheight = 0;
        for (int i = 0; i < lvAdapter.getCount(); i++) {
            View mView = lvAdapter.getView(i, null, this);
            mView.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            listviewElementsheight += mView.getMeasuredHeight();
        }
        return listviewElementsheight;
    }
    /*Sets menu visibility*/
    public void setMenuVisibility(boolean isVisible)
    {
        if(isVisible)
        {
            setVisibility(View.VISIBLE);

        }else{
            setVisibility(View.INVISIBLE);
        }
    }

}