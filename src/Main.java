import controller.*;
import dataModel.CaseData;
import dataModel.MedicData;
import model.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws NotAuthorizedException, InterruptedException {
        AuthenticationController authenticationController = AuthenticationController.getInstance();
        MedicController medicController = MedicController.getInstance();
        PatientController patientController = PatientController.getInstance();
        CaseController caseController = CaseController.getInstance();

        authenticationController.registerUser("P1", "123", "P", "1",
                "x", "", UserTypeEnum.PATIENT);
        String token1 = authenticationController.authenticateUser("P1", "123");

        authenticationController.registerUser("P2", "123", "P", "2",
                "x", "", UserTypeEnum.PATIENT);
        String token2 = authenticationController.authenticateUser("P2", "123");

        authenticationController.registerUser("P3", "123", "P", "3",
                "x", "", UserTypeEnum.PATIENT);
        String token3 = authenticationController.authenticateUser("P3", "123");

        authenticationController.registerUser("M1", "12", "M", "1",
                "x", "", UserTypeEnum.MEDIC);
        String token4 = authenticationController.authenticateUser("M1", "12");

        authenticationController.registerUser("M2", "abc", "M", "2",
                "x", "", UserTypeEnum.MEDIC);
        String token5 = authenticationController.authenticateUser("M2", "abc");

        List<MedicData> medics = medicController.getMedics();
        for (MedicData md : medics)
            System.out.println(md.getUsername() + " " + md.getId());

        medicController.addSpecialization(token4, new Specialization("cardio"));
        medicController.addSpecialization(token5, new Specialization("cardio"));
        medicController.addSpecialization(token5, new Specialization("nefrology"));

        medics = medicController.getMedicsBySpecialization(new Specialization("cardio"));
        System.out.println(medics.size());
        medics = medicController.getMedicsBySpecialization(new Specialization("nefrology"));
        System.out.println(medics.size());

        MedicData md = medicController.getMedicById(5);
        for (Specialization specialization : md.getSpecializations())
            System.out.print(specialization.getName() + " ");
        System.out.println();

        System.out.println(patientController.getPatientPersonalData(token1).getId() + " " +
                patientController.getPatientPersonalData(token1).getUsername());
        System.out.println(patientController.getPatientPersonalData(token2).getId() + " " +
                patientController.getPatientPersonalData(token2).getUsername());

        patientController.sendPatientRequest(token1, 4);
        patientController.sendPatientRequest(token2, 4);

        System.out.println(medicController.getRequestPatientIds(token4));
        System.out.println(patientController.getPatientById(3, token4).getUsername());

        medicController.acceptRequest(token4, 2);
        System.out.println(medicController.getRequestPatientIds(token4));
        System.out.println(medicController.getPatientIds(token4));

        patientController.sendPatientRequest(token2, 5);
        medicController.acceptRequest(token5, 2);
        System.out.println(medicController.getPatientIds(token4));
        System.out.println(medicController.getPatientIds(token5));

        caseController.createCase(token2);
        int caseId = patientController.getCaseIds(token5, 2).get(0);
        CaseData cd = caseController.getCaseById(token5, 1);
        System.out.println(cd.getPatientId());
        cd = caseController.getCaseById(token2, 1);
        System.out.println(cd.getOwnerMedicId());

        System.out.println(medicController.getCaseIds(token5).size());
        System.out.println(patientController.getCaseIds(token2, 2).size());

        cd = caseController.addMedic(token5, 1, 4);
        System.out.println(cd.getOtherMedicIds());

        caseController.addSymptom(token2, 1, new Symptom("aaa", new Date()));
        caseController.addSymptom(token2, 1, new Symptom("bbb", new Date()));
        Thread.sleep(1000);
        System.out.println(caseController.getSymptomsBeforeDate(token2, 1, new Date()).size());
        caseController.removeSymptom(token2, 1,
                caseController.getCaseById(token2, 1).getSymptomList().get(0));
        Thread.sleep(1000);
        System.out.println(caseController.getSymptomsBeforeDate(token2, 1, new Date()).size());

        caseController.addMedication(token4, 1, new Medication("bla", "", new Date(), new Date()));
        caseController.addMedication(token5, 1, new Medication("bla2", "", new Date(), new Date()));

        cd = caseController.getCaseById(token2, 1);
        System.out.println(cd.getSymptomList().stream().map(Symptom::getDescription).collect(Collectors.toList()));
        System.out.println(cd.getPrescription().stream().map(Medication::getName).collect(Collectors.toList()));

        caseController.setCompleted(token5, 1);
        System.out.println(caseController.getCaseById(token2, 1).isCompleted());
    }
}
