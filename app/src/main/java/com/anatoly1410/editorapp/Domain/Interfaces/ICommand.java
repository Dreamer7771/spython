package com.anatoly1410.editorapp.Domain.Interfaces;

import java.io.Serializable;

/**
 * Created by 1 on 23.03.2017.
 */

public interface ICommand {
    void Execute();
    void Unexecute();
}
