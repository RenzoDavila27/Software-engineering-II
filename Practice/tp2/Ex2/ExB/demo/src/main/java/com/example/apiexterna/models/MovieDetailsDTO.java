package com.example.apiexterna.models;


import java.util.List;

public class MovieDetailsDTO {
    private String id;
    private String primaryTitle;
    private String originalTitle;
    private String description;
    private String primaryImage; // URL de la imagen principal
    private String releaseDate;
    private Integer runtimeMinutes;
    private Double averageRating; // ⭐️ Clave: el rating promedio
    private Integer numVotes;
    private List<String> genres;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPrimaryTitle() {
        return primaryTitle;
    }
    public void setPrimaryTitle(String primaryTitle) {
        this.primaryTitle = primaryTitle;
    }
    public String getOriginalTitle() {
        return originalTitle;
    }
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getPrimaryImage() {
        return primaryImage;
    }
    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public Integer getRuntimeMinutes() {
        return runtimeMinutes;
    }
    public void setRuntimeMinutes(Integer runtimeMinutes) {
        this.runtimeMinutes = runtimeMinutes;
    }
    public Double getAverageRating() {
        return averageRating;
    }
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    public Integer getNumVotes() {
        return numVotes;
    }
    public void setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
    }
    public List<String> getGenres() {
        return genres;
    }
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
    
    
}