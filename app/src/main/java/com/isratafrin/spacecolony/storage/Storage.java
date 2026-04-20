package com.isratafrin.spacecolony.storage;

import com.isratafrin.spacecolony.model.CrewMember;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Storage implements Serializable {
    private final String name;
    private final HashMap<Integer, CrewMember> crewMap;

    protected Storage(String name) {
        this.name = name;
        this.crewMap = new HashMap<>();
    }

    public void addCrewMember(CrewMember cm) {
        if (cm != null) crewMap.put(cm.getId(), cm);
    }

    public void removeCrewMember(int id) {
        crewMap.remove(id);
    }

    public CrewMember getCrewMember(int id) {
        return crewMap.get(id);
    }

    public List<CrewMember> listCrewMembers() {
        return new ArrayList<>(crewMap.values());
    }

    public boolean contains(int id) {
        return crewMap.containsKey(id);
    }

    public int size() {
        return crewMap.size();
    }

    public String getName() {
        return name;
    }
}
