package com.magendanz.villanovamec;

/**
 * Object representing an event in a schedule
 */

public class ScheduleItem implements MecItem {
    private static final String type = "ScheduleEvent";

    private String event;
    private String time;
    private String location;

    /**
     * @param info String array with elements as following:
     *             message type, event description, time of event, location of event
     */
    public ScheduleItem(String[] info){
        this.event = info[1];
        this.time = info[2];
        this.location = info[3];
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
