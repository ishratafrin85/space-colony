package com.isratafrin.spacecolony.model;

public class Soldier extends CrewMember {
    public Soldier(String name) {
        super(name, "Soldier", 9, 0, 16);
    }

    @Override
    public int getBonus(String missionType) {
        return "Combat".equals(missionType) ? 2 : 0;
    }
}
