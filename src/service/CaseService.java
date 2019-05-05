package service;

import model.*;

import java.util.*;

public class CaseService {
    private static CaseService instance = null;
    private int currentId;
    private Map<Integer, Case> cases;

    //singleton pattern
    private CaseService() {
        currentId = 0;
        cases = new TreeMap<>();
        List<Case> caseList = DbService.getInstance().getCases();
        for (Case c : caseList)
            cases.put(c.getId(), c);
        if (cases.isEmpty())
            currentId = 0;
        else
            currentId = Collections.max(cases.keySet());
    }

    public static CaseService getInstance() {
        if (instance == null)
            instance = new CaseService();
        return instance;
    }

    private void save() {
        DbService.getInstance()
                .updateCases(new ArrayList<>(cases.values()));
    }

    public int addCase(Patient patient) {
        if (patient.getPersonalMedic() == null)
            throw new IllegalArgumentException("No personal medic assigned");
        currentId++;
        cases.put(currentId, new Case(currentId, patient));
        save();
        return currentId;
    }

    public Case getCaseById(int id) {
        return cases.getOrDefault(id, null);
    }

    public Case addSymptomToCase(int caseId, Symptom symptom) {
        Case c = getCaseById(caseId);
        c.getSymptomList().addSymptom(symptom);
        save();
        return c;
    }

    public Case removeSymptomFromCase(int caseId, Symptom symptom) {
        Case c = getCaseById(caseId);
        c.getSymptomList().removeSymptom(symptom);
        save();
        return c;
    }

    public Case addMedicationToCase(int caseId, Medication medication) {
        Case c = getCaseById(caseId);
        c.getPrescription().addMedication(medication);
        save();
        return c;
    }

    public Case removeMedicationFromCase(int caseId, Medication medication) {
        Case c = getCaseById(caseId);
        c.getPrescription().removeMedication(medication);
        save();
        return c;
    }

    public Case addMedicToCase(int caseId, Medic medic) {
        Case c = getCaseById(caseId);
        if (!c.getOtherMedics().contains(medic))
            c.addMedic(medic);
        save();
        return c;
    }

    public Case setCaseCompleted(int caseId) {
        Case c = getCaseById(caseId);
        c.setCompleted();
        save();
        return c;
    }
}
