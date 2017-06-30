package com.anatoly1410.editorapp.Domain;

import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;

import com.anatoly1410.editorapp.Domain.Interfaces.IXmlLangSyntaxParser;
import com.anatoly1410.editorapp.Presentation.Interfaces.IHighlightSyntaxManager;
import com.anatoly1410.editorapp.UtilityMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 1 on 10.05.2017.
 */

public class HighlightSyntaxManager implements IHighlightSyntaxManager {

    private IXmlLangSyntaxParser mXmlLangSyntaxParser;

    //Delay for syntax highlighting
    private int syntaxHighlightingDelay = 1000;

    private String stringRegExp;
    private String singleLineCommentsRegExp;
    private String multiLineCommentsRegExp;
    private String decoratorRegExp;
    private String constantRegExp;

    private ScheduledExecutorService sheduledSyntaxHighlightingExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> sheduledSyntaxHighlighting;
    public Handler mSyntaxHighlightHandler = new Handler();
    /*Highlights code syntax*/
    public void setSyntaxHighlightingDelay(int mls_delay){
        syntaxHighlightingDelay = mls_delay;
    }

    private ArrayList<Pair<Pattern, Integer>> mPatterns;


    private boolean mIsEnabled;
    public boolean isEnabled(){
        return mIsEnabled;
    }
    public void setEnabledState(boolean isEnabled){
        mIsEnabled = isEnabled;
    }


    OnSyntaxHighlightListener mSyntaxHighlightListener;


    class RangeRunnable implements Runnable {
        int start;
        int end;
        Editable editableText;
        RangeRunnable(int start,int end, Editable editableText) {
            this.start = start;
            this.end = end;
            this.editableText = editableText;
        }
        public void run() {
            final ArrayList<TextSyntaxRange> ranges = getSyntaxRanges(editableText, start, end);

            mSyntaxHighlightHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mSyntaxHighlightListener != null){
                        mSyntaxHighlightListener.fireEvent(ranges, start,end);
                    }
                }
            });
        }
    }

    public void setSyntaxHighlightListener(OnSyntaxHighlightListener listener) {
        mSyntaxHighlightListener = listener;
    }


    public HighlightSyntaxManager(IXmlLangSyntaxParser xmlLangSyntaxParser){
        setPatterns(xmlLangSyntaxParser.getSyntaxBlocks());
        mXmlLangSyntaxParser = xmlLangSyntaxParser;
    }
    public HighlightSyntaxManager(IXmlLangSyntaxParser xmlLangSyntaxParser,boolean initDuringConstruction){
        if(initDuringConstruction){
            setPatterns(xmlLangSyntaxParser.getSyntaxBlocks());
        }
        mXmlLangSyntaxParser = xmlLangSyntaxParser;
    }

    public void setPatterns(ArrayList<Pair<String, String>> syntaxBlocks){
        for(Pair<String,String> p:syntaxBlocks){
            switch (p.first){
                case "string":
                    stringRegExp = p.second;
                    break;
                case "singleLineComments":
                    singleLineCommentsRegExp = p.second;
                    break;
                case "multiLineComments":
                    multiLineCommentsRegExp = p.second;
                    break;
                case "decorator":
                    decoratorRegExp = p.second;
                    break;
                case "constant":
                    constantRegExp = p.second;
                    break;
            }
        }

        Pattern stringPattern = Pattern.compile(stringRegExp);
        Pattern singleLineCommentsPattern = Pattern.compile(singleLineCommentsRegExp);
        Pattern multiLineCommentsPattern = Pattern.compile(multiLineCommentsRegExp);
        Pattern decoratorCommentsPattern = Pattern.compile(decoratorRegExp);
        Pattern constantCommentsPattern = Pattern.compile(constantRegExp);

        mPatterns = new ArrayList<>();
        mPatterns.add(new Pair(multiLineCommentsPattern, TextSyntaxRange.COMMENTS_TYPE));
        mPatterns.add(new Pair(singleLineCommentsPattern, TextSyntaxRange.COMMENTS_TYPE));
        mPatterns.add(new Pair(stringPattern, TextSyntaxRange.STRING_TYPE));
        mPatterns.add(new Pair(decoratorCommentsPattern, TextSyntaxRange.DECORATOR_TYPE));
        mPatterns.add(new Pair(constantCommentsPattern, TextSyntaxRange.CONSTANT_TYPE));

    }

    public void  highlightSyntax(Editable editableText){
        highlightSyntax(editableText,0,editableText.length());
    }

    public void highlightSyntax(Editable editableText,int start,int end) {
        if(!mIsEnabled){
            return;
        }
        if(sheduledSyntaxHighlightingExecutor == null)
            return;

        if(sheduledSyntaxHighlighting != null && !sheduledSyntaxHighlighting.isDone()){
            sheduledSyntaxHighlighting.cancel(false);
        }

    final Editable editableTxt = editableText;
        Runnable highlightingRunnable = new RangeRunnable(start,end,editableText);
        sheduledSyntaxHighlighting = sheduledSyntaxHighlightingExecutor.schedule(highlightingRunnable,syntaxHighlightingDelay, TimeUnit.MILLISECONDS);
    }

    /*Checks word as keywords*/
    public boolean isKeyWord(String str) {
        if (str == null)
            return false;
        if (mXmlLangSyntaxParser.getKeyWords() == null)
            return false;

        for (String p : mXmlLangSyntaxParser.getKeyWords()) {
            if (p.equals(str.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    public ArrayList<TextSyntaxRange> getSyntaxRanges(Editable editableText,int startP, int endP) {

        ArrayList<TextSyntaxRange> textRanges = new ArrayList<TextSyntaxRange>();

        String text = editableText.subSequence(startP,endP).toString();
        int curPos = 0;

        int startPos = 0;
        if(mPatterns == null)
            return textRanges;
        Pair<Integer, String> res = new Pair(0,"");
        while(res.first >= 0){
            res = getNextWord(curPos, text);

            if (isKeyWord(res.second)) {
                TextSyntaxRange keyRange = new TextSyntaxRange(curPos,res.first,TextSyntaxRange.KEY_WORD_TYPE);
                textRanges.add(keyRange);
            }

            curPos = res.first;
        }

        for(Pair<Pattern,Integer> curPatternPair:mPatterns){
            startPos = 0;
            Collections.sort(textRanges, new Comparator<TextSyntaxRange>() {
                @Override
                public int compare(TextSyntaxRange lhs, TextSyntaxRange rhs) {
                    return lhs.start - rhs.start;
                }
            });

            ArrayList<TextSyntaxRange> newRanges = new ArrayList<>();

            while (startPos < text.length()) {
                String curStr = text.substring(startPos);
                Matcher matcher = curPatternPair.first.matcher(curStr);

                if (matcher.find()) {

                    int start = matcher.start();
                    int end = matcher.end();
                    start +=startPos;
                    end += startPos;
                    TextSyntaxRange keyRange = new TextSyntaxRange(start,end,curPatternPair.second);

                    int keyPos = UtilityMethods.binarySearch(textRanges, keyRange, new Comparator<TextSyntaxRange>() {
                        @Override
                        public int compare(TextSyntaxRange keyElement, TextSyntaxRange rhs) {
                            if(rhs.start > keyElement.start){
                                return -1;
                            }else{
                                return keyElement.start - rhs.start;
                            }
                        }
                    });
                    if(textRanges.size() > 0) {
                        if (textRanges.get(keyPos).start > start && keyPos > 0) {
                            --keyPos;
                        }
                    }

                    if(keyPos >= 0){
                        if(textRanges.size() > 0) {
                            if (textRanges.get(keyPos).start > start
                                    || textRanges.get(keyPos).end <= start) {
                                int remove_start = UtilityMethods.binarySearch(textRanges, keyRange, new Comparator<TextSyntaxRange>() {
                                    @Override
                                    public int compare(TextSyntaxRange keyElement, TextSyntaxRange rhs) {
                                        if (rhs.end > keyElement.start) {
                                            return -1;
                                        } else {
                                            return keyElement.start - rhs.end;
                                        }
                                    }
                                });
                                if(textRanges.get(remove_start).end> start && remove_start > 0){
                                    --remove_start;
                                }


                                int remove_end = UtilityMethods.binarySearch(textRanges, keyRange, new Comparator<TextSyntaxRange>() {
                                    @Override
                                    public int compare(TextSyntaxRange keyElement, TextSyntaxRange rhs) {
                                        if (rhs.start < keyElement.end) {
                                            return 1;
                                        } else {
                                            return keyElement.end - rhs.start;
                                        }
                                    }
                                });

                                if(textRanges.get(remove_end).start < end && remove_end < textRanges.size()){
                                    ++remove_end;
                                }
                                if(remove_start < remove_end){
                                    ArrayList<TextSyntaxRange> newTextRanges = new ArrayList<>();
                                    newTextRanges = new ArrayList<TextSyntaxRange>(textRanges.subList(0, remove_start + 1));

                                    newTextRanges.addAll(textRanges.subList(remove_end, textRanges.size()));

                                    textRanges = newTextRanges;
                                }

                                newRanges.add(keyRange);
                            }
                        }else{
                            newRanges.add(keyRange);
                        }

                    }
                    startPos = end;
                } else {
                    break;
                }
            }
            textRanges.addAll(newRanges);
        }

        return textRanges;

    }
    /*Cancels syntax highlighting*/
    public void stopHighlighting() {
        if(sheduledSyntaxHighlighting != null && !sheduledSyntaxHighlighting.isDone()){
            sheduledSyntaxHighlighting.cancel(false);
        }
    }

    private  boolean isIdentifierChar(char c){
        if(Character.isLetter(c) || c == '_' || Character.isDigit(c)){
            return true;
        }else{
            return false;
        }
    }

    /*Gets next word*/
    public Pair<Integer, String> getNextWord(int pos, String txt) {
        StringBuilder sb = new StringBuilder();
        if (pos >= txt.length()) {
            return new Pair(-1, "");
        }
        int i = pos;
        boolean identString = isIdentifierChar(txt.charAt(pos));
        for (; i < txt.length(); ++i) {
            char c = txt.charAt(i);
            if (!isIdentifierChar(c) && identString
                    || isIdentifierChar(c) && !identString) {
                break;
            }
            sb.append(c);
        }

        return new Pair(i, sb.toString());
    }

}
