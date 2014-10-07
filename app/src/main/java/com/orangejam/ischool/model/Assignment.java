package com.orangejam.ischool.model;

import java.util.Calendar;

/**
 * Created by bjornorri on 07/10/14.
 */
public class Assignment {

    public String name;
    public String courseName;
    public Calendar dueDate;
    public boolean handedIn;
    public String URL;

    public Assignment(String name, String courseName, Calendar dueDate, boolean handedIn, String URL) {
        this.name = name;
        this.courseName = courseName;
        this.dueDate = dueDate;
        this.handedIn = handedIn;
        this.URL = URL;
    }
}
