package com.anatoly1410.editorapp.Presentation;

import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IFileManager;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Domain.MyApplication;
import com.anatoly1410.editorapp.Presentation.Interfaces.ITabManager;
import com.anatoly1410.editorapp.R;
import com.anatoly1410.editorapp.UtilityMethods;

import java.io.File;
import java.io.FileOutputStream;

import javax.inject.Inject;

public class OpenOrSaveFileActivity extends AppCompatActivity {
    @Inject
    ITabManager tabManager;
    @Inject
    ISettingsManager settingsManager;
    /*Components of the activity*/
    LinearLayout mFilesMenu;
    TextView mLocationTextView;
    /*Current path*/
    String mCurrentPath;
    /*Flag that determines the mCurrentPath content: is it path to file or directory?*/
    boolean pathToFile;
    /*Path of the app package*/
    String mPackagePath;
    /*List of opened files/dirs*/
    File[] mListOfFiles;
    /*Save flag; shows mode - file saving or file opening*/
    private boolean mIsSaving;
    /*Parameters for distinguishing click from move action
    * - coordinates of the last tap
    * - max distance between two taps to consider it as a click*/
    private Point mLastActionDownCoords;
    private float maxClickDist = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getDomainComponent().inject(this);
        setContentView(R.layout.activity_open_or_save_file);
        mLocationTextView = (TextView) findViewById(R.id.open_file_location);
        mFilesMenu = (LinearLayout) findViewById(R.id.file_menu);
        LinearLayout save_file_block = (LinearLayout) findViewById(R.id.save_file_block);
        mPackagePath = getPackageSDCardPath();

        if(!restoreLastDirPath()){
            setPath(mPackagePath + "/" + getResources().getString(R.string.projectsDirName));
        }else{
            setPath(mCurrentPath);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        Bundle b = getIntent().getExtras();
        mIsSaving = b.getBoolean("isSaving");
        if (!mIsSaving) {
            setTitle(R.string.openFileActivityTitle);
            save_file_block.setVisibility(View.GONE);
        } else {
            setTitle(R.string.saveFileActivityTitle);
            save_file_block.setVisibility(View.VISIBLE);
            setExtensionsSpinner();
        }
        pathToFile = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*Sets extensions spinner*/
    private void setExtensionsSpinner() {
        Spinner ext_spinner = (Spinner) findViewById(R.id.ext_spinner);
        ;
        String exts[] = {"<none>", "py", "txt", "cpp", "java"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, exts);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ext_spinner.setAdapter(spinnerArrayAdapter);
    }
    /*Gets files and dirs from path*/
    private File[] getFilesAndDirs(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        return files;
    }
    /*Gets app package name on sdcard*/
    private String getPackageSDCardPath() {
        PackageManager m = getPackageManager();
        String packageName = getPackageName();

        return "/sdcard/" + packageName;
    }
    /*Sets menu content for current path*/
    private boolean setMenuForPath(String path) {
        File[] files = getFilesAndDirs(path);
        mListOfFiles = files;
        if (mListOfFiles == null) {
            return false;
        }
        mFilesMenu.removeAllViews();
        for (int i = 0; i < files.length; ++i) {
            createNewMenuItem(files[i].getName(), files[i].isDirectory());
        }
        return true;
    }
    /*Creates new menu item*/
    private void createNewMenuItem(String name, boolean isDirectory) {
        LinearLayout hLayout = new LinearLayout(this);
        hLayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView icon = new ImageView(this);
        TextView tv = new TextView(this);
        tv.setText(name);
        if (isDirectory) {
            icon.setImageResource(R.mipmap.ic_folder);
        } else {
            icon.setImageResource(R.mipmap.ic_file);
        }
        hLayout.addView(icon);
        hLayout.addView(tv);
        mFilesMenu.addView(hLayout);
    }
    /*Set new path*/
    private void setPath(String path) {
        String prevPath = mCurrentPath;
        mCurrentPath = path;
        if (!setMenuForPath(mCurrentPath)) {
            mCurrentPath = prevPath;
            Toast toast = Toast.makeText(this, "Can't open directory: no access", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            mLocationTextView.setText(getResources().getString(R.string.openFileLocationString) + ": " + mCurrentPath);
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int ac = event.getAction();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLastActionDownCoords = new Point((int) event.getX(), (int) event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Point touchPoint = new Point((int) event.getX(), (int) event.getY());
            if (mLastActionDownCoords == null) {
                fireClick(touchPoint);
            } else if (UtilityMethods.getDistance(touchPoint, mLastActionDownCoords) < maxClickDist) {
                fireClick(touchPoint);
            }
        }
        return super.dispatchTouchEvent(event);
    }
    /*Fires click on menu item*/
    private void fireClick(Point touchPoint) {
        int menuItemIdx = getClickedMenuItemIdx(touchPoint);
        if (menuItemIdx >= 0) {
            mCurrentPath += "/" + mListOfFiles[menuItemIdx].getName();
            if (mListOfFiles[menuItemIdx].isDirectory()) {
                pathToFile = false;
                setPath(mCurrentPath);
            } else {
                pathToFile = true;
                if (!mIsSaving) {
                    openFile(mCurrentPath);
                }
            }
        }
    }
    /*Gets clicked menu item index by point*/
    public int getClickedMenuItemIdx(Point point) {
        int count = mFilesMenu.getChildCount();
        for (int i = 0; i < count; ++i) {
            LinearLayout menuItem = (LinearLayout) mFilesMenu.getChildAt(i);
            Rect rect = new Rect();
            menuItem.getHitRect(rect);
            int[] menuItemCoords = new int[2];
            menuItem.getLocationOnScreen(menuItemCoords);
            rect = new Rect(menuItemCoords[0], menuItemCoords[1], menuItemCoords[0] + rect.width(),
                    menuItemCoords[1] + rect.height());
            if (rect.contains(point.x, point.y)) {
                return i;
            }
        }
        return -1;
    }
    /*Makes step back in current path*/
    private void makeStepBackInPath() {
        setPath(UtilityMethods.getParentDirectoryPath(mCurrentPath));
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (mPackagePath != null) {
                if (mCurrentPath.equals("/sdcard")) {
                    onBackPressed();
                } else {
                    makeStepBackInPath();
                }
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /*Open file by path*/
    private void openFile(String path) {
        tabManager.openDoc(path);
        finish();
    }
    /*Saves file*/
    public void saveFile(View view) {
        ITabManager tm = tabManager;
        EditText fileNameEditText = (EditText) findViewById(R.id.file_name_edit_text);
        String fullFileName = fileNameEditText.getText().toString();
        Spinner extSpinner = (Spinner) findViewById(R.id.ext_spinner);
        if (extSpinner.getSelectedItemId() != 0) {
            fullFileName += "." + extSpinner.getSelectedItem().toString();
        }
        fullFileName = mCurrentPath + "/" + fullFileName;
        String fileContent = tm.getOpenedTab().getContent();
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(fullFileName, false);
            outputStream.write(fileContent.getBytes());
            outputStream.close();
            tm.getOpenedTab().setPath(fullFileName);
            tm.getOpenedTab().setUnsavedFlag(false);
            finish();
        } catch (Exception e) {

        }
    }

    private boolean restoreLastDirPath(){
        String lastOpenDirPath = settingsManager.getLastOpenDirectory();
        if(lastOpenDirPath != ""){
            mCurrentPath = lastOpenDirPath;
            return true;
        }else{
            return false;
        }
    }

    private void saveLastDirPath() {
        if(!pathToFile) {
            settingsManager.setLastOpenDirectory(mCurrentPath);
        }else{
            settingsManager.setLastOpenDirectory(UtilityMethods.getParentDirectoryPath(mCurrentPath));
        }
    }

    public void onStop () {
        saveLastDirPath();
        super.onStop();
    }
}