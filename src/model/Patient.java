package model;

import java.io.Serializable;
import java.util.List;

public class Patient extends User implements Serializable {
    private Medic personalMedic;

    public Patient(int id, String username, String hashPassword, UserDetails userDetails) {
        super(id, username, hashPassword, userDetails);
        this.personalMedic = null;
    }

    @Override
    public UserTypeEnum getType() {
        return UserTypeEnum.PATIENT;
    }

    public Medic getPersonalMedic() {
        return personalMedic;
    }

    public void setPersonalMedic(Medic personalMedic) {
        this.personalMedic = personalMedic;
    }
}
