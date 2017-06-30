package com.anatoly1410.editorapp.Domain;

import com.anatoly1410.editorapp.Domain.Interfaces.ICommand;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 1 on 27.03.2017.
 */

public class CommandHistory implements Serializable {
    private ArrayList<ICommand> mCommands;
    private int mCurrentStatePosition;

    public ArrayList<ICommand>  getCommandsList(){
        return mCommands;
    }

    public void setCommandsList(ArrayList<ICommand> commands){
        mCommands = commands;
    }

    public int getCurrentStatePosition(){
        return mCurrentStatePosition;
    }

    public void incCurrentStatePosition(){
        ++mCurrentStatePosition;
    }

    public void decCurrentStatePosition(){
        --mCurrentStatePosition;
    }

    public void decCurrentStatePosition(int times){
        mCurrentStatePosition -= times;
    }

    public CommandHistory(){
        mCommands = new ArrayList<ICommand>();
        mCurrentStatePosition = 0;
    }

    public void addCommand(ICommand command){
        ArrayList<ICommand> commands = getCommandsList();
        if(getCurrentStatePosition() >=0 && getCurrentStatePosition() <=commands.size()){
            setCommandsList(new ArrayList<ICommand> (commands.subList(0,getCurrentStatePosition())));
        }else{
            return;
        }

        getCommandsList().add(command);
        incCurrentStatePosition();

        if(CommandHistoryManager.CommandsLimit > 0){
            if(getCommandsList().size() > CommandHistoryManager.CommandsLimit){
                int removed_commands_count = getCommandsList().size() - CommandHistoryManager.CommandsLimit;
                setCommandsList(new ArrayList<ICommand>(getCommandsList()
                        .subList(removed_commands_count,getCommandsList().size())));
                decCurrentStatePosition(removed_commands_count);
            }
        }
    }

    public void undoCommand(){
        if(getCurrentStatePosition() > 0)
        {
            decCurrentStatePosition();
            getCommandsList().get(getCurrentStatePosition()).Unexecute();
        }
    }
    public void redoCommand(){
        if(getCurrentStatePosition() < getCommandsList().size())
        {
            getCommandsList().get(getCurrentStatePosition()).Execute();
            incCurrentStatePosition();
        }
    }
    public ICommand getLastCommand(){
        if(getCommandsList().size() > 0){
            return getCommandsList().get(getCommandsList().size() - 1);
        }else{
            return null;
        }
    }
}
