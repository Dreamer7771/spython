package com.anatoly1410.editorapp.Data;

import android.app.Activity;
import android.content.Context;

import com.anatoly1410.editorapp.Domain.Interfaces.IXmlLangSyntaxParser;
import com.anatoly1410.editorapp.Domain.LanguageDescription;
import com.anatoly1410.editorapp.Domain.Pair;
import com.anatoly1410.editorapp.Domain.Snippet;
import com.anatoly1410.editorapp.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by 1 on 27.01.2017.
 */

public class XmlLangSyntaxParser implements IXmlLangSyntaxParser {
    @Inject
    Context mContext;

    private final int python_lang_res_id = R.xml.python_lang;
    private LanguageDescription mLanguageDescription;
    public XmlLangSyntaxParser(Context context){
        mContext = context;
        mLanguageDescription = parseXml();
    }
    public XmlLangSyntaxParser(Context context,boolean loadDuringInit){
        mContext = context;
        if(loadDuringInit){
            mLanguageDescription = parseXml();
        }
    }
    public XmlPullParser getLanguageDescriptionXmlParser(){
        return mContext.getResources().getXml(python_lang_res_id);
    }

    public LanguageDescription parseXml()
    {
        ArrayList<String> keyWords = new ArrayList();
        ArrayList<Snippet> embeddedSnippets = new ArrayList();
        ArrayList<Pair<String,String>> syntaxBlocks = new ArrayList();

        ArrayList<String> tags = new ArrayList<>();

        String lastTag = "";
        Snippet curSnippet = new Snippet();
        String syntaxBlockName = "";
        int keyWordNum = 0;
        try {
            XmlPullParser xpp = getLanguageDescriptionXmlParser();
            String tmp = "";
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tags.add(tagName);
                        lastTag = tagName;
                        if (tagName.equals("embeddedSnippet")) {
                            curSnippet = new Snippet();

                            curSnippet.cursorOffset = Integer.parseInt(xpp.getAttributeValue(null,"indent"));
                        }else if(tagName.equals("syntaxBlock")){
                            syntaxBlockName = xpp.getAttributeValue(null,"name");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(tags.size() == 0){
                            throw new ParseException("Error while parsing python language description",xpp.getLineNumber());
                        }

                        if(tags.size() > 0 && !tags.get(tags.size() - 1).equals(tagName)){
                            throw new ParseException("Error while parsing python language description",xpp.getLineNumber());
                        }

                        if (tagName.equals("embeddedSnippet")) {
                            embeddedSnippets.add(curSnippet);
                        }

                        tags.remove(tags.size() - 1);
                        break;
                    case XmlPullParser.TEXT:
                        if(!xpp.isWhitespace()) {
                            if (lastTag.equals("alias")) {
                                curSnippet.tag = xpp.getText();
                            } else if (lastTag.equals("text")) {
                                curSnippet.content = xpp.getText();
                            } else if (lastTag.equals("keyWord")) {
                                keyWords.add(xpp.getText());
                            } else if (lastTag.equals("syntaxBlock")) {
                                syntaxBlocks.add(new Pair(syntaxBlockName, xpp.getText()));
                            }
                        }
                        break;

                    default:
                        break;
                }
                 xpp.next();


            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }catch(ParseException e){
            return null;
        }
        String[] keyWordsArray = new String[keyWords.size()];
        Snippet[] snippetsArray = new Snippet[embeddedSnippets.size()];

        keyWords.toArray(keyWordsArray);
        embeddedSnippets.toArray(snippetsArray);

        return new LanguageDescription(keyWordsArray, snippetsArray, syntaxBlocks);
    }

    public String[] getKeyWords(){
        if(mLanguageDescription == null){
            return null;
        }

        return mLanguageDescription.keyWords;
    }

    public Snippet[] getEmbeddedSnippets(){
        if(mLanguageDescription == null){
            return null;
        }

        return mLanguageDescription.embeddedSnippets;
    }

    public ArrayList<Pair<String, String>> getSyntaxBlocks(){
        if(mLanguageDescription == null){
            return null;
        }

        return mLanguageDescription.syntaxBlocks;
    }

}
