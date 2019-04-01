package service;

import model.Case;
import model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CaseService {
    private static CaseService instance = null;
    private int currentId;
    private Map<Integer, Case> cases;

    //singleton pattern
    private CaseService() {
        currentId = 0;
        cases = new TreeMap<>();
    }

    public static CaseService getInstance() {
        if (instance == null)
            instance = new CaseService();
        return instance;
    }

    public int addCase(Patient patient) {
        if (patient.getPersonalMedic() == null)
            throw new IllegalArgumentException("No personal medic assigned");
        currentId++;
        cases.put(currentId, new Case(currentId, patient));
        return currentId;
    }

    public Case getCaseById(int id) {
        return cases.getOrDefault(id, null);
    }
}
