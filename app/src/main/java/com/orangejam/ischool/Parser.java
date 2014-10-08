package com.orangejam.ischool;

import android.util.Log;

import com.orangejam.ischool.model.Class;
import com.orangejam.ischool.model.Assignment;
import com.orangejam.ischool.model.Grade;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by bjornorri on 07/10/14.
 */
public class Parser {

    public static ArrayList<Class> parseClasses(String html) {
        ArrayList<Class> classes = new ArrayList<Class>();
        // Create a Jsoup Document object.
        Document page = Jsoup.parse(html);
        String query = "div.ruContentPage > center:eq(0) > table > tbody > tr";
        Elements nodes = page.select(query);

        if(nodes.size() > 0) {
            // Ignore irrelevant nodes (first two and the last)
            nodes.remove(nodes.size() - 1);
            for(int i = 0; i < 2; i++) {
                if(nodes.size() > 0) {
                    nodes.remove(0);
                } else {
                    // This should never happen. Return early.
                    return classes;
                }
            }

            // Extract the date row at the top of the timetable.
            Element dateRow = nodes.get(0);
            nodes.remove(0);

            // Put the date strings inside an array list.
            ArrayList<String> dateStrings = new ArrayList<String>();
            Elements dateColumns = dateRow.getElementsByTag("th");
            // Ignore first irrelevant column.
            dateColumns.remove(0);
            for(Element column : dateColumns) {
                String text = column.text();
                dateStrings.add(text);
            }

            // Create a date formatter to easily create Calendar objects.
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyyHH:mm");

            // Loop through the rows of the timetable.
            for(Element row : nodes) {
                // Split the row into columns.
                Elements columns = row.getElementsByTag("td");
                // Extract the column with the start and end time and then remove it.
                Element timeColumn = columns.remove(0);
                // Extract the strings for the start and end time.
                String timeText = timeColumn.text();
                String[] times = timeText.split("\u00a0"); // Split the string on &nbsp
                String startTime = times[0];
                String endTime = times[1];

                // Loop through the remaining columns of the row.
                for(int i = 0; i < columns.size(); i++) {
                    Element column = columns.get(i);
                    Elements classSpans = column.getElementsByTag("span");
                    if(classSpans.size() > 0) {
                        Calendar startDate = Calendar.getInstance();
                        Calendar endDate = Calendar.getInstance();
                        try {
                            startDate.setTime(formatter.parse(dateStrings.get(i) + startTime));
                            endDate.setTime(formatter.parse(dateStrings.get(i) + endTime));
                        } catch(ParseException e) {
                            Log.d("Parser", "ParseException", e);
                        }

                        // Create all the classes in this box.
                        for(Element classSpan : classSpans) {
                            String infoString = classSpan.attr("title");
                            String[] info = infoString.split("\n");
                            // Get the course name.
                            String courseName = "";
                            if(info.length > 0) {
                                courseName = info[0];
                            }
                            // Get the type.
                            String typeString = "";
                            if(info.length > 2) {
                                typeString = info[2];
                            }
                            String type;
                            // Could get more information here.

                            // Map the type string to the appropriate type.
                            if(typeString.equals("Fyrirlestrar")) {
                                type = Class.Lecture;
                            } else if(typeString.equals("Dæmatímar")) {
                                type = Class.Discussion;
                            } else if(typeString.equals("Viðtalstímar")) {
                                type = Class.Assistance;
                            } else if(typeString.equals("Lokapróf")) {
                                type = Class.Exam;
                            } else {
                                type = Class.Other;
                            }
                            // Get the location of the class.
                            String location = "";
                            Elements links = classSpan.getElementsByTag("a");
                            if(links.size() > 0) {
                                Elements smalls = links.first().getElementsByTag("small");
                                if(smalls.size() > 0) {
                                    Element small = smalls.last();
                                    String smallText = small.text();
                                    String[] parts = smallText.split(", ");
                                    location = parts[parts.length - 1];
                                }

                            }
                            // Create the class and add it to the array list. Prevent duplicates.
                            Class c = new Class(courseName, type, location, startDate, endDate);
                            if(!classes.contains(c)) {
                                classes.add(c);
                            }
                        }
                    }
                }
            }
        }
        // Sort the classes based on their start date.
        Collections.sort(classes);
        return classes;
    }

    public static ArrayList<Assignment> parseAssignments(String html) {
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        // Create a Jsoup Document object.
        Document page = Jsoup.parse(html);
        String query = "div.ruContentPage > center > table";
        Elements tables = page.select(query);
        // Quit ef there is no table.
        if(tables.size() == 0) {
            return assignments;
        }
        query = "tbody > tr";
        Elements nodes = tables.first().select(query);
        Element tableHeader = nodes.first();
        // If this is not the header, or the table does not contain 5 columns, quit.
        if(!tableHeader.className().equals("ruTableTitle") || tableHeader.children().size() != 5) {
            return assignments;
        }
        // Ignore the table header and the empty row at the end.
        nodes.remove(nodes.size() - 1);
        nodes.remove(0);

        // Create a date formatter to easily create Calendar objects.
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        // Loop through the rows of the table.
        for(Element row : nodes) {
            // Create an assignment for each row.
            String name = "";
            String courseName = "";
            Calendar dueDate = Calendar.getInstance();
            boolean handedIn = false;
            String URL = "";
            for(int i = 0; i < row.children().size(); i++) {
                Element column = row.children().get(i);
                // Fill in the data based on the column index.
                String text;
                switch(i) {
                    case 0:
                        try {
                            text = column.text();
                            dueDate.setTime(formatter.parse(text));
                        } catch(ParseException e) {
                            Log.d("Parser", "Parse Exception", e);
                        }
                        break;
                    case 1:
                        text = column.text();
                        handedIn = !(text.equals("Óskilað"));
                        break;
                    case 3:
                        text = column.text();
                        courseName = text;
                        break;
                    case 4:
                        Elements links = column.getElementsByTag("a");
                        if(links.size() > 0) {
                            Element link = links.first();
                            URL = link.attr("href");
                            text = link.text();
                            name = text;
                        }
                        break;
                    default:
                        break;
                }

            }
            // Create the assignment object and add it to the array list.
            Assignment assignment = new Assignment(name, courseName, dueDate, handedIn, URL);
            assignments.add(assignment);
        }
        for(Assignment a : assignments) {
            Log.d("", "Assignment: " + a.name);
        }
        return assignments;
    }

    public static ArrayList<Grade> parseGrades(String html) {
        ArrayList<Grade> grades = new ArrayList<Grade>();

        // Create a Jsoup Document object.
        Document page = Jsoup.parse(html);
        String query = "div.ruContentPage > center > table";
        Elements tables = page.select(query);
        // Quit ef there is no table.
        if(tables.size() == 0) {
            return grades;
        }
        query = "tbody > tr";
        Elements nodes = tables.last().select(query);
        // If there are less than 2 rows in the table it can't be the correct table.
        if(nodes.size() < 2) {
            return grades;
        }
        Element firstEntry = nodes.get(1);
        // If the entries in the table don't contain 4 columns, this can't be the correct table.
        if(firstEntry.children().size() != 4) {
            return grades;
        }
        // Ignore the empty row at the end.
        nodes.remove(nodes.size() - 1);

        String currentCourse = "";
        // Loop through all rows in the table.
        for(Element row : nodes) {
            String assignmentName = "";
            Float gradeValue = null;
            Integer firstRank = null;
            Integer lastRank = null;
            String feedback = "";
            String URL = "";
            Elements columns = row.children();
            boolean isHeader = false;
            for(int i = 0; i < columns.size(); i++) {
                isHeader = false;
                Element column = columns.get(i);
                if(column.tagName().equals("th")) {
                    String text = column.text();
                    String[] tokens = text.split(" ", 2);
                    currentCourse = tokens[1];
                    isHeader = true;
                } else {
                    String text;
                    switch(i) {
                        case 0:
                            // Name column.
                            Elements links = column.getElementsByTag("a");
                            if(links.size() > 0) {
                                Element link = links.first();
                                text = link.text();
                                assignmentName = text;
                                URL = link.attr("href");
                            }
                            break;
                        case 1:
                            // Grade column.
                            text = column.text();
                            if(!text.equals("\u00a0")) {
                                String[] gradeTokens = text.split(": ");
                                String gradeString = gradeTokens[gradeTokens.length - 1].replace(",", ".");
                                gradeValue = Float.parseFloat(gradeString);
                            }
                            break;
                        case 2:
                            // Rank column.
                            text = column.text();
                            if(!text.equals("\u00a0")) {
                                String[] rankTokens = text.split(": ");
                                String rankString = rankTokens[rankTokens.length - 1];
                                String[] rankBounds = rankString.split(" - ");
                                if (rankBounds.length == 1) {
                                    int rank = Integer.parseInt(rankBounds[0]);
                                    firstRank = rank;
                                    lastRank = rank;
                                } else if (rankBounds.length == 2) {
                                    firstRank = Integer.parseInt(rankBounds[0]);
                                    lastRank = Integer.parseInt(rankBounds[1]);
                                }
                            }
                            break;
                        case 3:
                            text = column.text();
                            if(!text.equals("\u00a0")) {
                                // Feedback column.
                                if (column.textNodes().size() > 0) {
                                    feedback = column.textNodes().get(0).getWholeText();
                                } else {
                                    feedback = text;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            // Create the grade and add it to the array list.
            if(!isHeader) {
                Grade grade = new Grade(assignmentName, currentCourse, gradeValue, firstRank, lastRank, feedback, URL);
                grades.add(grade);
            }
        }
        return grades;
    }
}
