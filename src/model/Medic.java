package model;

import java.util.ArrayList;
import java.util.List;

public class Medic extends User {
    private List<Specialization> specializations;
    private List<Patient> patients;
    private List<Case> cases;

    public Medic(int id, String username, String hashPassword, UserDetails userDetails,
                 List<Specialization> specializations) {
        super(id, username, hashPassword, userDetails);
        this.specializations = specializations;
        this.patients = new ArrayList<>();
        this.cases = new ArrayList<>();
    }

    public void addSpecialization(Specialization s) {
        specializations.add(s);
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public void addPatient(Patient p) {
        patients.add(p);
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void removePatient(Patient p) {
        patients.remove(p);
    }

    public void addCase(Case c) {
        cases.add(c);
    }

    public List<Case> getAssignedCases() {
        return cases;
    }

    @Override
    public UserTypeEnum getType() {
        return UserTypeEnum.MEDIC;
    }
}
