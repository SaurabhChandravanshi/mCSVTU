package com.saurabhchandr.em.Model;

public class CTPaper {
    private String subject,branch,semester,college,whichCT,fileUrl,visibility,ownerUid;

    public CTPaper() {

    }

    public CTPaper(String subject, String branch, String semester, String college, String whichCT, String fileUrl, String visibility, String ownerUid) {
        this.subject = subject;
        this.branch = branch;
        this.semester = semester;
        this.college = college;
        this.whichCT = whichCT;
        this.fileUrl = fileUrl;
        this.visibility = visibility;
        this.ownerUid = ownerUid;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getWhichCT() {
        return whichCT;
    }

    public void setWhichCT(String whichCT) {
        this.whichCT = whichCT;
    }
}
