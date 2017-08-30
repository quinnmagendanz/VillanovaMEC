package com.magendanz.villanovamec;

/**
 * Class represents a news article or announcement to be made
 */

public class NewsItem implements MecItem {
    private static final String type = "NewsStory";

    private String headline;
    private String description;
    private String location;

    /**
     * @param info String array with elements as following:
     *             message type, event headline, event description, location of event
     */
    public NewsItem(String[] info){
        this.headline = info[1];
        this.description = info[2];
        this.location = info[3];
    }

    @Override
    public String getTitle(){
        return headline;
    }

    @Override
    public String getDetails(){
        return description;
    }

    @Override
    public String getLocation(){
        return location;
    }

    /**
     * @param otherType a string describing the type of the associated object
     * @return if the type matches that of this object
     */
    public static boolean typeMatches(String otherType){
        return type.equals(type);
    }
}
