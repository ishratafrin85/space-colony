package com.isratafrin.spacecolony.model;

public class Scientist extends CrewMember {
    public Scientist(String name) {
        super(name, "Scientist", 8, 1, 17);
    }

    @Override
    public int getBonus(String missionType) {
        return "Research".equals(missionType) ? 2 : 0;
    }
}
