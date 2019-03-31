package model;

import java.util.ArrayList;
import java.util.List;

public class Case {
    private Patient patient;
    private Medic ownerMedic;
    private List<Medic> otherMedics;

    private SymptomList symptomList;
    private Prescription prescription;

    private boolean completed;

    public Case(Patient patient) {
        this.patient = patient;
        this.ownerMedic = patient.getPersonalMedic();
        this.otherMedics = new ArrayList<>();

        this.symptomList = new SymptomList();
        this.prescription = new Prescription();

        this.completed = false;

        this.patient.addCase(this);
        this.ownerMedic.addCase(this);
    }

    public SymptomList getSymptomList() {
        return symptomList;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public Medic getOwnerMedic() {
        return ownerMedic;
    }

    public List<Medic> getOtherMedics() {
        return otherMedics;
    }

    public void addMedic(Medic m) {
        otherMedics.add(m);
        m.addCase(this);
    }

    public Patient getPatient() {
        return patient;
    }

    public void setCompleted() {
        completed = true;
    }
}