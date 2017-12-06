package com.example.pratik.popular_movies_stage_2.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.example.pratik.popular_movies_stage_2.R;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pratik on 11/8/17.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private static Context mContext;
    private static List<String> mListGroupHeader;
    private static HashMap<String, List<String>> mListChildren;

    public ExpandableListAdapter(Context context, List<String> groupHeaders, HashMap<String, List<String>> listChildData)
    {

        this.mContext = context;
        this.mListGroupHeader = groupHeaders;
        this.mListChildren = listChildData;
        this.mListGroupHeader.add(0, "Trailers");
        this.mListGroupHeader.add(1, "Reviews");

    }

    public void setTrailerData(List<String> trailerData)
    {
        if (trailerData.size() ==  0)
            trailerData.add("No Trailers");

        mListChildren.put(mListGroupHeader.get(0), trailerData);

    }

    public void setReviewData(List<String> reviewData)
    {
        if (reviewData.size() == 0)
            reviewData.add("No Reviews");

        mListChildren.put(mListGroupHeader.get(1), reviewData);
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        String groupHeader = (String) getGroup(groupPosition);
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list, null);
        }

        TextView groupListHeader = (TextView) convertView.findViewById(R.id.movie_detail_list_header);
        groupListHeader.setTypeface(null, Typeface.BOLD);
        groupListHeader.setText(groupHeader);

        return convertView;
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.mListGroupHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return this.mListGroupHeader.size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        final String childText = (String) getChild(groupPosition,childPosition);

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_item,null);
        }

        TextView listChild = (TextView) convertView.findViewById(R.id.movie_detail_list_item);
        listChild.setText(childText);
        return listChild;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return this.mListChildren.get(this.mListGroupHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this.mListChildren.get(this.mListGroupHeader.get(groupPosition)).size();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
