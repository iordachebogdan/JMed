package controller;

import dataModel.MedicData;
import model.*;
import service.AuthenticationService;
import service.PatientRequestService;
import service.UserService;

import java.util.List;
import java.util.stream.Collectors;

public class MedicController {
    private static MedicController instance = null;

    //singleton pattern
    private MedicController() {}

    public static MedicController getInstance() {
        if (instance == null)
            instance = new MedicController();
        return instance;
    }

    public MedicData getMedicById(int id) {
        User medic = UserService.getInstance().getUserById(id);
        if (!(medic instanceof Medic))
            throw new IllegalArgumentException("Invalid id");
        return new MedicData((Medic)medic);
    }

    public List<MedicData> getMedics() {
        return UserService.getInstance().getMedics().stream()
                .map(MedicData::new)
                .collect(Collectors.toList());
    }

    public List<MedicData> getMedicsBySpecialization(Specialization specialization) {
        return UserService.getInstance().getMedics().stream()
                .filter(m -> m.getSpecializations().contains(specialization))
                .map(MedicData::new)
                .collect(Collectors.toList());
    }

    public List<Integer> getPatientIds(String token) throws NotAuthorizedException {
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic))
            throw new NotAuthorizedException("Invalid token");
        return ((Medic) medic).getPatients().stream()
                .map(Patient::getId)
                .collect(Collectors.toList());
    }

    public List<Integer> getRequestPatientIds(String token) throws NotAuthorizedException {
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic))
            throw new NotAuthorizedException("Invalid token");
        return PatientRequestService.getInstance().getPatientRequests((Medic)medic)
                .stream()
                .map(Patient::getId)
                .collect(Collectors.toList());
    }

    public void acceptRequest(String token, int patientId) throws NotAuthorizedException {
        User patient = UserService.getInstance().getUserById(patientId);
        if (!(patient instanceof Patient))
            throw new IllegalArgumentException("No such patient");
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic))
            throw new NotAuthorizedException("Invalid token");
        if (PatientRequestService.getInstance().getRequestedMedic((Patient)patient) != medic)
            throw new IllegalArgumentException("No such request");
        PatientRequestService.getInstance().acceptRequest((Patient)patient);
    }

    public List<Integer> getCaseIds(String token) throws NotAuthorizedException {
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic))
            throw new NotAuthorizedException("Invalid token");
        return medic.getAssignedCases().stream()
                .map(Case::getId)
                .collect(Collectors.toList());
    }

    public void addSpecialization(String token, Specialization specialization) throws NotAuthorizedException {
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic))
            throw new NotAuthorizedException("Invalid token");
        if (!((Medic) medic).getSpecializations().contains(specialization))
            ((Medic) medic).getSpecializations().add(specialization);
    }
}
