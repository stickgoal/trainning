package me.maiz.game.myairbattle.model;

import java.io.Serializable;
import java.util.Date;

public class Score implements Serializable, Comparable<Score> {
    private static final long serialVersionUID = 1L;
    private Date scoreDateTime;
    private int score;
    private long lastSeconds;

    public Score() {
	super();
    }

    public Score(Date scoreDateTime, int score, long lastSeconds) {
	super();
	this.scoreDateTime = scoreDateTime;
	this.score = score;
	this.lastSeconds = lastSeconds;
    }

    public Date getScoreDateTime() {
	return scoreDateTime;
    }

    public void setScoreDateTime(Date scoreDateTime) {
	this.scoreDateTime = scoreDateTime;
    }

    public int getScore() {
	return score;
    }

    public void setScore(int score) {
	this.score = score;
    }

    public long getLastSeconds() {
	return lastSeconds;
    }

    public void setLastSeconds(long lastSeconds) {
	this.lastSeconds = lastSeconds;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + score;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Score other = (Score) obj;
	if (score != other.score)
	    return false;
	return true;
    }

    @Override
    public int compareTo(Score score) {
	return this.score - score.getScore();
    }

}
