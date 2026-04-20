package com.isratafrin.spacecolony.storage;

import com.isratafrin.spacecolony.model.CrewMember;
import com.isratafrin.spacecolony.model.MissionResult;
import com.isratafrin.spacecolony.model.Threat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MissionControl extends Storage {
    private int missionCounter;
    private final Random random = new Random();

    public MissionControl() {
        super("MissionControl");
        this.missionCounter = 0;
    }

    public Threat generateThreat(int squadSize) {
        return Threat.generate(missionCounter, squadSize);
    }

    public MissionResult launchMission(List<CrewMember> squad, int squadSize) {
        missionCounter++;
        Threat threat = generateThreat(squadSize);
        List<String> log = new ArrayList<>();
        log.add("Mission " + missionCounter + ": " + threat.getType() +
                ". Enemy: " + threat.getName() + ".");

        int turn = 0;
        while (!threat.isDefeated() && anyAlive(squad)) {
            CrewMember actor = squad.get(turn % squad.size());
            if (!actor.isAlive()) { turn++; continue; }

            int bonus = actor.getBonus(threat.getType());
            int damage = actor.getEffectiveSkill() + bonus + random.nextInt(3);
            damage = Math.max(1, damage - threat.getResilience());
            threat.defend(damage);
            actor.addDamageDealt(damage);
            log.add(actor.getName() + " hits for " + damage +
                    (bonus > 0 ? " (+" + bonus + " bonus)" : "") + ".");

            if (!threat.isDefeated()) {
                threat.attack(actor);
                log.add("Enemy hits " + actor.getName() + ".");
                if (!actor.isAlive()) log.add(actor.getName() + " is down.");
            }
            turn++;
        }

        boolean success = threat.isDefeated();
        List<CrewMember> survivors = new ArrayList<>();
        List<CrewMember> defeated = new ArrayList<>();
        for (CrewMember c : squad) {
            c.addMissionCompleted();
            if (c.isAlive()) {
                survivors.add(c);
                if (success) { c.addMissionWon(); c.addExperience(2); }
            } else {
                defeated.add(c);
            }
        }
        log.add(success ? "Mission won!" : "Mission failed.");
        return new MissionResult(success, survivors, defeated,
                threat.getType(), threat.getName(), log);
    }

    private boolean anyAlive(List<CrewMember> squad) {
        for (CrewMember c : squad) if (c.isAlive()) return true;
        return false;
    }

    public int getMissionCounter() { return missionCounter; }
    public void incrementMissionCounter() { missionCounter++; }
}
