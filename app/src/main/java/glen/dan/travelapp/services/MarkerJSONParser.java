package glen.dan.travelapp.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarkerJSONParser {

    //gets a JSONObject and returns a list
    public List<HashMap<String,String>> parse(JSONObject jObject){

        JSONArray jMarkers = null;
        try {
            jMarkers = jObject.getJSONArray("map_markers");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getMarkers(jMarkers);
    }

    private List<HashMap<String, String>> getMarkers(JSONArray jMarkers){
        int markersCount = jMarkers.length();
        List<HashMap<String, String>> markersList = new ArrayList<>();
        HashMap<String, String> marker;

        //takes each marker, parses and adds to list object
        for(int i=0; i<markersCount;i++){
            try {
                //call getMarker with marker JSON object to parse the marker
                marker = getMarker((JSONObject)jMarkers.get(i));
                markersList.add(marker);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return markersList;
    }

    /** Parsing the Marker JSON object */
    private HashMap<String, String> getMarker(JSONObject jMarker){

        HashMap<String, String> marker = new HashMap<>();
        String lat = "-NA-";
        String lng ="-NA-";
        String username = "-NA-";
        String warning = "-NA-";
        String description = "-NA-";

        try {
            // Extracting latitude, if available
            if(!jMarker.isNull("latitude")){
                lat = jMarker.getString("latitude");
            }

            // Extracting longitude, if available
            if(!jMarker.isNull("longitude")){
                lng = jMarker.getString("longitude");
            }

            // Extracting username, if available
            if(!jMarker.isNull("username")){
                username = jMarker.getString("username");
            }

            // Extracting warning, if available
            if(!jMarker.isNull("type_of_warning")){
                warning = jMarker.getString("type_of_warning");
            }

            // Extracting description, if available
            if(!jMarker.isNull("description")){
                description = jMarker.getString("description");
            }

            marker.put("latitude", lat);
            marker.put("longitude", lng);
            marker.put("username", username);
            marker.put("warning", warning);
            marker.put("description", description);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return marker;
    }
}
