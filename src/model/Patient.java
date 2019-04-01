package model;

import java.util.List;

public class Patient extends User {
    private Medic personalMedic;
    private List<Case> cases;

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

    public void addCase(Case c) {
        cases.add(c);
    }

    public List<Case> getCases() {
        return cases;
    }
}
