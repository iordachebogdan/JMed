package model;

import java.util.Date;

public class Symptom {
    private int id;
    private String description;
    private Date firstAppearance;

    public Symptom(int id, String description, Date firstAppearance) {
        this.id = id;
        this.description = description;
        this.firstAppearance = firstAppearance;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getFirstAppearance() {
        return firstAppearance;
    }

    public void setFirstAppearance(Date firstAppearance) {
        this.firstAppearance = firstAppearance;
    }
}
