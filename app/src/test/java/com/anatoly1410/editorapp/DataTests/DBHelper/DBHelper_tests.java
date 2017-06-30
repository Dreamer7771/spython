package com.anatoly1410.editorapp.DataTests.DBHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.DBHelper;
import com.anatoly1410.editorapp.Domain.BufferFragment;
import com.anatoly1410.editorapp.Domain.Pair;
import com.anatoly1410.editorapp.Domain.Snippet;
import com.anatoly1410.editorapp.Domain.SnippetFolder;
import com.anatoly1410.editorapp.Domain.SnippetInsertedElement;
import com.anatoly1410.editorapp.Domain.SnippetTreeElement;
import com.anatoly1410.editorapp.Domain.SyntaxBlock;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by 1 on 05.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DBHelper_tests {
    private ContentValues create(){
        return new ContentValues();
    }
    @Test
    public void checkBufferFragmentsAddition(){
        Cursor mockCursor = mock(Cursor.class);
        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockCursor.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockCursor.getColumnIndex("ordernum")).thenReturn(0);
        when(mockCursor.getColumnIndex("textvalue")).thenReturn(1);
        when(mockCursor.getColumnIndex("_id")).thenReturn(2);
        when(mockCursor.getString(1)).thenReturn("text 3").thenReturn("text 1").thenReturn("text 2");
        when(mockCursor.getInt(0)).thenReturn(2).thenReturn(0).thenReturn(1);
        when(mockCursor.getLong(2)).thenReturn(1l).thenReturn(2l).thenReturn(3l);

        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        when(mockDB.query("MultibufElement",null,null,null,null,null,null)).thenReturn(mockCursor);

        DBHelper dbHelper = spy(new DBHelper(null));
        doReturn(mockDB).when(dbHelper).getDatabase();

        dbHelper.addMultibufElement(2,"text 3");
        dbHelper.addMultibufElement(0,"text 1");
        dbHelper.addMultibufElement(1,"text 2");

        ArgumentCaptor<ContentValues> argument = ArgumentCaptor.forClass(ContentValues.class);

        verify(mockDB,times(3)).insert(eq("MultibufElement"),isNull(String.class),argument.capture());

        List<ContentValues> contentValues = argument.getAllValues();
        assertEquals(Integer.valueOf(2), contentValues.get(0).getAsInteger("ordernum"));
        assertEquals("text 3", contentValues.get(0).getAsString("textvalue"));
        assertEquals(Integer.valueOf(0), contentValues.get(1).getAsInteger("ordernum"));
        assertEquals("text 1", contentValues.get(1).getAsString("textvalue"));
        assertEquals(Integer.valueOf(1), contentValues.get(2).getAsInteger("ordernum"));
        assertEquals("text 2", contentValues.get(2).getAsString("textvalue"));

        ArrayList<BufferFragment> fragments = dbHelper.getBufferFragments();

        assertEquals(fragments.size(),3);
        assertEquals(fragments.get(0).content,"text 1");
        assertEquals(fragments.get(1).content,"text 2");
        assertEquals(fragments.get(2).content,"text 3");
    }
    @Test
    public void checkMultibufElementUpdating(){
        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        DBHelper dbHelper = spy(new DBHelper(null));
        doReturn(mockDB).when(dbHelper).getDatabase();

        dbHelper.updateMultibufElement(4,new BufferFragment("old value",1),"updated value");

        ArgumentCaptor<ContentValues> argument = ArgumentCaptor.forClass(ContentValues.class);
        verify(mockDB).update(eq("MultibufElement"),argument.capture(),eq("_id = ?"),eq(new String[]{"1"}));
        assertEquals(Integer.valueOf(4),argument.getValue().getAsInteger("ordernum"));
        assertEquals("updated value",argument.getValue().getAsString("textvalue"));
    }
    @Test
    public void checkMultibufElementRemoval(){
        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        DBHelper dbHelper = spy(new DBHelper(null));
    }
    @Test
    public void checkGetBufferFragments(){
        Cursor mockCursor = mock(Cursor.class);
        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockCursor.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockCursor.getColumnIndex("ordernum")).thenReturn(0);
        when(mockCursor.getColumnIndex("textvalue")).thenReturn(1);
        when(mockCursor.getColumnIndex("_id")).thenReturn(2);
        when(mockCursor.getString(1)).thenReturn("text 3").thenReturn("text 1").thenReturn("text 2");
        when(mockCursor.getInt(0)).thenReturn(2).thenReturn(0).thenReturn(1);
        when(mockCursor.getLong(2)).thenReturn(1l).thenReturn(2l).thenReturn(3l);

        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        when(mockDB.query("MultibufElement",null,null,null,null,null,null)).thenReturn(mockCursor);

        DBHelper dbHelper = spy(new DBHelper(null));
        doReturn(mockDB).when(dbHelper).getDatabase();

        ArrayList<BufferFragment> fragments = dbHelper.getBufferFragments();

        assertEquals(fragments.size(),3);
        assertEquals(fragments.get(0).content,"text 1");
        assertEquals(fragments.get(1).content,"text 2");
        assertEquals(fragments.get(2).content,"text 3");
    }
    @Test
    public void checkSnippetElementAddition() {
        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        DBHelper dbHelper = spy(new DBHelper(null));
        doReturn(mockDB).when(dbHelper).getDatabase();

        dbHelper.addSnippetElement("snippet", "snip", null,
                "snippet content", false);

        ArgumentCaptor<ContentValues> argument = ArgumentCaptor.forClass(ContentValues.class);
        verify(mockDB).insert(eq("SnippetElement"),isNull(String.class),argument.capture());
        ContentValues cv = argument.getValue();
        assertEquals(cv.getAsString("name"),"snippet");
        assertEquals(cv.getAsString("alias"),"snip");
        assertEquals(cv.getAsString("textvalue"),"snippet content");
        assert(!cv.getAsBoolean("isfolder"));
    }
    @Test
    public void checkSnippetInsertedElementUpdating() {
        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);

        DBHelper dbHelper = spy(new DBHelper(null));
        doReturn(mockDB).when(dbHelper).getDatabase();
        SnippetInsertedElement snippetElement = new SnippetInsertedElement("snippet",null,8,new Snippet("sn_tag","con tent",3));

        dbHelper.updateSnippetElement(snippetElement);
        ArgumentCaptor<ContentValues> argument = ArgumentCaptor.forClass(ContentValues.class);
        verify(mockDB).update(eq("SnippetElement"),argument.capture(),eq("_id = ?"),eq(new String[]{"8"}));
        ContentValues cv = argument.getValue();
        assertEquals(cv.getAsString("name"),"snippet");
        assertEquals(cv.getAsString("alias"),"sn_tag");
        assertEquals(cv.getAsString("textvalue"),"con tent");
        assert(!cv.getAsBoolean("isfolder"));
    }
    @Test
    public void checkSnippetFolderElementUpdating() {
        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);

        DBHelper dbHelper = spy(new DBHelper(null));
        doReturn(mockDB).when(dbHelper).getDatabase();
        SnippetFolder snippetElement = new SnippetFolder("snippet folder",null,8,null);

        dbHelper.updateSnippetElement(snippetElement);
        ArgumentCaptor<ContentValues> argument = ArgumentCaptor.forClass(ContentValues.class);
        verify(mockDB).update(eq("SnippetElement"),argument.capture(),eq("_id = ?"),eq(new String[]{"8"}));
        ContentValues cv = argument.getValue();
        assertEquals(cv.getAsString("name"),"snippet folder");
        assert(!cv.containsKey("alias"));
        assert(!cv.containsKey("textvalue"));
        assert(!cv.containsKey("isfolder"));
    }
    @Test
    public void checkSnippetRemoval() {
        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        DBHelper dbHelper = spy(new DBHelper(null));
        doReturn(mockDB).when(dbHelper).getDatabase();

        dbHelper.removeSnippetElement(new SnippetInsertedElement("removed value",null,3,new Snippet("alias","my content",0)));

        verify(mockDB).delete("SnippetElement","_id = ?",new String[]{"3"});
    }
    @Test
    public void checkGetSnippetElements() {
        Cursor mockCursor = mock(Cursor.class);
        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockCursor.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockCursor.getColumnIndex("name")).thenReturn(0);
        when(mockCursor.getColumnIndex("alias")).thenReturn(1);
        when(mockCursor.getColumnIndex("_id")).thenReturn(2);
        when(mockCursor.getColumnIndex("parentSnippet")).thenReturn(3);
        when(mockCursor.getColumnIndex("isfolder")).thenReturn(4);
        when(mockCursor.getColumnIndex("textvalue")).thenReturn(5);

        when(mockCursor.getString(0)).thenReturn("parent 1").thenReturn("child 1").thenReturn("child 2");
        when(mockCursor.getString(1)).thenReturn("par_").thenReturn("chi1_").thenReturn("chi2_");
        when(mockCursor.getShort(4)).thenReturn((short)1).thenReturn((short)0).thenReturn((short)0);
        when(mockCursor.getString(5)).thenReturn("parent content").thenReturn("child 1 content").thenReturn("child 2 content");
        when(mockCursor.getInt(0)).thenReturn(2).thenReturn(0).thenReturn(1);
        when(mockCursor.getLong(2)).thenReturn(1l).thenReturn(2l).thenReturn(3l);
        when(mockCursor.isNull(3)).thenReturn(true).thenReturn(false).thenReturn(false);
        when(mockCursor.getLong(3)).thenReturn(1l);

        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        when(mockDB.query("SnippetElement",null,null,null,null,null,null)).thenReturn(mockCursor);

        DBHelper dbHelper = spy(new DBHelper(null));
        doReturn(mockDB).when(dbHelper).getDatabase();

        ArrayList<SnippetTreeElement> snippets = dbHelper.getSnippetElements();
        assertEquals(snippets.size(),3);
        int unvisited_count = 3;
        for(int i=0;i<snippets.size();++i){
            SnippetTreeElement sn = snippets.get(0);
            if(sn.Id == 1){
                SnippetFolder p_sn = (SnippetFolder)sn;
                assertEquals(p_sn.Name,"parent 1");
                assertEquals(p_sn.Parent,null);
                ArrayList<SnippetTreeElement> children = p_sn.getChilden();
                assertEquals(children.size(),2);
                int unvisited_child_count = 2;
                for(int j=0;j<children.size();++j){
                    if(children.get(j).Id == 2 || children.get(j).Id == 3){
                        --unvisited_child_count;
                    }
                }
                assertEquals(unvisited_child_count,0);
                --unvisited_count;
            }
            if(sn.Id == 2 || sn.Id == 3){
                SnippetFolder p_sn = (SnippetFolder)sn;
                assertEquals(p_sn.Name,"parent 1");
                assertEquals(p_sn.Parent,null);
                --unvisited_count;
            }
        }
        assertEquals(unvisited_count, 0);
    }
    @Test
    public void checkMultibufferClearing() {
        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        DBHelper dbHelper = spy(new DBHelper(null));
        doReturn(mockDB).when(dbHelper).getDatabase();
        dbHelper.clearMultibuffer();
        verify(mockDB).delete("MultibufElement",null,null);
    }
}
