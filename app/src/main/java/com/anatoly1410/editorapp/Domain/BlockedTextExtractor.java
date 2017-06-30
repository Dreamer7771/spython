package com.anatoly1410.editorapp.Domain;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Presentation.CEditText;
import com.anatoly1410.editorapp.Presentation.Interfaces.IBlockedTextExtractor;
import com.anatoly1410.editorapp.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 1 on 31.01.2017.
 */

public class BlockedTextExtractor implements IBlockedTextExtractor {
    private String fRegExp = "( |\t)*def +\\w+\\((.|\n)*?\\): *\n";
    private String cRegExp = "( |\t)*class +\\w+((\\((.|\n)*?\\))|( *?)): *\n";
    private String iRegExp = "((( |\t)*(import +((\\w|,|\\s)+) *\n))|(from +((\\w|,|\\s)+) +import +((\\w|,|\\s)+)))+";
    private String fContentPrefixRegExp = "( |\t)*def ";
    private String fContentPostfixRegExp = ": *\n";
    private String cContentPrefixRegExp = "( |\t)*class ";
    private String cContentPostfixRegExp = "(\\((.|\n)*?\\))?: *\n";

    //Must be loaded from file
    public static final int USUAL_BLOCK_TYPE = 0;
    public static final int FUNC_BLOCK_TYPE = 1;
    public static final int CLASS_BLOCK_TYPE = 2;
    public static final int IMPORT_BLOCK_TYPE = 3;
    private Pattern fPattern = Pattern.compile(fRegExp);
    private Pattern cPattern = Pattern.compile(cRegExp);
    private Pattern iPattern = Pattern.compile(iRegExp);
    ArrayList<Pair<Pattern, Integer>> patternsList;


    private Pattern fContentPrefixPattern = Pattern.compile(fContentPrefixRegExp);
    private Pattern fContentPostfixPattern = Pattern.compile(fContentPostfixRegExp);
    private Pattern cContentPrefixPattern = Pattern.compile(cContentPrefixRegExp);
    private Pattern cContentPostfixPattern = Pattern.compile(cContentPostfixRegExp);

    //TODO regexps and patterns should be taken from xml file
    //TODO make class as singleton
    public ArrayList<SyntaxBlock> listOfBlocks;
    private ArrayList<SyntaxBlock> displayOrderedBlocks;

    private int mDisableSpinnerItemSelectionHandlingCounter = 0;

    private int mLastSelectedBlock = -1;

    public int getLastSelectedBlock(){
        return mLastSelectedBlock;
    }

    private boolean mBlockSpinnerAfterUpdate;
    private BlockedTextSpinnerAdapter mBlockedTextSpinnerAdapter;
    public void setBlockedTextSpinnerAdapter(BlockedTextSpinnerAdapter adapter){
        mBlockedTextSpinnerAdapter = adapter;
    }
    private boolean mSpinnerWasTouched = false;
    public void setSpinnerIsTouchedFlag(boolean value){
        mSpinnerWasTouched = value;
    }
    private final Object mBlockSpinnerUpdateLock = new Object();

    private boolean mIsEnabled;
    public boolean isEnabled(){
        return mIsEnabled;
    }
    public void setEnabledState(boolean isEnabled){
        mIsEnabled = isEnabled;
    }

    //Delay for block extraction
    private int blockExtractionDelay = 1000;
    //ScheduledExecutorService for block extraction
    private ScheduledExecutorService sheduledBlockExtractionExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> sheduledBlockExtraction;
    public Handler mBlockExtractionHandler = new Handler();
    public void incDisableSpinnerItemSelCounter()
    {
        ++mDisableSpinnerItemSelectionHandlingCounter;
    }

    private boolean isTestingMode = false;

    private ISettingsManager mSettingsManager;


    public void setTestingMode(boolean isTestingMode){
        this.isTestingMode = isTestingMode;
    }

    public boolean isSpinnerItemSelectionHandlingDisabled()
    {
        return mDisableSpinnerItemSelectionHandlingCounter > 0;
    }


    private int maxDisplayedHeaderLength = 30;

    public int getMaxDisplayedHeaderLength(){
        return maxDisplayedHeaderLength;
    }

    OnChangeSelectionPosFromSpinnerListener mChangeSelectionPosFromSpinnerListener;
    OnSpinnerSelectedItemChangeListener mSpinnerSelectedItemChangeListener;

    public void setChangeSelectionPosFromSpinnerListener(OnChangeSelectionPosFromSpinnerListener listener) {
        mChangeSelectionPosFromSpinnerListener = listener;
    }
    public void setSpinnerSelectedItemChangeListener(OnSpinnerSelectedItemChangeListener listener) {
        mSpinnerSelectedItemChangeListener = listener;
    }

    private boolean blocksLexSorted;
    public boolean areBlocksLexSorted(){
        return blocksLexSorted;
    }

    public ArrayList<SyntaxBlock>  getDisplayedBlocks(){
        return displayOrderedBlocks;
    }

    public BlockedTextExtractor(ISettingsManager settingsManager){
        mSettingsManager = settingsManager;
        displayOrderedBlocks = new ArrayList<>();
        initPatternsList();
        setTestingMode(false);
    }

    public void initPatternsList(){
        patternsList = new ArrayList<>();
        patternsList.add(new Pair(fPattern, FUNC_BLOCK_TYPE));
        patternsList.add(new Pair(cPattern, CLASS_BLOCK_TYPE));
        patternsList.add(new Pair(iPattern, IMPORT_BLOCK_TYPE));
    }

    public void extractBlockedTextFromString(String str){
        if(!mIsEnabled){
            return;
        }
        if(sheduledBlockExtractionExecutor == null){
            return;
        }
        if(sheduledBlockExtraction != null && !sheduledBlockExtraction.isDone()){
            sheduledBlockExtraction.cancel(false);
        }
        final String text = str;
        final Runnable extractionRunnable = new Runnable() {
            public void run() { extractBlockedTextFromStringAsync(text); }
        };
        sheduledBlockExtraction = sheduledBlockExtractionExecutor.schedule(extractionRunnable,blockExtractionDelay,TimeUnit.MILLISECONDS);
    }

    public void setPatternsList( ArrayList<Pair<Pattern, Integer>> patternsList){
        this.patternsList = patternsList;
    }
    public void setListOfBlocks( ArrayList<SyntaxBlock> listOfBlocks){
        this.listOfBlocks = listOfBlocks;
    }

    public boolean areFunctionsShowed(){
        if(mSettingsManager == null){
            return true;
        }
        return mSettingsManager.getBooleanSetting(ISettingsManager.APP_PREFERENCES_SHOW_FUNCTIONS);
    }
    public boolean areClassesShowed(){
        if(mSettingsManager == null){
            return true;
        }
        return mSettingsManager.getBooleanSetting(ISettingsManager.APP_PREFERENCES_SHOW_CLASSES);
    }

    public boolean sortBlocksLexicographically(){
        if(mSettingsManager == null){
            return true;
        }
        return mSettingsManager.getIntSetting(ISettingsManager.APP_PREFERENCES_SORT_ORDER) == 0;
    }

    public void extractBlockedTextFromStringAsync(String str) {
        //Patterns and block types
        ArrayList<Pair<Pattern, Integer>> patterns = patternsList;

        TreeSet<SyntaxBlock> blocks = new TreeSet<>();
        TreeSet<SyntaxBlock> curBlocks = new TreeSet<>();

        int startPos;
        for (Pair<Pattern, Integer> curPatternPair : patterns) {
            startPos = 0;
            Pattern curPattern = curPatternPair.first;
            while (startPos < str.length()) {
                String curStr = str.substring(startPos);
                Matcher matcher = curPattern.matcher(curStr);

                if (matcher.find()) {

                    int start = matcher.start();
                    int end = matcher.end();
                    curBlocks.add(new SyntaxBlock(getBlockHeader(curStr.substring(start, end),
                            curPatternPair.second), start + startPos, curPatternPair.second));
                    startPos = end + startPos;
                } else {
                    break;
                }
            }
            blocks.addAll(curBlocks);
        }
        if (listOfBlocks == null) {
            listOfBlocks = new ArrayList<>();
        } else {
            listOfBlocks.clear();
        }
        Iterator iterator = blocks.iterator();
        while (iterator.hasNext()) {
            SyntaxBlock curBlock = (SyntaxBlock) iterator.next();
            listOfBlocks.add(curBlock);
        }
        if(!isTestingMode){
            mBlockExtractionHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateBlocksList();
                }
            });
        }

    }

    private int getBlockWithSameIndent(String str, int start) {
        int prevIndent = 0;
        int indentCounter = 0;
        boolean leftIndent = true;
        for (int i = start; i < str.length(); ++i) {
            if (str.charAt(i) == ' ' && leftIndent) {
                ++indentCounter;
            } else {
                if (leftIndent) {
                    leftIndent = false;
                    if (indentCounter < prevIndent) {
                        break;
                    }
                }

                if (str.charAt(i) == '\n') {
                    start = i;
                    leftIndent = true;
                    prevIndent = indentCounter;
                }
            }
        }
        return start;
    }

    private int getBlockIndent(String str) {
        int indent = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return indent;
            }
            ++indent;
        }
        return indent;
    }

    public String getBlockHeader(String blockRawHeader, int type) {
        String header = "";
        Matcher prefMatcher, postfMatcher;
        int start, end;
        switch (type) {
            case FUNC_BLOCK_TYPE:
                prefMatcher = fContentPrefixPattern.matcher(blockRawHeader);
                postfMatcher = fContentPostfixPattern.matcher(blockRawHeader);
                start = (prefMatcher.find() ? (prefMatcher.end()) : (0));
                end = (postfMatcher.find() ? (postfMatcher.start()) : (blockRawHeader.length()));
                header = blockRawHeader.substring(start, end);
                break;
            case CLASS_BLOCK_TYPE:
                prefMatcher = cContentPrefixPattern.matcher(blockRawHeader);
                postfMatcher = cContentPostfixPattern.matcher(blockRawHeader);
                start = (prefMatcher.find() ? (prefMatcher.end()) : (0));
                end = (postfMatcher.find() ? (postfMatcher.start()) : (blockRawHeader.length()));
                header = blockRawHeader.substring(start, end);
                break;
        }
        return header;
    }

    public void updateBlocksList() {
        synchronized (mBlockSpinnerUpdateLock) {
            ArrayList<SyntaxBlock> lexSortedBlocks = new ArrayList<>();
            for (int i = 0; i < listOfBlocks.size(); ++i) {
                if (blockIsDisplayed(listOfBlocks.get(i))) {
                    lexSortedBlocks.add(listOfBlocks.get(i));
                }
            }
            if(sortBlocksLexicographically()){
                Collections.sort(lexSortedBlocks, new Comparator<SyntaxBlock>() {
                    @Override
                    public int compare(SyntaxBlock lhs, SyntaxBlock rhs) {
                        return lhs.header.compareTo(rhs.header);
                    }
                });
            }

            displayOrderedBlocks.clear();
            String[] headers = new String[lexSortedBlocks.size()];
            for (int i = 0; i < lexSortedBlocks.size(); ++i) {
                displayOrderedBlocks.add(lexSortedBlocks.get(i));
                headers[i] = reduceBlockHeader(lexSortedBlocks.get(i).header);
            }

            mBlockSpinnerAfterUpdate = true;
            if( getLastSelectedBlock() < mBlockedTextSpinnerAdapter.getCount()){
                if(mSpinnerSelectedItemChangeListener != null){
                    mSpinnerSelectedItemChangeListener.fireEvent(getLastSelectedBlock());
                }
            }
            mBlockedTextSpinnerAdapter.notifyDataSetChanged();
        }
    }

    public String reduceBlockHeader(String blockHeader) {
        if (blockHeader.length() > getMaxDisplayedHeaderLength()) {
            blockHeader = blockHeader.substring(0, getMaxDisplayedHeaderLength()) + "...";
        }
        return blockHeader;
    }

    public synchronized  void onBlockSpinnerItemSelection(int itemIdx) {
        synchronized (mBlockSpinnerUpdateLock) {
            if(mBlockedTextSpinnerAdapter == null){
                return;
            }

            mLastSelectedBlock = itemIdx;

            if (!isSpinnerItemSelectionHandlingDisabled() && mSpinnerWasTouched) {
                SyntaxBlock selectedBlock = displayOrderedBlocks.get(itemIdx);
                if(mChangeSelectionPosFromSpinnerListener != null){
                    mChangeSelectionPosFromSpinnerListener.fireEvent(selectedBlock.getStartPos());
                }
                mSpinnerWasTouched = false;
            }
            if (mDisableSpinnerItemSelectionHandlingCounter > 0)
                --mDisableSpinnerItemSelectionHandlingCounter;

            mBlockSpinnerAfterUpdate = false;

        }
    }

    private void selectBlockSpinnerItem(SyntaxBlock block)
    {
        if (displayOrderedBlocks == null)
            return;


        int idx = displayOrderedBlocks.indexOf(block);

        if(idx < 0 || idx >= displayOrderedBlocks.size())
            return;
        int selectedBlock = -1;
        if(mBlockSpinnerAfterUpdate){
            selectedBlock = mLastSelectedBlock;
        }

        if(selectedBlock >= 0 && selectedBlock != idx)
        {
            ++mDisableSpinnerItemSelectionHandlingCounter;
        }
        mSpinnerSelectedItemChangeListener.fireEvent(idx);
    }

    public void onCursorPosChanged(int newCursorPos) {
        if(listOfBlocks == null){
            return;
        }

        int i = 0;
        int lastIdx = -1;
        while(i < listOfBlocks.size()) {
            SyntaxBlock curBlock = listOfBlocks.get(i);
            if (blockIsDisplayed(curBlock))
            {
                if(curBlock.getStartPos() <= newCursorPos)
                {
                    lastIdx = i;
                }else
                {
                    break;
                }
            }
            ++i;
        }

        if(lastIdx >= 0 )
        {
            selectBlockSpinnerItem(listOfBlocks.get(lastIdx));
        }
    }
    private boolean blockIsDisplayed(SyntaxBlock block)
    {
        if(block.type == FUNC_BLOCK_TYPE){
            if(areFunctionsShowed()){
                return true;
            }
        }else if(block.type == CLASS_BLOCK_TYPE){
            if(areClassesShowed()){
                return true;
            }
        }
        return false;
    }
}
