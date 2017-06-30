package com.anatoly1410.editorapp.Data;

import android.graphics.Paint;

import java.io.Serializable;

/**
 * Created by 1 on 18.05.2017.
 */

public class VisualStylesState implements Serializable {

    /* Paints and text settings for autocompletion menu*/
    public PaintState mAutocompMenuBackgroundPaintState;
    public PaintState mAutocompMenuSelectedItemPaintState;
    public int mAutocompMenuTextSize;
    public String mAutocompMenuTextFontFamily;
    public int mAutocompMenuTextColor;

    /* Paints and text settings for CEditText component*/
    public int mCEditTextBackgroundColor;
    public int  mCEditTextForegroundColor;
    public PaintState mCEditTextSelectionPaintState;
    public int mCEditTextTextSize;
    public String mCEditTextTextFontFamily;

    /* Paints for CScrollView component*/
    public PaintState mCScrollViewNumbersPaintState;
    public PaintState mCScrollViewLinePaintState;

    /* Paints and parameters for Tab component*/
    public PaintState mTabSelectedBackgroundPaintState;
    public PaintState mTabNormalBackgroundPaintState;
    public PaintState mTabOpenedBackgroundPaintState;
    public int mTabTextSize;
    public boolean showLineSelection;


    public VisualStylesState(){
        mAutocompMenuBackgroundPaintState = new PaintState();
        mAutocompMenuSelectedItemPaintState = new PaintState();
        mCEditTextSelectionPaintState = new PaintState();
        mCScrollViewNumbersPaintState = new PaintState();
        mCScrollViewLinePaintState = new PaintState();
        mTabSelectedBackgroundPaintState = new PaintState();
        mTabNormalBackgroundPaintState = new PaintState();
        mTabOpenedBackgroundPaintState = new PaintState();
    }
}
