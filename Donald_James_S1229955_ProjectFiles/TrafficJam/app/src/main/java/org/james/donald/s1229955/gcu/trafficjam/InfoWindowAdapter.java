package org.james.donald.s1229955.gcu.trafficjam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/*
 * STUDENT NAME: JAMES DONALD
 * METRIC NUMBER : S1229955
 * COURSE: COMPUTER GAMES SOFTWARE DEVELOPMENT (YEAR 4)
 * */

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{

    private  View infoWindow;
    private Context infoContext;
    private TextView snippet;
    private  TextView title;
    private static final int EXPANDED_LINE_COUNT = 50;
    private static final int MIN_LINE_COUNT = 0;

    public InfoWindowAdapter(Context context)
    {
        infoContext = context;
        infoWindow = LayoutInflater.from(context).inflate(R.layout.custom_window,null);
    }

    private void WindowText(Marker marker , View view)
    {
        String markerTitle = marker.getTitle();
        title = view.findViewById(R.id.tvTitle);

        if(!title.equals(""))
        {
            title.setText(markerTitle);
        }

        String mapSnippet = marker.getSnippet();
        snippet = view.findViewById(R.id.snippet);

        if(!mapSnippet.equals(""))
        {
            snippet.setText(mapSnippet);
        }

    }
    @Override
    public View getInfoContents(Marker marker)
    {
        WindowText(marker,infoWindow);
        return infoWindow;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        WindowText(marker,infoWindow);
        return infoWindow;
    }
    public void ExpandInfoWindow(Marker marker)
    {
        if(snippet.getMaxLines() == MIN_LINE_COUNT )
        {
            title.setMaxLines(3);
            snippet.setMaxLines(EXPANDED_LINE_COUNT);
        }
        else if(snippet.getMaxLines() == EXPANDED_LINE_COUNT)
        {
            snippet.setMaxLines(MIN_LINE_COUNT);
            title.setMaxLines(1);
        }

    }

    public void CollapseInfoWindow(Marker marker)
    {
        title.setMaxLines(1);
        snippet.setMaxLines(MIN_LINE_COUNT);
    }



}
