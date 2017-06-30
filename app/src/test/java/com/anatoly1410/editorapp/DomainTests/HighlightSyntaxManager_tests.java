package com.anatoly1410.editorapp.DomainTests;

import android.text.Editable;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.XmlLangSyntaxParser;
import com.anatoly1410.editorapp.Domain.HighlightSyntaxManager;
import com.anatoly1410.editorapp.Domain.Pair;
import com.anatoly1410.editorapp.Domain.TextSyntaxRange;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by 1 on 12.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class HighlightSyntaxManager_tests {
    @Test
    public void checkIsKeyWord(){
        XmlLangSyntaxParser mockXmlParser = mock(XmlLangSyntaxParser.class);
        String[] keyWords = new String[]{"word1","word2"};
        when(mockXmlParser.getKeyWords()).thenReturn(keyWords);
        HighlightSyntaxManager spyHighlightSyntaxMgr = spy(new HighlightSyntaxManager(mockXmlParser, false));
        assert(spyHighlightSyntaxMgr.isKeyWord("word1"));
    }
    @Test
    public void checkGetSyntaxRanges(){
        ArrayList<Pair<String, String>> syntaxBlocks = new ArrayList<>();
        syntaxBlocks.add(new Pair<String, String>("string","(\"[^\"\\n]*?\")|('[^'\\n]*?')"));
        syntaxBlocks.add(new Pair<String, String>("singleLineComments","#[^\\n]*?\\n"));
        syntaxBlocks.add(new Pair<String, String>
                ("multiLineComments","(\"\"\"(.|\\n)*?\"\"\")|('''(.|\\n)*?''')"));
        syntaxBlocks.add(new Pair<String, String>("decorator","@[^\\n]*?\\n"));
        syntaxBlocks.add(new Pair<String, String>
                ("constant",
                        "self|None|True|False|NotImplemented|Ellipsis|__debug__|__debug__|__name__"));

        XmlLangSyntaxParser mockXmlParser = mock(XmlLangSyntaxParser.class);
        HighlightSyntaxManager spyHighlightSyntaxMgr = spy(new HighlightSyntaxManager(mockXmlParser, false));
        spyHighlightSyntaxMgr.setPatterns(syntaxBlocks);
        Editable mockEditable = mock(Editable.class);
        String text = "\"string\" asd asd asad";
        when(mockEditable.subSequence(anyInt(),anyInt())).thenReturn(text);
        ArrayList<TextSyntaxRange> ranges = spyHighlightSyntaxMgr.getSyntaxRanges(mockEditable,0,10);
        assertEquals(ranges.size(),1);
        assertEquals(ranges.get(0).start,0);
        assertEquals(ranges.get(0).end,8);
        assertEquals(ranges.get(0).type,TextSyntaxRange.STRING_TYPE);
    }
    @Test
    public void checkGetNextWord(){
        XmlLangSyntaxParser mockXmlLangSyntaxParser = mock(XmlLangSyntaxParser.class);
        HighlightSyntaxManager spyHighlightSyntaxMgr
                = spy(new HighlightSyntaxManager(mockXmlLangSyntaxParser,false));
        Pair<Integer, String> res = spyHighlightSyntaxMgr.getNextWord(6,"word1 word2 word3");
        assert(res.first == 11);
        assertEquals(res.second,"word2");
    }
}
