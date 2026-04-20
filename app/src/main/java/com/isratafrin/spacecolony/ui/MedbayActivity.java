package com.isratafrin.spacecolony.ui;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.isratafrin.spacecolony.R;
import com.isratafrin.spacecolony.game.GameState;
import com.isratafrin.spacecolony.model.CrewMember;
import com.isratafrin.spacecolony.storage.Storage;

public class MedbayActivity extends CrewListActivity {

    @Override
    protected String getTitleText() { return getString(R.string.title_medbay); }

    @Override
    protected String getEmptyText() { return getString(R.string.empty_medbay); }

    @Override
    protected Storage getStorage() { return GameState.getInstance().getMedbay(); }

    @Override
    protected void addActions(LinearLayout actionBar) {
        boolean hasSelection = !selected.isEmpty();
        String label = getString(R.string.btn_recover) + " (" + selected.size() + ")";

        TextView recover = makeButton(label, R.drawable.btn_primary, R.color.white, 1f);
        recover.setAlpha(hasSelection ? 1f : 0.4f);
        recover.setOnClickListener(v -> {
            if (!hasSelection) return;
            GameState gs = GameState.getInstance();
            for (CrewMember c : getSelectedCrew()) {
                gs.getMedbay().recover(c);
                gs.moveCrew(c, gs.getMedbay(), gs.getQuarters());
            }
            selected.clear();
            render();
        });

        actionBar.addView(recover);
    }
}
