package com.anatoly1410.editorapp.Domain.Interfaces;

import com.anatoly1410.editorapp.Domain.Snippet;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by 1 on 21.05.2017.
 */

public interface IExtraSnippetsManager {
    ArrayList<Snippet> getExtraSnippets();
    ArrayList<Snippet> loadExtraSnippets();
}
