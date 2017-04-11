/*
Class for storing objects from the contacts database.
 */

package com.example.craig.ssgps;



public class SingleItem {
    private String name;
    private int id;
    private int number;
    private int priority;


    public SingleItem(String name, int id, int number, int priority){
        this.name=name;
        this.id=id;
        this.number=number;
        this.priority=priority;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public int getPriority() {
        return priority;
    }
}
