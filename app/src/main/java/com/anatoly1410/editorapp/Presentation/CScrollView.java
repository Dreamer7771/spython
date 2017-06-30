package com.anatoly1410.editorapp.Presentation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.anatoly1410.editorapp.R;
import com.anatoly1410.editorapp.Data.VisualStylesManager;

/**
 * Created by 1 on 14.12.2016.
 */
/*Vertical ScrollView component in CodeEditText
 */
public class CScrollView extends ScrollView {
    /*References to inner components*/
    private CEditText mCEditText;
    /*Reference to the parent activity*/
    private MainActivity mParentActivity;
    /*Component rectangle*/
    private Rect mRect;
    /*Paints*/
    private Paint mNumbersPaint;
    private Paint mLinePaint;
    /*Vertical line indent*/
    private int mVerticalLineIndent = 40;
    /*Constructor
    * - sets init style settings
    * - sets basic listeners*/
    public CScrollView(Context context, AttributeSet attrs) {
        super(context,attrs);
        mParentActivity = (MainActivity)context;
        mRect = new Rect();
        setStyleSettings();
        mParentActivity.visualStylesManager.addStyleChangedListener(new VisualStylesManager.OnStyleChangedListener() {
            @Override
            public void onStyleChanged() {
                setStyleSettings();
            }
        });
    }

    public void setBoundCEditText(CEditText cEditText){
        this.mCEditText = cEditText;
    }
    /*Sets style settings for component*/
    private void setStyleSettings() {
        mNumbersPaint =  mParentActivity.visualStylesManager.getCScrollViewNumbersPaint();
        setRowNumbersSize(mParentActivity.visualStylesManager.getCodeRowNumberSize());
        mLinePaint =  mParentActivity.visualStylesManager.getCScrollViewLinePaint();
    }
    /*Draw method*/
    @Override
    protected void onDraw(Canvas canvas) {
        getDrawingRect(mRect);
        int height = getMeasuredHeight();
        if(mCEditText.getHeight() > height)
        {
            height = mCEditText.getHeight();
        }
        int lineHeight = mCEditText.getLineHeight();
        int count = 1;

        if (mCEditText.getLineCount() > count) {
            count = mCEditText.getLineCount();
        }
        drawLineNumbers(canvas,lineHeight,count);
       // drawVerticalLine(canvas, height);
        super.onDraw(canvas);
    }

    public void setRowNumbersSize(int size){
        if(mNumbersPaint == null){
            return;
        }
        mNumbersPaint.setTextSize(size);
    }
    /*Draws line numbers*/
    private void drawLineNumbers(Canvas canvas, int lineHeight, int linesCount)
    {
        for(int i=0;i<linesCount;++i)
        {
            int lineNum = i+1;
            String numberS = Integer.toString(lineNum);
            Rect bounds = new Rect();
            mNumbersPaint.getTextBounds(numberS,0,numberS.length(),bounds);
            int numY = i*lineHeight + (lineHeight + bounds.height())/2;
            canvas.drawText(numberS,0,numY,mNumbersPaint);
        }
    }
    /*InterceptTouchEvent handler*/
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mParentActivity == null)
            return super.onInterceptTouchEvent(ev);

        AutocompletionMenu menu = (AutocompletionMenu) mParentActivity.findViewById(R.id.autocompMenu);
        int[] l = new int[2];
        menu.getLocationOnScreen(l);
        Rect menuRect = new Rect(menu.getLeft(),menu.getTop(), l[0] + menu.getWidth(), l[1] + menu.getHeight());
        int x = (int)ev.getX();
        int y = (int)ev.getY();

        int ac = ev.getAction();
        if(menuRect.contains((int)ev.getX(),(int)ev.getY()))
        {
            return false;
        }else{
           return super.onInterceptTouchEvent(ev);
        }

    }
    /*Sets parent activity reference*/
    public void setParentActivity(MainActivity activity)
    {
        this.mParentActivity = activity;
    }
}
