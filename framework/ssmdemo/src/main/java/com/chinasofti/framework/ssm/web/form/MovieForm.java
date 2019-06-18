package com.chinasofti.framework.ssm.web.form;

import java.util.Date;

public class MovieForm {

    private int movieId;

    private String movieName;

    private String director;

    private String protagonist;

    private Date releaseDate;

    private String language;

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getProtagonist() {
        return protagonist;
    }

    public void setProtagonist(String protagonist) {
        this.protagonist = protagonist;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }


    @Override
    public String toString() {
        return "MovieForm{" +
                "movieName='" + movieName + '\'' +
                "director='" + director + '\'' +
                ", protagonist='" + protagonist + '\'' +
                ", releaseDate=" + releaseDate +
                ", language='" + language + '\'' +
                '}';
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}
