# Space Colony

An Android game built for an Object-Oriented Programming course. The player
manages a space colony crew — recruit members, train them in a simulator, and
send them on turn-based missions against system-generated threats.

- **Student:** Israt Jahan Afrin
- **Platform:** Android (Java, Android Studio)
- **Min SDK:** 24 · **Target SDK:** 36

---

## How to Run

1. Open the project in Android Studio.
2. Let Gradle sync (uses Material Components + AppCompat — nothing extra).
3. Run on an emulator or a physical device (API 24+).

The app seeds a starter colony of three crew members on first launch so you
can jump straight into a mission.

---

## Features

### Crew Management
- Five specializations: Pilot, Engineer, Medic, Scientist, Soldier.
- Each has fixed base stats (skill, resilience, max energy) — see table below.
- Crew can be moved between Quarters, Simulator, Mission Control, and Medbay.

### Training
- In the Simulator, crew gain experience points at the cost of energy.
- Effective skill = base skill + experience.
- Returning to Quarters restores energy fully but keeps experience.

### Missions
- Select 2 or 3 crew for a squad in Mission Control.
- Threat is generated with difficulty scaling (`threat skill = 4 + mission count`).
- Random mission type: Asteroid / Repair / Medical / Research / Combat.
- Crew matching the mission type get a **+2 bonus**.
- Turn-based: Attack, Defend, or Special per crew member, then threat retaliates.
- Damage = `(skill + experience + bonus) − resilience + random(0-3)`, min 1.
- Win → survivors earn +2 XP. Defeated crew go to the Medbay, not lost permanently.

### Medbay
- Recovery restores energy but applies a small experience penalty (−1 XP).

### Statistics
- **Colony tab**: total missions, wins, success rate, specialization distribution.
- **Crew tab**: per-member missions, wins, damage dealt.

---

## Crew Stats

| Specialization | Skill | Resilience | Max Energy | Bonus Mission |
|---|---|---|---|---|
| Pilot          | 5 | 4 | 20 | Asteroid  |
| Engineer       | 6 | 3 | 19 | Repair    |
| Medic          | 7 | 2 | 18 | Medical   |
| Scientist      | 8 | 1 | 17 | Research  |
| Soldier        | 9 | 0 | 16 | Combat    |

The Medic also has a unique `healAlly(target)` special ability.

---

## Class Structure

### Model (`com.isratafrin.spacecolony.model`)
- **`CrewMember`** — abstract. Fields: name, specialization, skill, resilience,
  experience, energy, maxEnergy, auto-incrementing `id`, and per-crew stats
  (missionsCompleted, missionsWon, trainingCount, totalDamageDealt).
  Methods: `act()`, `defend(damage)`, `isAlive()`, `train()`, `restoreEnergy()`,
  `getEffectiveSkill()`, and the abstract `getBonus(missionType)`.
- **`Pilot`, `Engineer`, `Medic`, `Scientist`, `Soldier`** — concrete subclasses.
- **`Threat`** — enemy with `attack(target)`, `defend(damage)`, `isDefeated()`,
  and a static factory `generate(missionCount, squadSize)`.
- **`MissionResult`** — immutable record of success, survivors, defeated, mission
  type, threat name, and log.

### Storage (`com.isratafrin.spacecolony.storage`)
- **`Storage`** — abstract, holds a `HashMap<Integer, CrewMember>` keyed by id.
  Methods: `addCrewMember`, `removeCrewMember`, `getCrewMember`,
  `listCrewMembers`, `size`.
- **`Quarters`** — `createCrewMember`, `restoreEnergy`.
- **`Simulator`** — `train`.
- **`MissionControl`** — `launchMission`, `generateThreat`, plus `missionCounter`.
- **`Medbay`** — `recover`, `applyPenalty`.

### Game (`com.isratafrin.spacecolony.game`)
- **`GameState`** — singleton that owns Quarters, Simulator, MissionControl,
  Medbay, and GameStatistics. Seeds the starting crew on first access.
- **`GameStatistics`** — tracks totalMissions, totalWins, totalRecruits,
  totalCasualties. Exposes `recordMission`, `recordRecruit`, `getSuccessRate`,
  `getCrewStats`, `getColonyStats`.

### UI (`com.isratafrin.spacecolony.ui`)
- **`BaseActivity`** — common app bar setup, bottom navigation wiring, and
  window-insets padding (status bar + gesture nav).
- **`CrewListActivity`** — shared list screen for Quarters, Simulator, Medbay.
  Subclasses supply title, empty-state text, storage, and action buttons.
- **Activities:** `MainActivity` (Home), `RecruitActivity`, `QuartersActivity`,
  `SimulatorActivity`, `MissionControlActivity`, `MissionActivity`,
  `MedbayActivity`, `StatisticsActivity`.

---

## Screen Flow

```
Home ──► Recruit ──► Quarters
 │                    │
 │                    ├──► Simulator ──► (back to Quarters)
 │                    │
 │                    └──► Mission Control ──► Mission
 │                                               │ win  ──► Quarters
 │                                               │ lose ──► Medbay ──► Quarters
 │
 └──► Statistics (Colony / Crew)
```

The bottom nav bar has quick access to Home, Quarters, Sim, Mission, Stats.

---

## Folder Layout

```
app/src/main/
├── AndroidManifest.xml
├── java/com/isratafrin/spacecolony/
│   ├── MainActivity.java
│   ├── model/          # CrewMember + subclasses, Threat, MissionResult
│   ├── storage/        # Storage + Quarters, Simulator, MissionControl, Medbay
│   ├── game/           # GameState, GameStatistics
│   └── ui/             # Activities + BaseActivity + UiUtils
└── res/
    ├── layout/         # activity_*.xml, item_*.xml, include_*.xml
    ├── drawable/       # card_bg, avatar_circle, bar_fill, btn_*, fab_bg
    └── values/         # colors, strings, themes
```

---

## OOP Concepts Used

- **Inheritance** — `CrewMember` → Pilot/Engineer/Medic/Scientist/Soldier;
  `Storage` → Quarters/Simulator/MissionControl/Medbay;
  `BaseActivity` → every screen.
- **Abstract classes** — `CrewMember` and `Storage`.
- **Polymorphism** — `getBonus(missionType)` is overridden by each crew subclass;
  mission logic calls it without caring about the concrete type.
- **Encapsulation** — fields are private; state changes go through methods like
  `defend`, `train`, `setEnergy` that keep values valid.
- **Static factory** — `Threat.generate(...)` produces a scaled enemy.
- **Singleton** — `GameState.getInstance()` is the single source of truth.

---

## Notes / Known Limitations

- **Persistence**: the project plan mentions Gson + SharedPreferences for
  save/load. This build keeps state in the `GameState` singleton — preserved
  across activities, but reset on full process death. Adding persistence is a
  drop-in enhancement (all model/storage classes already implement
  `Serializable`).
- **Charts**: the Statistics screen uses simple bar visualizations instead of
  MPAndroidChart to avoid an extra dependency.
