package com.sundeep.buttonoverlay.gesture;

public class ActionModel {

    private int actionIcon;
    private String actionTitle;

    public ActionModel(int actionIcon, String actionTitle) {
        this.actionIcon = actionIcon;
        this.actionTitle = actionTitle;
    }

    public int getActionIcon() {
        return actionIcon;
    }

    public void setActionIcon(int actionIcon) {
        this.actionIcon = actionIcon;
    }

    public String getActionTitle() {
        return actionTitle;
    }

    public void setActionTitle(String actionTitle) {
        this.actionTitle = actionTitle;
    }
}
