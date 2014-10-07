package com.orangejam.ischool.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by bjornorri on 07/10/14.
 */
public class Class {

    public static final String Lecture = "Fyrirlestur";
    public static final String Discussion = "Dæmatími";
    public static final String Assistance = "Viðtalstími";
    public static final String Exam = "Próf";
    public static final String Other = "Annað";

    public String courseName;
    public String type;
    public String location;
    public Calendar startDate;
    public Calendar endDate;

    public Class(String courseName, String type, String location, Calendar startDate, Calendar endDate) {
        this.courseName = courseName;
        this.type = type;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isOver()
    {
        Calendar now = new GregorianCalendar();
        return now.after(this.endDate);
    }

    public boolean isNow()
    {
        Calendar now = new GregorianCalendar();
        return (now.after(this.startDate) && now.before(this.endDate));
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if(!o.getClass().equals(Class.class)) {
            return false;
        }
        Class otherClass = (Class)o;
        if(!otherClass.courseName.equals(this.courseName)) {
            return false;
        }
        if(!otherClass.type.equals(this.type)) {
            return false;
        }
        if(!otherClass.location.equals(this.location)) {
            return false;
        }
        if(!otherClass.startDate.equals(this.startDate)) {
            return false;
        }
        if(!otherClass.endDate.equals(this.endDate)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for(int i = 0; i < courseName.length(); i++) {
            hash += (int)courseName.charAt(i);
        }
        for(int i = 0; i < type.length(); i++) {
            hash += (int)type.charAt(i);
        }
        for(int i = 0; i < location.length(); i++) {
            hash += (int)location.charAt(i);
        }
        return hash;
    }
}
