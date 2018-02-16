package com.mu.smartdiffer;

import org.json.JSONObject;
/**
 * Created by arulsmv on 18/10/17.
 */
public class ReferenceEntry {
    private String referringFunction;
    private String referringFile;
    private int referringLine;
    private String referredFunction;
    private String refferedFile;
    private int referredLine;

    public ReferenceEntry(String referringFunction, String referringFile, int referringLine, String referredFunction, String refferedFile, int referredLine) {
        this.referringFunction = referringFunction;
        this.referringFile = referringFile;
        this.referringLine = referringLine;
        this.referredFunction = referredFunction;
        this.refferedFile = refferedFile;
        this.referredLine = referredLine;
    }

    public String getReferringFunction() {
        return referringFunction;
    }

    public void setReferringFunction(String referringFunction) {
        this.referringFunction = referringFunction;
    }

    public String getReferringFile() {
        return referringFile;
    }

    public void setReferringFile(String referringFile) {
        this.referringFile = referringFile;
    }

    public int getReferringLine() {
        return referringLine;
    }

    public void setReferringLine(int referringLine) {
        this.referringLine = referringLine;
    }

    public String getReferredFunction() {
        return referredFunction;
    }

    public void setReferredFunction(String referredFunction) {
        this.referredFunction = referredFunction;
    }

    public String getRefferedFile() {
        return refferedFile;
    }

    public void setRefferedFile(String refferedFile) {
        this.refferedFile = refferedFile;
    }

    public int getReferredLine() {
        return referredLine;
    }

    public void setReferredLine(int referredLine) {
        this.referredLine = referredLine;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("referringFunction", referringFunction);
        json.put("referringFile", referringFile);
        json.put("referringLine", referringLine);
        json.put("referredFunction", referredFunction);
        json.put("refferedFile", refferedFile);
        json.put("referredLine", referredLine);
        return json;
    }
}
