package com.saurabhchandr.em.Model;

import java.util.List;

public class BranchList {

    private List<Branch> branches;

    public BranchList() {

    }

    public BranchList(List<Branch> branches) {
        this.branches = branches;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
}
