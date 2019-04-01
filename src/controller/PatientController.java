package controller;

import dataModel.PatientData;
import dataModel.UserData;
import model.Case;
import model.Medic;
import model.Patient;
import model.User;
import service.AuthenticationService;
import service.PatientRequestService;
import service.UserService;

import java.util.List;
import java.util.stream.Collectors;

public class PatientController {
    private static PatientController instance = null;

    //singleton pattern
    private PatientController() {}

    public static PatientController getInstance() {
        if (instance == null)
            instance = new PatientController();
        return instance;
    }

    private boolean isAuthorizedToViewPatient(int id, String token) {
        User requester = AuthenticationService.getInstance().findUserByToken(token);
        User patient = UserService.getInstance().getUserById(id);
        if (requester == null || !(patient instanceof Patient))
            return false;
        return (patient == requester || requester instanceof Medic);
    }

    public PatientData getPatientById(int id, String token) throws NotAuthorizedException {
        if (!isAuthorizedToViewPatient(id, token))
            throw new NotAuthorizedException("Invalid token");
        return new PatientData((Patient)UserService.getInstance().getUserById(id));
    }

    public void sendPatientRequest(String token, int medicId) throws NotAuthorizedException {
        User patient = AuthenticationService.getInstance().findUserByToken(token);
        if (!(patient instanceof Patient))
            throw new NotAuthorizedException("Invalid token");
        User medic = UserService.getInstance().getUserById(medicId);
        if (!(medic instanceof Medic))
            throw new IllegalArgumentException("Wrong medic id");
        PatientRequestService.getInstance().patientRequestToMedic((Patient)patient, (Medic)medic);
    }

    public List<Integer> getCaseIds(String token, int patientId) throws NotAuthorizedException {
        if (!isAuthorizedToViewPatient(patientId, token))
            throw new NotAuthorizedException("Invalid token");
        Patient patient = (Patient)UserService.getInstance().getUserById(patientId);
        return patient.getAssignedCases().stream()
                .map(Case::getId)
                .collect(Collectors.toList());
    }
}
