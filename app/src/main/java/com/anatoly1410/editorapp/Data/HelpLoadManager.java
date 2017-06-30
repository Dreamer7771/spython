package com.anatoly1410.editorapp.Data;

import android.util.Xml;

import com.anatoly1410.editorapp.Domain.Interfaces.IFileManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IHelpLoadManager;
import com.anatoly1410.editorapp.Domain.Snippet;
import com.anatoly1410.editorapp.Domain.SnippetManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by 1 on 02.05.2017.
 */

public class HelpLoadManager implements IHelpLoadManager {

    IFileManager mFileManager;
    private final String helpIndexPath =  "/sdcard/com.anatoly1410.editorapp/help/help_index.xml";
    private final String helpDirectory =  "/sdcard/com.anatoly1410.editorapp/help";


    private final String helpContentTag = "helpContent";
    private final String helpElementTag = "helpElement";
    private final String aliasListTag = "aliasList";
    private final String aliasTag = "alias";
    private final String filePathTag = "filePath";
    private final String childrenTag = "children";
    private final String nameAttribute = "name";


    private boolean mHelpLoaded = false;

    public boolean HelpIsLoaded(){
        return mHelpLoaded;
    }

    public HelpLoadManager(IFileManager fileManager){
        mFileManager = fileManager;
    }


    public InputStream getInputStream(String helpPath){
        try{
            File xml_file = new File(helpPath);
            return new FileInputStream(xml_file);
        }catch(FileNotFoundException e){
            return null;
        }

    }

    public ArrayList<HelpIndexElement> LoadHelpIndex(){
        ArrayList<HelpIndexElement> list = new ArrayList<>();

        ArrayList<String> tagStack = new ArrayList<>();
        ArrayList<HelpIndexElement> helpElementStack = new ArrayList<>();
        HelpIndexElement curHelpElement = new HelpIndexElement();

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            InputStream in = getInputStream(helpIndexPath);
            parser.setInput(in, null);

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (parser.getEventType()) {
                    // document's beginning
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // tag's beginning
                    case XmlPullParser.START_TAG:
                        if (tagName.equals(helpContentTag)) {
                            tagStack.add(parser.getName());
                        }else if (tagName.equals(helpElementTag)) {
                            if(tagStack.isEmpty() || !(tagStack.get(tagStack.size() - 1).equals(helpContentTag)
                                        || tagStack.get(tagStack.size() - 1).equals(childrenTag))){
                                throw new ParseException("Error while parsing help index",parser.getLineNumber());
                            }

                            curHelpElement = new HelpIndexElement();
                            if(!helpElementStack.isEmpty()){
                                helpElementStack.get(helpElementStack.size() - 1).children.add(curHelpElement);
                            }
                            helpElementStack.add(curHelpElement);

                            tagStack.add(parser.getName());
                            curHelpElement.name = parser.getAttributeValue(null,nameAttribute);

                        }else if(tagName.equals(aliasListTag)){
                            if(tagStack.isEmpty() || !tagStack.get(tagStack.size() - 1).equals(helpElementTag)){
                                throw new ParseException("Error while parsing help index",parser.getLineNumber());
                            }

                            tagStack.add(parser.getName());
                            curHelpElement.aliases = new ArrayList<>();
                        }else if(tagName.equals(aliasTag)){
                            if(tagStack.isEmpty() || !tagStack.get(tagStack.size() - 1).equals(aliasListTag)){
                                throw new ParseException("Error while parsing help index",parser.getLineNumber());
                            }

                            tagStack.add(parser.getName());

                        }else if(tagName.equals(filePathTag)){
                            if(tagStack.isEmpty() || !tagStack.get(tagStack.size() - 1).equals(helpElementTag)){
                                throw new ParseException("Error while parsing help index",parser.getLineNumber());
                            }

                            tagStack.add(parser.getName());

                        }else if(tagName.equals(childrenTag)){
                            if(tagStack.isEmpty() || !tagStack.get(tagStack.size() - 1).equals(helpElementTag)){
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

                        if(tagName.equals(helpElementTag)){
                            list.add(curHelpElement);

                            helpElementStack.remove(helpElementStack.size() - 1);
                            if(helpElementStack.size() > 0){
                                curHelpElement = helpElementStack.get(helpElementStack.size() - 1);
                            }
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
                            curHelpElement.aliases.add(parser.getText());
                        }else if(tagName.equals(filePathTag)){
                            curHelpElement.filePath = parser.getText();
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

        for(HelpIndexElement element:list){
            for(HelpIndexElement child:element.children){
                child.parent = element;
            }
        }

        mHelpLoaded = true;
        return list;
    }


    public String LoadHelpFileContent(String path){

        String file_path = helpDirectory + "/" + path;
        return mFileManager.loadContent(file_path);
    }
}
