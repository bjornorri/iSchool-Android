package com.orangejam.ischool.model;

/**
 * Created by bjornorri on 07/10/14.
 */
public class Grade {

    public String assignmentName;
    public String courseName;
    public Float grade;
    public Integer firstRank;
    public Integer lastRank;
    public String feedback;
    public String URL;

    public Grade(String assignmentName, String courseName, Float grade, Integer firstRank, Integer lastRank, String feedback, String URL) {
        this.assignmentName = assignmentName;
        this.courseName = courseName;
        this.grade = grade;
        this.firstRank = firstRank;
        this.lastRank = lastRank;
        this.feedback = feedback;
        this.URL = URL;
    }
}
