package com.anatoly1410.editorapp.Presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.TextView;

import com.anatoly1410.editorapp.Data.VisualStylesManager;

/**
 * Created by 1 on 07.01.2017.
 */
/*View class for tab - presented as TextView child*/
public class Tab extends TextView {
    /*State of tab*/
    public enum TabState{Opened,Selected,Normal};
    public TabState mState;
    /*Paints for tab background*/
    private Paint mTabSelectedBkgPaint;
    private Paint mTabNormalBkgPaint;
    private Paint mTabOpenedBkgPaint;
    /*Size of header text*/
    private int textSize;

    private MainActivity parentActivity;
    public Tab(Context context)
    {
        super(context);
        parentActivity = (MainActivity)context;
        mTabSelectedBkgPaint = parentActivity.visualStylesManager.getTabSelectedBackgroundPaint();
        mTabNormalBkgPaint = parentActivity.visualStylesManager.getTabNormalBackgroundPaint();
        mTabOpenedBkgPaint = parentActivity.visualStylesManager.getTabOpenedBackgroundPaint();
        textSize = parentActivity.visualStylesManager.getTabtextSize();
        setTextSize(textSize);
        setPadding(10,10,10,10);
        mState = TabState.Normal;
    }
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        super.onDraw(canvas);
    }
    /*Draws background*/
    public void drawBackground(Canvas canvas)
    {
        Rect rect = new Rect();
        getDrawingRect(rect);
        Paint paint = new Paint();
        switch(mState)
        {
            case Normal:
                paint = mTabNormalBkgPaint;
                break;
            case Opened:
                paint = mTabOpenedBkgPaint;
                break;

            case Selected:
                paint = mTabSelectedBkgPaint;
                break;
        }
        canvas.drawRect(rect,paint);
    }
    /*Set tab state to selected*/
    public void select()
    {
        mState = TabState.Selected;
        invalidate();
    }
    /*Set tab state to open*/
    public void open()
    {
        mState = TabState.Opened;
        invalidate();
    }
    /*Set tab state to close*/
    public void close()
    {
        mState = TabState.Normal;
        invalidate();
    }
    public TabState getState()
    {
        return mState;
    }
}

