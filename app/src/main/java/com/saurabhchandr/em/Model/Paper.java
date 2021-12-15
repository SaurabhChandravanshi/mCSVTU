package com.saurabhchandr.em.Model;

public class Paper {

    private String title,url,semName;
    private boolean isDownloaded;

    public Paper() {

    }

    public Paper(String title, String url, String semName, boolean isDownloaded) {
        this.title = title;
        this.url = url;
        this.semName = semName;
        this.isDownloaded = isDownloaded;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public String getSemName() {
        return semName;
    }

    public void setSemName(String semName) {
        this.semName = semName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
