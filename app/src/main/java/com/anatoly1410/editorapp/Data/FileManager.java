package com.anatoly1410.editorapp.Data;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.anatoly1410.editorapp.Domain.HelpManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IFileManager;
import com.anatoly1410.editorapp.Presentation.MainActivity;
import com.anatoly1410.editorapp.R;
import com.anatoly1410.editorapp.Presentation.Tab;
import com.anatoly1410.editorapp.Domain.TabContent;
import com.anatoly1410.editorapp.Presentation.TabPanel;
import com.anatoly1410.editorapp.UtilityMethods;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by 1 on 09.01.2017.
 */

public class FileManager implements IFileManager{

    private transient Context mContext;

    public FileManager(Context context){
        mContext = context;
    }

    OnWarningMessageEventListener mWarningMessageEventListener;
    public interface OnWarningMessageEventListener {
        void fireEvent(String messageContent);
    }

    public void setWarningMessageEventListener(OnWarningMessageEventListener listener) {
        mWarningMessageEventListener = listener;
    }

    /*Serializes object to file*/
    public void saveToFile(String objFileName, Object obj){
        FileOutputStream out = null;
        try
        {
            out = mContext.openFileOutput(objFileName, Context.MODE_PRIVATE);

            try
            {
                ObjectOutputStream oos = new ObjectOutputStream(out);
                oos.writeObject(obj);
                oos.close();
            }
            catch(IOException e)
            {
                Log.d(this.getClass().toString(), e.getMessage());
            }
        }
        catch(FileNotFoundException e)
        {
            Log.d(this.getClass().toString(), e.getMessage());
        }
        finally
        {
            try
            {
                if(out != null) out.close();
            }
            catch(IOException e)
            {
                Log.d(this.getClass().toString(), e.getMessage());
            }
        }
    }
    /*Deserializes object from file*/
    public Object loadFromFile(String objFileName){
        FileInputStream in = null;
        Object obj = null;
        try
        {
            in = mContext.openFileInput(objFileName);

            try
            {
                ObjectInputStream oos = new ObjectInputStream(in);
                try {
                    obj = oos.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                oos.close();
            }
            catch(IOException e)
            {
                Log.d(this.getClass().toString(), "");
            }
        }
        catch(FileNotFoundException e)
        {
            Log.d(this.getClass().toString(), e.getMessage());
        }
        finally
        {
            try
            {
                if(in != null) in.close();
            }
            catch(IOException e)
            {
                Log.d(this.getClass().toString(), e.getMessage());
            }
        }
        return obj;
    }
    /*Loads text file content*/
    public String loadContent(String path)
    {
        File file = new File(path);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            if(mWarningMessageEventListener != null){
                mWarningMessageEventListener.fireEvent("File opening error: "+e.getMessage());
            }
        }
        String txtString = text.toString();
        return txtString;
    }

    /*Saves opened tab as existing file*/
    public void saveTabAsExistingFile(String path, String content)
    {
        if(mContext == null)
            return;

        FileOutputStream outputStream;
            try {
                outputStream = getFileOutputStream(path);
                outputStream.write(content.getBytes());
                outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    //for testing
    public FileOutputStream getFileOutputStream(String path) throws FileNotFoundException{
        try {
            FileOutputStream stream =  new FileOutputStream(path,false);
            return stream;

        } catch (Exception e) {
            throw e;
        }

    }

}
