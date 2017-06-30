package com.anatoly1410.editorapp.Presentation;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.anatoly1410.editorapp.Data.HelpIndexElement;
import com.anatoly1410.editorapp.Data.VisualStylesManager;
import com.anatoly1410.editorapp.Domain.HelpManager;
import com.anatoly1410.editorapp.Domain.HelpTreeListAdapter;
import com.anatoly1410.editorapp.Domain.Interfaces.IHelpLoadManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IVisualStylesManager;
import com.anatoly1410.editorapp.Domain.MyApplication;
import com.anatoly1410.editorapp.Presentation.Interfaces.IHelpManager;
import com.anatoly1410.editorapp.R;

import java.util.ArrayList;

import javax.inject.Inject;

public class HelpActivity extends AppCompatActivity {
    @Inject
    public IHelpManager helpManager;
    @Inject
    public IVisualStylesManager visualStylesManager;

    private WebView helpWebView;
    private String helpDirPath;
    private String styleTag =  "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getDomainComponent().inject(this);
        setContentView(R.layout.activity_help);

        setTitle(R.string.helpActivityTitle);
        initHelpDirPath();

        Bundle b = getIntent().getExtras();
        helpWebView = (WebView) findViewById(R.id.help_web_view);

        String alias = b.getString("alias");

        String helpContent = helpManager.openHelpFile(alias);

        setHelpWebViewContent(helpContent);
        HelpIndexElement curHelpIndexElement = helpManager.getCurrentHelpIndexElement();
        HelpIndexElement curDisplayedElement = helpManager.getCurrentDisplayedHelpElement();
        if(curDisplayedElement != null){
            showHelpPage(curDisplayedElement.name, curDisplayedElement.aliases,helpContent);
        }
        if(curHelpIndexElement != null){
            setHelpIndexName(curHelpIndexElement.name);
        }else{
            showRootPage();
        }

        helpManager.setHelpActivity(this);
        helpManager.setHelpItemSelectedListener(new HelpManager.OnHelpItemSelectedListener() {
            @Override
            public void fireEvent(HelpIndexElement element, HelpIndexElement displayedElement) {
                if(element != null){
                    String content = helpManager.getHelpFileContentByPath(displayedElement.filePath);
                    setHelpIndexName(element.name);
                    showHelpPage(displayedElement.name, displayedElement.aliases,content);
                }else{
                    showRootPage();
                }
            }
        });


        final HelpTreeListAdapter helpTreeListAdapter = new HelpTreeListAdapter(this, helpManager.getViewedHelpIndex());
        helpTreeListAdapter.setNotifyOnChange(true);
        helpManager.setHelpTreeListAdapter(helpTreeListAdapter);
        final ListView listView = (ListView) findViewById(R.id.help_list);
        listView.setAdapter(helpTreeListAdapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                if (position < 0 || position >= helpTreeListAdapter.getCount()) {
                    return;
                }
                helpManager.clickOnItem(position);
            }
        });
        helpManager.updateViewedSnippetsList();
    }

    private void setAliasesTextView(ArrayList<String> aliases){
        Resources res = getResources();
        TextView aliasesTextView = (TextView)findViewById(R.id.help_page_aliases);
        StringBuilder aliasesStr = new StringBuilder();
        aliasesStr.append(res.getString(R.string.aliasesHeader));
        aliasesStr.append(": ");
        for(int i=0;i< aliases.size();++i){
            String alias = aliases.get(i);
            aliasesStr.append(alias);
            if(i < aliases.size() - 1){
                aliasesStr.append(", ");
            }
        }
        aliasesTextView.setText(aliasesStr);
    }

    private void setHelpIndexName(String name){
        TextView nameHelpIndexTextView = (TextView)findViewById(R.id.cur_help_index);
        nameHelpIndexTextView.setText(name);
    }
    private void showHelpPage(String name, ArrayList<String> aliases, String content){
        TextView nameTextView = (TextView)findViewById(R.id.help_page_name);
        nameTextView.setText(name);
        setAliasesTextView(aliases);
        setHelpWebViewContent(content);
    }

    private void showRootPage(){
        TextView nameHelpIndexTextView = (TextView)findViewById(R.id.cur_help_index);
        nameHelpIndexTextView.setText(helpManager.getRootHelpDirectoryName());
    }

    public void onBack(View v){
        helpManager.gotoBack();
    }

    private void setHelpWebViewContent(String content){
        content = styleTag + content;
        helpWebView.loadDataWithBaseURL(helpDirPath, content,"text/html", "UTF-8", null);
    }

    private void initHelpDirPath(){
        helpDirPath = "file:///sdcard/" + getPackageName()+"/help/";
    }
}
