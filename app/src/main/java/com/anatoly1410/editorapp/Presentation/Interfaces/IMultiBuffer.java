package com.anatoly1410.editorapp.Presentation.Interfaces;

import android.app.Activity;

import com.anatoly1410.editorapp.Domain.BufferFragment;
import com.anatoly1410.editorapp.Domain.MultiBufferListAdapter;

import java.util.ArrayList;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IMultiBuffer {
    ArrayList<BufferFragment> getFragments();
    boolean getBufferUpdatingEnabled();
    void setBufferUpdatingEnabled(boolean value);
    int getLastClickedItem();
    void setLastClickedItem(int idx);
    boolean isClipboardStateChangeHandlingDisabling();
    interface OnFragmentInsertionListener {
        void fireEvent(String fragmentContent);
    }
    void setFragmentInsertionListener(IMultiBuffer.OnFragmentInsertionListener listener);
    void setMultiBufferListAdapter(MultiBufferListAdapter adapter);
    void setParentActivity(Activity activity);
    void addFragment(String fragment);
    void removeFragment(int idx);
    void clearFragments();
    void pasteFragment(int idx);
    String getFragmentByIdx(int idx);
    void updateFragment(int idx, String newContent);
}
