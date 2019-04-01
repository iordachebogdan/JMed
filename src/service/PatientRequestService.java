package service;

import model.Medic;
import model.Patient;

import java.util.*;

public class PatientRequestService {
    private static PatientRequestService instance = null;

    private Map<Patient, Medic> requestedMedic;
    private Map<Medic, Set<Patient>> patientRequests;

    //singleton pattern
    private PatientRequestService() {
        requestedMedic = new HashMap<>();
        patientRequests = new HashMap<>();
    }

    public static PatientRequestService getInstance() {
        if (instance == null)
            instance = new PatientRequestService();
        return instance;
    }

    public void patientRequestToMedic(Patient patient, Medic medic) {
        if (patient.getPersonalMedic() == medic || requestedMedic.get(patient) == medic)
            return;
        Medic old = requestedMedic.getOrDefault(patient, null);
        if (old != null) {
            patientRequests.get(old).remove(patient);
        }
        requestedMedic.put(patient, medic);
        patientRequests.computeIfAbsent(medic, k -> new HashSet<>());
        patientRequests.get(medic).add(patient);
    }

    public void acceptRequest(Patient patient) {
        Medic medic = requestedMedic.get(patient);
        if (medic == null)
            return;
        requestedMedic.remove(patient);
        patientRequests.get(medic).remove(patient);

        Medic old = patient.getPersonalMedic();
        if (old != null) {
            old.removePatient(patient);
        }
        patient.setPersonalMedic(medic);
        medic.addPatient(patient);
    }

    public Medic getRequestedMedic(Patient patient) {
        return requestedMedic.get(patient);
    }

    public List<Patient> getPatientRequests(Medic medic) {
        return new ArrayList<>(patientRequests.getOrDefault(medic, new HashSet<>()));
    }
}
