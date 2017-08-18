package com.magendanz.villanovamec;

/**
 * Object representing an event in a schedule
 */

public class ScheduleItem implements MecItem {
    private static final String type = "ScheduleEvent";

    private String event;
    private String time;
    private String location;

    public ScheduleItem(String event, String time, String location){
        this.event = event;
        this.time = time;
        this.location = location;
    }

    @Override
    public String getTitle(){
        return event;
    }

    @Override
    public String getDetails(){
        return time;
    }

    @Override
    public String getLocation() {
        return location;
    }

    /**
     * @param otherType a string describing the type of the associated object
     * @return if the type matches that of this object
     */
    public static boolean typeMatches(String otherType){
        return type.equals(otherType);
    }
}
