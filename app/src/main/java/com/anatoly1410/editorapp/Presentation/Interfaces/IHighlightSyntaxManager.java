package com.anatoly1410.editorapp.Presentation.Interfaces;

import android.text.Editable;

import com.anatoly1410.editorapp.Domain.HighlightSyntaxManager;
import com.anatoly1410.editorapp.Domain.TextSyntaxRange;

import java.util.ArrayList;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IHighlightSyntaxManager {
    interface OnSyntaxHighlightListener {
        void fireEvent(ArrayList<TextSyntaxRange> textRanges, int start,int end);
    }
    void setSyntaxHighlightListener(HighlightSyntaxManager.OnSyntaxHighlightListener listener);
    void highlightSyntax(Editable editableText);
    void highlightSyntax(Editable editableText,int start,int end);
    void stopHighlighting();
    boolean isEnabled();
    void setEnabledState(boolean isEnabled);
}
