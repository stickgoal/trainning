package com.chinasofti.framework.ssm.web.form;

import java.util.Date;

public class MovieQueryForm {

    private String director;

    private String protagonist;

    private Date releaseDateBegin;

    private Date releaseDateEnd;

    private Integer a;

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

    public Date getReleaseDateBegin() {
        return releaseDateBegin;
    }

    public void setReleaseDateBegin(Date releaseDateBegin) {
        this.releaseDateBegin = releaseDateBegin;
    }

    public Date getReleaseDateEnd() {
        return releaseDateEnd;
    }

    public void setReleaseDateEnd(Date releaseDateEnd) {
        this.releaseDateEnd = releaseDateEnd;
    }

    @Override
    public String toString() {
        return "MovieQueryForm{" +
                "director='" + director + '\'' +
                "a='" + a + '\'' +
                ", protagonist='" + protagonist + '\'' +
                ", releaseDateBegin=" + releaseDateBegin +
                ", releaseDateEnd=" + releaseDateEnd +
                '}';
    }

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }
}
