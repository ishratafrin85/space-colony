package com.isratafrin.spacecolony.ui;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.isratafrin.spacecolony.R;
import com.isratafrin.spacecolony.game.GameState;
import com.isratafrin.spacecolony.model.CrewMember;
import com.isratafrin.spacecolony.storage.Storage;

// Simulator overrides meta to expose training sessions (per spec).

public class SimulatorActivity extends CrewListActivity {

    @Override
    protected String getCurrentTab() { return TAB_SIM; }

    @Override
    protected String getTitleText() { return getString(R.string.title_simulator); }

    @Override
    protected String getEmptyText() { return getString(R.string.empty_simulator); }

    @Override
    protected Storage getStorage() { return GameState.getInstance().getSimulator(); }

    @Override
    protected String formatMeta(CrewMember cm) {
        return cm.getSpecialization()
                + " · XP " + cm.getExperience()
                + " · Sessions " + cm.getTrainingCount();
    }

    @Override
    protected void addActions(LinearLayout actionBar) {
        boolean hasSelection = !selected.isEmpty();

        TextView returnBtn = makeButton(getString(R.string.btn_return),
                R.drawable.btn_outline, R.color.primary, 1f);
        returnBtn.setAlpha(hasSelection ? 1f : 0.4f);
        returnBtn.setOnClickListener(v -> {
            if (!hasSelection) return;
            GameState gs = GameState.getInstance();
            for (CrewMember c : getSelectedCrew()) {
                gs.getQuarters().restoreEnergy(c);
                gs.moveCrew(c, gs.getSimulator(), gs.getQuarters());
            }
            selected.clear();
            render();
        });

        TextView trainBtn = makeButton(getString(R.string.btn_train),
                R.drawable.btn_primary, R.color.white, 1f);
        trainBtn.setAlpha(hasSelection ? 1f : 0.4f);
        trainBtn.setOnClickListener(v -> {
            if (!hasSelection) return;
            GameState gs = GameState.getInstance();
            for (CrewMember c : getSelectedCrew()) {
                gs.getSimulator().train(c);
            }
            render();
        });

        actionBar.addView(returnBtn);
        actionBar.addView(trainBtn);
    }
}
