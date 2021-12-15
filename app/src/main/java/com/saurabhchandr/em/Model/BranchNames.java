package com.saurabhchandr.em.Model;

import java.util.ArrayList;
import java.util.List;

public class BranchNames {
    public List<String> bTechBranches = new ArrayList<>();

    public BranchNames() {
        bTechBranches.add("Computer Science & Engineering");
        bTechBranches.add("Information Technology");
        bTechBranches.add("Mechanical Engineering");
        bTechBranches.add("Civil Engineering");
        bTechBranches.add("Electronic & Telecommunication Engineering");
        bTechBranches.add("Electrical Engineering");
        bTechBranches.add("Mining Engineering");
        bTechBranches.add("Mechatronics");
        bTechBranches.add("Bio-Technology Engineering");
        bTechBranches.add("Other Branch");
        bTechBranches.add("First Year Common to all Branches");
    }

    public List<String> getBTechBranches() {
        return bTechBranches;
    }
}
