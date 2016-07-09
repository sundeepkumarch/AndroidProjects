package com.sundeep.buttonoverlay.gesture;

public class ActionModel {

    private int actionIcon;
    private String actionTitle;
    private String actionType;

    public ActionModel(int actionIcon, String actionTitle, String actionType) {
        this.actionIcon = actionIcon;
        this.actionTitle = actionTitle;
        this.actionType = actionType;
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

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
