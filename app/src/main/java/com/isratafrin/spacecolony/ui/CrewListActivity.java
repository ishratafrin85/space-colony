package com.isratafrin.spacecolony.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.isratafrin.spacecolony.R;
import com.isratafrin.spacecolony.model.CrewMember;
import com.isratafrin.spacecolony.storage.Storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CrewListActivity extends BaseActivity {

    protected final Set<Integer> selected = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crew_list);
        setupAppBar(getTitleText(), true);
        setupBottomNav(getCurrentTab());
        addHeader((LinearLayout) findViewById(R.id.headerSlot));
    }

    protected String getCurrentTab() {
        return TAB_HOME;
    }

    protected String formatMeta(CrewMember cm) {
        return cm.getSpecialization() + " · XP " + cm.getExperience();
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    protected abstract String getTitleText();
    protected abstract String getEmptyText();
    protected abstract Storage getStorage();
    protected abstract void addActions(LinearLayout actionBar);

    protected void addHeader(LinearLayout headerSlot) {
        // optional override
    }

    protected List<CrewMember> getCrew() {
        return getStorage().listCrewMembers();
    }

    protected void render() {
        LinearLayout list = findViewById(R.id.crewList);
        TextView emptyText = findViewById(R.id.emptyText);
        View scroll = findViewById(R.id.listScroll);
        LinearLayout actionBar = findViewById(R.id.actionBar);

        list.removeAllViews();
        actionBar.removeAllViews();

        List<CrewMember> crew = new ArrayList<>(getCrew());
        selected.retainAll(collectIds(crew));

        if (crew.isEmpty()) {
            emptyText.setText(getEmptyText());
            emptyText.setVisibility(View.VISIBLE);
            scroll.setVisibility(View.GONE);
            actionBar.setVisibility(View.GONE);
            return;
        }

        emptyText.setVisibility(View.GONE);
        scroll.setVisibility(View.VISIBLE);
        actionBar.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < crew.size(); i++) {
            CrewMember cm = crew.get(i);
            View row = inflater.inflate(R.layout.item_crew, list, false);
            bindRow(row, cm);
            list.addView(row);
            if (i < crew.size() - 1) {
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

        addActions(actionBar);
    }

    private Set<Integer> collectIds(List<CrewMember> crew) {
        Set<Integer> ids = new HashSet<>();
        for (CrewMember c : crew) ids.add(c.getId());
        return ids;
    }

    private void bindRow(View row, CrewMember cm) {
        CheckBox checkbox = row.findViewById(R.id.checkbox);
        TextView avatar = row.findViewById(R.id.avatar);
        TextView name = row.findViewById(R.id.name);
        TextView meta = row.findViewById(R.id.meta);
        ProgressBar bar = row.findViewById(R.id.energyBar);
        TextView energyText = row.findViewById(R.id.energyText);

        UiUtils.styleAvatar(avatar, cm);
        name.setText(cm.getName());
        meta.setText(formatMeta(cm));
        UiUtils.setBar(bar, cm.getEnergy(), cm.getMaxEnergy());
        energyText.setText(cm.getEnergy() + "/" + cm.getMaxEnergy());

        boolean isSelected = selected.contains(cm.getId());
        checkbox.setChecked(isSelected);
        row.setBackgroundColor(isSelected
                ? ContextCompat.getColor(this, R.color.selected_row)
                : Color.TRANSPARENT);

        row.setOnClickListener(v -> {
            toggleSelection(cm.getId());
            render();
        });
    }

    protected void toggleSelection(int id) {
        if (selected.contains(id)) selected.remove(id);
        else selected.add(id);
    }

    protected List<CrewMember> getSelectedCrew() {
        List<CrewMember> list = new ArrayList<>();
        for (CrewMember c : getCrew()) {
            if (selected.contains(c.getId())) list.add(c);
        }
        return list;
    }

    protected TextView makeButton(String text, int bgDrawable, int textColor, float weight) {
        TextView btn = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, weight);
        lp.setMarginStart(dp(4));
        lp.setMarginEnd(dp(4));
        btn.setLayoutParams(lp);
        btn.setBackgroundResource(bgDrawable);
        btn.setText(text);
        btn.setAllCaps(true);
        btn.setTextColor(ContextCompat.getColor(this, textColor));
        btn.setTextSize(14f);
        btn.setGravity(android.view.Gravity.CENTER);
        btn.setPadding(dp(16), dp(12), dp(16), dp(12));
        btn.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        btn.setLetterSpacing(0.05f);
        return btn;
    }

    protected int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
