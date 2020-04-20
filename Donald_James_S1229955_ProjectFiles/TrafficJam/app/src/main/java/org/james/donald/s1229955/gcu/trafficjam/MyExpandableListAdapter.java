package org.james.donald.s1229955.gcu.trafficjam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
/*
 * STUDENT NAME: JAMES DONALD
 * METRIC NUMBER : S1229955
 * COURSE: COMPUTER GAMES SOFTWARE DEVELOPMENT (YEAR 4)
 * */
public class MyExpandableListAdapter extends BaseExpandableListAdapter
{
    private HashMap<String, LinkedList<TrafficData>> customStringListHM;
    public   String[] headerGroup;

    public MyExpandableListAdapter(HashMap<String, LinkedList<TrafficData>> stringListHashMap)
    {
        customStringListHM = stringListHashMap;
        headerGroup = customStringListHM.keySet().toArray(new String[0]);
    }

    @Override
    public int getGroupCount()
    {
        return headerGroup.length;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return customStringListHM.get(headerGroup[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return headerGroup[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return customStringListHM.get(headerGroup[groupPosition]).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }
    public String getGroupName(int groupPosition)
    {
        return String.valueOf(getGroup(groupPosition));
    }
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return groupPosition * childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if(convertView ==null)
        {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_list_group,parent,false);
        }

        TextView tv = convertView.findViewById(R.id.tvGroup);
        tv.setText(String.valueOf(getGroup(groupPosition)));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_list_item,parent,false);
        }
        TextView tv = convertView.findViewById(R.id.tvItem);
        TrafficData td = (TrafficData) getChild(groupPosition,childPosition);
        tv.setText( td.GetTitle()+ "\n \n" + td.GetDescription()+ "\n \n" + td.GetPublicationDate());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }


}
