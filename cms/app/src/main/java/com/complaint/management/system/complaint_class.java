package com.complaint.management.system;

public class complaint_class {
    String Date,Main_problem,Sub_problem,Status,Id,Key;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getDescription() {
        return Description;
    }

    String Description;


    public String getId() {
        return Id;
    }

    public String getDate() {
        return Date;
    }

    public String getMain_problem() {
        return Main_problem;
    }

    public String getSub_problem() {
        return Sub_problem;
    }

    public String getStatus() {
        return Status;
    }
}
