/**
 *   Sanicen
 *
 *   @author Emilio Jiménez del Moral
 *   Copyright (C) 2016 Emilio Jiménez del Moral
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package com.argenes.android.sanicen.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.argenes.android.sanicen.utils.NavigationManager;
import com.argenes.android.sanicen.utils.PermissionUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.CameraUpdateFactory;

import com.argenes.android.sanicen.db.DBHelper;
import com.argenes.android.sanicen.R;

import java.util.HashMap;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private DBHelper db;
    private String provinceCode;
    private String munCode;
    private String provinceName;
    private String munName;
    private String centerTypeName;
    private String centerTypeCode;
    private long [] specialities = null;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = new DBHelper(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        addZoomButtons();
        enableMyLocation();
        addCenters();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                NavigationManager.gotToDetailCenterActivity(MapActivity.this,
                                                            marker.getTitle(),
                                                            marker.getSnippet());

                }
            });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View myContentsView = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
                tvTitle.setText(marker.getTitle());
                return myContentsView;
            }
        });
    }

    private void addCenters(){
        Cursor cursor;

        HashMap<String, Integer> iconTypes = new HashMap<>();
        iconTypes.put(db.apCode, R.drawable.ap_marker);
        iconTypes.put(db.hoCode, R.drawable.ho_marker);
        iconTypes.put(db.auCode, R.drawable.au_marker);

        provinceCode = NavigationManager.getData(this, "provinceCode");
        provinceName = NavigationManager.getData(this, "provinceName");
        munCode = NavigationManager.getData(this, "munCode");
        munName = NavigationManager.getData(this, "munName");
        centerTypeName = NavigationManager.getData(this, "centerTypeName");
        centerTypeCode = NavigationManager.getData(this, "centerTypeCode");
        specialities = NavigationManager.getDataArray(this, "specialities");

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        cursor = db.getCenterTypeCursor(centerTypeCode, munCode, specialities);

        if(cursor.moveToFirst()){
            do{
                String name = centerTypeCode + ":  " + cursor.getString(cursor.getColumnIndex("name"));
                String snippet = db.getCenterInfo(cursor, provinceName, munName, centerTypeCode, centerTypeName);
                String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                LatLng position = new LatLng(Float.parseFloat(latitude),
                                             Float.parseFloat(longitude));
                builder.include(position);
                mMap.addMarker(new MarkerOptions()
                               .position(position)
                               .title(name)
                               .snippet(snippet)
                               .icon(BitmapDescriptorFactory.fromResource(iconTypes.get(centerTypeCode))));

            }while(cursor.moveToNext());

            if(cursor!=null && !cursor.isClosed()){
                cursor.close();
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
        }
        else {
            Toast toast = Toast.makeText(this, R.string.no_results, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void enableMyLocation(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
            //Toast toast = Toast.makeText(this, R.string.toast_location, Toast.LENGTH_SHORT);
            //toast.show();
        }else {
            mMap.setMyLocationEnabled(true);
        }

    }

    private  void addZoomButtons(){

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
        // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }
    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}
