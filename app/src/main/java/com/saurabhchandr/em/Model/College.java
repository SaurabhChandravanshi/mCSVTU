package com.saurabhchandr.em.Model;

import java.util.ArrayList;
import java.util.List;

public class College {

    private String id,name,location;

    public College() {

    }

    public College(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static class CollegeList {
        private List<College> colleges = new ArrayList<>();

        public CollegeList() {

        }

        public CollegeList(List<College> colleges) {
            this.colleges = colleges;
        }

        public List<College> getColleges() {
            return colleges;
        }

        public void setColleges(List<College> colleges) {
            this.colleges = colleges;
        }
    }
}
