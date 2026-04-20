package com.isratafrin.spacecolony.model;

public class Pilot extends CrewMember {
    public Pilot(String name) {
        super(name, "Pilot", 5, 4, 20);
    }

    @Override
    public int getBonus(String missionType) {
        return "Asteroid".equals(missionType) ? 2 : 0;
    }
}
