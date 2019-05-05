package controller;

import dataModel.PatientData;
import dataModel.UserData;
import model.Case;
import model.Medic;
import model.Patient;
import model.User;
import service.AuthenticationService;
import service.LoggingService;
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

    public PatientData getPatientPersonalData(String token) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "Patient tries to read his data"
        );

        User patient = AuthenticationService.getInstance().findUserByToken(token);
        if (!(patient instanceof Patient)) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }
        LoggingService.getInstance().log(entry, patient.getUsername(), LoggingService.Status.OK);
        return new PatientData((Patient)patient);
    }

    public PatientData getPatientById(int id, String token) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "User tries to read data from patient with id=" + id
        );
        if (!isAuthorizedToViewPatient(id, token)) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }
        LoggingService.getInstance().log(entry,
                AuthenticationService.getInstance().findUserByToken(token).getUsername(),
                LoggingService.Status.OK);
        return new PatientData((Patient)UserService.getInstance().getUserById(id));
    }

    public void sendPatientRequest(String token, int medicId) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Patient tries to send request to medic with id=" + medicId
        );

        User patient = AuthenticationService.getInstance().findUserByToken(token);
        if (!(patient instanceof Patient)) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }
        User medic = UserService.getInstance().getUserById(medicId);
        if (!(medic instanceof Medic)) {
            LoggingService.getInstance().log(entry, patient.getUsername(), LoggingService.Status.NOT_FOUND);
            throw new IllegalArgumentException("Wrong medic id");
        }
        LoggingService.getInstance().log(entry, patient.getUsername(), LoggingService.Status.OK);
        PatientRequestService.getInstance().patientRequestToMedic((Patient)patient, (Medic)medic);
    }

    public List<Integer> getCaseIds(String token, int patientId) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "User tries to read cases ids of patient with id=" + patientId
        );
        if (!isAuthorizedToViewPatient(patientId, token)) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }
        Patient patient = (Patient)UserService.getInstance().getUserById(patientId);

        LoggingService.getInstance().log(entry,
                AuthenticationService.getInstance().findUserByToken(token).getUsername(),
                LoggingService.Status.OK);
        return patient.getAssignedCases().stream()
                .map(Case::getId)
                .collect(Collectors.toList());
    }
}
