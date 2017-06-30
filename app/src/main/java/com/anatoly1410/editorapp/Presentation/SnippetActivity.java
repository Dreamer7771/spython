package com.anatoly1410.editorapp.Presentation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.anatoly1410.editorapp.Domain.MyApplication;
import com.anatoly1410.editorapp.Domain.SnippetInsertedElement;
import com.anatoly1410.editorapp.Presentation.Interfaces.IAutocompletionItemsKeeper;
import com.anatoly1410.editorapp.Presentation.Interfaces.IMultiBuffer;
import com.anatoly1410.editorapp.Presentation.Interfaces.ISnippetManager;
import com.anatoly1410.editorapp.R;

import javax.inject.Inject;

public class SnippetActivity extends AppCompatActivity {
    @Inject
    ISnippetManager snippetManager;
    @Inject
    IMultiBuffer multiBuffer;
    @Inject
    IAutocompletionItemsKeeper keyWordsKeeper;

    private int fragment_idx;
    private EditText mEditText;
    private EditText mNameEditText;
    private EditText mAliasEditText;

    private SnippetInsertedElement curSnippetElement;

    private String mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getDomainComponent().inject(this);
        setContentView(R.layout.activity_snippet);
        Bundle b = getIntent().getExtras();
        mEditText = (EditText) findViewById(R.id.snip_edit_text);
        TableLayout nameAliasLayout = (TableLayout) findViewById(R.id.name_alias_layout);
        mode = b.getString("mode");
        mEditText.setTextSize(25);
        mNameEditText = (EditText)findViewById(R.id.name_edit_text);
        mAliasEditText = (EditText)findViewById(R.id.alias_edit_text);

        switch (mode){
            case "createSnippet":
                setTitle(R.string.createSnippetActivityTitle);
                String snippet_content =  b.getString("content");

                mEditText.setText(snippet_content);
                nameAliasLayout.setVisibility(View.VISIBLE);
                break;
            case "modifySnippet":
                setTitle(R.string.changeSnippetActivityTitle);
                int snippet_idx = b.getInt("snippet_idx");
                curSnippetElement = (SnippetInsertedElement) snippetManager.getViewedSnippetByIdx(snippet_idx);

                mEditText.setText(curSnippetElement.getSnippet().content);
                nameAliasLayout.setVisibility(View.VISIBLE);
                mNameEditText.setText(curSnippetElement.Name);
                mAliasEditText.setText(curSnippetElement.getSnippet().tag);

                break;
            case "modifyBufferFragment":
                setTitle(R.string.changeFragmentActivityTitle);
                String fragment = b.getString("content");
                fragment_idx = b.getInt("fragment_idx");

                mEditText.setText(fragment);
                nameAliasLayout.setVisibility(View.GONE);
                break;
        }
    }

    public void onSave(View view){
        boolean res = true;
        switch(mode){
            case "modifyBufferFragment":
                String newContent = mEditText.getText().toString();
                multiBuffer.updateFragment(fragment_idx,newContent);
                res = true;
                break;
            case "createSnippet":
                String name = mNameEditText.getText().toString();
                String alias = mAliasEditText.getText().toString();
                String textvalue = mEditText.getText().toString();

                res = snippetManager.addSnippetInsertedElement(name,alias,textvalue);
                keyWordsKeeper.init();
                break;
            case "modifySnippet":
                name = mNameEditText.getText().toString();
                alias = mAliasEditText.getText().toString();
                textvalue = mEditText.getText().toString();
                curSnippetElement.Name = name;
                curSnippetElement.getSnippet().tag = alias;
                curSnippetElement.getSnippet().content = textvalue;
                res = snippetManager.updateSnippetInsertedElement(curSnippetElement);
                keyWordsKeeper.init();
                break;
        }
        if(res){
            this.finish();
        }else{
            Toast toastAliasExists = Toast.makeText(this,R.string.aliasAlreadyExistsMsgError,Toast.LENGTH_SHORT);
            toastAliasExists.show();
        }

    }

    public void onCancel(View view){
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        this.finish();
    }
}
