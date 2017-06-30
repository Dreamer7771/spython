package com.anatoly1410.editorapp.Domain.Interfaces;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IFileManager {
    void saveToFile(String objFileName, Object obj);
    Object loadFromFile(String objFileName);
    String loadContent(String path);
    void saveTabAsExistingFile(String path, String content);
}
