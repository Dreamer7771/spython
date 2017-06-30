package com.anatoly1410.editorapp.Domain;

import com.anatoly1410.editorapp.Domain.Interfaces.ICommand;
import com.anatoly1410.editorapp.Presentation.CEditText;
import com.anatoly1410.editorapp.Presentation.Interfaces.ICommandHistoryManager;

import java.io.Serializable;

/**
 * Created by 1 on 23.03.2017.
 */

public class TextChangeCommand implements ICommand, Serializable {
    private static CEditText cEditText;
    private static ICommandHistoryManager mCommandHistoryManager;
    int position;
    String textBefore;
    String textAfter;

    public int getPosition(){
        return position;
    }
    public String getTextBefore(){
        return textBefore;
    }
    public String getTextAfter(){
        return textAfter;
    }

    public static void setCEditText(CEditText cEditText){
        TextChangeCommand.cEditText = cEditText;
    }
    public static void setCommandHistoryManager(ICommandHistoryManager commandHistoryManager){
        mCommandHistoryManager = commandHistoryManager;
    }

    public TextChangeCommand(int position, String textBefore, String textAfter){
        this.position = position;
        this.textBefore = textBefore;
        this.textAfter = textAfter;
    }
    public void Execute(){
        mCommandHistoryManager.incCommandHistoryWritingEnabled(2);
        TextChangeCommand.cEditText.getText().replace(position, position + textBefore.length(),textAfter);
    }
    public void Unexecute(){
        mCommandHistoryManager.incCommandHistoryWritingEnabled(2);
        TextChangeCommand.cEditText.getText().replace(position, position + textAfter.length(),textBefore);
    }

}
