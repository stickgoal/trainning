package me.maiz.trainning.framework.ssm.dal.model;

import java.math.BigDecimal;

public class Road {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column TJS_ROAD.ROAD_ID
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    private BigDecimal roadId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column TJS_ROAD.ROAD_NAME
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    private String roadName;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column TJS_ROAD.ROAD_ID
     *
     * @return the value of TJS_ROAD.ROAD_ID
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    public BigDecimal getRoadId() {
        return roadId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column TJS_ROAD.ROAD_ID
     *
     * @param roadId the value for TJS_ROAD.ROAD_ID
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    public void setRoadId(BigDecimal roadId) {
        this.roadId = roadId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column TJS_ROAD.ROAD_NAME
     *
     * @return the value of TJS_ROAD.ROAD_NAME
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    public String getRoadName() {
        return roadName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column TJS_ROAD.ROAD_NAME
     *
     * @param roadName the value for TJS_ROAD.ROAD_NAME
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    public void setRoadName(String roadName) {
        this.roadName = roadName == null ? null : roadName.trim();
    }
}