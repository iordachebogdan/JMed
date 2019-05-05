package controller;

import dataModel.CaseData;
import model.*;
import service.AuthenticationService;
import service.CaseService;
import service.LoggingService;
import service.UserService;

import java.util.Date;
import java.util.List;

public class CaseController {
    private static CaseController instance = null;

    //singleton pattern
    private CaseController() {}

    public static CaseController getInstance() {
        if (instance == null)
            instance = new CaseController();
        return instance;
    }

    private User checkAuthorization(String token, int caseId, LoggingService.LogEntry entry)
            throws NotAuthorizedException {
        Case c = CaseService.getInstance().getCaseById(caseId);
        User user = AuthenticationService.getInstance().findUserByToken(token);

        if (user == null) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }
        if (c == null) {
            LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.NOT_FOUND);
            throw new IllegalArgumentException("No such case");
        }
        if (user != c.getPatient() && user != c.getOwnerMedic() &&
                (!(user instanceof  Medic) || !c.getOtherMedics().contains(user))) {
            LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Not permitted");
        }
        return user;
    }

    private User checkAuthorizationSymptoms(String token, int caseId, LoggingService.LogEntry entry)
            throws NotAuthorizedException {
        User patient = AuthenticationService.getInstance().findUserByToken(token);
        if (!(patient instanceof Patient)) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw new IllegalArgumentException("Invalid token");
        }
        Case c = CaseService.getInstance().getCaseById(caseId);
        if (c == null) {
            LoggingService.getInstance().log(entry, patient.getUsername(), LoggingService.Status.NOT_FOUND);
            throw new IllegalArgumentException("No such case");
        }
        if (c.getPatient() != patient) {
            LoggingService.getInstance().log(entry, patient.getUsername(), LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }
        return patient;
    }

    private User checkAuthorizationMedication(String token, int caseId, LoggingService.LogEntry entry)
            throws NotAuthorizedException {
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic)) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw new IllegalArgumentException("Invalid token");
        }

        Case c = CaseService.getInstance().getCaseById(caseId);
        if (c == null) {
            LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.NOT_FOUND);
            throw new IllegalArgumentException("No such case");
        }
        if (c.getOwnerMedic() != medic && !c.getOtherMedics().contains(medic)) {
            LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }
        return medic;
    }

    public CaseData getCaseById(String token, int caseId) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                String.format("Read case with id=%d", caseId)
        );
        User user = checkAuthorization(token, caseId, entry);
        Case c = CaseService.getInstance().getCaseById(caseId);
        LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.OK);
        return new CaseData(c);
    }

    public CaseData createCase(String token) {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.CREATE,
                this.getClass().getName(),
                "Patient tries to create case"
        );

        User patient = AuthenticationService.getInstance().findUserByToken(token);
        if (!(patient instanceof Patient)) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.NOT_FOUND);
            throw new IllegalArgumentException("No such patient");
        }
        int id = CaseService.getInstance().addCase((Patient)patient);
        LoggingService.getInstance().log(entry, patient.getUsername(), LoggingService.Status.OK);
        return new CaseData(CaseService.getInstance().getCaseById(id));
    }

    public CaseData addSymptom(String token, int caseId, Symptom symptom) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Patient tries to add symptom"
        );
        User user = checkAuthorizationSymptoms(token, caseId, entry);
        Case c = CaseService.getInstance().addSymptomToCase(caseId, symptom);
        LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.OK);
        return new CaseData(c);
    }

    public CaseData removeSymptom(String token, int caseId, Symptom symptom) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Patient tries to remove symptom"
        );
        User user = checkAuthorizationSymptoms(token, caseId, entry);
        Case c = CaseService.getInstance().removeSymptomFromCase(caseId, symptom);
        LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.OK);
        return new CaseData(c);
    }

    public CaseData addMedication(String token, int caseId, Medication medication) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic tries to add medication"
        );
        User user = checkAuthorizationMedication(token, caseId, entry);
        Case c = CaseService.getInstance().addMedicationToCase(caseId, medication);
        LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.OK);
        return new CaseData(c);
    }

    public CaseData removeMedication(String token, int caseId, Medication medication) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic tries to remove medication"
        );
        User user = checkAuthorizationMedication(token, caseId, entry);
        Case c = CaseService.getInstance().removeMedicationFromCase(caseId, medication);
        LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.OK);
        return new CaseData(c);
    }

    public CaseData addMedic(String token, int caseId, int medicId) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic tries to add another medic to case with id=" + caseId
        );

        User owner = AuthenticationService.getInstance().findUserByToken(token);
        Case c = CaseService.getInstance().getCaseById(caseId);
        if (!(owner instanceof Medic) || c == null || c.getOwnerMedic() != owner) {
            LoggingService.getInstance().log(entry, owner == null ? null : owner.getUsername(), LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Not permitted");
        }
        User medic = UserService.getInstance().getUserById(medicId);
        if (!(medic instanceof Medic)) {
            LoggingService.getInstance().log(entry, owner.getUsername(), LoggingService.Status.NOT_FOUND);
            throw new IllegalArgumentException("No such medic");
        }
        CaseService.getInstance().addMedicToCase(caseId, (Medic)medic);
        LoggingService.getInstance().log(entry, owner.getUsername(), LoggingService.Status.OK);
        return new CaseData(c);
    }

    public List<Symptom> getSymptomsBeforeDate(String token, int caseId, Date date) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "User tries to retrieve symptoms list"
        );

        User user = checkAuthorization(token, caseId, entry);
        Case c = CaseService.getInstance().getCaseById(caseId);
        LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.OK);
        return c.getSymptomList().getBeforeDate(date);
    }

    public List<Medication> getPrescriptionBeforeDate(String token, int caseId, Date date)
            throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "User tries to retrieve prescription"
        );

        User user = checkAuthorization(token, caseId, entry);
        Case c = CaseService.getInstance().getCaseById(caseId);
        LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.OK);
        return c.getPrescription().getBeforeDate(date);
    }

    public Case setCompleted(String token, int caseId) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic tries to complete case"
        );

        Case c = CaseService.getInstance().getCaseById(caseId);
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (c == null || !(medic instanceof Medic) || c.getOwnerMedic() != medic) {
            LoggingService.getInstance().log(entry, medic == null ? null : medic.getUsername(), LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid token");
        }

        LoggingService.getInstance().log(entry, medic.getUsername(), LoggingService.Status.OK);
        return CaseService.getInstance().setCaseCompleted(caseId);
    }

}
