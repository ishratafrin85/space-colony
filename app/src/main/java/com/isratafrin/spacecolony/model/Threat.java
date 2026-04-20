package com.isratafrin.spacecolony.model;

import java.io.Serializable;
import java.util.Random;

public class Threat implements Serializable {
    private static final String[] NAMES = {
            "Rogue Drone", "Ion Storm", "Asteroid Swarm", "Pirate Ship", "Space Anomaly"
    };
    private static final String[] TYPES = {
            "Asteroid", "Repair", "Medical", "Research", "Combat"
    };

    private final String name;
    private final String type;
    private final int skill;
    private int resilience;
    private int energy;
    private final int maxEnergy;

    public Threat(String name, String type, int skill, int resilience, int maxEnergy) {
        this.name = name;
        this.type = type;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
    }

    public void attack(CrewMember target) {
        if (target == null || !target.isAlive()) return;
        target.defend(skill);
    }

    public void defend(int damage) {
        int taken = Math.max(1, damage - resilience);
        energy = Math.max(0, energy - taken);
    }

    public boolean isDefeated() {
        return energy <= 0;
    }

    public static Threat generate(int missionCount, int squadSize) {
        Random r = new Random();
        String name = NAMES[r.nextInt(NAMES.length)];
        String type = TYPES[r.nextInt(TYPES.length)];
        int skill = 4 + missionCount;
        int resilience = Math.max(0, missionCount / 2);
        int maxEnergy = 10 + missionCount * 2 + squadSize * 2;
        return new Threat(name, type, skill, resilience, maxEnergy);
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }

    public void reduceResilience(int amount) {
        resilience = Math.max(0, resilience - amount);
    }
}
