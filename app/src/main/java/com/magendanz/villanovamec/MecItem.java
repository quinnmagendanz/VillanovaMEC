package com.magendanz.villanovamec;

/**
 * An object representing an entry in one of the MEC app fields
 */

public interface MecItem {

    /**
     * @return the title of the entry
     */
    public String getTitle();

    /**
     * @return the details and/or description of the entry
     */
    public String getDetails();

    /**
     * @return the location that the entry pertains to
     */
    public String getLocation();

}
