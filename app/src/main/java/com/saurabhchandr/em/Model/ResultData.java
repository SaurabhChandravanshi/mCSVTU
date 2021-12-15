package com.saurabhchandr.em.Model;

public class ResultData {

    private String regLink,rtLink,rvLink,rrvLink;

    public ResultData() {

    }

    public ResultData(String regLink, String rtLink, String rvLink, String rrvLink) {
        this.regLink = regLink;
        this.rtLink = rtLink;
        this.rvLink = rvLink;
        this.rrvLink = rrvLink;
    }

    public String getRegLink() {
        return regLink;
    }

    public void setRegLink(String regLink) {
        this.regLink = regLink;
    }

    public String getRtLink() {
        return rtLink;
    }

    public void setRtLink(String rtLink) {
        this.rtLink = rtLink;
    }

    public String getRvLink() {
        return rvLink;
    }

    public void setRvLink(String rvLink) {
        this.rvLink = rvLink;
    }

    public String getRrvLink() {
        return rrvLink;
    }

    public void setRrvLink(String rrvLink) {
        this.rrvLink = rrvLink;
    }
}
