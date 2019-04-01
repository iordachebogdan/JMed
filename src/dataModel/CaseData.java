package dataModel;

import model.Case;
import model.Medic;
import model.Medication;
import model.Symptom;

import java.util.List;
import java.util.stream.Collectors;

public class CaseData {
    private final int id;
    private final int patientId;
    private final int ownerMedicId;
    private final List<Integer> otherMedicIds;

    private final boolean completed;

    private final List<Medication> prescription;
    private final List<Symptom> symptomList;

    public CaseData(Case c) {
        this.id = c.getId();
        this.patientId = c.getPatient().getId();
        this.ownerMedicId = c.getOwnerMedic().getId();
        this.otherMedicIds = c.getOtherMedics().stream().map(Medic::getId).collect(Collectors.toList());
        this.completed = c.isCompleted();
        this.prescription = c.getPrescription().getMedication();
        this.symptomList = c.getSymptomList().getSymptoms();
    }

    public int getId() {
        return id;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getOwnerMedicId() {
        return ownerMedicId;
    }

    public List<Integer> getOtherMedicIds() {
        return otherMedicIds;
    }

    public boolean isCompleted() {
        return completed;
    }

    public List<Medication> getPrescription() {
        return prescription;
    }

    public List<Symptom> getSymptomList() {
        return symptomList;
    }
}
