package model;

import java.io.Serializable;
import java.util.Date;

public class Symptom implements Serializable {
    private final String description;
    private final Date firstAppearance;

    public Symptom(String description, Date firstAppearance) {
        this.description = description;
        this.firstAppearance = firstAppearance;
    }

    public String getDescription() {
        return description;
    }

    public Date getFirstAppearance() {
        return firstAppearance;
    }

    @Override
    public String toString() {
        return description;
    }
}
