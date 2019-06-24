package com.example.googlemaps;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googleServicesAvailable()) {
            Toast.makeText(this, "Perfect!!", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
            initMap();
        } else {
            // No Google Maps Layout (not supported)
        }
    }

    /* method to initialize the map */
    private void initMap() {
        Log.e("initMAp", "INITMAP INITMAP");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    // Check for Play Services
    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();

        /* isAvailable: returns 3 values*/

        int isAvailable = api.isGooglePlayServicesAvailable(this);

        // If the connection is successful
        if (isAvailable == ConnectionResult.SUCCESS) {

            Log.e("Success", "in the success condition");

            return true;
        }
        // if the error is resolvable,
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }
        // if the connection is failed
        else {
            Toast.makeText(this, "Can't connect to play service", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /* this method is called from mapFragment.getMapAsync(this)
     *
     * Once an instance of this interface is set on a MapFragment or MapView object, the onMapReady(GoogleMap)
     * method is triggered when the map is ready to be used and provides a non-null instance of GoogleMap.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install it, and the
     * onMapReady(GoogleMap) method will only be triggered when the user has installed it and returned to the app.*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // to customize the window, customization has to be done when the map is initialized

        // here checking if the google map exists or not
        if (mGoogleMap != null) {

            // on long press, want to add marker
            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    MainActivity.this.setMarker("Local", latLng.latitude, latLng.longitude);

                }
            });

            // method to drag and drop the marker from one place to another
            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    LatLng ll = marker.getPosition();
                    List<Address> list = null;
                    try {
                        list = geocoder.getFromLocation(ll.latitude, ll.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address add = list.get(0);
                    marker.setTitle(add.getLocality());
                    marker.showInfoWindow();
                }
            });

            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView tvLocality = (TextView)v.findViewById(R.id.tv_locality);
                    TextView tvLat = (TextView)v.findViewById(R.id.tv_lat);
                    TextView tvLng = (TextView)v.findViewById(R.id.tv_lng);
                    TextView tvSnippet = (TextView)v.findViewById(R.id.tv_snippet);

                    // get the latlng from the marker passed
                    LatLng ll = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvLat.setText("Latitude: " + ll.latitude);
                    tvLng.setText("Longitude: " + ll.longitude);
                    tvSnippet.setText(marker.getSnippet());

                    return v;
                }
            });
        }


        //  goToLocation(39.000761,-76.8826297);
         goToLocationZoom(39.000761,-76.8826297, 15);

        // this section is not working yet 06/13/19
        // If the android version is M then ask for checkPermission otherwise display the location
//       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//        }


        // PERMISSION
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {ACCESS_FINE_LOCATION}, 1);
        }

        Log.e("Location", "above location enabled section");
//            mGoogleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           mGoogleMap.setMyLocationEnabled(true);

        }
        else {
            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Rationale displayed", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Never ask again selected",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* this method is to just locate the place according to latitude and longitude
        private void goToLocation(double latitude, double longitude) {
        LatLng ll = new LatLng(latitude, longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);
    }
    */

    /* This method adds the zoom parameter that helps to zoom in the place you are looking for*/
    private void goToLocationZoom(double latitude, double longitude, float zoom) {
        LatLng ll = new LatLng(latitude, longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);
    }

    Marker marker;

    /* onClick button method for GO button
     *
     * geolocate is used to used to detect and stream location data to a live-updating map to smoothly watch location
     * updates as they change in the real world
     */
    public void geoLocate(View view) throws IOException {

        EditText edit_text = (EditText)findViewById(R.id.editText);

        // string that holds input that user entered
        String location = edit_text.getText().toString();

        // Geocoder is a class that converts the given string to latitude and longitude
        Geocoder geocoder = new Geocoder(this);

        // list of addresses we ll get from geocoder by calling a function getFromLocation
        List<Address> list = geocoder.getFromLocationName(location, 1);
        Address address = list.get(0);

        // will give the locality of the particular address that user fetches
        String locality = address.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double latitude = address.getLatitude();
        double longitude = address.getLongitude();
        goToLocationZoom(latitude, longitude, 15);

        // calling setMarker method to set the marker events in googlemap
        setMarker(locality, latitude, longitude);

    }

    /* Uncomment this circle and its method when you want to display circle in the map
       Circle circle;
    */

    // Initializing two markers to draw a line from one marker to another
//    Marker marker1, marker2;
//    Polyline line;


    // Drawing polygon
    ArrayList<Marker> markers = new ArrayList<Marker>();
    static final int POLYGON_POINTS = 5;
    Polygon shape;

    /* method to add the marker in the map*/
    private void setMarker(String locality, double latitude, double longitude) {
        // to remove the previous marker and only have the present marker

        /* Uncomment this while using marker and circle only
        if (marker != null) {
            removeEverything();
        }*/


        // drawing polygon part
        if (markers.size() == POLYGON_POINTS){
            removeEverything();
        }


        // to add a marker (a red drop is the default one), we need to call MarkerOptions and create object of options
        MarkerOptions options = new MarkerOptions().title(locality)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)) // customizing the icon color
                            //    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                                .draggable(true)
                                .position(new LatLng(latitude, longitude))
                                .snippet("I am here");

       // marker = mGoogleMap.addMarker(options);


        /* In this section, we add markers if they are null and then draw a line when two markers are
         * pointed in the google map
         *
         * comment this if you do not want to have a line between the markers
         * */

//        if (marker1 == null) {
//            marker1 = mGoogleMap.addMarker(options);
//        } else if (marker2 == null) {
//            marker2 = mGoogleMap.addMarker(options);
//            drawLine();
//        } else {
//            removeEverything();
//            marker1 = mGoogleMap.addMarker(options);
//        }



        // calling drawCircle method in the setMarker method
       // circle = drawCircle(new LatLng(latitude, longitude));


        // drawing polygon part
        markers.add(mGoogleMap.addMarker(options));

        // want to keep adding the markers until the size of the array is POLYGON_POINTS which is 5
        if (markers.size() == POLYGON_POINTS) {
            drawPolygon();
        }
    }

    private void drawPolygon() {

        PolygonOptions options = new PolygonOptions()
                                .fillColor(0x330000FF)
                                .strokeWidth(3)
                                .strokeColor(Color.RED);

        for (int i=0; i<POLYGON_POINTS; i++) {
            options.add(markers.get(i).getPosition());
        }
        shape = mGoogleMap.addPolygon(options);
    }

    // method to draw a line from one point to another
//    private void drawLine() {
//
//        PolylineOptions options = new PolylineOptions()
//                                .add(marker1.getPosition())
//                                .add(marker2.getPosition())
//                                .color(Color.BLUE)
//                                .width(3);
//
//        line = mGoogleMap.addPolyline(options);
//    }

    // to add a circle, create an object of option for CircleOptions
//    private Circle drawCircle(LatLng latLng) {
//
//        CircleOptions options = new CircleOptions()
//                                .center(latLng)
//                                .radius(100)
//                                .fillColor(0x33FF0000)
//                                .strokeColor(Color.BLUE)
//                                .strokeWidth(3);
//
//        return mGoogleMap.addCircle(options);
//    }

    private void removeEverything() {
        /* This section is for the marker and circle removal only
        marker.remove();
        marker = null;
        circle.remove();
        circle = null;*/

       /* Uncomment this section when drawing line from one marker to another
        marker1.remove();
        marker1 = null;
        marker2.remove();
        marker2 = null; */

       /* This section is for drawing polygon part */
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        shape.remove();
        shape = null;
    }

    // method for option menu, the menu.xml contains the type of maps we want to display
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;

            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;

            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            default:
               break;

        }
        return super.onOptionsItemSelected(item);
    }
}
