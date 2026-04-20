package com.isratafrin.spacecolony.model;

public class Medic extends CrewMember {
    public Medic(String name) {
        super(name, "Medic", 7, 2, 18);
    }

    @Override
    public int getBonus(String missionType) {
        return "Medical".equals(missionType) ? 2 : 0;
    }

    public void healAlly(CrewMember target) {
        if (target == null || !target.isAlive()) return;
        target.setEnergy(target.getEnergy() + 5);
    }
}
