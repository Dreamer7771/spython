package com.anatoly1410.editorapp.Presentation;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.Data.VisualStylesManager;
import com.anatoly1410.editorapp.Domain.CommandHistoryManager;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IVisualStylesManager;
import com.anatoly1410.editorapp.Domain.MyApplication;
import com.anatoly1410.editorapp.R;

import java.util.ArrayList;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity {
    @Inject
    ISettingsManager settingsManager;
    @Inject
    IVisualStylesManager visualStylesManager;
    private CTextView mTextView;
    private Integer[] textSizesArray;
    private String[] sortTypesArray;
    private Integer[] commandsCountArray;
    private String[] commandsCountStringArray;
    private String[] stylesArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getDomainComponent().inject(this);
        setContentView(R.layout.activity_settings);
        initArrays();
        initSettingsElements();
    }
    @Override
    protected void onPause() {
        super.onPause();
        visualStylesManager.saveVisualSettings();
    }
    public void initSettingsElements(){
        mTextView = (CTextView)findViewById(R.id.example_string_edit_text);
        mTextView.setParentActivity(this);
        int textSize = visualStylesManager.getCEditTextTextSize();
        int textBkgColor = visualStylesManager.getCEditTextBackgroundColor();
        int textColor = visualStylesManager.getCEditTextForeroundColor();

        mTextView.setText(R.string.exampleStringSettings);
        mTextView.setTextSize(textSize);
        mTextView.setTextColor(textColor);

        String fontFamily = visualStylesManager.getCEditTextTextFontFamily();
        mTextView.setTypeface(Typeface.create(fontFamily, Typeface.NORMAL));
        int leftPadding = visualStylesManager.getLeftCodeTextPadding();
        mTextView.setPadding(leftPadding,10,10,10);
        int leftIndent = visualStylesManager.getLeftIndent();
        mTextView.setVerticalLineIndent(leftIndent);

        setCTextViewParams();
        GradientDrawable bkgShape = (GradientDrawable )mTextView.getBackground();
        bkgShape.setColor(textBkgColor);

        final Spinner spinner = (Spinner)findViewById(R.id.sizes_spinner);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                 R.layout.settings_spinner_item,
        textSizesArray);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position < 0)
                    return;
                int item = (Integer)spinner.getItemAtPosition(position);
                setTextSize(item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Spinner stylesSpinner = (Spinner)findViewById(R.id.styles_spinner);
        stylesArray = getResources().getStringArray(R.array.stylesList);
        ArrayAdapter stylesSpinnerArrayAdapter = new ArrayAdapter(this,
                R.layout.settings_spinner_item,
                stylesArray);
        stylesSpinner.setAdapter(stylesSpinnerArrayAdapter);
        stylesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position < 0)
                    return;
                int pos =  stylesSpinner.getSelectedItemPosition();
                setStyle(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        visualStylesManager.addStyleChangedListener(new VisualStylesManager.OnStyleChangedListener() {
            @Override
            public void onStyleChanged() {
                setCTextViewParams();
            }
        });

        final CheckBox lineSelectionCheckBox = (CheckBox)findViewById(R.id.select_line_chkbox);

        lineSelectionCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = lineSelectionCheckBox.isChecked();
                visualStylesManager.setLineSelectionFlag(checked);
            }
        });
        final CheckBox highlightSyntaxCheckBox = (CheckBox)findViewById(R.id.highlight_syntax_chkbox);
        highlightSyntaxCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = highlightSyntaxCheckBox.isChecked();
                settingsManager.setBooleanSetting(settingsManager.APP_PREFERENCES_HIGHLIGHT_BY_DEFAULT,checked);
            }
        });
        final CheckBox extractBlocksCheckBox = (CheckBox)findViewById(R.id.extract_chkbox);
        extractBlocksCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = extractBlocksCheckBox.isChecked();
                settingsManager.setBooleanSetting(settingsManager.APP_PREFERENCES_EXTRACT_BLOCKS_BY_DEFAULT,checked);
            }
        });

        final CheckBox autocompCheckBox = (CheckBox)findViewById(R.id.autocomp_chkbox);
        autocompCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = autocompCheckBox.isChecked();
                settingsManager.setBooleanSetting(settingsManager.APP_PREFERENCES_AUTOCOMP_BY_DEFAULT,checked);
            }
        });
        final Spinner commandsCountSpinner = (Spinner)findViewById(R.id.commands_count_spinner);

        ArrayAdapter  commandsCountArrayAdapter = new ArrayAdapter(this,
                R.layout.settings_spinner_item,
                commandsCountStringArray);
        commandsCountSpinner.setAdapter(commandsCountArrayAdapter);

        commandsCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position < 0)
                    return;

                int pos =  commandsCountSpinner.getSelectedItemPosition();
                setCommandsCount(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final CheckBox showClassesCheckbox = (CheckBox)findViewById(R.id.show_classes_chkbox);
        showClassesCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = showClassesCheckbox.isChecked();
                settingsManager.setBooleanSetting(settingsManager.APP_PREFERENCES_SHOW_CLASSES,checked);
            }
        });

        final CheckBox showFunctionsCheckbox = (CheckBox)findViewById(R.id.show_functions_chkbox);
        showFunctionsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = showFunctionsCheckbox.isChecked();
                settingsManager.setBooleanSetting(settingsManager.APP_PREFERENCES_SHOW_FUNCTIONS,checked);
            }
        });
        final Spinner sortTypeSpinner = (Spinner)findViewById(R.id.sort_type_spinner);
        sortTypesArray =  getResources().getStringArray(R.array.sortingTypesList);
        ArrayAdapter sortSpinnerArrayAdapter = new ArrayAdapter(this,
                R.layout.settings_spinner_item,
                sortTypesArray);
        sortTypeSpinner.setAdapter(sortSpinnerArrayAdapter);
        sortTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position < 0)
                    return;
                setSortType(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        initSettings();
    }

    private void setCTextViewParams(){
        int txtSize = visualStylesManager.getCEditTextTextSize();
        mTextView.setTextSize(txtSize);
        int leftIndent = visualStylesManager.getLeftCodeTextPadding();
        mTextView.setPadding(leftIndent,10,10,10);

        mTextView.setVerticalLineIndent(visualStylesManager.getLeftIndent());
        mTextView.setRowNumberSize(visualStylesManager.getCodeRowNumberSize());
    }

    void initSettings(){
        final Spinner spinner = (Spinner)findViewById(R.id.sizes_spinner);
        int size = visualStylesManager.getCEditTextTextSize();
        for(int i=0;i<spinner.getCount();++i){
            int curSize = (Integer)spinner.getItemAtPosition(i);
            if(curSize == size){
                spinner.setSelection(i);
                break;
            }
        }

        boolean lineSelectionEnabled = visualStylesManager.getLineSelectionFlag();
        final CheckBox lineSelectionCheckBox = (CheckBox)findViewById(R.id.select_line_chkbox);
        lineSelectionCheckBox.setChecked(lineSelectionEnabled);

        final CheckBox highlightSyntaxCheckBox = (CheckBox)findViewById(R.id.highlight_syntax_chkbox);
        boolean highlighSyntaxEnabled = settingsManager.getBooleanSetting(settingsManager.APP_PREFERENCES_HIGHLIGHT_BY_DEFAULT);
        highlightSyntaxCheckBox.setChecked(highlighSyntaxEnabled);

        final CheckBox extractBlocksCheckBox = (CheckBox)findViewById(R.id.extract_chkbox);
        boolean extractBlocksEnabled = settingsManager.getBooleanSetting(settingsManager.APP_PREFERENCES_EXTRACT_BLOCKS_BY_DEFAULT);
        extractBlocksCheckBox.setChecked(extractBlocksEnabled);

        final CheckBox autocompCheckBox = (CheckBox)findViewById(R.id.autocomp_chkbox);
        boolean autocompEnabled = settingsManager.getBooleanSetting(settingsManager.APP_PREFERENCES_AUTOCOMP_BY_DEFAULT);
        autocompCheckBox.setChecked(autocompEnabled);

        final Spinner commandsCountSpinner = (Spinner)findViewById(R.id.commands_count_spinner);

        int commandsCount = settingsManager.getIntSetting(settingsManager.APP_PREFERENCES_COMMANDS_NUM_BY_DEFAULT);
        if(commandsCount == 0){
            commandsCountSpinner.setSelection(0);
        }else{
            for(int i=1;i<commandsCountSpinner.getCount();++i){
                int curCount = Integer.parseInt((String)commandsCountSpinner.getItemAtPosition(i));
                if(curCount == commandsCount){
                    commandsCountSpinner.setSelection(i);
                    break;
                }
            }
        }
        final Spinner sortTypeSpinner = (Spinner)findViewById(R.id.sort_type_spinner);
        int sortType = settingsManager.getIntSetting(settingsManager.APP_PREFERENCES_SORT_ORDER);
        sortTypeSpinner.setSelection(sortType);

        final CheckBox showFunctionsCheckbox = (CheckBox)findViewById(R.id.show_functions_chkbox);
        boolean showFunctions = settingsManager.getBooleanSetting(settingsManager.APP_PREFERENCES_SHOW_FUNCTIONS);
        showFunctionsCheckbox.setChecked(showFunctions);
        final CheckBox showClassesCheckbox = (CheckBox)findViewById(R.id.show_classes_chkbox);
        boolean showClasses = settingsManager.getBooleanSetting(settingsManager.APP_PREFERENCES_SHOW_CLASSES);
        showClassesCheckbox.setChecked(showClasses);
    }

    private void initArrays(){
        textSizesArray = new Integer[]{6,8,10,12,14,16,18,25,32,48};
        commandsCountArray = new Integer[]{15,30,60,100,200};

        String defaultCommandNumValue = getResources().getString(R.string.commandsCountUnlimitedValueSettingsItem);
        commandsCountStringArray = new String[commandsCountArray.length + 1];
        commandsCountStringArray[0] = defaultCommandNumValue;
        for(int i=1;i<commandsCountStringArray.length;++i){
            commandsCountStringArray[i] = commandsCountArray[i - 1].toString();
        }
    }

    private void setTextSize(int textSize){
        visualStylesManager.setCEditTextTextSize(textSize);
    }

    private void setStyle(int stylePos){
        switch(stylePos){
            case 0:
                visualStylesManager.setDefaultStyle();
                break;
            case 1:
                visualStylesManager.setDarkStyle();
                break;
        }
    }

    private void setCommandsCount(int itemPos){
        if(itemPos == 0){
            settingsManager.setIntSetting(settingsManager.APP_PREFERENCES_COMMANDS_NUM_BY_DEFAULT,0);
            CommandHistoryManager.CommandsLimit = 0;
        }else {
            int value = commandsCountArray[itemPos - 1];
            settingsManager.setIntSetting(settingsManager.APP_PREFERENCES_COMMANDS_NUM_BY_DEFAULT, value);
            CommandHistoryManager.CommandsLimit = value;
        }
    }
    private void setSortType(int type){
        settingsManager.setIntSetting(settingsManager.APP_PREFERENCES_SORT_ORDER,type);
    }

}
