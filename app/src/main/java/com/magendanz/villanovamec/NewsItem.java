package com.magendanz.villanovamec;

/**
 * Class represents a news article or announcement to be made
 */

public class NewsItem implements MecItem {
    private static final String type = "NewsStory";

    private String headline;
    private String description;
    private String location;

    public NewsItem(String headline, String description, String location){
        this.headline = headline;
        this.description = description;
        this.location = location;
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
