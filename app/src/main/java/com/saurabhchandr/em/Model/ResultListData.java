package com.saurabhchandr.em.Model;

public class ResultListData {

    private String title,category,updateDate,link;

    public ResultListData() {

    }

    public ResultListData(String title, String category, String updateDate, String link) {
        this.title = title;
        this.category = category;
        this.updateDate = updateDate;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
