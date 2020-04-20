package org.james.donald.s1229955.gcu.trafficjam;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/*
 * STUDENT NAME: JAMES DONALD
 * METRIC NUMBER : S1229955
 * COURSE: COMPUTER GAMES SOFTWARE DEVELOPMENT (YEAR 4)
 * */

public class ClusterMarker implements ClusterItem
{
    private String snippet;
    private LatLng position;
    private double latitude,longitude;
    private String title;
    private  int id;
    private TrafficData trafficData;

    public ClusterMarker(LatLng position, String title, String snippet, int iconId)  {
        this.id = iconId;
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.latitude = position.latitude;
        this.longitude = position.longitude;
    }

    @Override
    public LatLng getPosition()
    {
        return position;
    }
    public double GetLat()
    {
        return latitude;
    }
    public  double GetLong()
    {
        return longitude;
    }

    public int GetIconId()
    {
        return id;
    }

    public void setPosition(LatLng position)
    {
        this.position = position;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public String getSnippet()
    {
        return snippet;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }


}
