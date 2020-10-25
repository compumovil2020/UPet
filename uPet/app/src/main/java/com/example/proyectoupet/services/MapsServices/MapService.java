package com.example.proyectoupet.services.MapsServices;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapService {
    enum Order {CLOSEST , FIFO}

    private GoogleMap mMap;
    private RouteService routeService;
    Map<String, Polyline> polylines;

    public MapService(GoogleMap mMap){
        this.mMap = mMap;
        this.polylines = new HashMap<>();
        this.routeService = new RouteService();
    }

    public Marker addMarker(LatLng latLng, String title){
        return this.mMap.addMarker(new MarkerOptions().position(latLng).title(title));
    }

    public String getLatLngName(Activity context , LatLng latLng){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String address = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public Float calculateDistance(LatLng pos1, LatLng pos2){
        float[] results = new float[1];
        Location.distanceBetween(pos1.latitude , pos1.longitude,
                pos2.latitude, pos2.longitude, results);
        return results[0];
    }

    public void makeRoute(Activity activeActivity, List<LatLng> points, String order){
        if(points.size() < 2){
            return;
        }
        List<LatLng> auxPoints = order.equals(Order.CLOSEST) ? sortPoints(points) : points;

        for(int i = 1 ; i < points.size() ; i++){
            this.routeService.makeRoute(activeActivity,this.mMap,points.get(i-1),points.get(i),this.polylines);
        }
        this.routeService.makeRoute(activeActivity,this.mMap,points.get(points.size()-1),points.get(0),this.polylines);
    }

    private List<LatLng> sortPoints(List<LatLng> points){
        List<LatLng> pointsCopy = new ArrayList<>(points);
        List<LatLng> sortedList = new ArrayList<>();
        sortedList.add(pointsCopy.get(0));
        pointsCopy.remove(0);
        while(!pointsCopy.isEmpty()){
            LatLng closestPoint = getClosestPoint(sortedList.get(sortedList.size()-1),pointsCopy);
            sortedList.add(closestPoint);
            pointsCopy.remove(closestPoint);
        }
        return sortedList;
    }

    private LatLng getClosestPoint(LatLng point, List<LatLng> points){
        LatLng closest = points.get(0);
        Float distance = calculateDistance(point,closest);
        for(LatLng x : points){
            Float auxDis = calculateDistance(point,x);
            if(auxDis < distance){
                distance = auxDis;
                closest = x;
            }
        }
        return closest;
    }

    public void makeRoute(Activity activeActivity, LatLng startPoint, LatLng endingPoint){
        this.routeService.makeRoute(activeActivity,this.mMap,startPoint,endingPoint,this.polylines);
    }

    public Polyline getPolyline(LatLng startPoint, LatLng endingPoint){
        String key = startPoint.latitude+"-"+startPoint.longitude+"-"+endingPoint.latitude+"-"+endingPoint.longitude;
        return this.polylines.get(key);
    }

    public void removePolylineFromMap(LatLng startPoint, LatLng endingPoint){
        String key = startPoint.latitude+"-"+startPoint.longitude+"-"+endingPoint.latitude+"-"+endingPoint.longitude;
        Polyline polyline = this.polylines.get(key);
        if(polyline != null){
            polyline.remove();
        }
    }
}
