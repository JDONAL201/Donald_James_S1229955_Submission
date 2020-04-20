package org.james.donald.s1229955.gcu.trafficjam;
import android.content.Context;
import android.widget.ImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

/*
 * STUDENT NAME: JAMES DONALD
 * METRIC NUMBER : S1229955
 * COURSE: COMPUTER GAMES SOFTWARE DEVELOPMENT (YEAR 4)
 * */

public class CustomClusterRenderer extends DefaultClusterRenderer<ClusterMarker>
{
    private final IconGenerator iconGenerator;
    private ImageView imageView;

    private  final int markerWidth;
    private  final  int markerHeight;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager)
    {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = 100;
        markerHeight = 100;
        iconGenerator.setContentView(imageView);
    }


    @Override
    public Marker getMarker(ClusterMarker clusterMarker)
    {
        return super.getMarker(clusterMarker);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions)
    {
        BitmapDescriptor bt;

        switch (item.GetIconId())
        {
            case 0:
                 bt = BitmapDescriptorFactory.fromResource(R.mipmap.icon_incident_marker_foreground);
                markerOptions.icon(bt);
                break;
            case 1:
                 bt = BitmapDescriptorFactory.fromResource(R.mipmap.icon_roadwork_marker_foreground);
                markerOptions.icon(bt);
                break;
            case 2:
                bt = BitmapDescriptorFactory.fromResource(R.mipmap.icon_plannedrw_marker_foreground);
                markerOptions.icon(bt);
                break;
            case 3:
                bt = BitmapDescriptorFactory.fromResource(R.mipmap.icon_destination_marker_foreground);
                markerOptions.icon(bt);
            break;

        }

        markerOptions.anchor( 0.5f,0.5f);
        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster)
    {
        return cluster.getSize()> 2;
    }

}
