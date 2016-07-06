package com.sundeep.buttonoverlay.gesture;


import android.gesture.Gesture;

public class GestureHolder {
    private Gesture gesture;
    private String gestureName;

    public GestureHolder(Gesture gesture, String gestureName) {
        this.gesture = gesture;
        this.gestureName = gestureName;
    }

    public GestureHolder() {
    }

    public Gesture getGesture() {
        return gesture;
    }

    public void setGesture(Gesture gesture) {
        this.gesture = gesture;
    }

    public String getGestureName() {
        return gestureName;
    }

    public void setGestureName(String gestureName) {
        this.gestureName = gestureName;
    }
}
