package com.isratafrin.spacecolony.storage;

import com.isratafrin.spacecolony.model.CrewMember;

public class Medbay extends Storage {
    public Medbay() {
        super("Medbay");
    }

    public void recover(CrewMember cm) {
        if (cm == null) return;
        cm.restoreEnergy();
        applyPenalty(cm);
    }

    public void applyPenalty(CrewMember cm) {
        if (cm != null) cm.addExperience(-1);
    }
}
