package com.isratafrin.spacecolony.storage;

import com.isratafrin.spacecolony.model.CrewMember;

public class Quarters extends Storage {
    public Quarters() {
        super("Quarters");
    }

    public void createCrewMember(CrewMember cm) {
        addCrewMember(cm);
    }

    public void restoreEnergy(CrewMember cm) {
        if (cm != null) cm.restoreEnergy();
    }
}
