package com.anatoly1410.editorapp.Presentation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.Domain.AutocompletionListAdapter;
import com.anatoly1410.editorapp.Domain.AutocompletionManager;
import com.anatoly1410.editorapp.Domain.BlockedTextExtractor;
import com.anatoly1410.editorapp.Domain.BlockedTextSpinnerAdapter;
import com.anatoly1410.editorapp.Domain.HighlightSyntaxManager;

import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IVisualStylesManager;
import com.anatoly1410.editorapp.Domain.MultiBuffer;
import com.anatoly1410.editorapp.Domain.MultiBufferListAdapter;
import com.anatoly1410.editorapp.Domain.Pair;
import com.anatoly1410.editorapp.Domain.QPythonScriptRunner;
import com.anatoly1410.editorapp.Domain.Snippet;
import com.anatoly1410.editorapp.Domain.SnippetManager;
import com.anatoly1410.editorapp.Domain.SnippetTreeListAdapter;
import com.anatoly1410.editorapp.Domain.SyntaxBlock;
import com.anatoly1410.editorapp.Domain.TabContent;
import com.anatoly1410.editorapp.Domain.MyApplication;
import com.anatoly1410.editorapp.Domain.TextChangeCommand;
import com.anatoly1410.editorapp.Domain.TextSyntaxRange;
import com.anatoly1410.editorapp.Presentation.Interfaces.IAutocompletionItemsKeeper;
import com.anatoly1410.editorapp.Presentation.Interfaces.IAutocompletionManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.IBlockedTextExtractor;
import com.anatoly1410.editorapp.Presentation.Interfaces.ICommandHistoryManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.IHelpManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.IHighlightSyntaxManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.IMultiBuffer;
import com.anatoly1410.editorapp.Presentation.Interfaces.IQPythonScriptRunner;
import com.anatoly1410.editorapp.Presentation.Interfaces.ISnippetManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.ITabManager;
import com.anatoly1410.editorapp.R;
import com.anatoly1410.editorapp.UtilityMethods;
import com.anatoly1410.editorapp.Presentation.slidinglayer.SlidingLayer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

/*Main activity class*/
public class MainActivity extends AppCompatActivity {

    @Inject
    ITabManager tabManager;
    @Inject
    IMultiBuffer multiBuffer;
    @Inject
    ISettingsManager settingsManager;
    @Inject
    public IVisualStylesManager visualStylesManager;
    @Inject
    public ISnippetManager snippetManager;
    @Inject
    public IHelpManager helpManager;
    @Inject
    public IQPythonScriptRunner qPythonScriptRunner;
    @Inject
    public IAutocompletionItemsKeeper keyWordsKeeper;
    @Inject
    public ICommandHistoryManager commandHistoryManager;
    @Inject
    public IHighlightSyntaxManager highlightSyntaxManager;
    @Inject
    IAutocompletionManager autocompletionManager;
    @Inject
    IBlockedTextExtractor blockedTextExtractor;

    private static MainActivity mActivity;
    public static MainActivity getMainActivity(){
        return mActivity;
    }
    // for debug only
    private final boolean reloadHelp = true;

    /*References to inner components*/
    private AutocompletionMenu mAutocompMenu;
    private CEditText mInnerEditText;
    private CScrollView mCScrollView;
    private boolean anotherActivityOpened = false;
    private TabPanel tabPanel;
    private Spinner mBlocksSpinner;

    /*Options menu items*/
    private final int MENU_CLOSE = 0;
    private final int MENU_RENAME = 1;
    private final int FRAGMENT_REMOVE = 2;
    private final int FRAGMENT_MODIFY = 3;
    private final int FRAGMENT_TO_SNIPPET = 8;
    private final int SNIPPET_REMOVE = 4;
    private final int SNIPPET_MODIFY = 5;
    private final int SNIPPET_FOLDER_REMOVE = 6;
    private final int SNIPPET_FOLDER_MODIFY = 7;

    /*Request codes to other activities*/
    private final int FRAGMENT_MODIFY_REQUEST_CODE = 1;

    /*Collection of last reg exp mathes (search panel)*/
    private ArrayList<Pair<Integer,Integer>> reg_mathes_buffer;

    /*Root snippet folder name*/
    private final String rootSnippetFolderName = "Snippets collection";

    private enum SlidingLayerTabsEnum{Multibuffer,Snippets, Symbols};
    SlidingLayerTabsEnum curSlidingLayerTab = SlidingLayerTabsEnum.Multibuffer;
    //TODO need tobe changed to dynamic loading

    private ScheduledExecutorService helpStatusUpdatingExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> helpStatusUpdating;
    private int helpStatusUpdatingDelay = 500;
    public Handler mHelpStatusHandler = new Handler();


    private int autocompMenuMaxWidth = 300;
    /*On creation:
    * - sets references to inner components
    * - inits AutocompletionItemsKeeper
    * - sets min height for codeEditLayout as a current screenSize
    * - sets parent activity for components
    * - hides AutocompletionMenu*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getDomainComponent().inject(this);

        Display display = getWindowManager().getDefaultDisplay();

        Point screenSize = new Point();
        display.getSize(screenSize);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(SettingsManager.APP_PREFERENCES, Context.MODE_PRIVATE);
        settingsManager.setSettings(preferences);

        highlightSyntaxManager.setEnabledState(settingsManager.getBooleanSetting(settingsManager.APP_PREFERENCES_HIGHLIGHT_BY_DEFAULT));
        autocompletionManager.setAutocompletionEnabled(settingsManager.getBooleanSetting(settingsManager.APP_PREFERENCES_AUTOCOMP_BY_DEFAULT));
        blockedTextExtractor.setEnabledState(settingsManager.getBooleanSetting(settingsManager.APP_PREFERENCES_EXTRACT_BLOCKS_BY_DEFAULT));

        mAutocompMenu = new AutocompletionMenu(this,null);
        mBlocksSpinner = (Spinner)findViewById(R.id.blocks_spinner);
        mAutocompMenu.setId(R.id.autocompMenu);
        mInnerEditText = (CEditText) findViewById(R.id.mainEdit);
        mInnerEditText.setParentActivity(this);
        MenuItem actionBarOpenButton = (MenuItem) findViewById(R.id.action_bar_open_button);

        FrameLayout codeEditLayout = (FrameLayout) findViewById(R.id.codeEditLayout);
        tabPanel = (TabPanel) findViewById(R.id.tabs_panel);
        codeEditLayout.addView(mAutocompMenu,-1);

        codeEditLayout.setMinimumHeight(screenSize.y);

        mCScrollView = (CScrollView) findViewById(R.id.codeScrollView);
        mCScrollView.setParentActivity(this);
        mAutocompMenu.setMenuVisibility(false);

        SlidingLayer slidingLayer = (SlidingLayer)findViewById(R.id.slidingLayer);

        multiBuffer.setParentActivity(this);
        mAutocompMenu.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                helpCallingFromAutocompMenu();
                return true;
            }
        });

        final ListView listView = (ListView) findViewById(R.id.multibuf_list);
        listView.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                registerForContextMenu(listView);
                openContextMenu(listView);
                return true;
            }
        });

        TextChangeCommand.setCEditText(mInnerEditText);
        TextChangeCommand.setCommandHistoryManager(commandHistoryManager);
        setHelpCatalog();
        helpManager.LoadHelp();
        updateCurrentSnippetFolderTextView();


        snippetManager.setItemViewCreationListener(new SnippetTreeListAdapter.ItemViewCreationListener() {
            @Override
            public void onItemViewCreation(View view) {
                registerForContextMenu(view);
            }
        });

        final ListView snippetsListView = (ListView) findViewById(R.id.snippets_list);
        snippetsListView.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                registerForContextMenu(snippetsListView);
                openContextMenu(snippetsListView);
                return true;
            }
        });

        initToggleButtons();
        setHScrollViewLeftIndent();
        setSlidingBarSize(screenSize);
        setInteraction();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    /*Subscribing to events between:
     * - CEditText and AutocompletionMenu(text changing, cursor position changing)
     * */
    private void setSlidingBarSize(Point screenSize){
        SlidingLayer slidingLayer = (SlidingLayer)findViewById(R.id.slidingLayer);
        int slidingBarWidth;
        if(screenSize.x < screenSize.y){
            slidingBarWidth = (int)(screenSize.x*0.75);
        }else{
            slidingBarWidth = (int)(screenSize.x*0.45);
        }

        slidingLayer.setLayoutParams(new FrameLayout.LayoutParams( slidingBarWidth,
                FrameLayout.LayoutParams.MATCH_PARENT));
    }

    private void setHScrollViewLeftIndent(){
        HorizontalScrollView horizontalScrollView
                = (HorizontalScrollView)findViewById(R.id.hCodeScrollView);
        HorizontalScrollView.LayoutParams lp
                = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        int leftIndent = visualStylesManager.getLeftCodeTextPadding();
        lp.setMargins(leftIndent,0,0,0);
        horizontalScrollView.setLayoutParams(lp);
    }

    private void setAdapters(){
        AutocompletionListAdapter autocompletionListAdapter;
        autocompletionListAdapter = new AutocompletionListAdapter(this, autocompletionManager.getSnippets());
        autocompletionListAdapter.setNotifyOnChange(true);

        AutocompletionMenu autocompletionMenu = (AutocompletionMenu)findViewById(R.id.autocompMenu);
        final ListView autocompListView = autocompletionMenu.getmAutocompListView();
        autocompListView.setAdapter(autocompletionListAdapter);

        autocompletionManager.setAutocompletionListAdapter(autocompletionListAdapter);
        autocompListView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                if(position < 0 || position >= adapter.getCount())
                {
                    return;
                }
                autocompletionManager.clickOnItem(position);
            }
        });
        autocompletionMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    int pos =  autocompListView.pointToPosition((int)event.getX(),(int)event.getY());
                    autocompletionManager.setLastClickedItem(pos);
                }
                return false;
            }
        });

        BlockedTextSpinnerAdapter blockedTextSpinnerAdapter;
        blockedTextSpinnerAdapter = new BlockedTextSpinnerAdapter(this, blockedTextExtractor.getDisplayedBlocks());
        blockedTextSpinnerAdapter.setNotifyOnChange(true);
        ArrayList<SyntaxBlock> ss = blockedTextExtractor.getDisplayedBlocks();
        int cc = blockedTextSpinnerAdapter.getCount();
        mBlocksSpinner.setAdapter(blockedTextSpinnerAdapter);
        blockedTextExtractor.setBlockedTextSpinnerAdapter(blockedTextSpinnerAdapter);

        final MultiBufferListAdapter multiBufferListAdapter = new MultiBufferListAdapter(this, multiBuffer.getFragments());
        multiBufferListAdapter.setNotifyOnChange(true);
        multiBuffer.setMultiBufferListAdapter(multiBufferListAdapter);

        final ListView listView = (ListView) findViewById(R.id.multibuf_list);
        listView.setAdapter(multiBufferListAdapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position,long id){
                if(position < 0 || position >= multiBufferListAdapter.getCount())
                {
                    return;
                }
                multiBuffer.pasteFragment(position);
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    multiBuffer.setLastClickedItem(listView.pointToPosition((int)event.getX(),(int)event.getY()));
                }
                return false;
            }
        });

        multiBuffer.setFragmentInsertionListener(new MultiBuffer.OnFragmentInsertionListener() {
            @Override
            public void fireEvent(String fragmentContent) {
                mInnerEditText.pasteTextInCurrentPosition(fragmentContent);
            }
        });

        final SnippetTreeListAdapter snippetTreeListAdapter = new SnippetTreeListAdapter(this, snippetManager.getViewedSnippets());
        snippetTreeListAdapter.setNotifyOnChange(true);
        snippetManager.setmSnippetTreeListAdapter(snippetTreeListAdapter);
        final ListView snippetListView = (ListView) findViewById(R.id.snippets_list);
        snippetListView.setAdapter(snippetTreeListAdapter);


        snippetListView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                if(position < 0 || position >= snippetTreeListAdapter.getCount())
                {
                    return;
                }
                snippetManager.clickOnItem(position);
            }
        });
        snippetListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    snippetManager.setLastClickedItem(snippetListView.pointToPosition((int)event.getX(),(int)event.getY()));
                }
                return false;
            }
        });
    }
    private void setInteraction() {
        setAdapters();

        mInnerEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean handled = !(mInnerEditText.getProgrammChangeFlag()
                        || !mInnerEditText.isTextChangeHandlingEnabled()
                        || !commandHistoryManager.isCommandHistoryWritingEnabled());

                        tabManager.setContentForOpenedTab(mInnerEditText.getText().toString(),
                        handled);
                if(mInnerEditText.isTextChangeHandlingEnabled())
                {
                    PointF pos = mInnerEditText.getCursorCoordinates();
                    setAutocompletion(mInnerEditText.getCurrentWord());
                    mAutocompMenu.setPosition(pos, mInnerEditText.getLineHeight(), mCScrollView.getHeight(), mCScrollView.getScrollY());
                }

                commandHistoryManager.decCommandHistoryWritingEnabled();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mInnerEditText.setCursorPosChListener(new CEditText.CursorPosChangedListener() {
            @Override
            public void onCursorPosChanged() {
                int cursorPos = mInnerEditText.getSelectionStart();
                tabManager.setCursorPosForOpenedTab(cursorPos);
                blockedTextExtractor.onCursorPosChanged(cursorPos);
                updateHelpStatus();
            }
        });
        mBlocksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                blockedTextExtractor.onBlockSpinnerItemSelection(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
        mBlocksSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    blockedTextExtractor.setSpinnerIsTouchedFlag(true);
                }
                return false;
            }
        });
        mCScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                                       int oldBottom) {
                // its possible that the layout is not complete in which case
                // we will get all zero values for the positions, so ignore the event
                if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                    return;
                }
                if(mAutocompMenu == null)
                    return;

                int autocHeight = (bottom - top)/2;
                mAutocompMenu.setHeight(autocHeight);
                int autocWidth = (right - left)/2;
                if(autocWidth > autocompMenuMaxWidth){
                    autocWidth = autocompMenuMaxWidth;
                }

                mAutocompMenu.setWidth(autocWidth);
                autocompletionManager.setMenuWidth(autocWidth);
            }
        });

        final SlidingLayer slidingLayer = (SlidingLayer)findViewById(R.id.slidingLayer);
        LinearLayout bottom_but = (LinearLayout)findViewById(R.id.bottom_button_panel);
        registerForContextMenu(bottom_but);
        registerForContextMenu(tabPanel);
        registerForContextMenu(slidingLayer);
        slidingLayer.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                openContextMenu(slidingLayer);
                return true;
            }

        });
        registerForContextMenu(slidingLayer);

        snippetManager.setViewedSnippetListUpdatingListener(new SnippetManager.OnViewedSnippetListUpdatingListener() {
            public void fireEvent() {
                updateCurrentSnippetFolderTextView();
            }
        });

        tabManager.setTabOpeningListener(new ITabManager.OnTabOpeningListener() {
            @Override
            public void fireEvent(String content, int position) {

                setCEditText(content,position);
            }
        });
        final Activity curActivity = this;
        tabManager.setWarningMessageEventListener(new ITabManager.OnWarningMessageEventListener() {
            @Override
            public void fireEvent(String messageContent) {
                String warning = getResources().getString(R.string.warningString);
                UtilityMethods.showOKInfoMessage(warning,messageContent,curActivity);
            }
        });
        snippetManager.setLoadSnippetsListener(new SnippetManager.OnLoadSnippetsListener() {
            @Override
            public void fireEvent() {
                keyWordsKeeper.loadFromSnippetManager();
            }
        });
        snippetManager.setSnippetInsertionListener(new SnippetManager.OnSnippetInsertionListener() {
            @Override
            public void fireEvent(String snippetContent) {
                mInnerEditText.pasteTextInCurrentPosition(snippetContent);
            }
        });

        highlightSyntaxManager.setSyntaxHighlightListener(new HighlightSyntaxManager.OnSyntaxHighlightListener() {
            @Override
            public void fireEvent(ArrayList<TextSyntaxRange> textRanges, int start, int end) {
                mInnerEditText.setSyntaxRanges(textRanges,start,end);
            }
        });
        autocompletionManager.setSnippetSelectionListener(new AutocompletionManager.OnSnippetSelectionListener() {
            @Override
            public void fireEvent(String snippetContent, int cursorOffset) {
                int start_pos = mInnerEditText.getCurrentWordBegin();
                mInnerEditText.InsertInCurrentPosition(snippetContent,true);
                if(cursorOffset >= 0){
                    mInnerEditText.setSelection(start_pos + cursorOffset);
                }
            }
        });
        blockedTextExtractor.setChangeSelectionPosFromSpinnerListener(new BlockedTextExtractor.OnChangeSelectionPosFromSpinnerListener() {
            @Override
            public void fireEvent(int pos) {
                mInnerEditText.setSelection(pos);
            }
        });

        blockedTextExtractor.setSpinnerSelectedItemChangeListener(new BlockedTextExtractor.OnSpinnerSelectedItemChangeListener() {
            @Override
            public void fireEvent(int itemPos) {
                mBlocksSpinner.setSelection(itemPos);
            }
        });
        blockedTextExtractor.setChangeSelectionPosFromSpinnerListener(new BlockedTextExtractor.OnChangeSelectionPosFromSpinnerListener() {
            @Override
            public void fireEvent(int pos) {
                mInnerEditText.setSelection(pos);
            }
        });

        qPythonScriptRunner.setQPythonActivityStartinListener(new QPythonScriptRunner.OnQPythonActivityStartinListener() {
            @Override
            public void fireEvent(Intent intent, int requestCode) {
                String codeExtraStringName = qPythonScriptRunner.getCodeIntentExtra();
                String code =  mInnerEditText.getText().toString();
                intent.putExtra(codeExtraStringName, code);
                startActivityForResult(intent, requestCode);
            }
        });
        qPythonScriptRunner.setActivityStartingListener(new QPythonScriptRunner.OnActivityStartingListener() {
            @Override
            public void fireEvent(Intent intent) {
                startActivity(intent);
            }
        });
        qPythonScriptRunner.setShowMessageListener(new QPythonScriptRunner.OnShowMessageListener() {
            @Override
            public void fireEvent(String message) {
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        });

        visualStylesManager.addStyleChangedListener(new IVisualStylesManager.OnStyleChangedListener() {
            @Override
            public void onStyleChanged() {
                HorizontalScrollView hScrollView
                        = (HorizontalScrollView)findViewById(R.id.hCodeScrollView);
                CScrollView cScrollView
                        = (CScrollView)findViewById(R.id.codeScrollView);
                CodeEditTextLayout codeEditTextLayout = (CodeEditTextLayout)findViewById(R.id.codeEditText);
                HorizontalScrollView.LayoutParams lp
                        = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                int leftIndent = visualStylesManager.getLeftCodeTextPadding();
                lp.setMargins(leftIndent,0,0,0);
                hScrollView.setLayoutParams(lp);
                cScrollView.setRowNumbersSize(visualStylesManager.getCodeRowNumberSize());
                codeEditTextLayout.setVerticalLineIndent(visualStylesManager.getLeftIndent());
            }
        });
    }

    private void initToggleButtons(){
       boolean autocompEnabled = autocompletionManager.getAutocompletionEnabled();
        ImageButton autocButton = (ImageButton) findViewById(R.id.autoc_menu_toggle_but);
        if(autocompEnabled){
            autocButton.setImageResource(R.mipmap.ic_autoc_menu);
        }else{
            autocButton.setImageResource(R.mipmap.ic_autoc_menu_inactive);
        }

        boolean highlightSyntaxEnabled = highlightSyntaxManager.isEnabled();
        ImageButton highlightSyntaxButton = (ImageButton) findViewById(R.id.highlight_syntax_toggle_but);
        if(highlightSyntaxEnabled){
            highlightSyntaxButton.setImageResource(R.mipmap.ic_highlight_syntax);
        }else{
            highlightSyntaxButton.setImageResource(R.mipmap.ic_highlight_syntax_inactive);
        }

    }
    /*Shows AutocompletionMenu for current word if corresponding snippets were found*/
    private void setAutocompletion(String currentPrintedWord)
    {
        if(!autocompletionManager.getAutocompletionEnabled()){
            mAutocompMenu.setMenuVisibility(false);
            return;
        }
        Snippet[] snippets = keyWordsKeeper.getWordsForPrefix(currentPrintedWord);
        if(snippets.length > 0)
        {
            autocompletionManager.setMenuContent(snippets);
            mAutocompMenu.setMenuVisibility(true);
        }else{
            mAutocompMenu.setMenuVisibility(false);
        }
    }
    /*Context menu creation handler*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        String fileName = UtilityMethods.getFileNameFromPath(tabManager.getOpenedTab().getPath());
        if(v.getId() == R.id.tabs_panel){
            menu.setHeaderTitle(fileName);
        }

        switch (v.getId()) {
            case R.id.tabs_panel:
                String closeItem = getResources().getString(R.string.closeOptionCMenu);
                String renameItem = getResources().getString(R.string.renameFileOptionCMenu);
                menu.add(0, MENU_CLOSE, 0, closeItem);
                menu.add(0, MENU_RENAME, 0, renameItem);

                ITabManager fm = tabManager;
                if(tabPanel.getChildCount() > 1 || !fm.getOpenedTab().getPath().equals("")){
                    menu.getItem(MENU_CLOSE).setEnabled(true);
                }else{
                    menu.getItem(MENU_CLOSE).setEnabled(false);
                }

                if(tabManager.getOpenedTab() == null) {

                    menu.getItem(MENU_RENAME).setEnabled(false);
                }else if(tabManager.getOpenedTab().getPath().equals("")){
                    menu.getItem(MENU_RENAME).setEnabled(false);
                } else {
                    menu.getItem(MENU_RENAME).setEnabled(true);
                }
                break;
            case R.id.slidingLayer:
                if(curSlidingLayerTab == SlidingLayerTabsEnum.Multibuffer){
                    String removeItem = getResources().getString(R.string.removeFragmentOptionCMenu);
                    String modifyItem = getResources().getString(R.string.modifyFragmentOptionCMenu);
                    String fragmentToSnippet = getResources().getString(R.string.convertFragmentToSnippetOptionCMenu);

                    menu.add(0, FRAGMENT_REMOVE, 0, removeItem);
                    menu.add(0, FRAGMENT_MODIFY, 0, modifyItem);
                    menu.add(0, FRAGMENT_TO_SNIPPET, 0, fragmentToSnippet);
                }else if(curSlidingLayerTab == SlidingLayerTabsEnum.Snippets){
                    if(!snippetManager.isLastSelectedItemIsFolder()){
                        String removeSnippet = getResources().getString(R.string.removeSnippetOptionCMenu);
                        String modifySnippet = getResources().getString(R.string.modifySnippetOptionCMenu);
                        menu.add(0, SNIPPET_REMOVE, 0, removeSnippet);
                        menu.add(0, SNIPPET_MODIFY, 0, modifySnippet);
                    }else{
                        String removeFolder = getResources().getString(R.string.removeSnippetFolderOptionCMenu);
                        String modifyFolder = getResources().getString(R.string.modifySnippetFolderOptionCMenu);
                        menu.add(0, SNIPPET_FOLDER_REMOVE, 0, removeFolder);
                        menu.add(0, SNIPPET_FOLDER_MODIFY, 0, modifyFolder);
                    }
                }
                break;
        }
    }
    /*Context menu item selection handler*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CLOSE:
                closeOpenedTab();
                break;
            case MENU_RENAME:
                renameOpenedTab();
                break;
            case FRAGMENT_REMOVE:
                int snippet_idx = multiBuffer.getLastClickedItem();
                if(snippet_idx >= 0){
                    multiBuffer.removeFragment(snippet_idx);
                }
                break;
            case FRAGMENT_MODIFY:
                Intent intent = new Intent(MainActivity.this, SnippetActivity.class);
                anotherActivityOpened = true;
                int idx = multiBuffer.getLastClickedItem();
                String fragment_content = multiBuffer.getFragmentByIdx(idx);
                Bundle b = new Bundle();
                b.putString("mode", "modifyBufferFragment");
                b.putString("content", fragment_content);
                b.putInt("fragment_idx", idx);
                intent.putExtras(b);
                startActivity(intent);
                break;
            case FRAGMENT_TO_SNIPPET:
                idx = multiBuffer.getLastClickedItem();
                fragment_content = multiBuffer.getFragmentByIdx(idx);

                intent = new Intent(MainActivity.this, SnippetActivity.class);
                anotherActivityOpened = true;

                b = new Bundle();
                b.putString("mode", "createSnippet");
                b.putString("content", fragment_content);

                intent.putExtras(b);
                startActivity(intent);
                break;
            case SNIPPET_MODIFY:
                intent = new Intent(MainActivity.this, SnippetActivity.class);
                anotherActivityOpened = true;
                idx = snippetManager.getLastClickedItem();
                if(snippetManager.isLastSelectedItemIsFolder()){

                }else {
                    b = new Bundle();
                    b.putString("mode", "modifySnippet");
                    b.putInt("snippet_idx", idx);
                    intent.putExtras(b);
                    startActivity(intent);
                }
                break;
            case SNIPPET_FOLDER_MODIFY:
                showRenameSnippetFolderDialog();
                break;
            case SNIPPET_REMOVE:
                idx = snippetManager.getLastClickedItem();
                snippetManager.removeViewedSnippetByIdx(idx);
                keyWordsKeeper.init();
                break;
            case SNIPPET_FOLDER_REMOVE:
                idx = snippetManager.getLastClickedItem();
                snippetManager.removeViewedSnippetByIdx(idx);
                keyWordsKeeper.init();
                break;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_action_bar_menu, menu);
        return true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        tabManager.saveState();
    }
    @Override
    protected void onResume() {
        super.onResume();
        String fileName = getResources().getString(R.string.tabsFileName);
        TabPanel tabPanel = (TabPanel) findViewById(R.id.tabs_panel);
        if(!anotherActivityOpened)
        {
            ITabManager fm = tabManager;

            boolean resumeSuccess = tabManager.resumeTabsState(getFilesDir().getAbsolutePath());
            if(!resumeSuccess){
                addEmptyTab();
            }

            tabManager.setTabPanel(tabPanel);
        }
        addTabViewsFromtabManager(tabPanel);
        tabPanel.invalidate();
        anotherActivityOpened = false;
        MyApplication.firstMainActivityInit = false;
    }
    /*Adds empty tab with empty path*/
    private void addEmptyTab()
    {
        ITabManager fm = tabManager;
        TabContent tabContent = fm.addTab("","");
        fm.openTab(tabContent);
        Tab tab = addTab(tabContent.getHeader());
        tabContent.setTabView(tab);
        tabPanel.openTab(tab);
    }
    /*Adds tab views to tabPanel according to TabContent collection in tabManager*/
    private void addTabViewsFromtabManager(TabPanel tabPanel)
    {
        tabPanel.removeAllViews();
        ITabManager fm = tabManager;
        ArrayList<TabContent> tabs = fm.getTabs();
        TabContent openedTab = fm.getOpenedTab();
        for(TabContent tab:tabs)
        {
            Tab newTabView = tabPanel.addTab(tab.getHeader());
            if(tab.equals(openedTab))
            {
                newTabView.open();
            }
            tab.setTabView(newTabView);
        }
    }
    /*Starts new OpenOrSaveFileActivity*/
    public void onOpenActionClick(MenuItem view)
    {
        Intent intent = new Intent(MainActivity.this, OpenOrSaveFileActivity.class);
        anotherActivityOpened = true;
        Bundle b = new Bundle();
        b.putBoolean("isSaving", false);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }
    /*Starts new OpenOrSaveFileActivity for file creation*/
    public void onCreateActionClick(MenuItem view)
    {
        addEmptyTab();
        Intent intent = new Intent(MainActivity.this, OpenOrSaveFileActivity.class);
        anotherActivityOpened = true;
        Bundle b = new Bundle();
        b.putBoolean("isSaving", true);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }
    /*Starts new SettingsActivity for settings adjusting*/
    public void onSettingsActionClick(MenuItem view)
    {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        anotherActivityOpened = true;
        startActivity(intent);
    }
    /*Starts new AboutActivity for about info displaying*/
    public void onAboutActionClick(MenuItem view)
    {

        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        anotherActivityOpened = true;
        startActivity(intent);
    }
    /*Sets value and cursor position for CEditText*/
    public void setCEditText(String text,int cursorPosition)
    {
        commandHistoryManager.incCommandHistoryWritingEnabled(2);
        highlightSyntaxManager.stopHighlighting();
        mInnerEditText.setText(text);
        if(cursorPosition > text.length())
        {
            cursorPosition = 0;
        }
        if(text.length() > 0)
        {
            mInnerEditText.setSelection(cursorPosition);
        }
    }
    /*Adds new tab*/
    public Tab addTab(String name)
    {
        TabPanel tabPanel = (TabPanel) findViewById(R.id.tabs_panel);
        return tabPanel.addTab(name);
    }
    /*Save button click handler*/
    public void onSaveButtonClick(View v) {
        ITabManager fm = tabManager;
        if(fm.getOpenedTab() == null)
            return;
        if(!fm.getOpenedTab().getPath().equals("")) {
            fm.saveOpenedTabAsExistingFile();
        }else{
            Intent intent = new Intent(MainActivity.this, OpenOrSaveFileActivity.class);
            anotherActivityOpened = true;
            Bundle b = new Bundle();
            b.putBoolean("isSaving", true);
            b.putString("fileContent", fm.getOpenedTab().getContent());
            intent.putExtras(b);
            startActivity(intent);
        }
    }
    /*Remove indent button click handler*/
    public void onRemoveIndentButtonClick(View v) {
        mInnerEditText.removeIntent();
    }
    /*Add indent button click handler*/
    public void onAddIndentButtonClick(View v) {
        mInnerEditText.addIndent();
    }
    public void onSearchPanelClosing(View v){
        LinearLayout searchPanel = (LinearLayout)findViewById(R.id.search_panel);
        LinearLayout bottomButtonPanel = (LinearLayout)findViewById(R.id.bottom_button_panel);

        searchPanel.setVisibility(View.GONE);
        bottomButtonPanel.setVisibility(View.VISIBLE);
    }
    public void onAddMultilineComment(View v){
        mInnerEditText.commentSelectedBlock();
    }
    public void onRemoveMultilineComment(View v){
        mInnerEditText.uncommentSelectedBlock();
    }

    public void onSearchPanelOpening(View v){
        LinearLayout searchPanel = (LinearLayout)findViewById(R.id.search_panel);
        LinearLayout bottomButtonPanel = (LinearLayout)findViewById(R.id.bottom_button_panel);
        EditText searchEditText = (EditText)findViewById(R.id.searchEdit);

        searchPanel.setVisibility(View.VISIBLE);
        bottomButtonPanel.setVisibility(View.GONE);
        searchEditText.requestFocus();
    }
    /*Open dilaog for opened tab renaming*/
    private void renameOpenedTab()
    {
        ITabManager fm = tabManager;
        final TabContent openedTab = fm.getOpenedTab();
        if(openedTab == null)
        {
            return;
        }

        if(openedTab.getPath() != "")
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String fileName = UtilityMethods.getFileNameFromPath(tabManager.getOpenedTab().getPath());
            String nonSavedMsg = getResources().getString(R.string.nonSavedMsg);
            builder.setTitle(this.getResources().getString(R.string.renameFileDialogMsg));
            final EditText inputEditText = new EditText(MainActivity.this);
            inputEditText.setText(fileName);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            inputEditText.setLayoutParams(lp);
            inputEditText.setSingleLine();
            builder.setView(inputEditText);
            final Toast toastForIncorrectFileName = Toast.makeText(this,R.string.incorrectFileNameMsgError,Toast.LENGTH_SHORT);
            builder.setPositiveButton(R.string.renameButtonLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String newFileName = inputEditText.getText().toString();
                    if(checkNewFileName(newFileName))
                    {
                        renameOpenedTable(newFileName);
                        dialog.dismiss();
                    }else{getResources().getString(R.string.incorrectFileNameMsgError);
                        toastForIncorrectFileName.show();
                    }
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            closeOpenedFile(openedTab);
        }
    }

    public void onRemoveViewedSnippets(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String nonSavedMsg = getResources().getString(R.string.nonSavedMsg);
        builder.setTitle(this.getResources().getString(R.string.warningString));
        String text = this.getResources().getString(R.string.removeViewedSnippetsQuestion);

        builder.setTitle(text);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                snippetManager.clearViewedSnippets();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /*Checks if file name is valid*/
    private boolean checkNewFileName(String fileName)
    {
        if(fileName.equals(""))
        {
            return false;
        }
        return true;
    }
    private void renameOpenedTable(String newName) {
        ITabManager fm = tabManager;
        TabContent openedTab = fm.getOpenedTab();
        File file = new File(openedTab.getPath());
        String parentDir = UtilityMethods.getParentDirectoryPath(openedTab.getPath());
        try
        {
            String newPath = parentDir + "/" + newName;
            file.renameTo(new File(newPath));
            openedTab.setPath(newPath);
            openedTab.getTab().invalidate();
        }catch(Exception e)
        {
            Toast.makeText(this,R.string.incorrectFileNameMsgError,Toast.LENGTH_SHORT).show();
        }
    }
    /*Closes opened tab*/
    private void closeOpenedTab()
    {
        ITabManager fm = tabManager;
        final TabContent openedTab = fm.getOpenedTab();
        if(openedTab == null)
        {
            return;
        }
        if(openedTab.isNonSaved())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String fileName = UtilityMethods.getFileNameFromPath(tabManager.getOpenedTab().getPath());
            if(fileName.equals(""))
            {
                fileName = getResources().getString(R.string.defaultFileName);
            }
            String nonSavedMsg = getResources().getString(R.string.nonSavedMsg);
            builder.setTitle(fileName+": "+ nonSavedMsg);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    closeOpenedFile(openedTab);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            closeOpenedFile(openedTab);
        }
    }
    /*Closes opened file*/
    private void closeOpenedFile(TabContent tabContent)
    {
        ITabManager fm = tabManager;
        tabPanel.removeTab(tabContent.getTab());
        fm.removeTab(tabContent);
        if(fm.getTabs().size() < 1)
        {
            addEmptyTab();
        }else
        {
            TabContent remainTabContent = fm.getTabs().get(0);
            tabPanel.openTab(remainTabContent.getTab());
            fm.openTab(remainTabContent);
        }
    }
    /*Context menu option selected handler*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onCommandUndoButtonClick(View v) {
        commandHistoryManager.undoCommand();
    }
    public void onCommandRedoButtonClick(View v) {
        commandHistoryManager.redoCommand();
    }
    public void onSearchSettingsOpening(View v){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.search_settings);
        dialog.setTitle(R.string.search_settings_title);

        Button closeButton = (Button)dialog.findViewById(R.id.search_settings_close_but);
        final CheckBox checkBoxWords = (CheckBox)dialog.findViewById(R.id.search_setts_chbx_words);
        final CheckBox checkBoxMatchCase = (CheckBox)dialog.findViewById(R.id.search_setts_chbx_match_case);
        final CheckBox checkBoxLoop  = (CheckBox)dialog.findViewById(R.id.search_setts_chbx_loop_search);
        final CheckBox checkBoxRegExp  = (CheckBox)dialog.findViewById(R.id.search_setts_chbx_reg_exp);

        checkBoxWords.setChecked(settingsManager.getBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_BAR_WORDS));
        checkBoxMatchCase.setChecked(settingsManager.getBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_BAR_MATCH_CASE));
        checkBoxLoop.setChecked(settingsManager.getBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_BAR_LOOP));
        checkBoxRegExp.setChecked(settingsManager.getBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_REG_EXP));

        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                settingsManager.setBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_BAR_WORDS, checkBoxWords.isChecked());
                settingsManager.setBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_BAR_MATCH_CASE, checkBoxMatchCase.isChecked());
                settingsManager.setBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_BAR_LOOP, checkBoxLoop.isChecked());
                settingsManager.setBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_REG_EXP, checkBoxRegExp.isChecked());

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void helpCallingFromAutocompMenu(){
        String lastSelectedItemAlias = autocompletionManager.getSelectedItemAlias();
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        anotherActivityOpened = true;
        Bundle b = new Bundle();

        String curWord = lastSelectedItemAlias;
        b.putString("alias", curWord);

        intent.putExtras(b);
        startActivity(intent);
    }

    public void onHelpCalling(View v) {
        if(!helpManager.HelpIsLoaded()){
            Toast.makeText(this, R.string.helpIsNotLoaded, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        anotherActivityOpened = true;
        Bundle b = new Bundle();

        String curWord = mInnerEditText.grabCurrentWord();
        b.putString("alias", curWord);

        intent.putExtras(b);
        startActivity(intent);
    }
    public void onAutocomplMenuToggle(View v){
        boolean enabled = autocompletionManager.getAutocompletionEnabled();
        autocompletionManager.setAutocompletionEnabled(!enabled);
        ImageButton autocButton = (ImageButton) findViewById(R.id.autoc_menu_toggle_but);
        if(!enabled){
            autocButton.setImageResource(R.mipmap.ic_autoc_menu);
        }else{
            autocButton.setImageResource(R.mipmap.ic_autoc_menu_inactive);
        }
    }
    public void onHighlightSyntaxToggle(View v){
        boolean enabled = highlightSyntaxManager.isEnabled();
        highlightSyntaxManager.setEnabledState(!enabled);
        ImageButton highlightSyntaxButton = (ImageButton) findViewById(R.id.highlight_syntax_toggle_but);
        if(!enabled){
            highlightSyntaxButton.setImageResource(R.mipmap.ic_highlight_syntax);
        }else{
            highlightSyntaxButton.setImageResource(R.mipmap.ic_highlight_syntax_inactive);
        }
    }
    public void onGotoLine(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getResources().getString(R.string.gotoLineDialogTitle));
        final EditText inputEditText = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        final Toast toastForIncorrectLineNumber = Toast.makeText(this,R.string.toastIncorrectLineNumber,Toast.LENGTH_SHORT);
        inputEditText.setLayoutParams(lp);
        inputEditText.setSingleLine();
        builder.setView(inputEditText);
        builder.setPositiveButton(R.string.gotoDialogConfirmButtonLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    int line_num = Integer.parseInt(inputEditText.getText().toString());
                    boolean res = mInnerEditText.gotoLine(line_num);
                    if(res) {
                        dialog.dismiss();
                    }else{
                        toastForIncorrectLineNumber.show();
                    }
                }catch(Exception e){
                    toastForIncorrectLineNumber.show();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onNextSearchCase(View v){
        EditText searchedText = (EditText)findViewById(R.id.searchEdit);
        mInnerEditText.requestFocus();
        SearchText(searchedText.getText().toString(),true);
    }

    public void onPrevSearchCase(View v){
        EditText searchedText = (EditText)findViewById(R.id.searchEdit);
        mInnerEditText.requestFocus();
        SearchText(searchedText.getText().toString(),false);
    }

    public void onMultibufferClearing(View v) {
        multiBuffer.clearFragments();
    }

    public void onMultibufferTabOpening(View v) {
        LinearLayout multibufTab = (LinearLayout) findViewById(R.id.multibuf_tab);
        LinearLayout snippetsTab = (LinearLayout) findViewById(R.id.snippets_tab);
        LinearLayout symbolsTab = (LinearLayout) findViewById(R.id.symbols_tab);
        multibufTab.setVisibility(View.VISIBLE);
        snippetsTab.setVisibility(View.GONE);
        symbolsTab.setVisibility(View.GONE);
        curSlidingLayerTab = SlidingLayerTabsEnum.Multibuffer;
    }

    public void onSnippetTabOpening(View v) {
        LinearLayout multibufTab = (LinearLayout) findViewById(R.id.multibuf_tab);
        LinearLayout snippetsTab = (LinearLayout) findViewById(R.id.snippets_tab);
        LinearLayout symbolsTab = (LinearLayout) findViewById(R.id.symbols_tab);
        multibufTab.setVisibility(View.GONE);
        snippetsTab.setVisibility(View.VISIBLE);
        symbolsTab.setVisibility(View.GONE);
        curSlidingLayerTab = SlidingLayerTabsEnum.Snippets;
    }
    public void onSymbolTabOpening(View v) {
        LinearLayout multibufTab = (LinearLayout) findViewById(R.id.multibuf_tab);
        LinearLayout snippetsTab = (LinearLayout) findViewById(R.id.snippets_tab);
        LinearLayout symbolsTab = (LinearLayout) findViewById(R.id.symbols_tab);
        multibufTab.setVisibility(View.GONE);
        snippetsTab.setVisibility(View.GONE);
        symbolsTab.setVisibility(View.VISIBLE);
        curSlidingLayerTab = SlidingLayerTabsEnum.Symbols;
    }

    public void SearchText(String text, boolean forward){
        boolean reg_exp = settingsManager.getBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_REG_EXP);
        boolean words = settingsManager.getBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_BAR_WORDS);
        boolean match_case = settingsManager.getBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_BAR_MATCH_CASE);
        boolean loop = settingsManager.getBooleanSetting(SettingsManager.APP_PREFERENCES_SEARCH_BAR_LOOP);

        int selectionStart = mInnerEditText.getSelectionStart();
        int selectionEnd = mInnerEditText.getSelectionEnd();

        String editText = mInnerEditText.getText().toString();

        if(!match_case){
            editText = editText.toLowerCase();
            text = text.toLowerCase();
        }

        Pair <Integer, Integer> match;

        if(forward){
            if(selectionEnd == editText.length() - 1){
                match = new Pair(-1,0);
            }else {
                match = getIndexOf(editText.substring(selectionEnd), text,0, reg_exp, forward);
            }
            if(match.first == -2){
                Toast.makeText(this, R.string.search_reg_exp_parsing_error, Toast.LENGTH_SHORT).show();
            }else if(match.first >= 0){
                match.first+=selectionEnd;

                if(!words || words && UtilityMethods.isTextIsWord(editText, match.first,match.first + match.second))
                    mInnerEditText.setSelection(match.first, match.first + match.second);
                else
                    Toast.makeText(this, R.string.search_no_matches, Toast.LENGTH_SHORT).show();
            }else if(loop){
                match = getIndexOf(editText.substring(0,selectionStart), text,0, reg_exp, forward);
                if(match.first == -2){
                    Toast.makeText(this, R.string.search_reg_exp_parsing_error, Toast.LENGTH_SHORT).show();
                }else if(match.first >= 0 && (!words || words && UtilityMethods.isTextIsWord(editText, match.first,match.first + match.second)))
                    mInnerEditText.setSelection(match.first, match.first + match.second);
                else
                    Toast.makeText(this, R.string.search_no_matches, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, R.string.search_no_matches, Toast.LENGTH_SHORT).show();
            }
        }else{
            if(selectionStart == 0){
                match = new Pair(-1,0);
            }else {
                match = getIndexOf(editText.substring(0, selectionStart), text,0, reg_exp, forward);
            }
            if(match.first == -2){
                Toast.makeText(this, R.string.search_reg_exp_parsing_error, Toast.LENGTH_SHORT).show();
            }else if(match.first >= 0){
                if(!words || words && UtilityMethods.isTextIsWord(editText, match.first,match.first + match.second))
                    mInnerEditText.setSelection(match.first, match.first + match.second);
                else
                    Toast.makeText(this, R.string.search_no_matches, Toast.LENGTH_SHORT).show();
            }else if(loop){
                match = getIndexOf(editText.substring(selectionEnd), text,0, reg_exp, forward);
                if(match.first == -2){
                    Toast.makeText(this, R.string.search_reg_exp_parsing_error, Toast.LENGTH_SHORT).show();
                }else if(match.first >= 0) {
                    match.first += selectionEnd;

                    if (!words || words && UtilityMethods.isTextIsWord(editText, match.first, match.first + match.second))
                        mInnerEditText.setSelection(match.first, match.first + match.second);
                    else
                        Toast.makeText(this, R.string.search_no_matches, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, R.string.search_no_matches, Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, R.string.search_no_matches, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private Pair<Integer, Integer> getIndexOf(String text, String searched, int startIdx, boolean isRegular, boolean isForward){
        if(!isRegular){
            if(isForward){
                return new Pair(text.indexOf(searched,startIdx),searched.length());
            }else{
                text = text.substring(startIdx);
                return new Pair(text.lastIndexOf(searched),searched.length());
            }
        }else{
            text = text.substring(startIdx);
            Pattern regExpPattern;
            ArrayList<Pair<Integer, Integer>> matches;
            try {
                regExpPattern = Pattern.compile(searched);
            }catch(PatternSyntaxException exc){
                return new Pair(-2, 0);
            }
            matches = new ArrayList<>();
            Matcher matcher = regExpPattern.matcher(text);
            while(matcher.find()){
                matches.add(new Pair(matcher.start(), matcher.end()));
            }

            if(matches.size() == 0){
                return new Pair(-1, 0);
            }
            if(isForward){
                Pair<Integer, Integer> match =  matches.get(0);
                return  new Pair(match.first, match.second - match.first);
            }else{
                Pair<Integer, Integer> match =  matches.get(matches.size() - 1);
                return  new Pair(match.first, match.second - match.first);
            }
        }
    }
    public void onRunScript(View v) {
        qPythonScriptRunner.onQPyExec();
    }

    public void onSnippetHierarchyBack(View v) {
        snippetManager.goBackFromCurrentFolder();
        updateCurrentSnippetFolderTextView();
    }

    public void onNewSnippetFolderCreation(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getResources().getString(R.string.createSnippetFolderDialogTitle));
        final EditText inputEditText = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        final Toast toastForTooLongNameNumber = Toast.makeText(this,R.string.createSnippetFolderConfirmButtonLabel,Toast.LENGTH_SHORT);
        inputEditText.setLayoutParams(lp);
        inputEditText.setSingleLine();
        builder.setView(inputEditText);
        builder.setPositiveButton(R.string.createString, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    String name = inputEditText.getText().toString();
                    if(name.length() <= 50) {
                        snippetManager.addSnippetFolder(name);
                        dialog.dismiss();
                    }else{
                        toastForTooLongNameNumber.show();
                    }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateCurrentSnippetFolderTextView(){
        String currentFolderName = snippetManager.getCurrentSnippetFolderName();
        TextView curFolderEditText = (TextView)findViewById(R.id.cur_snippet_folder);
        if(currentFolderName == null){
            curFolderEditText.setText(rootSnippetFolderName);
        }else{
            curFolderEditText.setText(currentFolderName);
        }
    }

    public void onNewSnippetCreation(View v) {
        Intent intent = new Intent(MainActivity.this, SnippetActivity.class);
        anotherActivityOpened = true;

        int selStart = mInnerEditText.getSelectionStart();
        int selEnd = mInnerEditText.getSelectionEnd();
        String text = mInnerEditText.getText().toString();
        String snippet_content = "";
        if(selStart != selEnd){
            snippet_content = text.substring(selStart, selEnd);
        }
        Bundle b = new Bundle();
        b.putString("mode", "createSnippet");
        b.putString("content", snippet_content);

        intent.putExtras(b);
        startActivity(intent);
    }
    public void onMultibufToggle(View v){
        boolean enabled = multiBuffer.getBufferUpdatingEnabled();
        multiBuffer.setBufferUpdatingEnabled(!enabled);
        ImageButton button = (ImageButton)findViewById(R.id.multibuf_toggle_but);
        if(enabled){
            button.setImageResource(R.mipmap.ic_multibuf_inactive);
        }else{
            button.setImageResource(R.mipmap.ic_multibuf_active);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case  QPythonScriptRunner.SCRIPT_EXEC_PY:
                if (data!=null) {
                    Bundle bundle = data.getExtras();
                    String flag = bundle.getString("flag"); // flag you set
                    String param = bundle.getString("param"); // param you set
                    String result = bundle.getString("result"); // Result your Pycode generate
                    Toast.makeText(this, "onQPyExec: return ("+result+")", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "onQPyExec: program returned no data", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean unpackZipFromResourses(int resourse_id, String destinationPath)
    {
        destinationPath+="/";
        InputStream is;
        ZipInputStream zis;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        }
        try
        {
            String filename;
            is =  getResources().openRawResource(resourse_id);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;

            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();
                if (ze.isDirectory()) {
                    File fmd = new File(destinationPath + filename);
                    fmd.mkdirs();
                    continue;
                }
                FileOutputStream fout = new FileOutputStream(destinationPath + filename);
                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }
                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }


    public void setHelpCatalog(){
        String helpPath =  "/sdcard/com.anatoly1410.editorapp/help";
        File file = new File(helpPath);
        if(!file.exists() || reloadHelp){
            File helpDir = new File(helpPath);
            helpDir.mkdirs();
            try {
                boolean res = unpackZipFromResourses(R.raw.help, helpPath);
            }catch(Exception e){
            }
        }
    }

    public void showRenameSnippetFolderDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getResources().getString(R.string.renameSnippetFolderDialogTitle));
        final EditText inputEditText = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        final Toast toastForTooLongNameNumber =
                Toast.makeText(this,R.string.createSnippetFolderConfirmButtonLabel,Toast.LENGTH_SHORT);
        inputEditText.setLayoutParams(lp);
        inputEditText.setSingleLine();
        String curFolderName = snippetManager.getLastSelectedSnippetName();
        inputEditText.setText(curFolderName);
        builder.setView(inputEditText);
        builder.setPositiveButton(R.string.renameSnippetFolderConfirmButtonLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String name = inputEditText.getText().toString();
                if(name.length() <= 50) {
                    snippetManager
                            .renameLastSelectedSnippetFolder(inputEditText.getText().toString());

                    dialog.dismiss();
                }else{
                    toastForTooLongNameNumber.show();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateHelpStatus(){
        if(helpStatusUpdatingExecutor == null)
            return;

        if(helpStatusUpdating != null && !helpStatusUpdating.isDone()){
            helpStatusUpdating.cancel(false);
        }

        final Runnable helpStatusUdatingRunnable = new Runnable() {
            public void run() {
                String curWord = mInnerEditText.grabCurrentWord();
                final boolean aliasExistsFlag = helpManager.aliasExists(curWord);
                mHelpStatusHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setHelpStatus(aliasExistsFlag);
                    }
                });
            }
        };
        helpStatusUpdating = helpStatusUpdatingExecutor.schedule(helpStatusUdatingRunnable,helpStatusUpdatingDelay, TimeUnit.MILLISECONDS);
    }

    public void setHelpStatus(boolean isActive){
        ImageButton helpButton = (ImageButton) findViewById(R.id.help_but);
        int image_id = (isActive)? (R.mipmap.ic_help_active):(R.mipmap.ic_help);
        helpButton.setImageResource(image_id);
    }

    public void onPasteSpecSymbol(View v){
        int id = v.getId();
        Button but = (Button)v;
        String pasteString = but.getText().toString().toLowerCase();

        mInnerEditText.InsertInCurrentPosition(pasteString, false);
    }

}
