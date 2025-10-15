package com.tienda.app.business.logic.service;

import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {

    public double[] obtenerCoordenadas(String direccion) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + direccion.replace(" ", "+");
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // Parsear JSON
            org.json.JSONArray jsonArray = new org.json.JSONArray(response);
            if (jsonArray.length() > 0) {
                JSONObject obj = jsonArray.getJSONObject(0);
                double lat = obj.getDouble("lat");
                double lon = obj.getDouble("lon");
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[]{0, 0}; // valor por defecto
    }
}
