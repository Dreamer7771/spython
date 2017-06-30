package com.anatoly1410.editorapp.Domain.Interfaces;

import android.graphics.Paint;

import com.anatoly1410.editorapp.Data.VisualStylesManager;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IVisualStylesManager {
    /*Style change event*/
    interface OnStyleChangedListener {
        public void onStyleChanged();
    }
    void addStyleChangedListener(OnStyleChangedListener styleChangedListener);
    int getTabtextSize();
    void setCEditTextTextSize(int textSize);
    void setDefaultStyle();
    void setDarkStyle();

    Paint getAutocompMenuBackgroundPaint();
    Paint getAutocompMenuSelectedItemPaint();
    int getAutocompMenuTextSize();
    String getAutocompMenuTextFontFamily();
    int getAutocompMenuTextColor();

    int getCEditTextBackgroundColor();
    int getCEditTextForeroundColor();
    Paint getCEditTextSelectionPaint();
    int getCEditTextTextSize();
    String getCEditTextTextFontFamily();

    Paint getCScrollViewNumbersPaint();
    Paint getCScrollViewLinePaint();

    Paint getTabSelectedBackgroundPaint();
    Paint getTabNormalBackgroundPaint();
    Paint getTabOpenedBackgroundPaint();
    boolean getLineSelectionFlag();
    void setLineSelectionFlag(boolean enabled);
    void saveVisualSettings();

    int getLeftIndent();
    int getCodeRowNumberSize();
    int getLeftCodeTextPadding();
}
