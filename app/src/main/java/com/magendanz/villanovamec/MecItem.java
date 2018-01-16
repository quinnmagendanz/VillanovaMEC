package com.magendanz.villanovamec;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.util.HashMap;

/**
 * Object representing a piece of information for the MEC
 */

@DynamoDBTable(tableName = "MecItems")
public class MecItem implements Comparable<MecItem>{
    public static final int PointOfContactItem = 0;
    public static final int LocationItem = 1;
    public static final int CalendarItem = 2;
    public static final int NoticeItem = 3;

    public static final HashMap<String, Integer> months;
    static {
        months = new HashMap<>();
        months.put("JAN", 1);
        months.put("FEB", 2);
        months.put("MAR", 3);
        months.put("APR", 4);
        months.put("MAY", 5);
        months.put("JUN", 6);
        months.put("JUL", 7);
        months.put("AUG", 8);
        months.put("SEP", 9);
        months.put("OCT", 10);
        months.put("NOV", 11);
        months.put("DEC", 12);
    }

    private String title;
    private String mainField;
    private String secondaryField;
    private String time;
    private String location;
    private Integer type;

    @DynamoDBHashKey(attributeName = "Title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    @DynamoDBAttribute(attributeName = "MainField")
    public String getMainField() {
        return mainField;
    }

    public void setMainField(String newMain) {
        mainField = newMain;
    }

    @DynamoDBAttribute(attributeName = "SecondaryField")
    public String getSecondaryField() {
        return secondaryField;
    }

    public void setSecondaryField(String newSecond) {
        secondaryField = newSecond;
    }

    /**
     * @return time of MecItem in format APR/14/0800-APR/14/0900 if exists
     */
    @DynamoDBAttribute(attributeName = "Time")
    public String getTime() {
        return time;
    }

    /**
     * @param newMonthDayTime time of MecItem in format APR/14/0800-APR/14/0900 if exists
     */
    public void setTime(String newMonthDayTime) {
        time = newMonthDayTime;
    }

    // APR/14/0800-APR/14/0900
    @DynamoDBIgnore
    public String getStartTime() {
        return time.substring(7,11);
    }

    @DynamoDBIgnore
    public String getStartDay() {
        return time.substring(4,6);}

    @DynamoDBIgnore
    public String getStartMonth() {
        return time.substring(0,3);
    }

    @DynamoDBIgnore
    public String getEndTime() {
        return time.substring(19,23);
    }

    @DynamoDBIgnore
    public String getEndDay() {
        return time.substring(16,18);
    }

    @DynamoDBIgnore
    public String getEndMonth() {
        return time.substring(12,15);
    }

    @DynamoDBIgnore
    public void setTime(String sMonth, String sDay, String sTime,
                        String eMonth, String eDay, String eTime) {
        time = sMonth + "/" + sDay + "/" + sTime + "-" +eMonth + "/" + eDay + "/" + eTime;
    }

    @DynamoDBAttribute(attributeName = "Location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String newLocation) {
        location = newLocation;
    }

    /**
     * @return 0 if point of contact, 1 if location, 2 if calendar event, 3 if notice
     */
    @DynamoDBAttribute(attributeName = "MecItemType")
    public Integer getMecItemType() {
        return type;
    }

    public void setMecItemType(Integer newType) {
        type = newType;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo(MecItem that) {
        int monthsDiff = months.get(this.getStartMonth()) - months.get(that.getStartMonth());
        if (monthsDiff != 0) {
            return monthsDiff;
        }
        int dayDiff = Integer.parseInt(this.getStartDay()) - Integer.parseInt(that.getStartDay());
        if (dayDiff != 0) {
            return dayDiff;
        }
        return Integer.parseInt(this.getStartTime()) - Integer.parseInt(that.getStartTime());
    }
}
