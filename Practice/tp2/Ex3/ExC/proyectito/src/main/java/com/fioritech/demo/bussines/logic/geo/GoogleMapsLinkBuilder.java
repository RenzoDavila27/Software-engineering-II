package com.fioritech.demo.bussines.logic.geo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class GoogleMapsLinkBuilder implements MapLinkBuilder {

    @Value("${maps.google.search-base:https://www.google.com/maps/search/?api=1&query=}")
    private String searchBase;

    @Value("${maps.google.directions-base:https://www.google.com/maps/dir/?api=1&destination=}")
    private String directionsBase;

    @Override
    public String forCoordinates(double lat, double lng) {
        validate(lat, lng);
        return searchBase + formatLatLng(lat, lng);
    }

    @Override
    public String forDirectionsTo(double lat, double lng) {
        validate(lat, lng);
        return directionsBase + formatLatLng(lat, lng);
    }

    @Override
    public String forQuery(String freeText) {
        if (freeText == null || freeText.isBlank()) {
            throw new IllegalArgumentException("El texto de búsqueda no puede ser vacío");
        }
        String q = URLEncoder.encode(freeText.trim(), StandardCharsets.UTF_8);
        return searchBase + q;
    }

    private String formatLatLng(double lat, double lng) {
        return String.format(Locale.US, "%f,%f", lat, lng);
    }

    private void validate(double lat, double lng) {
        if (Double.isNaN(lat) || Double.isNaN(lng)) {
            throw new IllegalArgumentException("Lat/Lng inválidos (NaN)");
        }
        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("Latitud fuera de rango (-90 a 90)");
        }
        if (lng < -180 || lng > 180) {
            throw new IllegalArgumentException("Longitud fuera de rango (-180 a 180)");
        }
    }
}
