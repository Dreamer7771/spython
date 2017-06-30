package com.anatoly1410.editorapp.Presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 1 on 15.05.2017.
 */

public class CTextView extends TextView {
    private Paint mLinePaint;
    private Paint mNumbersPaint;
    private int mVerticalLineIndent;
    public void setVerticalLineIndent(int indent){
        mVerticalLineIndent = indent;
    }
    private SettingsActivity parentActivity;
    public void setParentActivity(SettingsActivity activity){
        parentActivity = activity;
        mLinePaint = parentActivity.visualStylesManager.getCScrollViewLinePaint();
        mNumbersPaint = parentActivity.visualStylesManager.getCScrollViewNumbersPaint();
    }
    public void setRowNumberSize(int size){
        if(mNumbersPaint == null){
            return;
        }

        mNumbersPaint.setTextSize(size);
    }
    public CTextView(Context context){
        super(context);
    }
    public CTextView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public CTextView(Context context,  AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }
    /*Draw method*/
    @Override
    protected void onDraw(Canvas canvas) {
        Rect rect = new Rect();
        getDrawingRect(rect);
        int height = getHeight();


        int count = 1;

        if (getLineCount() > count) {
            count = getLineCount();
        }
        int cursorPosition = getSelectionEnd();

        Rect bounds = new Rect();

        int baseline = getLineBounds(0, bounds);
        int firstLineHeight = bounds.bottom;

        int lineTop;

        drawNumber(canvas);
        drawVerticalLine(canvas, getMeasuredHeight());
        super.onDraw(canvas);
    }
    /*Draws vertical left-side line*/
    private void drawVerticalLine(Canvas canvas, int height)
    {
        canvas.drawLine(mVerticalLineIndent,0,mVerticalLineIndent,height,mLinePaint);
    }
    private void drawNumber(Canvas canvas){
        int lineHeight = getHeight();
        String numberS = "1";
        Rect bounds = new Rect();
        mNumbersPaint.getTextBounds(numberS,0,numberS.length(),bounds);
        int numY = (lineHeight + bounds.height())/2;
        canvas.drawText(numberS,5,numY,mNumbersPaint);
    }
}
