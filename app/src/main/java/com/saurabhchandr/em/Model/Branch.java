package com.saurabhchandr.em.Model;

import java.util.List;

public class Branch {

    private String name;
    private String sName;
    private List<Semester> semesters;

    public Branch() {

    }

    public Branch(String name, String sName, List<Semester> semesters) {
        this.name = name;
        this.sName = sName;
        this.semesters = semesters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public List<Semester> getSemesters() {
        return semesters;
    }

    public void setSemesters(List<Semester> semesters) {
        this.semesters = semesters;
    }
}
