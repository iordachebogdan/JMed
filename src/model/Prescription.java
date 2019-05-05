package model;

import java.io.Serializable;
import java.util.*;

public class Prescription implements Snapshotable<Medication>, Serializable {
    private Set<Medication> medicationSet;
    private List<Event<Medication>> events;

    Prescription() {
        medicationSet = new HashSet<>();
        events = new ArrayList<>();
    }

    public void addMedication(Medication m) {
        medicationSet.add(m);
        events.add(new Event<>(Event.EventTypeEnum.ADD, m, new Date()));
    }

    public void removeMedication(Medication m) {
        medicationSet.remove(m);
        events.add(new Event<>(Event.EventTypeEnum.REMOVE, m, new Date()));
    }

    public List<Medication> getMedication() {
        return new ArrayList<>(medicationSet);
    }

    @Override
    public List<Medication> getBeforeDate(Date date) {
        Collections.sort(events);
        Set<Medication> querySet = new HashSet<>();
        for (Event<Medication> event : events) {
            if (event.getDate().before(date)) {
                if (event.getType() == Event.EventTypeEnum.ADD)
                    querySet.add(event.getItem());
                else
                    querySet.remove(event.getItem());
            }
        }
        return new ArrayList<>(querySet);
    }
}
