package com.anatoly1410.editorapp.Presentation.Interfaces;

import android.app.Activity;

import com.anatoly1410.editorapp.Data.HelpIndexElement;
import com.anatoly1410.editorapp.Domain.HelpTreeListAdapter;
import com.anatoly1410.editorapp.Domain.Snippet;

import java.util.ArrayList;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IHelpManager {
    String getRootHelpDirectoryName();
    int getLastClickedItemIdx();
    void setLastClickedItemIdx(int idx);
    void setItemViewCreationListener(HelpTreeListAdapter.ItemViewCreationListener listener);
    void setHelpTreeListAdapter(HelpTreeListAdapter adapter);
    public interface OnViewedHelpListUpdatingListener {
        void fireEvent();
    }

    public interface OnHelpItemSelectedListener {
        void fireEvent(HelpIndexElement element, HelpIndexElement displayedElement);
    }
    void LoadHelp();
    String openHelpFile(String alias);
    String getHelpFileContentByPath(String path);
    boolean aliasExists(String alias);
    void setHelpActivity(Activity helpActivity);
    void clickOnItem(int idx);
    void gotoBack();
    void updateViewedSnippetsList();
    HelpIndexElement getCurrentHelpIndexElement();
    HelpIndexElement getCurrentDisplayedHelpElement();
    void setHelpItemSelectedListener(OnHelpItemSelectedListener listener);
    ArrayList<HelpIndexElement> getViewedHelpIndex();
    boolean HelpIsLoaded();
}
