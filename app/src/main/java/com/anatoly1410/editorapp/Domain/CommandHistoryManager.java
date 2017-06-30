package com.anatoly1410.editorapp.Domain;

import com.anatoly1410.editorapp.Domain.Interfaces.ICommand;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.ICommandHistoryManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.ITabManager;
import com.anatoly1410.editorapp.UtilityMethods;

/**
 * Created by 1 on 23.03.2017.
 */

public class CommandHistoryManager implements ICommandHistoryManager {

    ITabManager mTabManager;
    ISettingsManager mSettingsManager;
    public static int CommandsLimit = 50;

    public CommandHistoryManager(ITabManager tabManager,
                                 ISettingsManager settingsManager){
        mTabManager = tabManager;
        mSettingsManager = settingsManager;
        settingsManager.setSettingsChangedEventListener(new ISettingsManager.OnSettingsChangedEventListener() {
            @Override
            public void fireEvent() {
                CommandsLimit = mSettingsManager.getIntSetting(mSettingsManager.APP_PREFERENCES_COMMANDS_NUM_BY_DEFAULT);
            }
        });
    }

    public CommandHistory getCommandHistoryForCurrentTab(){
        TabContent curTabContent = mTabManager.getOpenedTab();

        return curTabContent.getCommandHistory();
    }

    public boolean isCommandHistoryWritingEnabled(){ return commandHistoryWritingEnabled == 0; }

    private int commandHistoryWritingEnabled = 0;

    public void incCommandHistoryWritingEnabled(int subscribersCount){
        commandHistoryWritingEnabled += subscribersCount;
    }
    public void decCommandHistoryWritingEnabled(){
        if(commandHistoryWritingEnabled > 0){
            --commandHistoryWritingEnabled;
        }
    }


    public void addCommand(ICommand command){
        CommandHistory commandHistory = getCommandHistoryForCurrentTab();
        if(!unitWithLast(command)){
            commandHistory.addCommand(command);
        }
    }

    public void undoCommand(){
        CommandHistory commandHistory = getCommandHistoryForCurrentTab();
        commandHistory.undoCommand();
    }
    public void redoCommand(){
        CommandHistory commandHistory = getCommandHistoryForCurrentTab();
        commandHistory.redoCommand();
    }

    public boolean unitWithLast(ICommand command){
        CommandHistory commandHistory = getCommandHistoryForCurrentTab();
        ICommand lastCommand = commandHistory.getLastCommand();
        if(lastCommand == null){
            return false;
        }
        //for text addition
        if(lastCommand.getClass() == TextChangeCommand.class
                && command.getClass() == TextChangeCommand.class){
            TextChangeCommand txtCommand = (TextChangeCommand)command;
            TextChangeCommand lastTxtCommand = (TextChangeCommand)lastCommand;
            if(txtCommand.position == lastTxtCommand.position +lastTxtCommand.textAfter.length()
                    && txtCommand.textAfter.length() >= txtCommand.textBefore.length()
                    && lastTxtCommand.textAfter.length() >= lastTxtCommand.textBefore.length()
                    && !UtilityMethods.isWhitespaceString(txtCommand.textAfter)
                    && !UtilityMethods.isWhitespaceString(lastTxtCommand.textAfter)){

                lastTxtCommand.textBefore += txtCommand.textBefore;
                lastTxtCommand.textAfter += txtCommand.textAfter;

                return true;

                //for text removal
            }else if(lastTxtCommand.position == txtCommand.position +txtCommand.textBefore.length()
                    && txtCommand.textAfter.length() < txtCommand.textBefore.length()
                    && lastTxtCommand.textAfter.length() < lastTxtCommand.textBefore.length()
                    && !UtilityMethods.isWhitespaceString(txtCommand.textBefore)
                    && !UtilityMethods.isWhitespaceString(lastTxtCommand.textBefore)){
                lastTxtCommand.position = txtCommand.position;
                lastTxtCommand.textAfter = txtCommand.textAfter + lastTxtCommand.textAfter;
                lastTxtCommand.textBefore = txtCommand.textBefore + lastTxtCommand.textBefore;
                return true;
            }
        }
        return false;
    }
}
