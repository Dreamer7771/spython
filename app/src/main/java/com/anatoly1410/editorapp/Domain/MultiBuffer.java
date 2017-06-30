package com.anatoly1410.editorapp.Domain;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;

import com.anatoly1410.editorapp.Domain.Interfaces.IDBHelper;
import com.anatoly1410.editorapp.Presentation.Interfaces.IMultiBuffer;

import java.util.ArrayList;

/**
 * Created by 1 on 13.02.2017.
 */

public class MultiBuffer implements IMultiBuffer {

    private IDBHelper mDBHelper;

    ArrayList<BufferFragment> mFragments;
    public ArrayList<BufferFragment> getFragments(){
        return mFragments;
    }
    Activity mParentActivity;
    ClipboardManager mClipboardManager;
    MultiBufferListAdapter multiBufferListAdapter;
    private int mDisableClipboardStateChangeHandling = 0;

    /*Helps to detect first parent activity assign from app start*/
    private boolean firstParentActivityAssign = true;
    private boolean mBufferUpdatingEnabled = true;

    public boolean getBufferUpdatingEnabled(){
        return mBufferUpdatingEnabled;
    }
    public void setBufferUpdatingEnabled(boolean value){
        mBufferUpdatingEnabled = value;
    }

    private int mLastClickedItemIdx = -1;

    public int getLastClickedItem(){
        return mLastClickedItemIdx;
    }

    public void setLastClickedItem(int idx){
        mLastClickedItemIdx = idx;
    }

    public boolean isClipboardStateChangeHandlingDisabling()
    {
        return mDisableClipboardStateChangeHandling > 0;
    }


    public MultiBuffer(IDBHelper dbHelper)
    {
        mDBHelper = dbHelper;
        loadBufferFragments();
    }

    OnFragmentInsertionListener mFragmentInsertionListener;

    public void setFragmentInsertionListener(OnFragmentInsertionListener listener) {
        mFragmentInsertionListener = listener;
    }

    public void setMultiBufferListAdapter(MultiBufferListAdapter adapter){
        multiBufferListAdapter = adapter;
    }


    public void setParentActivity(Activity activity) {
        mParentActivity = activity;


        if(firstParentActivityAssign) {
            mClipboardManager = (ClipboardManager) mParentActivity.getSystemService(activity.CLIPBOARD_SERVICE);

            mClipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                public void onPrimaryClipChanged() {
                    if (!mBufferUpdatingEnabled) {
                        return;
                    }
                    if (!mClipboardManager.hasPrimaryClip() ||
                            !(mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))) {
                        return;
                    }
                    if (!isClipboardStateChangeHandlingDisabling()) {
                        ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
                        String pasteFragmentContent = item.getText().toString();
                        addFragment(pasteFragmentContent);
                    } else {
                        --mDisableClipboardStateChangeHandling;
                    }
                }
            });
        }

        loadBufferFragments();
        firstParentActivityAssign = false;
    }

    private void loadBufferFragments(){
        ArrayList<BufferFragment> fragments = mDBHelper.getBufferFragments();
        if(mFragments == null){
            mFragments = new ArrayList<>();
        }
        mFragments.clear();
        mFragments.addAll(fragments);

        if(multiBufferListAdapter != null){
            multiBufferListAdapter.notifyDataSetChanged();
        }
    }
    public void addFragment(String fragment){
        long id = mDBHelper.addMultibufElement(multiBufferListAdapter.getCount(),fragment);
        multiBufferListAdapter.add(new BufferFragment(fragment, id));
    }
    public void removeFragment(int idx){
        if(idx < 0 || idx >=mFragments.size())
        {
            return;
        }
        BufferFragment fragment = multiBufferListAdapter.getItem(idx);
        mDBHelper.removeMultibufElement(fragment);
        multiBufferListAdapter.remove(fragment);
    }
    public void clearFragments(){
        mDBHelper.clearMultibuffer();
        multiBufferListAdapter.clear();
    }

    public void pasteFragment(int idx)
    {
        if(idx < 0 || idx >= multiBufferListAdapter.getCount())
        {
            return;
        }
        String content = multiBufferListAdapter.getItem(idx).content;
        ClipData clip = ClipData.newPlainText("",content);
        ++mDisableClipboardStateChangeHandling;
        mClipboardManager.setPrimaryClip(clip);

        if(mFragmentInsertionListener != null){
            mFragmentInsertionListener.fireEvent(content);
        }
    }

    public String getFragmentByIdx(int idx){
        if(idx < 0 || idx >= multiBufferListAdapter.getCount())
        {
            return "";
        }
        return multiBufferListAdapter.getItem(idx).content;

    }

    public void updateFragment(int idx, String newContent){
        if(idx < 0 || idx >= multiBufferListAdapter.getCount())
        {
            return;
        }

        BufferFragment fragment =   multiBufferListAdapter.getItem(idx);

        mDBHelper.updateMultibufElement(idx,fragment,newContent);
        multiBufferListAdapter.getItem(idx).content = newContent;
        multiBufferListAdapter.notifyDataSetChanged();
    }
}
