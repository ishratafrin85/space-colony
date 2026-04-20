package com.isratafrin.spacecolony.storage;

import com.isratafrin.spacecolony.model.CrewMember;

public class Simulator extends Storage {
    public Simulator() {
        super("Simulator");
    }

    public void train(CrewMember cm) {
        if (cm != null) cm.train();
    }
}
