import controller.*;
import dataModel.CaseData;
import dataModel.MedicData;
import dataModel.PatientData;
import model.*;
import service.AuthenticationService;
import service.LoggingService;

import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        LoggingService.getInstance().log(
                new LoggingService.LogEntry(LoggingService.Operation.INFO, "Main", "Program started"),
                null,
                LoggingService.Status.OK
        );

        AuthenticationController authenticationController = AuthenticationController.getInstance();
        MedicController medicController = MedicController.getInstance();
        PatientController patientController = PatientController.getInstance();
        CaseController caseController = CaseController.getInstance();

        String[] welcomeMenu = {"1. Login", "2. Register", "3. Quit"};
        String[] patientMenu = {"1. View medics", "2. Send request to medic", "3. Get personal data", "4. Create case",
            "5. View case", "6. Add symptom to case", "7. Logout"};
        String[] medicMenu = {"1. View medics", "2. View patients", "3. View request patients", "4. Accept request", "5. Get cases",
            "6. View case", "7. Add medication to case", "8. Add another medic to case", "9. Logout"};

        Scanner scanner = new Scanner(System.in);
        while (true) {
            for (String option : welcomeMenu) {
                System.out.println(option);
            }
            int pick = scanner.nextInt();
            if (pick == 1) {
                System.out.print("Enter username: ");
                String username = scanner.next();
                System.out.print("Enter password: ");
                String password = scanner.next();
                String token;
                try {
                    token = authenticationController.authenticateUser(username, password);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    continue;
                }
                UserTypeEnum type = authenticationController.getType(token);
                if (type == UserTypeEnum.MEDIC) {
                    while (true) {
                        for (String option : medicMenu) {
                            System.out.println(option);
                        }
                        int opt = scanner.nextInt();
                        if (opt == 1) {
                            List<MedicData> medics;
                            try {
                                medics = medicController.getMedics();
                            } catch(Exception ex) {
                                System.out.println(ex.getMessage());
                                continue;
                            }
                            for (MedicData medic : medics) {
                                System.out.println(medic.getId() + "# " + medic.getFirstName() + " " + medic.getLastName());
                            }
                        } else if (opt == 2) {
                            List<Integer> patientIds;
                            try {
                                patientIds = medicController.getPatientIds(token);
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                                continue;
                            }
                            System.out.println(patientIds);
                        } else if (opt == 3) {
                            List<Integer> requestPatientIds;
                            try {
                                requestPatientIds = medicController.getRequestPatientIds(token);
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                                continue;
                            }
                            System.out.println(requestPatientIds);
                        } else if (opt == 4) {
                            System.out.print("Enter patient id: ");
                            int id = scanner.nextInt();
                            try {
                                medicController.acceptRequest(token, id);
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                                continue;
                            }
                            System.out.println("ACCEPTED");
                        } else if (opt == 5) {
                            List<Integer> caseIds;
                            try {
                                caseIds = medicController.getCaseIds(token);
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                                continue;
                            }
                            System.out.println(caseIds);
                        } else if (opt == 6) {
                            System.out.print("Enter case id: ");
                            int id = scanner.nextInt();
                            CaseData c;
                            try {
                                c = caseController.getCaseById(token, id);
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                                continue;
                            }
                            System.out.println(c.getPrescription());
                            System.out.println(c.getSymptomList());
                            System.out.println("Patient id: " + c.getPatientId());
                            System.out.println("Medic id: " + c.getOwnerMedicId());
                            System.out.println("Other medics ids: " + c.getOtherMedicIds());
                            System.out.println("Completed: " + c.isCompleted());
                        } else if (opt == 7) {
                            System.out.print("Enter case id: ");
                            int id = scanner.nextInt();
                            System.out.print("Enter medication name: ");
                            String medication = scanner.next();
                            try {
                                caseController.addMedication(token, id, new Medication(medication, null, null, null));
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                                continue;
                            }
                            System.out.println("Added");
                        } else if (opt == 8) {
                            System.out.print("Enter case id: ");
                            int caseId = scanner.nextInt();
                            System.out.print("Enter other medic id: ");
                            int medicId = scanner.nextInt();
                            try {
                                caseController.addMedic(token, caseId, medicId);
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                                continue;
                            }
                            System.out.println("Added");
                        } else if (opt == 9) {
                            break;
                        }
                    }
                } else if (type == UserTypeEnum.PATIENT) {
                    while (true) {
                        for (String option : patientMenu) {
                            System.out.println(option);
                        }
                        int opt = scanner.nextInt();
                        if (opt == 1) {
                            List<MedicData> medics;
                            try {
                                medics = medicController.getMedics();
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                                continue;
                            }
                            for (MedicData medic : medics) {
                                System.out.println(medic.getId() + "# " + medic.getFirstName() + " " + medic.getLastName());
                            }
                        } else if (opt == 2) {
                            System.out.print("Enter medic id: ");
                            int medicId = scanner.nextInt();
                            try {
                                patientController.sendPatientRequest(token, medicId);
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                                continue;
                            }
                            System.out.println("SENT");
                        } else if (opt == 3) {
                            PatientData patientData;
                            List<Integer> caseIds;
                            try {
                                patientData = patientController.getPatientPersonalData(token);
                                caseIds = patientController.getCaseIds(token, patientData.getId());
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                                continue;
                            }
                            System.out.println(patientData.getId() + "# " + patientData.getFirstName() + " " +
                                    patientData.getLastName() + "\n" + "Case ids: " + caseIds);
                        } else if (opt == 4) {
                            CaseData c;
                            try {
                                c = caseController.createCase(token);
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                                continue;
                            }
                            System.out.println("CREATED with id=" + c.getId());
                        } else if (opt == 5) {
                            System.out.print("Enter case id: ");
                            int id = scanner.nextInt();
                            CaseData c;
                            try {
                                c = caseController.getCaseById(token, id);
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                                continue;
                            }
                            System.out.println(c.getPrescription());
                            System.out.println(c.getSymptomList());
                            System.out.println("Patient id: " + c.getPatientId());
                            System.out.println("Medic id: " + c.getOwnerMedicId());
                            System.out.println("Other medics ids: " + c.getOtherMedicIds());
                            System.out.println("Completed: " + c.isCompleted());
                        } else if (opt == 6) {
                            System.out.print("Enter case id: ");
                            int id = scanner.nextInt();
                            System.out.print("Enter symptom name: ");
                            String symptom = scanner.next();
                            try {
                                caseController.addSymptom(token, id, new Symptom(symptom, null));
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                                continue;
                            }
                            System.out.println("Added");
                        } else if (opt == 7) {
                            break;
                        }
                    }
                }
            } else if (pick == 2) {
                System.out.print("Enter username: ");
                String username = scanner.next();
                System.out.print("Enter password: ");
                String password = scanner.next();
                System.out.print("Enter first name: ");
                String firstName = scanner.next();
                System.out.print("Enter last name: ");
                String lastName = scanner.next();
                System.out.print("Enter email: ");
                String email = scanner.next();
                System.out.println("Enter bio: ");
                scanner.nextLine();
                String bio = scanner.nextLine();
                System.out.print("Enter type(0 = Patient, 1 = Medic): ");
                int t = scanner.nextInt();
                UserTypeEnum type = t == 0 ? UserTypeEnum.PATIENT : UserTypeEnum.MEDIC;

                try {
                    authenticationController.registerUser(username, password, firstName, lastName, email, bio, type);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            } else if (pick == 3) {
                break;
            }
        }

        LoggingService.getInstance().log(
                new LoggingService.LogEntry(LoggingService.Operation.INFO, "Main", "Program finished"),
                null,
                LoggingService.Status.OK
        );
    }
}
