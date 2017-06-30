package com.anatoly1410.editorapp.Domain.Interfaces;

import com.anatoly1410.editorapp.Data.HelpIndexElement;
import com.anatoly1410.editorapp.Domain.Snippet;

import java.util.ArrayList;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IHelpLoadManager {
    ArrayList<HelpIndexElement> LoadHelpIndex();
    String LoadHelpFileContent(String path);
    boolean HelpIsLoaded();
}
