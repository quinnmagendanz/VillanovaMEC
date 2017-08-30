package com.magendanz.villanovamec;

/**
 * Object representing a point of contact to send text messages to
 */

public class PointOfContact implements MecItem {
    private static final String type = "PointOfContact";

    private String name;
    private String number;
    private String location;

    /**
     * @param info String array with elements as following:
     *             message type, name of POC, phone number of POC, location of POC
     */
    public PointOfContact(String[] info) {
        this.name = info[1];
        this.number = info[2];
        this.location = info[3];
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
