package com.chinasofti.framework.ssm.service.info;

import java.util.Date;

public class MovieInfo {

    private Integer movieId;

    private String movieName;

    private Date releaseDate;

    private String direct;

    private String protagonist;

    private String language;


    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getProtagonist() {
        return protagonist;
    }

    public void setProtagonist(String protagonist) {
        this.protagonist = protagonist;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "MovieInfo{" +
                "movieId=" + movieId +
                ", movieName='" + movieName + '\'' +
                ", releaseDate=" + releaseDate +
                ", direct='" + direct + '\'' +
                ", protagonist='" + protagonist + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
