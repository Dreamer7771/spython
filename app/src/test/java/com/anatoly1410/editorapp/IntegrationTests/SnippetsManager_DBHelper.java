package com.anatoly1410.editorapp.IntegrationTests;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.DBHelper;
import com.anatoly1410.editorapp.Domain.Snippet;
import com.anatoly1410.editorapp.Domain.SnippetManager;
import com.anatoly1410.editorapp.Domain.SnippetTreeElement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 1 on 13.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SnippetsManager_DBHelper {
    @Test
    public void check(){
        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        Context mockContext = mock(Context.class);
        DBHelper spyDBHelper = spy(new DBHelper(mockContext));
        doReturn(mockDB).when(spyDBHelper).getDatabase();
        doReturn(new ArrayList<SnippetTreeElement>()).when(spyDBHelper).getSnippetElements();
        SnippetManager spySnippetMgr = spy(new SnippetManager(spyDBHelper, false));
        spySnippetMgr.addSnippetInsertedElement("name","alias","value");
        ArgumentCaptor<ContentValues> argument = ArgumentCaptor.forClass(ContentValues.class);
        verify(mockDB).insert(eq("SnippetElement"),isNull(String.class),argument.capture());
        assertEquals(argument.getValue().getAsString("textvalue"),"value");
        assert(!argument.getValue().getAsBoolean("isfolder"));
    }
}
