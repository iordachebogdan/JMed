package dataModel;

import model.Medic;
import model.Specialization;
import model.User;

import java.util.List;

public class MedicData extends UserData {
    private final List<Specialization> specializations;

    public MedicData(Medic medic) {
        super(medic);
        specializations = medic.getSpecializations();
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }
}
