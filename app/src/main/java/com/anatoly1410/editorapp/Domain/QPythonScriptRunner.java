package com.anatoly1410.editorapp.Domain;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Presentation.CEditText;
import com.anatoly1410.editorapp.Presentation.Interfaces.IQPythonScriptRunner;
import com.anatoly1410.editorapp.Presentation.MainActivity;
import com.anatoly1410.editorapp.R;

/**
 * Created by 1 on 02.04.2017.
 */

public class QPythonScriptRunner implements IQPythonScriptRunner {

  //  private transient MainActivity mainActivity;

    private Context mContext;
    public final static int SCRIPT_EXEC_PY = 40001;
    private final String extPlgPlusName = "com.hipipal.qpyplus";
    private final String codeIntentExtra = "pycode";

    public String getCodeIntentExtra(){
        return codeIntentExtra;
    }


    OnQPythonActivityStartinListener mQPythonActivityStartinListener;
    OnActivityStartingListener mActivityStartingListener;
    OnShowMessageListener mShowMessageListener;

    public void setQPythonActivityStartinListener(OnQPythonActivityStartinListener listener) {
        mQPythonActivityStartinListener = listener;
    }
    public void setActivityStartingListener(OnActivityStartingListener listener) {
        mActivityStartingListener = listener;
    }
    public void setShowMessageListener(OnShowMessageListener listener) {
        mShowMessageListener = listener;
    }


    public QPythonScriptRunner(Context context){
        mContext = context;
    }

    public boolean checkAppInstalledByName(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    packageName, PackageManager.GET_UNINSTALLED_PACKAGES);

            Log.d("QPYMAIN",  "checkAppInstalledByName:"+packageName+" found");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("QPYMAIN",  "checkAppInstalledByName:"+packageName+" not found");

            return false;
        }
    }

    public void onQPyExec() {

        if (checkAppInstalledByName(mContext.getApplicationContext(), extPlgPlusName)) {

            Intent intent = new Intent();
            intent.setClassName(extPlgPlusName, "com.hipipal.qpyplus.MPyApi");
            intent.setAction(extPlgPlusName + ".action.MPyApi");

            Bundle mBundle = new Bundle();
            mBundle.putString("app", "myappid");
            mBundle.putString("act", "onPyApi");
            mBundle.putString("flag", "onQPyExec");            // any String flag you may use in your context
            mBundle.putString("param", "");          // param String param you may use in your context

	        /*
	         * The String Python code, you can put your py file in res or raw or intenet, so that you can get it the same way, which can make it scalable
	         */

            intent.putExtras(mBundle);

            if(mQPythonActivityStartinListener != null){
                mQPythonActivityStartinListener.fireEvent(intent, SCRIPT_EXEC_PY);
            }
        } else {
            if(mShowMessageListener != null){
                mShowMessageListener.fireEvent("Please install QPython first");
            }


            try {
                Uri uLink = Uri.parse("market://details?id=com.hipipal.qpyplus");
                Intent intent = new Intent( Intent.ACTION_VIEW, uLink );
                if(mActivityStartingListener != null){
                    mActivityStartingListener.fireEvent(intent);
                }
            } catch (Exception e) {
                Uri uLink = Uri.parse("http://qpython.com");
                Intent intent = new Intent( Intent.ACTION_VIEW, uLink );
                if(mActivityStartingListener != null){
                    mActivityStartingListener.fireEvent(intent);
                }
            }

        }
    }



}
