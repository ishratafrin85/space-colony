package com.isratafrin.spacecolony.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.isratafrin.spacecolony.R;
import com.isratafrin.spacecolony.game.GameState;
import com.isratafrin.spacecolony.model.CrewMember;
import com.isratafrin.spacecolony.storage.Storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MissionControlActivity extends CrewListActivity {

    private int squadSize = 2;
    private TextView btn2;
    private TextView btn3;
    private TextView selectionCount;

    @Override
    protected String getCurrentTab() { return TAB_MISSION; }

    @Override
    protected String getTitleText() { return getString(R.string.title_mission_control); }

    @Override
    protected String getEmptyText() { return getString(R.string.empty_mission_control); }

    @Override
    protected Storage getStorage() { return GameState.getInstance().getMissionControl(); }

    @Override
    protected void addHeader(LinearLayout header) {
        header.removeAllViews();
        header.setOrientation(LinearLayout.VERTICAL);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_bg);
        card.setElevation(dp(2));
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.setMargins(dp(16), dp(16), dp(16), dp(8));
        card.setLayoutParams(clp);

        TextView label = new TextView(this);
        label.setText(R.string.squad_size);
        label.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        label.setTextSize(13f);
        label.setPadding(0, 0, 0, dp(8));
        card.addView(label);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        btn2 = makeToggle(getString(R.string.squad_2), 2);
        btn3 = makeToggle(getString(R.string.squad_3), 3);

        LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        mlp.setMarginEnd(dp(4));
        btn2.setLayoutParams(mlp);
        LinearLayout.LayoutParams mlp2 = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        mlp2.setMarginStart(dp(4));
        btn3.setLayoutParams(mlp2);

        row.addView(btn2);
        row.addView(btn3);
        card.addView(row);
        header.addView(card);

        selectionCount = new TextView(this);
        selectionCount.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        selectionCount.setTextSize(12f);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slp.setMargins(dp(16), 0, dp(16), dp(8));
        selectionCount.setLayoutParams(slp);
        header.addView(selectionCount);

        refreshSquadButtons();
    }

    private TextView makeToggle(String text, int size) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp(12), dp(10), dp(12), dp(10));
        tv.setTextSize(13f);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setOnClickListener(v -> {
            squadSize = size;
            trimSelection();
            refreshSquadButtons();
            render();
        });
        return tv;
    }

    private void refreshSquadButtons() {
        if (btn2 == null || btn3 == null) return;
        styleToggle(btn2, squadSize == 2);
        styleToggle(btn3, squadSize == 3);
    }

    private void styleToggle(TextView tv, boolean selected) {
        if (selected) {
            tv.setBackgroundResource(R.drawable.btn_primary);
            tv.setTextColor(Color.WHITE);
        } else {
            tv.setBackgroundResource(R.drawable.btn_outline);
            tv.setTextColor(ContextCompat.getColor(this, R.color.primary));
        }
    }

    private void trimSelection() {
        if (selected.size() <= squadSize) return;
        Iterator<Integer> it = selected.iterator();
        int keep = 0;
        List<Integer> toRemove = new ArrayList<>();
        for (Integer id : selected) {
            if (keep >= squadSize) toRemove.add(id);
            keep++;
        }
        selected.removeAll(toRemove);
    }

    @Override
    protected void toggleSelection(int id) {
        if (selected.contains(id)) {
            selected.remove(id);
            return;
        }
        if (selected.size() >= squadSize) {
            Integer oldest = selected.iterator().next();
            selected.remove(oldest);
        }
        selected.add(id);
    }

    @Override
    protected void render() {
        super.render();
        if (selectionCount != null) {
            selectionCount.setText(selected.size() + "/" + squadSize + " selected");
        }
    }

    @Override
    protected void addActions(LinearLayout actionBar) {
        boolean canLaunch = selected.size() == squadSize;

        actionBar.setOrientation(LinearLayout.VERTICAL);
        actionBar.addView(buildSquadPreview());

        TextView launch = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(8);
        launch.setLayoutParams(lp);
        launch.setBackgroundResource(R.drawable.btn_accent);
        launch.setText(R.string.btn_launch);
        launch.setAllCaps(true);
        launch.setTextColor(ContextCompat.getColor(this, R.color.white));
        launch.setTextSize(14f);
        launch.setGravity(android.view.Gravity.CENTER);
        launch.setPadding(dp(16), dp(12), dp(16), dp(12));
        launch.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        launch.setLetterSpacing(0.05f);
        launch.setAlpha(canLaunch ? 1f : 0.4f);
        launch.setOnClickListener(v -> {
            if (!canLaunch) return;
            int[] ids = new int[selected.size()];
            int i = 0;
            for (CrewMember c : getSelectedCrew()) ids[i++] = c.getId();
            Intent it = new Intent(this, MissionActivity.class);
            it.putExtra(MissionActivity.EXTRA_SQUAD_IDS, ids);
            startActivity(it);
        });
        actionBar.addView(launch);
    }

    private View buildSquadPreview() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.HORIZONTAL);
        panel.setGravity(android.view.Gravity.CENTER_VERTICAL);
        panel.setPadding(dp(12), dp(8), dp(12), dp(8));
        panel.setBackgroundResource(R.drawable.card_bg);

        int totalSkill = 0;
        int totalResilience = 0;
        int totalEnergy = 0;
        for (CrewMember c : getSelectedCrew()) {
            totalSkill += c.getEffectiveSkill();
            totalResilience += c.getResilience();
            totalEnergy += c.getEnergy();
        }

        panel.addView(previewCell("Skill", String.valueOf(totalSkill)));
        panel.addView(previewCell("Res", String.valueOf(totalResilience)));
        panel.addView(previewCell("Energy", String.valueOf(totalEnergy)));
        return panel;
    }

    private View previewCell(String label, String value) {
        LinearLayout cell = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        cell.setLayoutParams(lp);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(android.view.Gravity.CENTER);

        TextView v = new TextView(this);
        v.setText(value);
        v.setTextColor(ContextCompat.getColor(this, R.color.primary));
        v.setTextSize(18f);
        v.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

        TextView l = new TextView(this);
        l.setText(label);
        l.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        l.setTextSize(11f);

        cell.addView(v);
        cell.addView(l);
        return cell;
    }
}
