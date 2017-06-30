package com.anatoly1410.editorapp.Data;

import android.util.Xml;

import com.anatoly1410.editorapp.Domain.Interfaces.IExtraSnippetsManager;
import com.anatoly1410.editorapp.Domain.Snippet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by 1 on 20.05.2017.
 */

public class ExtraSnippetsManager implements IExtraSnippetsManager {

    private final String extraSnippetsPath =  "/sdcard/com.anatoly1410.editorapp/help/snippets.xml";
    private final String substElementsTag = "substElements";
    private final String substElementTag = "substElement";
    private final String aliasTag = "alias";
    private final String textTag = "text";
    private final String indentAttribute = "indent";
    private final String typeAttribute = "type";

    private final int UNDEF_SNIPPET_TYPE = -1;

    private  ArrayList<Snippet> mExtraSnippets;
    public ArrayList<Snippet> getExtraSnippets(){
        return mExtraSnippets;
    }

    public ExtraSnippetsManager(){
        mExtraSnippets = loadExtraSnippets();
    }
    public ExtraSnippetsManager(boolean loadSnippetsDuringInit){
        if(loadSnippetsDuringInit){
            mExtraSnippets = loadExtraSnippets();
        }
    }

    public InputStream getInputStream(String extraSnippetsPath){
        try{
            File xml_file = new File(extraSnippetsPath);
            return new FileInputStream(xml_file);
        }catch(FileNotFoundException e){
            return null;
        }

    }

    public ArrayList<Snippet> loadExtraSnippets(){
        File xml_file = new File(extraSnippetsPath);
        ArrayList<Snippet> snippets = new ArrayList<>();

        ArrayList<String> tagStack = new ArrayList<>();
        Snippet curSnippet = new Snippet();

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            InputStream in = getInputStream(extraSnippetsPath);
            parser.setInput(in, null);

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (parser.getEventType()) {
                    // document's beginning
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // tag's beginning
                    case XmlPullParser.START_TAG:
                        if (tagName.equals(substElementsTag)) {
                            tagStack.add(parser.getName());
                        }else if (tagName.equals(substElementTag)) {
                            if(tagStack.isEmpty() || !(tagStack.get(tagStack.size() - 1).equals(substElementsTag))){
                                throw new ParseException("Error while parsing help index",parser.getLineNumber());
                            }

                            curSnippet = new Snippet();
                            tagStack.add(parser.getName());
                            curSnippet.cursorOffset = Integer.parseInt(parser.getAttributeValue(null,indentAttribute));
                            String type = parser.getAttributeValue(null,typeAttribute);
                            if(type != null){
                                curSnippet.type =  Integer.parseInt(type);
                            }else{
                                curSnippet.type = UNDEF_SNIPPET_TYPE;
                            }

                        }else if(tagName.equals(aliasTag)){
                            if(tagStack.isEmpty() || !tagStack.get(tagStack.size() - 1).equals(substElementTag)){
                                throw new ParseException("Error while parsing help index",parser.getLineNumber());
                            }

                            tagStack.add(parser.getName());
                        }else if(tagName.equals(textTag)){
                            if(tagStack.isEmpty() || !tagStack.get(tagStack.size() - 1).equals(substElementTag)){
                                throw new ParseException("Error while parsing help index",parser.getLineNumber());
                            }

                            tagStack.add(parser.getName());

                        }
                        break;
                    // tag's end
                    case XmlPullParser.END_TAG:
                        if(tagStack.isEmpty() || !tagStack.get(tagStack.size() - 1).equals(tagName)){
                            throw new ParseException("Error while parsing help index",parser.getLineNumber());
                        }

                        if(tagName.equals(substElementTag)){
                            snippets.add(curSnippet);
                        }

                        tagStack.remove(tagStack.size() - 1);

                        //   Log.d(LOG_TAG, "END_TAG: name = " + xpp.getName());
                        break;
                    // tag's content
                    case XmlPullParser.TEXT:
                        if(tagStack.isEmpty()){
                            throw new ParseException("Error while parsing help index",parser.getLineNumber());
                        }

                        tagName = tagStack.get(tagStack.size() - 1);
                        if(tagName.equals(aliasTag)){
                            curSnippet.tag = parser.getText();
                        }else if(tagName.equals(textTag)){
                            curSnippet.content = parser.getText();
                        }

                        break;

                    default:
                        break;
                }
                // next element
                parser.next();
            }

        }catch(IOException e){
            return null;
        }catch(XmlPullParserException e){
            return null;
        }catch(ParseException e){
            return null;
        }catch(Exception e){
            return null;
        }

        return snippets;
    }

}
