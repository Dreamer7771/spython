package com.anatoly1410.editorapp.Presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Data.VisualStylesManager;

/**
 * Created by 1 on 07.01.2017.
 */

public class TabPanel extends LinearLayout {
    private Paint mBottomLinePaint;
    private int mBottomLineSize = 3;
    private Tab openedTab;
    private Tab selectedTab;

    private MainActivity parentActivity;
    public TabPanel(Context context)
    {
        super(context);
        parentActivity = (MainActivity)context;
        init();
    }
    public TabPanel(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        parentActivity = (MainActivity)context;
        init();
    }
    public TabPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parentActivity = (MainActivity)context;
        init();
    }
    public void init()
    {
        setWillNotDraw(false);
        mBottomLinePaint = parentActivity.visualStylesManager.getTabOpenedBackgroundPaint();
    }
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawBottomLine(canvas);
    }
    private void drawBottomLine(Canvas canvas)
    {
        int h = getHeight();
        int w = getWidth();
        canvas.drawRect(0,getHeight() - mBottomLineSize,getWidth(),getHeight(),mBottomLinePaint);
    }
    public Tab addTab(String name)
    {
        Tab tab = new Tab(getContext());
        tab.setText(name);
        addView(tab);
        return tab;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        int childrenCount = getChildCount();
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            for(int i=0;i<childrenCount;++i)
            {
                Tab tab = (Tab)getChildAt(i);
                Rect rect = new Rect();
                tab.getHitRect(rect);
                int[] tabCoords = new int[2];
                tab.getLocationOnScreen(tabCoords);
                float x = event.getX();
                float y = event.getY();
                rect = new Rect(tab.getLeft(),tab.getTop(),tab.getLeft()+rect.width(),tab.getTop()
                        +rect.height());
                if(rect.contains((int)event.getX(),(int)event.getY())){
                    if(tab.getState() == Tab.TabState.Normal)
                    {
                        selectTab(tab);
                        break;
                    }
                }
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            boolean tabTouched = false;
            for(int i=0;i<childrenCount;++i)
            {
                Tab tab = (Tab)getChildAt(i);
                Rect rect = new Rect();
                tab.getHitRect(rect);
                int[] tabCoords = new int[2];
                tab.getLocationOnScreen(tabCoords);
                rect = new Rect(tab.getLeft(),tab.getTop(),tab.getLeft()+rect.width(),tab.getTop()
                        +rect.height());
                if(rect.contains((int)event.getX(),(int)event.getY())){
                    if(tab.getState() != Tab.TabState.Opened)
                    {
                        openTab(tab);
                        tabTouched = true;
                        break;
                    }
                }
                if(!tabTouched && selectedTab!=null)
                {
                    deselectTab(selectedTab);
                }
            }
        }
        return true;
    }

    public void selectTab(Tab tab)
    {
        if(selectedTab !=null)
        {
            selectedTab.close();
        }
        tab.select();
        selectedTab = tab;
    }

    public void openTab(Tab tab)
    {
        if(openedTab !=null)
        {
            openedTab.close();
        }

        tab.open();
        selectedTab = null;
        openedTab = tab;
        int tabsCount = getChildCount();
        for(int i=0;i<tabsCount;++i)
        {
            Tab tabView = (Tab)getChildAt(i);
            if(!tabView.equals(tab))
            {
                tabView.close();
            }
        }
        parentActivity.tabManager.openTab(tab);
    }
    private void deselectTab(Tab tab)
    {
        tab.close();
    }
    public void removeTab(Tab tab)
    {
        removeView(tab);
    }
}
