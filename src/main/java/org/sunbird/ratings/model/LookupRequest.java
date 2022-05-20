package org.sunbird.ratings.model;

public class LookupRequest {

    private String activityId;
    private String activityType;
    private Float rating;
    private int limit;
    private Long updateOn;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Long getUpdateOn() {
        return updateOn;
    }

    public void setUpdateOn(Long updateOn) {
        this.updateOn = updateOn;
    }
}
