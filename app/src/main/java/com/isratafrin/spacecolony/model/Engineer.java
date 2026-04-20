package com.isratafrin.spacecolony.model;

public class Engineer extends CrewMember {
    public Engineer(String name) {
        super(name, "Engineer", 6, 3, 19);
    }

    @Override
    public int getBonus(String missionType) {
        return "Repair".equals(missionType) ? 2 : 0;
    }
}
