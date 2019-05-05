package controller;

import dataModel.MedicData;
import model.*;
import service.AuthenticationService;
import service.LoggingService;
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
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "Get medic query with id=" + id
        );

        User medic = UserService.getInstance().getUserById(id);
        if (!(medic instanceof Medic)) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.NOT_FOUND);
            throw new IllegalArgumentException("Invalid id");
        }
        LoggingService.getInstance().log(entry, null, LoggingService.Status.OK);
        return new MedicData((Medic)medic);
    }

    public List<MedicData> getMedics() {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "Get all medics"
        );
        LoggingService.getInstance().log(entry, null, LoggingService.Status.OK);

        return UserService.getInstance().getMedics().stream()
                .map(MedicData::new)
                .collect(Collectors.toList());
    }

    public List<MedicData> getMedicsBySpecialization(Specialization specialization) {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "Get all medics with specialization=" + specialization.getName()
        );
        LoggingService.getInstance().log(entry, null, LoggingService.Status.OK);

        return UserService.getInstance().getMedics().stream()
                .filter(m -> m.getSpecializations().contains(specialization))
                .map(MedicData::new)
                .collect(Collectors.toList());
    }

    public List<Integer> getPatientIds(String token) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "Medic tries to read his patients ids"
        );
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic)) {
            LoggingService.getInstance().log(entry, medic == null ? null : medic.getUsername(), LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }
        LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.OK);
        return ((Medic) medic).getPatients().stream()
                .map(Patient::getId)
                .collect(Collectors.toList());
    }

    public List<Integer> getRequestPatientIds(String token) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "Medic tries to read patients ids that requested them"
        );
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic)) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }
        LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.OK);
        return PatientRequestService.getInstance().getPatientRequests((Medic)medic)
                .stream()
                .map(Patient::getId)
                .collect(Collectors.toList());
    }

    public void acceptRequest(String token, int patientId) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic tries to accept request from patient with id=" + patientId
        );

        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic)) {
            LoggingService.getInstance().log(entry, medic == null ? null : medic.getUsername(), LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }

        User patient = UserService.getInstance().getUserById(patientId);
        if (!(patient instanceof Patient)) {
            LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.NOT_FOUND);
            throw new IllegalArgumentException("No such patient");
        }

        if (PatientRequestService.getInstance().getRequestedMedic((Patient)patient) != medic) {
            LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.NOT_FOUND);
            throw new IllegalArgumentException("No such request");
        }

        LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.OK);
        PatientRequestService.getInstance().acceptRequest((Patient)patient);
    }

    public List<Integer> getCaseIds(String token) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "Medic tries to read his cases ids"
        );

        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic)) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }

        LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.OK);
        return medic.getAssignedCases().stream()
                .map(Case::getId)
                .collect(Collectors.toList());
    }

    public void addSpecialization(String token, Specialization specialization) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic tries to add this specialization " + specialization.getName()
        );

        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic)) {
            LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }

        LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.OK);
        UserService.getInstance().addSpecializationToMedic(medic.getId(), specialization);
    }
}
