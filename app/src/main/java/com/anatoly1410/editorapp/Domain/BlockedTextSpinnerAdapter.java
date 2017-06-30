package com.anatoly1410.editorapp.Domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anatoly1410.editorapp.Presentation.Interfaces.ISnippetManager;
import com.anatoly1410.editorapp.Presentation.MainActivity;
import com.anatoly1410.editorapp.R;

import java.util.ArrayList;

/**
 * Created by 1 on 11.05.2017.
 */

public class BlockedTextSpinnerAdapter extends ArrayAdapter<SyntaxBlock> {
    private ISnippetManager mSnippetManager;
    private MainActivity mainActivity;
    private int mAutocompletionMenuWidth;
    public void setAutocompletionMenuWidth(int width){
        mAutocompletionMenuWidth = width;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView name;
        int position;
    }

    public BlockedTextSpinnerAdapter(Context context, ArrayList<SyntaxBlock> items) {
        super(context, 0, items);
        mainActivity = (MainActivity) context;
        mSnippetManager = mainActivity.snippetManager;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent, true);
    }

    private View initView(int position, View convertView, ViewGroup parent, boolean isDropDown) {
        // Get the data item for this position
        SyntaxBlock syntaxBlock = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            if(isDropDown){
                convertView = inflater.inflate(R.layout.block_extr_item_drop_down_layout,parent, false);
            }else{
                convertView = inflater.inflate(R.layout.block_extr_item_layout,parent, false);
            }

            viewHolder.icon = (ImageView) convertView.findViewById(R.id.block_extr_item_image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.block_extr_item_text);
            viewHolder.position = position;
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);

        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(syntaxBlock.header);

        // Return the completed view to render on screen
        ViewGroup.LayoutParams params = convertView.getLayoutParams();
        convertView.setLayoutParams(params);
        return convertView;
    }


}
