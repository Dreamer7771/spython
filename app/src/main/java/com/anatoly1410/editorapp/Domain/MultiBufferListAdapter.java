package com.anatoly1410.editorapp.Domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anatoly1410.editorapp.Domain.BufferFragment;
import com.anatoly1410.editorapp.Domain.MultiBuffer;
import com.anatoly1410.editorapp.Presentation.MainActivity;
import com.anatoly1410.editorapp.R;

import java.util.ArrayList;

/**
 * Created by 1 on 13.02.2017.
 */

public class MultiBufferListAdapter extends ArrayAdapter<BufferFragment> {
    private static class ViewHolder {
        TextView index;
        TextView content;
        int position;
    }

    public MultiBufferListAdapter(Context context, ArrayList<BufferFragment> fragments) {
        super(context, 0, fragments);
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
        BufferFragment fragment = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.buffer_item_layout,parent, false);
            viewHolder.index = (TextView) convertView.findViewById(R.id.tv_idx);
            viewHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.position = position;
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);

        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.index.setText(Integer.toString(position+1)+":");
        viewHolder.content.setText(fragment.content);
        // Return the completed view to render on screen
        return convertView;
    }


}
