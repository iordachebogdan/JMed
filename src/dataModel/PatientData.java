package dataModel;

import model.Patient;
import model.User;

public class PatientData extends UserData {
    private final Integer personalMedicId;

    public PatientData(Patient patient) {
        super(patient);
        if (patient.getPersonalMedic() == null)
            personalMedicId = null;
        else
            personalMedicId = patient.getPersonalMedic().getId();
    }

    public Integer getPersonalMedicId() {
        return personalMedicId;
    }
}
