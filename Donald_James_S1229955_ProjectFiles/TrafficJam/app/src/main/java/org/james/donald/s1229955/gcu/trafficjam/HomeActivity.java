package org.james.donald.s1229955.gcu.trafficjam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
/*
* STUDENT NAME: JAMES DONALD
* METRIC NUMBER : S1229955
* COURSE: COMPUTER GAMES SOFTWARE DEVELOPMENT (YEAR 4)
* */

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnPolylineClickListener,GoogleMap.OnPolygonClickListener
{
    /*
   ||======== Verification content =============||
   */
    private FirebaseAuth fAuth;

    /*
   ||======== Maps content =============||
   */
    private boolean permissionGranted = false;
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private static final float DEFAULT_ZOOM = 8f;
    private static final float CLOSE_ZOOM = 15f;
    private SearchView searchView;
    private GoogleMap GMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment mapFragment;
    private TrafficDataParser dataParser;

    private enum activeMarker {NOMARKERS,INCIDENTS,ROADWORKS,PLANNEDRW}
    private activeMarker currentActiveMarker;
    private static final String GROUP_INCIDENTS = "Live Incidents";
    private static final String GROUP_ROADWORKS = "Live Roadworks";
    private static final String GROUP_PLANNEDRW = "Planned Roadworks";
    private Location deviceLocation = null;

    /*
    ||============= Directions =============||
    */
    List<LatLng> decodedPolyPath = new ArrayList<>();
     private GeoApiContext geoApiContext = null;
     private ClusterMarker activeSearchMarker;

    /*
    ||======== Traffic Data ================||
    */
    private LinkedList<TrafficData> map_incidents = new LinkedList<>();
    private LinkedList<TrafficData> map_roadworks = new LinkedList<>();
    private LinkedList<TrafficData> map_plannedRoadWorks= new LinkedList<>();

    /*
    ||======== Cluster content =============||
    */
    private ClusterManager clusterManager;
    private CustomClusterRenderer customClusterRenderer;
    private  InfoWindowAdapter infoAdapter;
    private  Marker selectedClusterMarker;

    /*
    ||======== Date picker content =============||
     */
    private TextView dateEntry,clearFilters;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog.OnDateSetListener dateEntryListener;
    private int[] enteredDate = new int[3];

    /*||========= Expandable List ==============||
    */
    private MyExpandableListAdapter myExpandableListAdapter;
    private ExpandableListView expandableListView;
    private HashMap<String,LinkedList<TrafficData>> item;
    private SearchView listSearchView;

    /* ||=========== Animation ==================||
    */
    private RelativeLayout mapContainer;
    private RelativeLayout listContainer;

    private enum layoutStates {MAP_EXPANDED,MAP_COLLAPSED}

    private layoutStates currentLayoutState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        currentLayoutState = layoutStates.MAP_COLLAPSED;

        mapContainer = findViewById(R.id.mapContainer);
        listContainer = findViewById(R.id.listContainer);

         //Traffic Data
        InitTrafficData();
        //Expandable List
        InitExpandableList();
        //INIT MAPS
        GetLocationPermission();
        //For logging out
        fAuth = FirebaseAuth.getInstance();
        //Set up custom toolbar
        Toolbar custom_toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(custom_toolbar);
        //Buttons
        ButtonInterface();
        InitDatePicker();
}

    /*
        ||======== Init Traffic data  =============||
        */
    private void InitTrafficData()
    {
        dataParser = new TrafficDataParser();
        dataParser.ParseData();

        map_incidents = dataParser.GetIncidents();
        map_roadworks = dataParser.GetRoadWorks();
        map_plannedRoadWorks = dataParser.GetPlannedRoadWorks();

    }
    private void UpdateContent(activeMarker inputMarker, boolean refresh)
    {

        if(currentActiveMarker == inputMarker && refresh == false)
        {
            ClearAllMarkers();
            currentActiveMarker = activeMarker.NOMARKERS;
        }
        else
        {
            ClearAllMarkers();

            if(inputMarker == activeMarker.ROADWORKS)
            {
                AddMarkers(map_roadworks,true);
            }
            else if(inputMarker == activeMarker.PLANNEDRW)
            {
                AddMarkers(map_plannedRoadWorks,true);
            }
            else if(inputMarker == activeMarker.INCIDENTS)
            {
                AddMarkers(map_incidents,false);
            }

            currentActiveMarker = inputMarker;
        }
    }
    private void InitExpandableList()
    {
        item = new HashMap<>();


        item.put(GROUP_INCIDENTS,map_incidents);
        item.put(GROUP_ROADWORKS,map_roadworks);
        item.put(GROUP_PLANNEDRW,map_plannedRoadWorks);


        expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            String clickedGroup =  String.valueOf(parent.getExpandableListAdapter().getGroup(groupPosition));
            if(clickedGroup.equalsIgnoreCase(GROUP_INCIDENTS))
            {
                GMap.clear();
              UpdateContent(activeMarker.INCIDENTS,false);
            }
            else if(clickedGroup.equalsIgnoreCase(GROUP_ROADWORKS))
            {  GMap.clear();
                UpdateContent(activeMarker.ROADWORKS,false);
            }
            else if(clickedGroup.equalsIgnoreCase(GROUP_PLANNEDRW))
            {  GMap.clear();
               UpdateContent(activeMarker.PLANNEDRW,false);
            }
            return false;
        });

        expandableListView.setChoiceMode( expandableListView.CHOICE_MODE_SINGLE);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String parentObject =  String.valueOf(parent.getExpandableListAdapter().getGroup(groupPosition));
            TrafficData clickedChild = (TrafficData)parent.getExpandableListAdapter().getChild(groupPosition,childPosition);
            LatLng markerPos = new LatLng(clickedChild.GetLat(),clickedChild.GetLong());
            CameraUpdate center = CameraUpdateFactory.newLatLng(markerPos);
            GMap.animateCamera(center);

            return true;
        });

        myExpandableListAdapter = new MyExpandableListAdapter(item);
        expandableListView.setAdapter(myExpandableListAdapter);
        myExpandableListAdapter.notifyDataSetChanged();
    }
    /*
    ||======== Init button interface content =============||
    */
    private void ButtonInterface()
    {
        findViewById(R.id.fullScreenBtn).setOnClickListener(v -> {
            if(currentLayoutState == layoutStates.MAP_COLLAPSED)
                ExpandAnimation();
            else if(currentLayoutState == layoutStates.MAP_EXPANDED)
                CollapseAnimation();
        });

         clearFilters = findViewById(R.id.clearFilters);
         clearFilters.setOnClickListener(v -> {
           dateEntry.setText("Select Date");
           searchView.setQuery(" ",false);
           CloseExpandableList();
           enteredDate[0] = 0;
           GMap.clear();

        });
    }
    /*
    ||=========== Init Date picker ===================||
    */
    private void InitDatePicker()
    {
        dateEntry = findViewById(R.id.dateEntry);
        dateEntry.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            datePickerDialog = new DatePickerDialog(HomeActivity.this,R.style.DatePicker1, dateEntryListener,year,month,day);
            datePickerDialog.show();
        });

        dateEntryListener = (view, year, month, dayOfMonth) -> {
            month = month+1;

            enteredDate[0] = dayOfMonth;
            enteredDate[1] = month;
            enteredDate[2] = year;

            String date = dayOfMonth +"/" + month + "/" + year;
            dateEntry.setText(date);

            GMap.clear();
            UpdateContent(currentActiveMarker,true);
        };

    }
    /*
    ||=========== Maps Content  ===================||
    */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        GMap = googleMap;

        MapStyleOptions custom_map_style = MapStyleOptions.loadRawResourceStyle(this,R.raw.map_styles);
        GMap.setMapStyle(custom_map_style);

        clusterManager = new ClusterManager<ClusterMarker>(this,GMap);
        customClusterRenderer = new CustomClusterRenderer(this,GMap,clusterManager);
        clusterManager.setRenderer(customClusterRenderer);

        GMap.setOnCameraIdleListener(clusterManager);
        GMap.setOnMarkerClickListener(clusterManager);

        infoAdapter = new InfoWindowAdapter(HomeActivity.this);
        clusterManager.getMarkerCollection().setInfoWindowAdapter(infoAdapter);

        GMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        GMap.setOnInfoWindowClickListener(clusterManager);


        clusterManager.setOnClusterItemInfoWindowClickListener(item -> {
            infoAdapter.ExpandInfoWindow(selectedClusterMarker);
            selectedClusterMarker.showInfoWindow();
        });

        clusterManager.setOnClusterItemClickListener(item -> {
            //get the map container height
            int container_height = mapContainer.getHeight();

            Projection projection = GMap.getProjection();
            LatLng markerLatLng = new LatLng(item.getPosition().latitude,item.getPosition().longitude);
            Point markerScreenPosition = projection.toScreenLocation(markerLatLng);
            Point pointHalfScreenAbove = new Point(markerScreenPosition.x,markerScreenPosition.y - container_height/10);

            LatLng aboveMarkerLatLng = projection.fromScreenLocation(pointHalfScreenAbove);
            selectedClusterMarker = customClusterRenderer.getMarker((ClusterMarker) item);
            if(selectedClusterMarker != null)
            {
                selectedClusterMarker.showInfoWindow();
            }
            CameraUpdate center = CameraUpdateFactory.newLatLng(aboveMarkerLatLng);
            GMap.animateCamera(center);
            return true;
        });

        GMap.setOnInfoWindowCloseListener(marker -> infoAdapter.CollapseInfoWindow(marker));

        if(permissionGranted)
        {
            GetDeviceLocation();
            GMap.setMyLocationEnabled(true);
        }
    }
    private void GetLocationPermission()
    {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                permissionGranted = true;
                InitMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_CODE);
        }
    }

    //===INITIALISE GOOGLE MAPS====//
    private void InitMap()
    {
        searchView = findViewById(R.id.mapSearch);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {

            @Override
            public boolean onQueryTextSubmit(String query)
            {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(HomeActivity.this);

                    try
                    {
                        addressList = geocoder.getFromLocationName(location,1);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    if(addressList.size() > 0)
                    {
                        GMap.clear();
                        Address address = addressList.get(0);
                        if(activeSearchMarker != null)
                        {
                            clusterManager.clearItems();
                            activeSearchMarker = null;
                        }

                        ClusterMarker newClusterMarker = new ClusterMarker(new LatLng(address.getLatitude(), address.getLongitude()),address.getAddressLine(0), address.getCountryName() +"\n" +address.getPostalCode(),3);
                        clusterManager.addItem(newClusterMarker);
                        activeSearchMarker = newClusterMarker;

                        CameraUpdate center = CameraUpdateFactory.newLatLng(activeSearchMarker.getPosition());
                        GMap.animateCamera(center);

                        CalculateDirection(activeSearchMarker);
                        clusterManager.cluster();
                    }
                    else
                    {
                        Toast.makeText(HomeActivity.this,"Unrecognised destination",Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });

        mapFragment.getMapAsync(HomeActivity.this);
        if(geoApiContext == null)
        {
            geoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyCRXUZj9_pzNBZsZmfnj8QJljo-XvrYdRI").build();
        }
    }

    //====GET THE DEVICE LOCATION======///
    private void GetDeviceLocation()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try
        {
            if(permissionGranted)
            {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        deviceLocation = (Location)task.getResult();
                        MoveMapCamera(new LatLng(deviceLocation.getLatitude(),deviceLocation.getLongitude()),DEFAULT_ZOOM);
                    }
                });
            }
        }
        catch (SecurityException e)
        {

        }
    }
    //====REQUEST PERMISSION FOR DEVICE =====//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        permissionGranted = false;

        switch (requestCode)
        {
            case LOCATION_PERMISSION_CODE:
                if(grantResults.length > 0)
                {
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            permissionGranted = false;
                            Log.d("MYTAG","PERMISSION FAILED");
                            return;
                        }
                    }
                    permissionGranted = true;
                    Log.d("MYTAG","PERMISSION GRANTED");
                    InitMap();
                }
        }
    }
    private void AddMarkers(LinkedList<TrafficData> trafficDataLinkedList,boolean sortByDate)
    {
        if(GMap != null)
        {
          //ClearAllMarkers();

            for(TrafficData td: trafficDataLinkedList)
            {
                try
                {
                    if(sortByDate == true)
                    {

                        if(enteredDate[0] == 0)
                        {
                            ClusterMarker newClusterMarker = new ClusterMarker(new LatLng(td.GetLat(), td.GetLong()),td.GetTitle(), td.GetDisplayInfo(),td.GetIconId());
                            clusterManager.addItem(newClusterMarker);
                        }
                        else
                        {
                            if(td.GetIconId() != 0)
                            {
                                int[] startArr = td.GetStartDate();
                                Log.e("MYTAG", startArr[0] + " :" + enteredDate[0] + " " +  startArr [1] +  " :" + enteredDate[1] +" " + startArr[2]+ " :" + enteredDate[2]);
                                int[] endArr = td.GetEndDate();



                                if( enteredDate[2] >= startArr[2] && enteredDate[2] <= endArr[2])// && endArr[2] <= endDateArray[2])
                                {
                                    if(enteredDate[1] >= startArr[1] && enteredDate[1] <= endArr[1])// && endArr[1] <= endDateArray[1])
                                    {
                                        if(enteredDate[0] >= startArr[0] && enteredDate[0] <= endArr[0])// && endArr[0] <= endDateArray[0])
                                        {
                                            ClusterMarker newClusterMarker = new ClusterMarker(new LatLng(td.GetLat(), td.GetLong()),td.GetTitle(), td.GetDisplayInfo(),td.GetIconId());
                                            clusterManager.addItem(newClusterMarker);
                                        }
                                    }
                                }
                            }
                            else
                            {
                                ClusterMarker newClusterMarker = new ClusterMarker(new LatLng(td.GetLat(), td.GetLong()),td.GetTitle(), td.GetDisplayInfo(),td.GetIconId());
                                clusterManager.addItem(newClusterMarker);
                            }
                        }

                    }
                    else
                    {
                        ClusterMarker newClusterMarker = new ClusterMarker(new LatLng(td.GetLat(), td.GetLong()),td.GetTitle(), td.GetDisplayInfo(),td.GetIconId());
                        clusterManager.addItem(newClusterMarker);
                    }

                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }
            clusterManager.cluster();
        }
    }

    private void ClearAllMarkers()
    {
        for(Marker marker :clusterManager.getMarkerCollection().getMarkers() )
            marker.remove();
        for(Marker marker :clusterManager.getClusterMarkerCollection().getMarkers() )
            marker.remove();
        clusterManager.clearItems();
    }
    private void CalculateDirection(ClusterMarker marker)
    {
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(marker.GetLat(),marker.GetLong());
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
        //directions.alternatives(true);
        directions.origin(new com.google.maps.model.LatLng(deviceLocation.getLatitude(),deviceLocation.getLongitude()));
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
               @Override
            public void onResult(DirectionsResult result)
            {
                LinkedList<TrafficData>trafficOnRoute = new LinkedList<>();
                for(TrafficData td : map_plannedRoadWorks)
                {
                    for(int i = 0; i< result.routes.length; i++)
                    {
                        if(result.routes[i].summary.contains("and"))
                        {
                            String[] stringArr = result.routes[i].summary.split("and",2);
                            if(td.GetTitle().contains(stringArr[0]) || td.GetTitle().contains(stringArr[1]))
                            {
                                Log.e("MYTAG:" ," ADDED FROM SPLIT TITLE: " + td.GetTitle() + " SUMMARY: " + stringArr[0] + " : " + stringArr[1]);
                                trafficOnRoute.add(td);
                            }
                        }
                        else
                        {
                            if(td.GetTitle().contains(result.routes[i].summary))
                            {
                                trafficOnRoute.add(td);
                            }
                        }
                    }
                }

                for(TrafficData td : map_roadworks)
                {
                    for(int i = 0; i< result.routes.length; i++)
                    {
                        if(result.routes[i].summary.contains("and"))
                        {
                            String[] stringArr = result.routes[i].summary.split("and",1);
                            if(td.GetTitle().contains(stringArr[0]) || td.GetTitle().contains(stringArr[1]))
                            {
                                Log.e("MYTAG:" ," ADDED FROM SPLIT TITLE: " + td.GetTitle() + " SUMMARY: " + stringArr[0] + " : " + stringArr[1]);
                                trafficOnRoute.add(td);
                            }
                        }
                        else
                        {
                            if(td.GetTitle().contains(result.routes[i].summary))
                            {
                                trafficOnRoute.add(td);
                            }
                        }
                    }
                }

                for(TrafficData td : map_incidents)
                {
                    for(int i = 0; i< result.routes.length; i++)
                    {
                        if(result.routes[i].summary.contains("and"))
                        {
                            String[] stringArr = result.routes[i].summary.split("and",0);
                            if(td.GetTitle().contains(stringArr[0]) || td.GetTitle().contains(stringArr[1]))
                            {
                                Log.e("MYTAG:" ," ADDED FROM SPLIT TITLE: " + td.GetTitle() + " SUMMARY: " + stringArr[0] + " : " + stringArr[1]);
                                trafficOnRoute.add(td);
                            }
                        }
                        else
                        {
                            if(td.GetTitle().contains(result.routes[i].summary))
                            {
                                trafficOnRoute.add(td);
                            }
                        }
                    }
                }
                Log.e("MYTAG:" ," COUNT: " + String.valueOf(trafficOnRoute.size()));

                ShowPolyLines(result,trafficOnRoute);

            }

            @Override
            public void onFailure(Throwable e)
            {
                Log.e("MYTAG", "COULD NOT GET DIRECTIONS " + e.getMessage());

            }
        });
    }
    private void AddMarker(TrafficData td ,int iconID)
    {
        if(GMap != null)
        {
                try
                {
                    ClusterMarker newClusterMarker = new ClusterMarker(new LatLng(td.GetLat(), td.GetLong()),td.GetTitle(), td.GetDisplayInfo(),iconID);
                    clusterManager.addItem(newClusterMarker);
                }
                catch (NullPointerException e)
                {
                    Log.e("MYTAG","FAILED: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            clusterManager.cluster();
    }

    private void MoveMapCamera(LatLng latLng, float defaultZoom)
    {
        GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,defaultZoom));
    }

    @Override
    public void onPolygonClick(Polygon polygon)
    {

    }
    @Override
    public void onPolylineClick(Polyline polyline)
    {

    }
    private void ShowPolyLines(DirectionsResult result, LinkedList<TrafficData> trafficOnRoute)
    {
        new Handler(Looper.getMainLooper()).post(() -> {
            for(DirectionsRoute route: result.routes)
            {
                List<com.google.maps.model.LatLng> encodedPolyPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                decodedPolyPath.clear();

                for(com.google.maps.model.LatLng latlng: encodedPolyPath)
                {
                    decodedPolyPath.add(new LatLng(latlng.lat,latlng.lng));
                }
                Polyline polyline = GMap.addPolyline(new PolylineOptions().addAll(decodedPolyPath));
                polyline.setColor(ContextCompat.getColor(HomeActivity.this,R.color.tangrine));
                polyline.setClickable(true);
            }

            AddMarkers(trafficOnRoute,true);

        });
    }

    //==================TOOLBAR AND ACTIONS======================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.Refresh:
                Refresh();
                break;

            case R.id.Logout:
                LogOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Refresh()
    {
        startActivity(new Intent(this,HomeActivity.class));
        Toast.makeText(this,"Page Refreshed",Toast.LENGTH_SHORT).show();
    }
    private void LogOut()
    {
        fAuth.signOut();
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }

    private void ExpandAnimation()
    {
        ViewAnimationWrapper mapAnimationWrapper = new ViewAnimationWrapper(mapContainer);
        ObjectAnimator mapAnim = ObjectAnimator.ofFloat(mapAnimationWrapper,"weight",39,82);
        mapAnim.setDuration(800);

        ViewAnimationWrapper listAnimationWrapper = new ViewAnimationWrapper(listContainer);
        ObjectAnimator listAnim = ObjectAnimator.ofFloat(listAnimationWrapper,"weight",49,0);
        listAnim.setDuration(800);

        listAnim.start();
        mapAnim.start();


        currentLayoutState = layoutStates.MAP_EXPANDED;
    }
    private void CollapseAnimation()
    {
        ViewAnimationWrapper mapAnimationWrapper = new ViewAnimationWrapper(mapContainer);
        ObjectAnimator mapAnim = ObjectAnimator.ofFloat(mapAnimationWrapper,"weight",82,39);
        mapAnim.setDuration(800);

        ViewAnimationWrapper listAnimationWrapper = new ViewAnimationWrapper(listContainer);
        ObjectAnimator listAnim = ObjectAnimator.ofFloat(listAnimationWrapper,"weight",0,49);
        listAnim.setDuration(800);

        listAnim.start();
        mapAnim.start();
        currentLayoutState = layoutStates.MAP_COLLAPSED;

    }
    private void CloseExpandableList()
    {
        for (int i = 0; i < myExpandableListAdapter.getGroupCount(); i++)
        {
                ClearAllMarkers();
                currentActiveMarker = activeMarker.NOMARKERS;
                expandableListView.collapseGroup(i);
        }

    }
}
