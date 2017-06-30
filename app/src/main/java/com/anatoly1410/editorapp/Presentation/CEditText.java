package com.anatoly1410.editorapp.Presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import com.anatoly1410.editorapp.Domain.Pair;
import com.anatoly1410.editorapp.Domain.TextChangeCommand;
import com.anatoly1410.editorapp.Domain.TextSyntaxRange;
import com.anatoly1410.editorapp.Presentation.Interfaces.ICommandHistoryManager;
import com.anatoly1410.editorapp.UtilityMethods;
import com.anatoly1410.editorapp.Data.VisualStylesManager;

import java.util.ArrayList;

/**
 * Created by 1 on 14.12.2016.
 */
/*EditText component inside CodeEditText*/
public class CEditText extends EditText {
    /*Paints and display parameters*/
    private Paint mCEditTextSelectionPaint;
    private int mBkgColor;
    private int mTextSize;
    private String mFontFamily;
    private int mIndentLength = 3;
    /*Used for syntax highlighting and others text processing -
    * Disables TextChange handling*/
    private boolean mDisableTextChangedHandling;

    private boolean mChangedProgrammatically;
    /*Disables commands writing*/
    private boolean mDisableCommandHistoryWriting;

    private char indentCharacter = '\t';
    /*Cursor position changing event*/
    public interface CursorPosChangedListener {
        public void onCursorPosChanged();
    }

    private int sharedSelStart;
    private int sharedSelEnd;

    private String mOldText;
    public void setBeforeChangedText(String text){
        mOldText = text;
    }
    private int v;

    private MainActivity parentActivity;
    private ICommandHistoryManager mCommandHistoryManager;
    public void setParentActivity(MainActivity activity){
        parentActivity = activity;
        mCommandHistoryManager = parentActivity.commandHistoryManager;
    }
    public void setProgrammChangeFlag(boolean isChangedProgrammatically) {
        mChangedProgrammatically = isChangedProgrammatically;
    }

    public boolean getProgrammChangeFlag() {
        return mChangedProgrammatically;
    }

    private CursorPosChangedListener mCursorPosChListener;

    public void setCursorPosChListener(CursorPosChangedListener listener) {
        this.mCursorPosChListener = listener;
    }

    public boolean isTextChangeHandlingEnabled() {
        return !mDisableTextChangedHandling;
    }

    public int getSharedSelectionStart() {
        return sharedSelStart;
    }

    public int getSharedSelectionEnd() {
        return sharedSelEnd;
    }

    public void setTextChangeHandlingEnabled(boolean isEnabled){
        mDisableTextChangedHandling = !isEnabled;
    }
    /*Constructor
    * - sets concrete width of component
    * - set initial style settings
    * - set basic listeners*/
    public CEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        parentActivity = (MainActivity)context;
        //Width of CodeEditText was set to big fixed number
        setWidth(10000);

        mDisableTextChangedHandling = false;
        mDisableCommandHistoryWriting = false;
        mCursorPosChListener = null;

        setStyleSettings();

        parentActivity.visualStylesManager.addStyleChangedListener(new VisualStylesManager.OnStyleChangedListener() {
            @Override
            public void onStyleChanged() {
                setStyleSettings();
            }
        });
        mOldText = "";

        this.addTextChangedListener(new TextWatcher() {
            private String oldString = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                PointF pos = getCursorCoordinates();
                if(isTextChangeHandlingEnabled()
                        && mCommandHistoryManager.isCommandHistoryWritingEnabled()) {
                    oldString = s.subSequence(start, start + count).toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(parentActivity == null){
                    return;
                }
                boolean handled = !(getProgrammChangeFlag()
                        || !isTextChangeHandlingEnabled()
                        || !mCommandHistoryManager.isCommandHistoryWritingEnabled());

                if (!mDisableTextChangedHandling && mCommandHistoryManager.isCommandHistoryWritingEnabled()) {
                    String text = getText().toString();
                    String inserted = text.substring(start, start + count);
                    String newInserted = inserted;
                    if(parentActivity.commandHistoryManager.isCommandHistoryWritingEnabled()){
                        if (before < count)//If characters were inserted
                        {
                            newInserted = setIndents(text, inserted, start, count);
                        } else {//If characters were removed
                            newInserted = inserted;
                        }
                    }

                    int selStart = getSelectionStart();
                    mDisableTextChangedHandling = true;

                    if (isOpenBracket(newInserted)) {
                        if(curSymbolIsSpace(text, selStart)) {
                            newInserted = addCloseBracket(newInserted);

                            getText().replace(start, start + count,newInserted,
                                    0,newInserted.length());

                            setSelection(selStart);
                        }
                    } else if (before > count && isOpenBracket(mOldText.substring(start, start + before))) {
                        CharSequence rightStr = s.subSequence(start, s.length());
                        newInserted = "";
                        if(rightStr.length() != 0 && isCloseBracket(rightStr.subSequence(0,1).charAt(0))){
                            getText().replace(start,start + 1,newInserted,0,newInserted.length());
                        }

                        setSelection(selStart);
                    } else {
                        newInserted = addCloseBracket(newInserted);

                        getText().replace(start, start + count,newInserted,0,newInserted.length());

                        setSelection(start + newInserted.length());
                    }

                    mDisableTextChangedHandling = false;

                    if(isTextChangeHandlingEnabled()){
                        TextChangeCommand txtChangeCommand = new TextChangeCommand(start, oldString, newInserted);
                        mCommandHistoryManager.addCommand(txtChangeCommand);
                    }
                    int ss = getSelectionStart();
                    int se = getSelectionEnd();

                    if(before > count){
                        removeLeftSpaceIndent(start, before);
                    }

                }else if(!mCommandHistoryManager.isCommandHistoryWritingEnabled()){
                    setSelection(start + count);
                }
                if(!mDisableTextChangedHandling){
                    mOldText = getText().toString();
                }

                if(!mDisableTextChangedHandling){
                    parentActivity.blockedTextExtractor.extractBlockedTextFromString(getText().toString());

                    parentActivity.highlightSyntaxManager.highlightSyntax(getText());
                }

                mDisableCommandHistoryWriting = false;
                mCommandHistoryManager.decCommandHistoryWritingEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    /*Sets style settings for component*/
    private void setStyleSettings() {
        mCEditTextSelectionPaint =  parentActivity.visualStylesManager.getCEditTextSelectionPaint();
        mBkgColor =  parentActivity.visualStylesManager.getCEditTextBackgroundColor();
        setBackgroundColor(mBkgColor);
        setTextColor(parentActivity.visualStylesManager.getCEditTextForeroundColor());
        mTextSize =  parentActivity.visualStylesManager.getCEditTextTextSize();
        setTextSize(mTextSize);
        mFontFamily =  parentActivity.visualStylesManager.getCEditTextTextFontFamily();
        setTypeface(Typeface.create(mFontFamily, Typeface.NORMAL));
    }
    /*Draw method*/
    protected void onDraw(Canvas canvas) {
        Rect rect = new Rect();
        getDrawingRect(rect);
        int height = getHeight();

        int lineHeight = getLineHeight();

        int count = 1;

        if (getLineCount() > count) {
            count = getLineCount();
        }
        int cursorPosition = getSelectionEnd();
        int rowNum = getRowNumber(cursorPosition);

        Rect bounds = new Rect();

        int baseline = getLineBounds(0, bounds);
        int firstLineHeight = bounds.bottom;

        int lineTop;
        if (rowNum == 0) {
            lineTop = 0;
        } else {
            lineTop = firstLineHeight + lineHeight * (rowNum - 1);
        }

        if (rowNum == 0) {
            lineHeight = firstLineHeight;
        }
        if(parentActivity.visualStylesManager.getLineSelectionFlag()) {
            canvas.drawRect(rect.left, lineTop, rect.right, lineTop + lineHeight, mCEditTextSelectionPaint);
        }
        super.onDraw(canvas);
    }
    /*Gets row number for cursor position*/
    private int getRowNumber(int cursorPos) {
        String text = getText().toString();
        int rowNum = 0;
        for (int i = 0; i < cursorPos && i < text.length(); ++i) {
            if (text.charAt(i) == '\n') {
                ++rowNum;
            }
        }
        return rowNum;
    }
    /*Sets text without onTextChanged event calling*/
    private void SetTextQuietly(String text) {
        mDisableTextChangedHandling = true;
        setText(text);
        mDisableTextChangedHandling = false;
    }
    /*Sets text without onTextChanged event calling - as Editable*/
    private void SetTextQuietly(Editable text) {
        mDisableTextChangedHandling = true;
        setText(text);
        mDisableTextChangedHandling = false;
    }
    /*Checks if current characters is space character or not */
    private boolean isSpace(char c){
        return c == ' ' || c == '\t';
    }
    private void removeLeftSpaceIndent(int start,int before){
        String text = getText().toString();
        int indentStart = start - 1;
        if(UtilityMethods.isWhitespaceString(mOldText.substring(start, start + before))){
            while(indentStart >= 0){
                if(text.charAt(indentStart) != ' '
                        && text.charAt(indentStart) != '\t'){
                    break;
                }
                --indentStart;
            }

            boolean delete = false;
            if(indentStart < 0){
                delete = true;
            }else if(text.charAt(indentStart) == '\n'){
                delete = true;
            }
            ++indentStart;

            if(indentStart < start && delete){
                getText().replace(indentStart,start, "",0,0);
            }
        }
    }

    private boolean curSymbolIsSpace(String text, int cursorPos){
        if(cursorPos < 0 || cursorPos >= text.length()){
            return true;
        }

        if(Character.isWhitespace(text.charAt(cursorPos))){
            return true;
        }else{
            return false;
        }
    }

    private String removeCloseBracket(CharSequence s, int start, int before, int count) {
        if (before - count != 1 || s.length() <= start)
            return s.toString();

        if (isCloseBracket(s.charAt(start))) {
            String beforeTxt = (start == 0) ? "" : (s.subSequence(0, start).toString());
            String afterTxt = (s.length() - 1 == start) ? "" : (s.subSequence(start + 1, s.length()).toString());
            return beforeTxt + afterTxt;
        }
        return s.toString();
    }
    /*Get begin position of word near the cursor*/
    public int getCurrentWordBegin() {
        String content = getText().toString();
        if (content.equals("")) {
            return 0;
        }
        int i = getSelectionStart() - 1;

        for (; i >= 0; --i) {
            char c = content.charAt(i);
            if (!(Character.isLetter((int)c) || c=='_'|| Character.isDigit(c))) {
                break;
            }
        }
        return i + 1;
    }
    /*Get word near current cursor position*/
    public String getCurrentWord() {
        String content = getText().toString();
        int startIdx = getSelectionStart() - 1;
        int i = startIdx;
        if (content.equals(""))
            return "";

        for (; i >= 0; --i) {
            char c = content.charAt(i);
            if (!(Character.isLetter((int)c) || c=='_'|| Character.isDigit(c))) {
                break;
            }
        }
        if (i + 1 >= content.length()) {
            return "";
        } else {
            return content.toString().substring(i + 1, startIdx + 1);
        }

    }
    /*Adds close bracket to open bracket*/
    private String addCloseBracket(String str) {
        if (str.equals("(")) {
            str = str.concat(")");
        }
        if (str.equals("{")) {
            str = str.concat("}");
        }
        if (str.equals("[")) {
            str = str.concat("]");
        }
        return str;
    }
    /*Checks if string is open bracket*/
    private boolean isOpenBracket(String str) {
        if (str.equals("(") || str.equals("{") || str.equals("[")) {
            return true;
        }
        return false;
    }
    /*Checks if string is close bracket*/
    private boolean isCloseBracket(String str) {
        if (str.equals(")") || str.equals("}") || str.equals("]")) {
            return true;
        }
        return false;
    }
    /*Checks if string is close bracket*/
    private boolean isCloseBracket(Character str) {
        if (str.equals(')') || str.equals('}') || str.equals(']')) {
            return true;
        }
        return false;
    }
    /*Set indents for text block^
    * - if previous character is colon, adds new indent for every next string*/
    private String setIndents(String text, String inserted, int start, int count) {
        String indent = getPrevIndent(start);
        if (isPrevNonSpaceCharacterIsColon(text, start)) {
            if (getFirstNonSpaceCharacter(text, start) == '\n') {
                indent = appendCharacter(indent, indentCharacter, mIndentLength);
            }
        }
        String to = "\n" + indent;
        String newInserted = inserted.replace("\n", to);
        return newInserted;
    }
    /*Append character to string 'count' times*/
    private String appendCharacter(String str, char character, int count) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < count; ++i) {
            sb.append(character);
        }
        return sb.toString();
    }
    /*Gets current cursor coordinates*/
    public PointF getCursorCoordinates() {
        int pos = getSelectionEnd();
        Layout layout = getLayout();
        if (layout == null) {
            return new PointF(0, 0);
        }
        int line = layout.getLineForOffset(pos);
        int baseline = layout.getLineBaseline(line);
        int ascent = layout.getLineAscent(line);
        float x = layout.getPrimaryHorizontal(pos);
        float y = baseline + ascent + getLineHeight();
        return new PointF(x, y);
    }
    /*SelectionChanged handler*/
    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        if (mCursorPosChListener == null)
            return;
        if (!mDisableTextChangedHandling) {
            sharedSelStart = getSelectionStart();
            sharedSelEnd = getSelectionEnd();
        }
        mCursorPosChListener.onCursorPosChanged();
    }
    /*Inserts string in current cursor position
    * Replaces current word
    * */
    public void InsertInCurrentPosition(String str, boolean insteadCurWord) {
        String text = getText().toString();
        int wordBegin = 0;
        int selStart;
        int selEnd;
        if(insteadCurWord) {
            wordBegin = getCurrentWordBegin();
        }
        selStart = getSelectionStart();
        selEnd = getSelectionEnd();
        Editable editableText = getText();
        if(insteadCurWord){
            editableText.replace(wordBegin,selEnd,str,0,str.length());
            setSelection(wordBegin + str.length());
        }else{
            editableText.replace(selStart,selEnd,str,0,str.length());
            setSelection(selStart + str.length());
        }
    }
    public void pasteTextInCurrentPosition(String str) {

        InsertInCurrentPosition(str,false);
        parentActivity.blockedTextExtractor.incDisableSpinnerItemSelCounter();
    }
    /*Gets indent of previous string*/
    private String getPrevIndent(int position) {
        Editable text = getText();
        int lastIndent = 0;
        boolean isIndent = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < position; ++i) {
            char cur_c = text.charAt(i);
            if (cur_c == '\n') {
                isIndent = true;
                sb.setLength(0);
            } else if (isSpace(cur_c)) {
                if (isIndent) {
                    sb.append(cur_c);
                }
            } else {
                isIndent = false;
            }
        }
        return sb.toString();
    }

    /*Get first non space character from string (beginning  with 'startPos')*/
    private char getFirstNonSpaceCharacter(String txt, int startPos) {
        for (int i = startPos; i < txt.length(); ++i) {
            if (!isSpace(txt.charAt(i))) {
                return txt.charAt(i);
            }
        }
        return ' ';
    }
    /*Checks if previous non space character was a colon*/
    private boolean isPrevNonSpaceCharacterIsColon(String text, int currentPos) {
        int i = currentPos - 1;
        if (currentPos >= text.length()) {
            return false;
        }
        while (i >= 0) {
            if (!isSpace(text.charAt(i))) {
                break;
            }
            --i;
        }
        if (i < 0)
            return false;
        return text.charAt(i) == ':';
    }

    public void addIndent() {
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < mIndentLength; ++i) {
            indentBuilder.append(indentCharacter);
        }
        String indents = indentBuilder.toString();
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();
        String text = getText().toString();
        if (selStart == selEnd) {
            int cursorPos = selStart;
            getText().replace(selStart, selStart,indents,0,indents.length());
            setSelection(cursorPos + indents.length());
        } else {
            String beforeTxt = text.substring(0, selStart);
            String afterTxt = text.substring(selEnd);
            String selText = text.substring(selStart, selEnd);
            selText = selText.replace("\n", "\n" + indents);
            selText = indents + selText;
            getText().replace(selStart, selEnd,selText,0,selText.length());
            setSelection(selStart, selStart + selText.length());
        }
    }

    public void removeIntent() {
        String text = getText().toString();

        int indentCount = mIndentLength;
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();

        if (selStart == selEnd) {
            int cursorPos = getSelectionStart();
            int startIndentPos = cursorPos - 1;
            for (; startIndentPos >= 0 && indentCount > 0; --startIndentPos) {
                if (!isSpace(text.charAt(startIndentPos))) {
                    break;
                }
                --indentCount;
            }
            String beforeTxt = text.substring(0, startIndentPos + 1);
            String afterTxt = text.substring(cursorPos);
            String resText = beforeTxt + afterTxt;
            this.setText(resText);
            setSelection(startIndentPos + 1);
        } else {
            StringBuilder indentBuilder = new StringBuilder();
            for (int i = 0; i < mIndentLength; ++i) {
                indentBuilder.append(indentCharacter);
            }
            String indents = indentBuilder.toString();

            String beforeTxt = text.substring(0, selStart);
            String afterTxt = text.substring(selEnd);
            String selText = text.substring(selStart, selEnd);
            selText = selText.replaceAll("\n\\s{0," + Integer.toString(indentCount) + "}", "\n");
            int startPos = 0;
            while (startPos < selText.length() && startPos < indentCount) {
                if (!Character.isWhitespace(selText.charAt(startPos))) {
                    break;
                }
                ++startPos;
            }
            selText = selText.substring(startPos);
            this.setText(beforeTxt + selText + afterTxt);
            setSelection(selStart, selStart + selText.length());
        }
    }
    public void commentSelectedBlock() {
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();
        String text = getText().toString();

        String selText = text.substring(selStart,selEnd);
        if(selText.length() > 0 && selText.charAt(0) !='#'){
            selText = "#" + selText;
        }

        selText = selText.replaceAll("\n([^#])", "\n#$1");
        getText().replace(selStart, selEnd,selText,0,selText.length());
        setSelection(selStart, selStart + selText.length());
    }
    public void uncommentSelectedBlock() {
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();
        String text = getText().toString();

        String selText = text.substring(selStart,selEnd);
        if(selText.length() > 0 && selText.charAt(0) =='#'){
            selText = selText.substring(1);
        }
        selText = selText.replaceAll("\n#", "\n");
        getText().replace(selStart, selEnd,selText,0,selText.length());
        setSelection(selStart, selStart + selText.length());
    }
    public boolean gotoLine(int lineNumber){
        if(lineNumber < 0)
            return false;
        if(lineNumber == 1){
            setSelection(0);
            return true;
        }
        int cur_line_num = 1;
        int line_start_pos = 0;
        String text = getText().toString();

        boolean found = false;

        for(int i=0;i<text.length() && !found;++i){
            if(text.charAt(i) == '\n'){
                ++cur_line_num;
                line_start_pos = i+1;
                if(cur_line_num == lineNumber){
                    found = true;
                }
            }
        }
        if(found){
            setSelection(line_start_pos);
            return true;
        }

        return false;
    }

    private boolean isIdentifierCharacter(char c){
        if(Character.isLetter((int)c)
                ||Character.isDigit(c) || c == '_'){
            return true;
        }else{
            return false;
        }
    }

    public String grabCurrentWord(){
        Editable text = getText();
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();

        if(selStart == text.length()){
            return "";
        }

        while(selStart >= 0 && isIdentifierCharacter(text.charAt(selStart))){
            --selStart;
        }
        ++selStart;
        while(selEnd < text.length() && isIdentifierCharacter(text.charAt(selEnd))){
            ++selEnd;
        }

        if(selStart >= selEnd){
            return "";
        }
        return text.subSequence(selStart, selEnd).toString();
    }

    public  void setSyntaxRanges(ArrayList<TextSyntaxRange> textRanges) {
        setSyntaxRanges(textRanges, 0,getText().length());
    }

    public void setSyntaxRanges(ArrayList<TextSyntaxRange> textRanges, int start, int end) {
        mDisableTextChangedHandling = true;
        int curPos = 0;
        Editable editableText = getText();
        String text = editableText.toString();

        ForegroundColorSpan[] spansForRemove = editableText.getSpans(start,end,ForegroundColorSpan.class);
        for(ForegroundColorSpan span : spansForRemove){
            editableText.removeSpan(span);
        }

        for(TextSyntaxRange range:textRanges){
            if(range.end > text.length()){
                continue;
            }
            switch(range.type){
                case TextSyntaxRange.COMMENTS_TYPE:
                    editableText.setSpan(new ForegroundColorSpan(Color.GRAY), range.start, range.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case TextSyntaxRange.CONSTANT_TYPE:
                    editableText.setSpan(new ForegroundColorSpan(Color.MAGENTA), range.start, range.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case TextSyntaxRange.STRING_TYPE:
                    editableText.setSpan(new ForegroundColorSpan(Color.RED), range.start, range.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case TextSyntaxRange.DECORATOR_TYPE:
                    editableText.setSpan(new ForegroundColorSpan(Color.GREEN), range.start, range.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case TextSyntaxRange.KEY_WORD_TYPE:
                    editableText.setSpan(new ForegroundColorSpan(Color.BLUE), range.start, range.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
            }

        }

        mDisableTextChangedHandling = false;
    }

    public Pair<Integer,Integer> getSpanBounds(int start, int end){
        Editable text = getText();

        ForegroundColorSpan[] allSpans = text.getSpans(0,text.length(),ForegroundColorSpan.class);
        if(allSpans.length == 0){
            return new Pair(0,text.length());
        }
        int leftBound = start;
        int rightBound = end;
        for(int i=0;i < allSpans.length;++i){
            int spanStart = text.getSpanStart(allSpans[i]);
            int spanEnd = text.getSpanEnd(allSpans[i]);
            if(spanStart <= leftBound && spanEnd >= rightBound
                    ||spanStart >= leftBound && spanStart <= rightBound
                    ||spanEnd >= leftBound && spanEnd <= rightBound){
                if(spanStart < leftBound){
                    leftBound = spanStart;
                }
                if(spanEnd > rightBound){
                    rightBound = spanEnd;
                }
            }

        }
        return new Pair(leftBound,rightBound);
    }
}
