package com.anatoly1410.editorapp.DataTests.DBHelper;

import android.content.Context;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.FileManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by 1 on 07.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class FileManager_tests {
    Context context;
    @Before
    public void beforeTesting(){
        context =  mock(Context.class);
    }
    @Test
    public void checkloadContent() {
        ClassLoader classLoader = getClass().getClassLoader();
        FileManager spyExtraSnippetsMgr = spy(new FileManager(context));
        String filePath =  classLoader.getResource("raw/test_file.py").getPath();
        String fileContent = spyExtraSnippetsMgr.loadContent(filePath);
        assertEquals(fileContent,"def main():\n    pass\n");
    }
    @Test
    public void checkloadContent_emptyFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        FileManager spyExtraSnippetsMgr = spy(new FileManager(context));
        String filePath =  classLoader.getResource("raw/test_file2.py").getPath();
        String fileContent = spyExtraSnippetsMgr.loadContent(filePath);
        assertEquals(fileContent,"");
    }
    @Test
    public void checksaveTabAsExistingFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        FileManager spyFileMgr = spy(new FileManager(context));
        FileOutputStream mockFileOutputStream = mock(FileOutputStream.class);
        try {
            doReturn(mockFileOutputStream).when(spyFileMgr).getFileOutputStream(anyString());
        }catch(FileNotFoundException e){
            assert(false);
        }
        try {
            spyFileMgr.saveTabAsExistingFile("tab_file_path", "content");
            verify(spyFileMgr).getFileOutputStream("tab_file_path");
            verify(mockFileOutputStream).write("content".getBytes());
            verify(mockFileOutputStream).close();
        }catch(FileNotFoundException e){
            assert(false);
        }catch(IOException e){
            assert(false);
        }

    }
}
