package com.fioritech.demo.bussines.logic.geo;

public interface MapLinkBuilder {

    String forCoordinates(double lat, double lng);

    String forDirectionsTo(double lat, double lng);

    String forQuery(String freeText);
}
