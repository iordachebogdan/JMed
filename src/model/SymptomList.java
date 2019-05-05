package model;

import java.io.Serializable;
import java.util.*;

public class SymptomList implements Snapshotable<Symptom>, Serializable {
    private Set<Symptom> symptomSet;
    private List<Event<Symptom>> events;

    SymptomList() {
        symptomSet = new HashSet<>();
        events = new ArrayList<>();
    }

    public void addSymptom(Symptom s) {
        symptomSet.add(s);
        events.add(new Event<>(Event.EventTypeEnum.ADD, s, new Date()));
    }

    public void removeSymptom(Symptom s) {
        symptomSet.remove(s);
        events.add(new Event<>(Event.EventTypeEnum.REMOVE, s, new Date()));
    }

    public List<Symptom> getSymptoms() {
        return new ArrayList<>(symptomSet);
    }

    @Override
    public List<Symptom> getBeforeDate(Date date) {
        Collections.sort(events);
        Set<Symptom> querySet = new HashSet<>();
        for (Event<Symptom> event : events) {
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
