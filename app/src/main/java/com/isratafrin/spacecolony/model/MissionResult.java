package com.isratafrin.spacecolony.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MissionResult implements Serializable {
    private final boolean success;
    private final List<CrewMember> survivors;
    private final List<CrewMember> defeated;
    private final String missionType;
    private final String threatName;
    private final List<String> log;

    public MissionResult(boolean success, List<CrewMember> survivors,
                         List<CrewMember> defeated, String missionType,
                         String threatName, List<String> log) {
        this.success = success;
        this.survivors = new ArrayList<>(survivors);
        this.defeated = new ArrayList<>(defeated);
        this.missionType = missionType;
        this.threatName = threatName;
        this.log = new ArrayList<>(log);
    }

    public boolean isSuccess() { return success; }
    public List<CrewMember> getSurvivors() { return survivors; }
    public List<CrewMember> getDefeated() { return defeated; }
    public String getMissionType() { return missionType; }
    public String getThreatName() { return threatName; }
    public List<String> getLog() { return log; }
}
