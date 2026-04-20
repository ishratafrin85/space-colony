package com.isratafrin.spacecolony.game;

import com.isratafrin.spacecolony.model.CrewMember;
import com.isratafrin.spacecolony.storage.Medbay;
import com.isratafrin.spacecolony.storage.MissionControl;
import com.isratafrin.spacecolony.storage.Quarters;
import com.isratafrin.spacecolony.storage.Simulator;
import com.isratafrin.spacecolony.storage.Storage;

import java.io.Serializable;

public class GameState implements Serializable {
    private static GameState instance;

    private final Quarters quarters;
    private final Simulator simulator;
    private final MissionControl missionControl;
    private final Medbay medbay;
    private final GameStatistics statistics;

    private GameState() {
        this.quarters = new Quarters();
        this.simulator = new Simulator();
        this.missionControl = new MissionControl();
        this.medbay = new Medbay();
        this.statistics = new GameStatistics();
    }

    public static synchronized GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
            instance.seed();
        }
        return instance;
    }

    private void seed() {
        addCrew(new com.isratafrin.spacecolony.model.Pilot("Alex Chen"), quarters);
        addCrew(new com.isratafrin.spacecolony.model.Engineer("Maya Park"), quarters);
        addCrew(new com.isratafrin.spacecolony.model.Soldier("Tara Novak"), quarters);
        statistics.recordRecruit();
        statistics.recordRecruit();
        statistics.recordRecruit();
    }

    private void addCrew(CrewMember cm, Storage s) {
        s.addCrewMember(cm);
    }

    public void recruit(CrewMember cm) {
        quarters.addCrewMember(cm);
        statistics.recordRecruit();
    }

    public void moveCrew(CrewMember cm, Storage from, Storage to) {
        if (cm == null || from == null || to == null) return;
        from.removeCrewMember(cm.getId());
        to.addCrewMember(cm);
    }

    public Storage findLocation(int crewId) {
        if (quarters.contains(crewId)) return quarters;
        if (simulator.contains(crewId)) return simulator;
        if (missionControl.contains(crewId)) return missionControl;
        if (medbay.contains(crewId)) return medbay;
        return null;
    }

    public int totalCrew() {
        return quarters.size() + simulator.size() + missionControl.size() + medbay.size();
    }

    public Quarters getQuarters() { return quarters; }
    public Simulator getSimulator() { return simulator; }
    public MissionControl getMissionControl() { return missionControl; }
    public Medbay getMedbay() { return medbay; }
    public GameStatistics getStatistics() { return statistics; }
}
