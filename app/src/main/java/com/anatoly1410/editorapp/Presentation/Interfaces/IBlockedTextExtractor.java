package com.anatoly1410.editorapp.Presentation.Interfaces;

import com.anatoly1410.editorapp.Domain.BlockedTextSpinnerAdapter;
import com.anatoly1410.editorapp.Domain.SyntaxBlock;

import java.util.ArrayList;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IBlockedTextExtractor {
    void setBlockedTextSpinnerAdapter(BlockedTextSpinnerAdapter adapter);
    void incDisableSpinnerItemSelCounter();
    boolean isSpinnerItemSelectionHandlingDisabled();
    interface OnChangeSelectionPosFromSpinnerListener {
        void fireEvent(int pos);
    }
    interface OnSpinnerSelectedItemChangeListener {
        void fireEvent(int itemPos);
    }
    void setChangeSelectionPosFromSpinnerListener(OnChangeSelectionPosFromSpinnerListener listener);
    void setSpinnerSelectedItemChangeListener(OnSpinnerSelectedItemChangeListener listener);
    ArrayList<SyntaxBlock> getDisplayedBlocks();
    void extractBlockedTextFromString(String str);
    void onBlockSpinnerItemSelection(int itemIdx);
    void onCursorPosChanged(int newCursorPos);
    void setSpinnerIsTouchedFlag(boolean value);
    boolean isEnabled();
    void setEnabledState(boolean isEnabled);
}
