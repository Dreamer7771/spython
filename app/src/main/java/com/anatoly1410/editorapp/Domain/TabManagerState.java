package com.anatoly1410.editorapp.Domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 1 on 17.05.2017.
 */

public class TabManagerState implements Serializable {
    public ArrayList<TabContent> mTabs;
    public TabContent mOpenedTab;
}
