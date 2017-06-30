package com.anatoly1410.editorapp.Domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anatoly1410.editorapp.Presentation.Interfaces.ISnippetManager;
import com.anatoly1410.editorapp.Presentation.MainActivity;
import com.anatoly1410.editorapp.R;

import java.util.ArrayList;

/**
 * Created by 1 on 08.05.2017.
 */

public class AutocompletionListAdapter extends ArrayAdapter<Snippet> {
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

    public AutocompletionListAdapter(Context context, ArrayList<Snippet> fragments) {
        super(context, 0, fragments);
        mainActivity = (MainActivity) context;
        mSnippetManager = mainActivity.snippetManager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Snippet snippet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.autocomp_item_layout,parent, false);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.autoc_item_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.autoc_item_name);
            viewHolder.position = position;
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);

        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.

        if(snippet.type == mSnippetManager.get_FUNC_SNIPPET_TYPE()){
            viewHolder.icon.setImageResource(R.mipmap.ic_func_type);
        }else if(snippet.type == mSnippetManager.get_CLASS_SNIPPET_TYPE()){
            viewHolder.icon.setImageResource(R.mipmap.ic_class_type);
        }else if(snippet.type == mSnippetManager.get_CONSTANT_SNIPPET_TYPE()){
            viewHolder.icon.setImageResource(R.mipmap.ic_constant_type);
        }else{
            viewHolder.icon.setImageBitmap(null);
        }

        viewHolder.name.setText(snippet.tag);

        // Return the completed view to render on screen
        ViewGroup.LayoutParams params = convertView.getLayoutParams();
        params.width = mAutocompletionMenuWidth;
        convertView.setLayoutParams(params);
        return convertView;
    }


}
