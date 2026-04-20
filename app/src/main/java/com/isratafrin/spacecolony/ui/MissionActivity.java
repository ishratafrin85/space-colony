package com.isratafrin.spacecolony.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.isratafrin.spacecolony.R;
import com.isratafrin.spacecolony.game.GameState;
import com.isratafrin.spacecolony.model.CrewMember;
import com.isratafrin.spacecolony.model.Medic;
import com.isratafrin.spacecolony.model.Scientist;
import com.isratafrin.spacecolony.model.MissionResult;
import com.isratafrin.spacecolony.model.Threat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MissionActivity extends BaseActivity {

    public static final String EXTRA_SQUAD_IDS = "squad_ids";

    private List<CrewMember> squad = new ArrayList<>();
    private final Set<Integer> defending = new HashSet<>();
    private Threat threat;
    private final List<String> log = new ArrayList<>();
    private int turn = 0;
    private boolean done = false;
    private boolean won = false;

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        setupAppBar(getString(R.string.title_mission), false);

        initMission();
        buildActions();

        findViewById(R.id.btnContinue).setOnClickListener(v -> finishMission());

        render();
    }

    private void initMission() {
        int[] ids = getIntent().getIntArrayExtra(EXTRA_SQUAD_IDS);
        GameState gs = GameState.getInstance();
        if (ids != null) {
            for (int id : ids) {
                CrewMember cm = gs.getMissionControl().getCrewMember(id);
                if (cm != null) squad.add(cm);
            }
        }
        threat = Threat.generate(gs.getMissionControl().getMissionCounter(), squad.size());
        gs.getMissionControl().incrementMissionCounter();
        log.add("Mission: " + threat.getType() + ". Enemy: " + threat.getName() + ".");
    }

    private void buildActions() {
        LinearLayout bar = findViewById(R.id.actionBar);
        bar.removeAllViews();

        TextView attack = makeAction(getString(R.string.btn_attack),
                R.drawable.btn_primary, R.color.white);
        attack.setOnClickListener(v -> doAction("attack"));

        TextView defend = makeAction(getString(R.string.btn_defend),
                R.drawable.btn_outline, R.color.primary);
        defend.setOnClickListener(v -> doAction("defend"));

        TextView special = makeAction(getString(R.string.btn_special),
                R.drawable.btn_outline, R.color.primary);
        special.setOnClickListener(v -> doAction("special"));

        bar.addView(attack);
        bar.addView(defend);
        bar.addView(special);
    }

    private TextView makeAction(String text, int bg, int fg) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMarginStart(dp(4));
        lp.setMarginEnd(dp(4));
        tv.setLayoutParams(lp);
        tv.setBackgroundResource(bg);
        tv.setText(text);
        tv.setAllCaps(true);
        tv.setTextColor(ContextCompat.getColor(this, fg));
        tv.setTextSize(14f);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp(12), dp(12), dp(12), dp(12));
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setLetterSpacing(0.05f);
        return tv;
    }

    private CrewMember getActive() {
        List<CrewMember> alive = new ArrayList<>();
        for (CrewMember c : squad) if (c.isAlive()) alive.add(c);
        if (alive.isEmpty()) return null;
        return alive.get(turn % alive.size());
    }

    private void doAction(String action) {
        if (done) return;
        CrewMember actor = getActive();
        if (actor == null) return;

        int bonus = actor.getBonus(threat.getType());

        switch (action) {
            case "attack": {
                int damage = Math.max(1,
                        actor.getEffectiveSkill() + bonus + random.nextInt(4) - threat.getResilience());
                threat.defend(damage);
                actor.addDamageDealt(damage);
                log.add(0, actor.getName() + " attacks for " + damage +
                        (bonus > 0 ? " (+" + bonus + " bonus)" : "") + ".");
                break;
            }
            case "defend":
                defending.add(actor.getId());
                log.add(0, actor.getName() + " defends.");
                break;
            case "special":
                if (actor instanceof Medic) {
                    CrewMember weakest = findWeakestAlly();
                    if (weakest != null) {
                        ((Medic) actor).healAlly(weakest);
                        log.add(0, actor.getName() + " heals " + weakest.getName() + " for 5.");
                    }
                } else if (actor instanceof Scientist) {
                    threat.reduceResilience(2);
                    log.add(0, actor.getName() + " analyzes weakness (−2 enemy resilience).");
                } else {
                    int damage = Math.max(1,
                            actor.getEffectiveSkill() + bonus + 3 + random.nextInt(4) - threat.getResilience());
                    threat.defend(damage);
                    actor.addDamageDealt(damage);
                    log.add(0, actor.getName() + " uses special for " + damage + ".");
                }
                break;
        }

        if (!threat.isDefeated() && actor.isAlive()) {
            int before = actor.getEnergy();
            int dmg = Math.max(1, threat.getSkill() - actor.getResilience() + random.nextInt(3));
            if (defending.contains(actor.getId())) {
                dmg = (dmg + 1) / 2;
                defending.remove(actor.getId());
            }
            actor.setEnergy(before - dmg);
            log.add(0, "Enemy attacks " + actor.getName() + " for " + dmg + ".");
            if (!actor.isAlive()) log.add(0, actor.getName() + " is defeated.");
        }

        turn++;
        checkEnd();
        render();
    }

    private CrewMember findWeakestAlly() {
        CrewMember weakest = null;
        for (CrewMember c : squad) {
            if (!c.isAlive()) continue;
            if (weakest == null || c.getEnergy() < weakest.getEnergy()) weakest = c;
        }
        return weakest;
    }

    private void checkEnd() {
        if (threat.isDefeated()) {
            done = true;
            won = true;
            log.add(0, "Mission won!");
        } else if (allDown()) {
            done = true;
            won = false;
            log.add(0, "Mission failed.");
        }
    }

    private boolean allDown() {
        for (CrewMember c : squad) if (c.isAlive()) return false;
        return true;
    }

    private void render() {
        ((TextView) findViewById(R.id.threatType))
                .setText("Enemy · " + threat.getType());
        ((TextView) findViewById(R.id.threatName)).setText(threat.getName());
        ((TextView) findViewById(R.id.threatStats)).setText(
                "Sk " + threat.getSkill() + " · Res " + threat.getResilience());
        UiUtils.setBar((ProgressBar) findViewById(R.id.threatBar),
                threat.getEnergy(), threat.getMaxEnergy());
        ((TextView) findViewById(R.id.threatEnergy)).setText(
                threat.getEnergy() + "/" + threat.getMaxEnergy());

        renderSquad();
        renderLog();

        LinearLayout actions = findViewById(R.id.actionBar);
        LinearLayout banner = findViewById(R.id.endBanner);
        if (done) {
            actions.setVisibility(View.GONE);
            banner.setVisibility(View.VISIBLE);
            int bg = won ? R.color.win_bg : R.color.lose_bg;
            banner.setBackgroundColor(ContextCompat.getColor(this, bg));
            TextView endTitle = findViewById(R.id.endTitle);
            endTitle.setText(won ? R.string.mission_won : R.string.mission_lost);
            endTitle.setTextColor(ContextCompat.getColor(this, won ? R.color.ok : R.color.bad));
            ((TextView) findViewById(R.id.endDesc)).setText(
                    won ? R.string.mission_won_desc : R.string.mission_lost_desc);
        } else {
            actions.setVisibility(View.VISIBLE);
            banner.setVisibility(View.GONE);
        }
    }

    private void renderSquad() {
        LinearLayout list = findViewById(R.id.squadList);
        list.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        CrewMember active = done ? null : getActive();

        for (int i = 0; i < squad.size(); i++) {
            CrewMember c = squad.get(i);
            View row = inflater.inflate(R.layout.item_mission_squad, list, false);

            TextView indicator = row.findViewById(R.id.indicator);
            TextView avatar = row.findViewById(R.id.avatar);
            TextView name = row.findViewById(R.id.name);
            TextView down = row.findViewById(R.id.downBadge);
            ProgressBar bar = row.findViewById(R.id.energyBar);
            TextView energyText = row.findViewById(R.id.energyText);

            boolean isActive = active != null && active.getId() == c.getId();
            boolean isDown = !c.isAlive();

            indicator.setVisibility(isActive ? View.VISIBLE : View.INVISIBLE);
            UiUtils.styleAvatar(avatar, c);
            name.setText(c.getName());
            down.setVisibility(isDown ? View.VISIBLE : View.GONE);
            UiUtils.setBar(bar, c.getEnergy(), c.getMaxEnergy());
            energyText.setText(c.getEnergy() + "/" + c.getMaxEnergy());

            if (isActive) {
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.win_bg));
            } else {
                row.setBackgroundColor(Color.TRANSPARENT);
            }
            if (isDown) row.setAlpha(0.5f);
            else row.setAlpha(1f);

            list.addView(row);

            if (i < squad.size() - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
                dlp.setMarginStart(dp(16));
                dlp.setMarginEnd(dp(16));
                divider.setLayoutParams(dlp);
                divider.setBackgroundResource(R.color.divider);
                list.addView(divider);
            }
        }
    }

    private void renderLog() {
        LinearLayout card = findViewById(R.id.logCard);
        card.removeAllViews();
        int max = Math.min(6, log.size());
        for (int i = 0; i < max; i++) {
            TextView line = new TextView(this);
            line.setText(log.get(i));
            line.setTextSize(12f);
            line.setTextColor(ContextCompat.getColor(this,
                    i == 0 ? R.color.text_primary : R.color.text_secondary));
            line.setPadding(0, dp(2), 0, dp(2));
            card.addView(line);
        }
        if (log.isEmpty()) {
            TextView line = new TextView(this);
            line.setText("—");
            line.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            card.addView(line);
        }
    }

    private void finishMission() {
        GameState gs = GameState.getInstance();
        List<CrewMember> survivors = new ArrayList<>();
        List<CrewMember> defeated = new ArrayList<>();

        for (CrewMember c : squad) {
            c.addMissionCompleted();
            gs.getMissionControl().removeCrewMember(c.getId());
            if (c.isAlive() && won) {
                c.addMissionWon();
                c.addExperience(2);
                gs.getQuarters().addCrewMember(c);
                survivors.add(c);
            } else if (c.isAlive()) {
                gs.getQuarters().addCrewMember(c);
                survivors.add(c);
            } else {
                gs.getMedbay().addCrewMember(c);
                defeated.add(c);
            }
        }

        MissionResult result = new MissionResult(won, survivors, defeated,
                threat.getType(), threat.getName(), log);
        gs.getStatistics().recordMission(result);

        Intent clear = new Intent(this, com.isratafrin.spacecolony.MainActivity.class);
        clear.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(clear);
        finish();
    }

    @Override
    public void onBackPressed() {
        // no-op; user must finish mission
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
