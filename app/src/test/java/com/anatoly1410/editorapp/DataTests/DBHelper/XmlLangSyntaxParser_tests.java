package com.anatoly1410.editorapp.DataTests.DBHelper;

import android.content.Context;
import android.util.Xml;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.XmlLangSyntaxParser;
import com.anatoly1410.editorapp.Domain.LanguageDescription;

import org.apache.xerces.xni.parser.XMLPullParserConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 1 on 08.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class XmlLangSyntaxParser_tests {
    private XmlPullParser getXmlParserForFile(String fileName){
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("xml/"+fileName);
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            parser.setInput(inputStream, null);

            return parser;
        } catch(XmlPullParserException e){
            return null;
        }
    }
    @Test
    public void checkParseXml() {
        Context mockContext = mock(Context.class);
        XmlLangSyntaxParser spyLangSyntaxParser = spy(new XmlLangSyntaxParser(mockContext, false));
        XmlPullParser langParser = getXmlParserForFile("python_lang.xml");
        doReturn(langParser).when(spyLangSyntaxParser).getLanguageDescriptionXmlParser();
        LanguageDescription langDescr = spyLangSyntaxParser.parseXml();

        assertEquals(langDescr.keyWords.length,2);
        assertEquals(langDescr.keyWords[0],"def");
        assertEquals(langDescr.keyWords[1],"if");
        assertEquals(langDescr.syntaxBlocks.size(),1);
        assertEquals(langDescr.syntaxBlocks.get(0).first,"string");

        assertEquals(langDescr.syntaxBlocks.get(0).second,"(\"[^\"\\n]*?\")|('[^'\\n]*?')");
        assertEquals(langDescr.embeddedSnippets.length,2);
        assertEquals(langDescr.embeddedSnippets[0].type,-1);
        assertEquals(langDescr.embeddedSnippets[0].cursorOffset,4);
        assertEquals(langDescr.embeddedSnippets[0].content,"def ():\n\t\t\t");
        assertEquals(langDescr.embeddedSnippets[0].tag,"def");

        assertEquals(langDescr.embeddedSnippets[1].type,-1);
        assertEquals(langDescr.embeddedSnippets[1].cursorOffset,3);
        assertEquals(langDescr.embeddedSnippets[1].content,"if :\n\t\t\t");
        assertEquals(langDescr.embeddedSnippets[1].tag,"if");
    }
    @Test
    public void checkParseXml_withError() {
        Context mockContext = mock(Context.class);
        XmlLangSyntaxParser spyLangSyntaxParser = spy(new XmlLangSyntaxParser(mockContext, false));
        XmlPullParser langParser = getXmlParserForFile("python_lang2.xml");
        doReturn(langParser).when(spyLangSyntaxParser).getLanguageDescriptionXmlParser();
        LanguageDescription langDescr = spyLangSyntaxParser.parseXml();

        assertEquals(langDescr, null);

    }
}
