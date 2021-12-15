package com.saurabhchandr.em.Model;

public class Semester {
    private String semName, paper, syllabus,scheme;

    public Semester() {

    }


    public Semester(String semName, String paper, String syllabus, String scheme) {
        this.semName = semName;
        this.paper = paper;
        this.syllabus = syllabus;
        this.scheme = scheme;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getSemName() {
        return semName;
    }

    public void setSemName(String semName) {
        this.semName = semName;
    }

    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }
}
