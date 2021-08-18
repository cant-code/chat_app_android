package com.damnation.etachat.model;

public class GroupAction {

    private final String name;
    private final int icon;

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
