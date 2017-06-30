package com.anatoly1410.editorapp.Data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.ViewUtils;

import com.anatoly1410.editorapp.Domain.Interfaces.IFileManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IVisualStylesManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by 1 on 29.12.2016.
 *
 * Keeps basic graphic data of application
 * Allows to change visual style
 * Implemented as a singleton
 */

public class VisualStylesManager implements IVisualStylesManager {

    private Context mContext;
    private IFileManager mFileManager;

    private VisualStylesState mState;

    private final float leftIndentScaleK = 2.7083f;//65/24;
    private final float numberSizeScaleK = 0.8333f;//20/24;


    public enum Styles{Light, Dark};

    private final String styleSettingsFileName= "visual_settings.dat";

    private ArrayList<OnStyleChangedListener> mStyleChangedListenersList = new ArrayList<>();

    public void addStyleChangedListener(OnStyleChangedListener styleChangedListener)
    {
        mStyleChangedListenersList.add(styleChangedListener);
    }
    /*Fires style change event*/
    private void fireStyleChangeEvent()
    {

        for(OnStyleChangedListener listener : mStyleChangedListenersList)
        {
            listener.onStyleChanged();
        }
    }
    //indent for vertical line
    public int getLeftIndent(){
        return (int)(leftIndentScaleK*getCEditTextTextSize());
    }
    //left indent for con
    public int getLeftCodeTextPadding(){
        return (int)(1.1*getLeftIndent());
    }
    public int getCodeRowNumberSize(){
        return (int)(numberSizeScaleK*getCEditTextTextSize());
    }

    public Paint getAutocompMenuBackgroundPaint() {
        return getPaintFromPaintState(mState.mAutocompMenuBackgroundPaintState);
    }
    public Paint getAutocompMenuSelectedItemPaint() {
        return getPaintFromPaintState(mState.mAutocompMenuSelectedItemPaintState);
    }
    public int getAutocompMenuTextSize() { return mState.mAutocompMenuTextSize;}
    public String getAutocompMenuTextFontFamily() { return mState.mAutocompMenuTextFontFamily;}
    public int getAutocompMenuTextColor() { return mState.mAutocompMenuTextColor;}

    public int getCEditTextBackgroundColor() { return mState.mCEditTextBackgroundColor;}
    public int getCEditTextForeroundColor() { return mState.mCEditTextForegroundColor;}
    public Paint getCEditTextSelectionPaint() {
        return getPaintFromPaintState(mState.mCEditTextSelectionPaintState);
    }
    public int getCEditTextTextSize() { return mState.mCEditTextTextSize;}
    public String getCEditTextTextFontFamily() { return mState.mCEditTextTextFontFamily;}

    public Paint getCScrollViewNumbersPaint() {
        return getPaintFromPaintState(mState.mCScrollViewNumbersPaintState);
    }
    public Paint getCScrollViewLinePaint() {
        return getPaintFromPaintState(mState.mCScrollViewLinePaintState);
    }

    public Paint getTabSelectedBackgroundPaint() {
        return getPaintFromPaintState(mState.mTabSelectedBackgroundPaintState);
    }
    public Paint getTabNormalBackgroundPaint() {
        return getPaintFromPaintState(mState.mTabNormalBackgroundPaintState);
    }
    public Paint getTabOpenedBackgroundPaint() {
        return getPaintFromPaintState(mState.mTabOpenedBackgroundPaintState);
    }
    public int getTabtextSize() { return mState.mTabTextSize;}

    public boolean getLineSelectionFlag(){
        return mState.showLineSelection;
    }

    public void setCEditTextTextSize(int textSize) {
        mState.mCEditTextTextSize = textSize;
        fireStyleChangeEvent();
    }

    public void setLineSelectionFlag(boolean enabled){
        mState.showLineSelection = enabled;
        fireStyleChangeEvent();
    }

    public VisualStylesManager(IFileManager fileManager, Context context)
    {
        mContext = context;
        mFileManager = fileManager;
        loadVisualSettings();
        fireStyleChangeEvent();
    }

    public void setDefaultStyle()
    {
        mState.mAutocompMenuBackgroundPaintState.antiAlias = true;
        mState.mAutocompMenuBackgroundPaintState.textSize = 16;
        mState.mAutocompMenuBackgroundPaintState.color = "#ffcccccc";
        mState.mAutocompMenuBackgroundPaintState.style = Paint.Style.FILL;
        mState.mAutocompMenuTextSize = 25;
        mState.mAutocompMenuTextFontFamily = "Courier";
        mState.mAutocompMenuTextColor = 0xff000000;
        mState.mAutocompMenuSelectedItemPaintState.color = "#FF979AB2";
        mState.mAutocompMenuSelectedItemPaintState.style = Paint.Style.FILL;

        mState.mCEditTextBackgroundColor = 0xfffafafa;
        mState.mCEditTextForegroundColor = 0xff000000;

        mState.mCEditTextSelectionPaintState.style = Paint.Style.FILL;
        mState.mCEditTextSelectionPaintState.color = "#ffdddddd";
        mState.mCEditTextTextSize = 25;
        mState.mCEditTextTextFontFamily = "Courier";

        mState.mCScrollViewNumbersPaintState.style = Paint.Style.FILL;
        mState.mCScrollViewNumbersPaintState.color = "#ff000000";
        mState.mCScrollViewNumbersPaintState.textSize = 20;

        float numbTextSize = numberSizeScaleK*mState.mCEditTextTextSize;
        mState.mCScrollViewNumbersPaintState.textSize = (int)numbTextSize;

        mState.mCScrollViewLinePaintState.style = Paint.Style.FILL;
        mState.mCScrollViewLinePaintState.color = "#000000";

        mState.mTabNormalBackgroundPaintState.style = Paint.Style.FILL;
        mState.mTabNormalBackgroundPaintState.color = "#ffdbdbdb";

        mState.mTabOpenedBackgroundPaintState.style = Paint.Style.FILL;
        mState.mTabOpenedBackgroundPaintState.color = "#fff3f3f3";

        mState.mTabSelectedBackgroundPaintState.style = Paint.Style.FILL;
        mState.mTabSelectedBackgroundPaintState.color = "#ffa0a0a0";

        mState.mTabTextSize = 20;

        mState.showLineSelection = true;

        fireStyleChangeEvent();
    }
    public void setDarkStyle()
    {
        mState.mAutocompMenuBackgroundPaintState.antiAlias = true;
        mState.mAutocompMenuBackgroundPaintState.textSize = 16;
        mState.mAutocompMenuBackgroundPaintState.color = "#ffcccccc";
        mState.mAutocompMenuBackgroundPaintState.style = Paint.Style.FILL;
        mState.mAutocompMenuTextSize = 25;
        mState.mAutocompMenuTextFontFamily = "Courier";
        mState.mAutocompMenuTextColor = 0xff000000;
        mState.mAutocompMenuSelectedItemPaintState.color = "#FF979AB2";
        mState.mAutocompMenuSelectedItemPaintState.style = Paint.Style.FILL;

        mState.mCEditTextBackgroundColor = 0xffffffff;
        mState.mCEditTextForegroundColor = 0xff000000;

        mState.mCEditTextSelectionPaintState.style = Paint.Style.FILL;
        mState.mCEditTextSelectionPaintState.color = "#ffdddddd";
        mState.mCEditTextTextSize = 25;
        mState.mCEditTextTextFontFamily = "Courier";

        mState.mCScrollViewNumbersPaintState.style = Paint.Style.FILL;
        mState.mCScrollViewNumbersPaintState.color = "#ff000000";
        mState.mCScrollViewNumbersPaintState.textSize = 20;

        float numbTextSize = numberSizeScaleK*mState.mCEditTextTextSize;
        mState.mCScrollViewNumbersPaintState.textSize = (int)numbTextSize;

        mState.mCScrollViewLinePaintState.style = Paint.Style.FILL;
        mState.mCScrollViewLinePaintState.color = "#ff000000";

        mState.mTabNormalBackgroundPaintState.style = Paint.Style.FILL;
        mState.mTabNormalBackgroundPaintState.color = "#ffdbdbdb";

        mState.mTabOpenedBackgroundPaintState.style = Paint.Style.FILL;
        mState.mTabOpenedBackgroundPaintState.color = "#fff3f3f3";

        mState.mTabSelectedBackgroundPaintState.style = Paint.Style.FILL;
        mState.mTabSelectedBackgroundPaintState.color = "#ffa0a0a0";

        mState.mTabTextSize = 20;

        mState.showLineSelection = true;

        fireStyleChangeEvent();
    }

    public void saveVisualSettings(){
        mFileManager.saveToFile(styleSettingsFileName, mState);
    }

    private void loadVisualSettings(){
        String fullFileStatePath = mContext.getFilesDir().getAbsolutePath() + "/"
                + styleSettingsFileName;
        File dataStateFile = new File(fullFileStatePath);
        if(dataStateFile.exists())
        {
            mState = (VisualStylesState)mFileManager.loadFromFile(styleSettingsFileName);
        }else{
            mState = new VisualStylesState();
            setDefaultStyle();
        }
    }

    private Paint getPaintFromPaintState(PaintState state){
        Paint p = new Paint();

        if(state.color != null){
            p.setColor(Color.parseColor(state.color));
        }
        if(state.style != null){
            p.setStyle(state.style);
        }
        if(state.textSize != null){
            p.setTextSize(state.textSize);
        }
        if(state.antiAlias != null){
            p.setAntiAlias(state.antiAlias);
        }
        return p;
    }

}
