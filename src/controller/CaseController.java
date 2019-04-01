package controller;

import dataModel.CaseData;
import model.*;
import service.AuthenticationService;
import service.CaseService;
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

    private void checkAuthorization(String token, int caseId) throws NotAuthorizedException {
        Case c = CaseService.getInstance().getCaseById(caseId);
        if (c == null)
            throw new IllegalArgumentException("No such case");
        User user = AuthenticationService.getInstance().findUserByToken(token);
        if (user == null)
            throw new NotAuthorizedException("Invalid token");
        if (user != c.getPatient() && user != c.getOwnerMedic() &&
                (!(user instanceof  Medic) || !c.getOtherMedics().contains(user)))
            throw new NotAuthorizedException("Not permitted");
    }

    private void checkAuthorizationSymptoms(String token, int caseId) throws NotAuthorizedException {
        User patient = AuthenticationService.getInstance().findUserByToken(token);
        if (!(patient instanceof Patient))
            throw new IllegalArgumentException("No such patient");
        Case c = CaseService.getInstance().getCaseById(caseId);
        if (c == null)
            throw new IllegalArgumentException("No such case");
        if (c.getPatient() != patient)
            throw new NotAuthorizedException("Invalid token");
    }

    private void checkAuthorizationMedication(String token, int caseId) throws NotAuthorizedException {
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (!(medic instanceof Medic))
            throw new IllegalArgumentException("No such patient");
        Case c = CaseService.getInstance().getCaseById(caseId);
        if (c == null)
            throw new IllegalArgumentException("No such case");
        if (c.getOwnerMedic() != medic && !c.getOtherMedics().contains(medic))
            throw new NotAuthorizedException("Invalid token");
    }

    public CaseData getCaseById(String token, int caseId) throws NotAuthorizedException {
        checkAuthorization(token, caseId);
        Case c = CaseService.getInstance().getCaseById(caseId);
        return new CaseData(c);
    }

    public CaseData createCase(String token) {
        User patient = AuthenticationService.getInstance().findUserByToken(token);
        if (!(patient instanceof Patient))
            throw new IllegalArgumentException("No such patient");
        int id = CaseService.getInstance().addCase((Patient)patient);
        return new CaseData(CaseService.getInstance().getCaseById(id));
    }

    public CaseData addSymptom(String token, int caseId, Symptom symptom) throws NotAuthorizedException {
        checkAuthorizationSymptoms(token, caseId);
        Case c = CaseService.getInstance().getCaseById(caseId);
        c.getSymptomList().addSymptom(symptom);
        return new CaseData(c);
    }

    public CaseData removeSymptom(String token, int caseId, Symptom symptom) throws NotAuthorizedException {
        checkAuthorizationSymptoms(token, caseId);
        Case c = CaseService.getInstance().getCaseById(caseId);
        c.getSymptomList().removeSymptom(symptom);
        return new CaseData(c);
    }

    public CaseData addMedication(String token, int caseId, Medication medication) throws NotAuthorizedException {
        checkAuthorizationMedication(token, caseId);
        Case c = CaseService.getInstance().getCaseById(caseId);
        c.getPrescription().addMedication(medication);
        return new CaseData(c);
    }

    public CaseData removeMedication(String token, int caseId, Medication medication) throws NotAuthorizedException {
        checkAuthorizationMedication(token, caseId);
        Case c = CaseService.getInstance().getCaseById(caseId);
        c.getPrescription().removeMedication(medication);
        return new CaseData(c);
    }

    public CaseData addMedic(String token, int caseId, int medicId) throws NotAuthorizedException {
        User owner = AuthenticationService.getInstance().findUserByToken(token);
        Case c = CaseService.getInstance().getCaseById(caseId);
        if (!(owner instanceof Medic) || c == null || c.getOwnerMedic() != owner)
            throw new NotAuthorizedException("Not permitted");
        User medic = UserService.getInstance().getUserById(medicId);
        if (!(medic instanceof Medic))
            throw new IllegalArgumentException("No such medic");
        if (!c.getOtherMedics().contains(medic))
            c.addMedic((Medic)medic);
        return new CaseData(c);
    }

    public List<Symptom> getSymptomsBeforeDate(String token, int caseId, Date date) throws NotAuthorizedException {
        checkAuthorization(token, caseId);
        Case c = CaseService.getInstance().getCaseById(caseId);
        return c.getSymptomList().getBeforeDate(date);
    }

    public List<Medication> getPrescriptionBeforeDate(String token, int caseId, Date date)
            throws NotAuthorizedException {
        checkAuthorization(token, caseId);
        Case c = CaseService.getInstance().getCaseById(caseId);
        return c.getPrescription().getBeforeDate(date);
    }

    public void setCompleted(String token, int caseId) throws NotAuthorizedException {
        Case c = CaseService.getInstance().getCaseById(caseId);
        User medic = AuthenticationService.getInstance().findUserByToken(token);
        if (c == null || !(medic instanceof Medic) || c.getOwnerMedic() != medic)
            throw new NotAuthorizedException("Invalid token");
        c.setCompleted();
    }

}
