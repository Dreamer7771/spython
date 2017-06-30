package com.anatoly1410.editorapp.Presentation.Interfaces;

import android.content.Context;
import android.content.Intent;

import com.anatoly1410.editorapp.Domain.QPythonScriptRunner;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IQPythonScriptRunner {
    interface OnQPythonActivityStartinListener {
        void fireEvent(Intent intent, int requestCode);
    }
    interface OnActivityStartingListener {
        void fireEvent(Intent intent);
    }
    interface OnShowMessageListener {
        void fireEvent(String message);
    }
    void setQPythonActivityStartinListener(OnQPythonActivityStartinListener listener);
    void setActivityStartingListener(QPythonScriptRunner.OnActivityStartingListener listener);
    void setShowMessageListener(OnShowMessageListener listener);
    boolean checkAppInstalledByName(Context context, String packageName);
    String getCodeIntentExtra();
    void onQPyExec();
}
