package com.anatoly1410.editorapp.Domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anatoly1410.editorapp.Data.HelpIndexElement;
import com.anatoly1410.editorapp.Presentation.HelpActivity;
import com.anatoly1410.editorapp.Presentation.Interfaces.IHelpManager;
import com.anatoly1410.editorapp.Presentation.MainActivity;
import com.anatoly1410.editorapp.R;

import java.util.ArrayList;

/**
 * Created by 1 on 07.05.2017.
 */

public class HelpTreeListAdapter  extends ArrayAdapter<HelpIndexElement> {
    private IHelpManager helpManager;
    private HelpActivity helpActivity;
    private static class ViewHolder {
        ImageView icon;
        TextView name;
    }

    public HelpTreeListAdapter(Context context, ArrayList<HelpIndexElement> helpIndexElements) {
        super(context, 0, helpIndexElements);
        helpActivity = (HelpActivity) context;
        helpManager = helpActivity.helpManager;
    }

    public interface ItemViewCreationListener
    {
        public void onItemViewCreation(View view);
    }

    private HelpTreeListAdapter.ItemViewCreationListener mItemViewCreationListener;

    public void setItemViewCreationListener(HelpTreeListAdapter.ItemViewCreationListener listener){
        mItemViewCreationListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        HelpIndexElement helpElement = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        HelpTreeListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new HelpTreeListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.help_item_layout,parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.help_element_name);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.help_element_icon);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);

        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (HelpTreeListAdapter.ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.name.setText(helpElement.name);
        viewHolder.icon.setImageResource(R.mipmap.ic_help_item);

        return convertView;
    }

}
