package com.anatoly1410.editorapp.Domain;

import android.content.Context;
import android.media.Image;
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
 * Created by 1 on 01.05.2017.
 */

public class SnippetTreeListAdapter  extends ArrayAdapter<SnippetTreeElement> {
    private MainActivity mainActivity;

    private static class ViewHolder {
        ImageView icon;
        TextView name;
    }

    public SnippetTreeListAdapter(Context context, ArrayList<SnippetTreeElement> snippets) {
        super(context, 0, snippets);
        mainActivity = (MainActivity)context;
    }

    public interface ItemViewCreationListener
    {
        public void onItemViewCreation(View view);
    }

    private ItemViewCreationListener mItemViewCreationListener;

    public void setItemViewCreationListener(ItemViewCreationListener listener){
        mItemViewCreationListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SnippetTreeElement snippetElement = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        SnippetTreeListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new SnippetTreeListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.snippet_item_layout,parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.snippet_name);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.snippet_icon);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);

        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (SnippetTreeListAdapter.ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.name.setText(snippetElement.Name);
        if(snippetElement.getClass() == SnippetFolder.class){
            viewHolder.icon.setImageResource(R.mipmap.ic_folder);
        }else if (snippetElement.getClass() == SnippetInsertedElement.class){
            viewHolder.icon.setImageResource(R.mipmap.ic_file);
        }

        return convertView;
    }


}
