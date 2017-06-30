package com.anatoly1410.editorapp.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anatoly1410.editorapp.Domain.BufferFragment;
import com.anatoly1410.editorapp.Domain.Interfaces.IDBHelper;
import com.anatoly1410.editorapp.Domain.Pair;
import com.anatoly1410.editorapp.Domain.Snippet;
import com.anatoly1410.editorapp.Domain.SnippetFolder;
import com.anatoly1410.editorapp.Domain.SnippetInsertedElement;
import com.anatoly1410.editorapp.Domain.SnippetTreeElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by 1 on 01.05.2017.
 */

public class DBHelper extends SQLiteOpenHelper implements IDBHelper{

    @Inject
    public DBHelper(Context context) {
        super(context, "spythonDB", null, 1);
    }

    public SQLiteDatabase getDatabase(){

        return getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table SnippetElement ("
                    + "_id integer primary key autoincrement,"
                    + "name text,"
                    + "alias text,"
                    + "parentSnippet integer,"
                    + "textvalue text,"
                    + "isfolder numeric,"
                    + "foreign key(parentSnippet) references SnippetElement(_id));");


        db.execSQL("create table MultibufElement ("
                + "_id integer primary key autoincrement,"
                + "ordernum integer,"
                + "textvalue text);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addMultibufElement(int ordernum, String textvalue){
        SQLiteDatabase db = getDatabase();

        ContentValues cv = new ContentValues();
        cv.put("ordernum",ordernum);
        cv.put("textvalue",textvalue);

        long id = db.insert("MultibufElement",null,cv);
        db.close();
        return id;
    }

    public long addSnippetElement(String name, String alias, SnippetTreeElement parentSnippet,
                                  String textvalue, boolean isfolder){
        SQLiteDatabase db = getDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name",name);
        cv.put("alias",alias);
        if(parentSnippet != null){
            cv.put("parentSnippet",parentSnippet.Id);
        }
        cv.put("textvalue",textvalue);
        cv.put("isfolder",isfolder?(1):(0));

        long id = db.insert("SnippetElement",null,cv);
        db.close();
        return id;
    }


    public void updateMultibufElement(int idx, BufferFragment fragment, String newContent){
        SQLiteDatabase db = getDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("ordernum", idx);
        newValues.put("textvalue", newContent);

        db.update("MultibufElement",newValues,"_id = ?",new String[]{String.valueOf(fragment.id)});
        db.close();

    }


    public void updateSnippetElement(SnippetTreeElement snippet){
        SQLiteDatabase db = getDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("name", snippet.Name);
        if(snippet.Parent != null){
            newValues.put("parentSnippet", snippet.Parent.Id);
        }
        if(snippet.getClass() == SnippetInsertedElement.class){
            SnippetInsertedElement insertedSnippet = (SnippetInsertedElement) snippet;
            newValues.put("alias", insertedSnippet.getSnippet().tag);
            newValues.put("textvalue", insertedSnippet.getSnippet().content);
            newValues.put("isfolder", 0);
        }

        db.update("SnippetElement",newValues,"_id = ?",new String[]{String.valueOf(snippet.Id)});
        db.close();

    }


    public void removeMultibufElement(BufferFragment bufferFragment){
        SQLiteDatabase db = getDatabase();
        String idString = Long.toString(bufferFragment.id);
        int id = db.delete("MultibufElement","_id = ?",new String[]{idString});

        db.close();
    }

    public void removeSnippetElement(SnippetTreeElement snippet){
        SQLiteDatabase db = getDatabase();
        String idString = Long.toString(snippet.Id);
        int id = db.delete("SnippetElement","_id = ?",new String[]{idString});

        db.close();
    }

    public void clearMultibuffer(){
        SQLiteDatabase db = getDatabase();

        db.delete("MultibufElement",null,null);

        db.close();
    }

    public ArrayList<BufferFragment> getBufferFragments(){
        SQLiteDatabase db = getDatabase();
        Cursor c = db.query("MultibufElement",null,null,null,null,null,null);
        ArrayList<Pair<BufferFragment, Integer>> res = new ArrayList<>();
        ArrayList<BufferFragment> res_sorted = new ArrayList<>();
        if (c.moveToFirst()) {
            int ordernum = c.getColumnIndex("ordernum");
            int textvalue = c.getColumnIndex("textvalue");
            int id = c.getColumnIndex("_id");

            do {
                BufferFragment fragment = new BufferFragment(c.getString(textvalue),c.getLong(id));
                res.add(new Pair(fragment, c.getInt(ordernum)));
            } while (c.moveToNext());
        }

        Collections.sort(res, new Comparator<Pair<BufferFragment, Integer>>() {
            @Override
            public int compare(Pair<BufferFragment, Integer> lhs, Pair<BufferFragment, Integer> rhs) {
                return lhs.second - rhs.second;
            }
        });

        for(int i=0;i<res.size();++i){
            res_sorted.add(res.get(i).first);
        }

        db.close();
        return res_sorted;
    }

    public ArrayList<SnippetTreeElement> getSnippetElements(){
        SQLiteDatabase db = getDatabase();
        Cursor c = db.query("SnippetElement",null,null,null,null,null,null);
        HashMap<Long, SnippetTreeElement> snippetsMap = new HashMap<>();
        ArrayList<Pair<Long, SnippetTreeElement>> snippetParents = new ArrayList<>();

        if (c.moveToFirst()) {
            int name = c.getColumnIndex("name");
            int alias = c.getColumnIndex("alias");
            int parentSnippet = c.getColumnIndex("parentSnippet");
            int isfolder = c.getColumnIndex("isfolder");
            int textvalue = c.getColumnIndex("textvalue");
            int id = c.getColumnIndex("_id");

            do {
                long parent_id;
                if(c.isNull(parentSnippet)){
                    parent_id = -1;
                }else{
                    parent_id = c.getLong(parentSnippet);
                }
                if(c.getShort(isfolder) == 0) {
                    SnippetInsertedElement snippetInsertedElement = new SnippetInsertedElement();
                    snippetInsertedElement.Id = c.getLong(id);
                    snippetInsertedElement.Name = c.getString(name);
                    snippetInsertedElement.getSnippet().content = c.getString(textvalue);
                    snippetInsertedElement.getSnippet().tag = c.getString(alias);

                    snippetsMap.put(snippetInsertedElement.Id , snippetInsertedElement);
                    snippetParents.add(new Pair(parent_id, snippetInsertedElement));

                }else{
                    SnippetFolder snippetFolderElement = new SnippetFolder();
                    snippetFolderElement.Id = c.getLong(id);
                    snippetFolderElement.Name = c.getString(name);

                    snippetsMap.put(snippetFolderElement.Id , snippetFolderElement);
                    snippetParents.add(new Pair(parent_id, snippetFolderElement));
                }


            } while (c.moveToNext());
        }

        for(Pair<Long, SnippetTreeElement> curElement:snippetParents){
            if(curElement.first >= 0) {
                SnippetTreeElement parentSnippet = snippetsMap.get(curElement.first);
                curElement.second.Parent = parentSnippet;
                if (parentSnippet.getClass() == SnippetFolder.class) {
                    SnippetFolder folder = (SnippetFolder) parentSnippet;
                    folder.addChildren(curElement.second);
                }
            }
        }
        ArrayList<SnippetTreeElement> resList = new ArrayList<>();
        for(SnippetTreeElement snippet:snippetsMap.values()){
            resList.add(snippet);
        }

        db.close();
        return resList;
    }
}
