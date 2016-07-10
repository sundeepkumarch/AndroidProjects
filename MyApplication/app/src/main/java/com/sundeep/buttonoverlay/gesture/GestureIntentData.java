package com.sundeep.buttonoverlay.gesture;


public class GestureIntentData {

    public String id;//gesturename
    public String intentAction;
    public int intentFlag;
    public String intentURI;

    public GestureIntentData() {
    }

    public GestureIntentData(String id, String intentAction, int intentFlag, String intentURI) {
        this.id = id;
        this.intentAction = intentAction;
        this.intentFlag = intentFlag;
        this.intentURI = intentURI;
    }
}
