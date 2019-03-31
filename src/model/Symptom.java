package model;

import java.util.Date;

public class Symptom {
    private String description;
    private Date firstAppearance;

    public Symptom(String description, Date firstAppearance) {
        this.description = description;
        this.firstAppearance = firstAppearance;
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
