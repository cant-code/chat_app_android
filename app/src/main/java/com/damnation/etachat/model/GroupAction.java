package com.damnation.etachat.model;

public class GroupAction {

    private String name;
    private int icon;

    public GroupAction(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
}
