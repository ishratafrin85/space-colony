package com.isratafrin.spacecolony.ui;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isratafrin.spacecolony.R;
import com.isratafrin.spacecolony.game.GameState;
import com.isratafrin.spacecolony.model.CrewMember;
import com.isratafrin.spacecolony.storage.Storage;

public class QuartersActivity extends CrewListActivity {

    @Override
    protected String getCurrentTab() { return TAB_QUARTERS; }

    @Override
    protected String getTitleText() { return getString(R.string.title_quarters); }

    @Override
    protected String getEmptyText() { return getString(R.string.empty_quarters); }

    @Override
    protected Storage getStorage() { return GameState.getInstance().getQuarters(); }

    @Override
    protected void addActions(LinearLayout actionBar) {
        boolean hasSelection = !selected.isEmpty();

        TextView toSim = makeButton(getString(R.string.btn_to_simulator),
                R.drawable.btn_outline, R.color.primary, 1f);
        toSim.setAlpha(hasSelection ? 1f : 0.4f);
        toSim.setOnClickListener(v -> {
            if (!hasSelection) return;
            GameState gs = GameState.getInstance();
            for (CrewMember c : getSelectedCrew()) {
                gs.moveCrew(c, gs.getQuarters(), gs.getSimulator());
            }
            selected.clear();
            render();
        });

        TextView toMission = makeButton(getString(R.string.btn_to_mission),
                R.drawable.btn_primary, R.color.white, 1f);
        toMission.setAlpha(hasSelection ? 1f : 0.4f);
        toMission.setOnClickListener(v -> {
            if (!hasSelection) return;
            GameState gs = GameState.getInstance();
            for (CrewMember c : getSelectedCrew()) {
                gs.moveCrew(c, gs.getQuarters(), gs.getMissionControl());
            }
            selected.clear();
            render();
        });

        actionBar.addView(toSim);
        actionBar.addView(toMission);
    }
}
