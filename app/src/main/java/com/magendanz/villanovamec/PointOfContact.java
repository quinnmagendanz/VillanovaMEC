package com.magendanz.villanovamec;

/**
 * Object representing a point of contact to send text messages to
 */

public class PointOfContact implements MecItem {
    private static final String type = "PointOfContact";

    private String name;
    private String number;
    private String location;

    public PointOfContact(String name, String number, String location) {
        this.name = name;
        this.number = number;
        this.location = location;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getDetails() {
        return number;
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
