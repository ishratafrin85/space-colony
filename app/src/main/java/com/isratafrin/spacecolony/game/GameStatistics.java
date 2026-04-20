package com.isratafrin.spacecolony.game;

import com.isratafrin.spacecolony.model.CrewMember;
import com.isratafrin.spacecolony.model.MissionResult;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameStatistics implements Serializable {
    private int totalMissions;
    private int totalRecruits;
    private int totalCasualties;
    private int totalWins;

    public void recordMission(MissionResult result) {
        if (result == null) return;
        totalMissions++;
        if (result.isSuccess()) totalWins++;
        totalCasualties += result.getDefeated().size();
    }

    public void recordRecruit() {
        totalRecruits++;
    }

    public double getSuccessRate() {
        if (totalMissions == 0) return 0.0;
        return (double) totalWins / totalMissions;
    }

    public Map<String, Object> getCrewStats(CrewMember cm) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (cm == null) return map;
        map.put("Name", cm.getName());
        map.put("Role", cm.getSpecialization());
        map.put("Skill", cm.getSkill());
        map.put("Resilience", cm.getResilience());
        map.put("Experience", cm.getExperience());
        map.put("Energy", cm.getEnergy() + "/" + cm.getMaxEnergy());
        map.put("Missions", cm.getMissionsCompleted());
        map.put("Wins", cm.getMissionsWon());
        map.put("Training", cm.getTrainingCount());
        map.put("Damage", cm.getTotalDamageDealt());
        return map;
    }

    public Map<String, Object> getColonyStats() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Total Missions", totalMissions);
        map.put("Total Wins", totalWins);
        map.put("Total Recruits", totalRecruits);
        map.put("Casualties", totalCasualties);
        map.put("Success Rate", Math.round(getSuccessRate() * 100) + "%");
        return map;
    }

    public int getTotalMissions() { return totalMissions; }
    public int getTotalRecruits() { return totalRecruits; }
    public int getTotalCasualties() { return totalCasualties; }
    public int getTotalWins() { return totalWins; }
}
