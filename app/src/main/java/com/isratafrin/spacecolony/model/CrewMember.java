package com.isratafrin.spacecolony.model;

import java.io.Serializable;

public abstract class CrewMember implements Serializable {
    private static int idCounter = 0;

    private final int id;
    private String name;
    private String specialization;
    private int skill;
    private int resilience;
    private int experience;
    private int energy;
    private int maxEnergy;

    private int missionsCompleted;
    private int missionsWon;
    private int trainingCount;
    private int totalDamageDealt;

    protected CrewMember(String name, String specialization,
                        int skill, int resilience, int maxEnergy) {
        this.id = ++idCounter;
        this.name = name;
        this.specialization = specialization;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.experience = 0;
    }

    public int act() {
        int damage = getEffectiveSkill();
        totalDamageDealt += damage;
        return damage;
    }

    public void defend(int damage) {
        int taken = Math.max(1, damage - resilience);
        energy = Math.max(0, energy - taken);
    }

    public abstract int getBonus(String missionType);

    public boolean isAlive() {
        return energy > 0;
    }

    public void train() {
        if (energy >= 3) {
            experience++;
            trainingCount++;
            energy = Math.max(0, energy - 3);
        }
    }

    public void restoreEnergy() {
        energy = maxEnergy;
    }

    public int getEffectiveSkill() {
        return skill + experience;
    }

    public static int getNumberOfCreated() {
        return idCounter;
    }

    public static void resetIdCounter() {
        idCounter = 0;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public int getExperience() { return experience; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getMissionsCompleted() { return missionsCompleted; }
    public int getMissionsWon() { return missionsWon; }
    public int getTrainingCount() { return trainingCount; }
    public int getTotalDamageDealt() { return totalDamageDealt; }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(maxEnergy, energy));
    }

    public void addExperience(int xp) { experience = Math.max(0, experience + xp); }
    public void addMissionCompleted() { missionsCompleted++; }
    public void addMissionWon() { missionsWon++; }
    public void addDamageDealt(int amount) { totalDamageDealt += amount; }
}
