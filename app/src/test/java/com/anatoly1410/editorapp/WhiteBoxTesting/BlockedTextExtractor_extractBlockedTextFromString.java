package com.anatoly1410.editorapp.WhiteBoxTesting;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.Domain.BlockedTextExtractor;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Domain.Pair;
import com.anatoly1410.editorapp.Domain.SyntaxBlock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
/**
 * Created by 1 on 05.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BlockedTextExtractor_extractBlockedTextFromString {
    @Test
    public void test1(){
        ISettingsManager mockSettingsManager = mock(SettingsManager.class);
        BlockedTextExtractor blockedTextExtractor = new BlockedTextExtractor(mockSettingsManager);
        ArrayList<Pair<Pattern, Integer>> patternsList = new ArrayList<>();
        String fRegExp = "( |\t)*def +\\w+\\((.|\n)*?\\): *\n";
        Pattern fPattern = Pattern.compile(fRegExp);
        patternsList.add(new Pair(fPattern, 1));
        blockedTextExtractor.setPatternsList(patternsList);
        blockedTextExtractor.setListOfBlocks(new ArrayList<SyntaxBlock>());
        blockedTextExtractor.setTestingMode(true);
        blockedTextExtractor.extractBlockedTextFromStringAsync("");

        ArrayList<SyntaxBlock> listOfBlocks = blockedTextExtractor.listOfBlocks;
        assertEquals(listOfBlocks,new ArrayList<SyntaxBlock>());
    }
    @Test
    public void test2(){
        ISettingsManager mockSettingsManager = mock(SettingsManager.class);
        BlockedTextExtractor blockedTextExtractor = new BlockedTextExtractor(mockSettingsManager);
        ArrayList<Pair<Pattern, Integer>> patternsList = new ArrayList<>();
        String fRegExp = "( |\t)*def +\\w+\\((.|\n)*?\\): *\n";
        Pattern fPattern = Pattern.compile(fRegExp);
        patternsList.add(new Pair(fPattern, 1));
        blockedTextExtractor.setPatternsList(patternsList);
        blockedTextExtractor.setListOfBlocks(null);
        blockedTextExtractor.setTestingMode(true);
        blockedTextExtractor.extractBlockedTextFromStringAsync("");

        ArrayList<SyntaxBlock> listOfBlocks = blockedTextExtractor.listOfBlocks;
        assertEquals(listOfBlocks,new ArrayList<SyntaxBlock>());
    }
    @Test
    public void test3(){
        ISettingsManager mockSettingsManager = mock(SettingsManager.class);
        BlockedTextExtractor blockedTextExtractor = new BlockedTextExtractor(mockSettingsManager);
        ArrayList<Pair<Pattern, Integer>> patternsList = new ArrayList<>();
        String fRegExp = "( |\t)*def +\\w+\\((.|\n)*?\\): *\n";
        Pattern fPattern = Pattern.compile(fRegExp);
        patternsList.add(new Pair(fPattern, 1));
        blockedTextExtractor.setPatternsList(patternsList);
        blockedTextExtractor.setListOfBlocks(new ArrayList<SyntaxBlock>());
        blockedTextExtractor.setTestingMode(true);
        ArrayList<SyntaxBlock> resBlocksList = new ArrayList<SyntaxBlock>();
        resBlocksList.add(new SyntaxBlock("main()",0,1));

        blockedTextExtractor.extractBlockedTextFromStringAsync("def main():\n\t\t pass");

        ArrayList<SyntaxBlock> listOfBlocks = blockedTextExtractor.listOfBlocks;

        assertEquals(listOfBlocks.size(),resBlocksList.size());
        SyntaxBlock block = listOfBlocks.get(0);
        SyntaxBlock block_need = listOfBlocks.get(0);
        assertEquals(block.header, block_need.header);
        assertEquals(block.startPos, block_need.startPos);
        assertEquals(block.type, block_need.type);
    }
    @Test
    public void test4(){
        ISettingsManager mockSettingsManager = mock(SettingsManager.class);
        BlockedTextExtractor blockedTextExtractor = new BlockedTextExtractor(mockSettingsManager);
        ArrayList<Pair<Pattern, Integer>> patternsList = new ArrayList<>();

        blockedTextExtractor.setPatternsList(patternsList);
        blockedTextExtractor.setListOfBlocks(new ArrayList<SyntaxBlock>());
        blockedTextExtractor.setTestingMode(true);
        ArrayList<SyntaxBlock> resBlocksList = new ArrayList<SyntaxBlock>();
        resBlocksList.add(new SyntaxBlock("main()",0,1));

        blockedTextExtractor.extractBlockedTextFromStringAsync("def main():\n\t\t pass");

        ArrayList<SyntaxBlock> listOfBlocks = blockedTextExtractor.listOfBlocks;

        assertEquals(listOfBlocks,new ArrayList<SyntaxBlock>());
    }
    @Test
    public void test5(){
        ISettingsManager mockSettingsManager = mock(SettingsManager.class);
        BlockedTextExtractor blockedTextExtractor = new BlockedTextExtractor(mockSettingsManager);
        ArrayList<Pair<Pattern, Integer>> patternsList = new ArrayList<>();

        blockedTextExtractor.setPatternsList(patternsList);
        blockedTextExtractor.setListOfBlocks(null);
        blockedTextExtractor.setTestingMode(true);
        ArrayList<SyntaxBlock> resBlocksList = new ArrayList<SyntaxBlock>();
        resBlocksList.add(new SyntaxBlock("main()",0,1));

        blockedTextExtractor.extractBlockedTextFromStringAsync("def main():\n\t\t pass");

        ArrayList<SyntaxBlock> listOfBlocks = blockedTextExtractor.listOfBlocks;

        assertEquals(listOfBlocks,new ArrayList<SyntaxBlock>());
    }
}
