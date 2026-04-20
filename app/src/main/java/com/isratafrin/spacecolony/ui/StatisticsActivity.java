package com.isratafrin.spacecolony.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.isratafrin.spacecolony.R;
import com.isratafrin.spacecolony.game.GameState;
import com.isratafrin.spacecolony.model.CrewMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends BaseActivity {

    private static final String[] ROLES =
            {"Pilot", "Engineer", "Medic", "Scientist", "Soldier"};

    private String currentTab = "colony";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setupAppBar(getString(R.string.title_statistics), true);
        setupBottomNav(TAB_STATS);

        findViewById(R.id.tabColony).setOnClickListener(v -> { currentTab = "colony"; render(); });
        findViewById(R.id.tabCrew).setOnClickListener(v -> { currentTab = "crew"; render(); });

        render();
    }

    private void render() {
        ((TextView) findViewById(R.id.tabColony)).setAlpha(currentTab.equals("colony") ? 1f : 0.7f);
        ((TextView) findViewById(R.id.tabCrew)).setAlpha(currentTab.equals("crew") ? 1f : 0.7f);

        LinearLayout content = findViewById(R.id.content);
        content.removeAllViews();

        if (currentTab.equals("colony")) renderColony(content);
        else renderCrew(content);
    }

    private void renderColony(LinearLayout content) {
        GameState gs = GameState.getInstance();
        Map<String, Object> colony = gs.getStatistics().getColonyStats();

        LinearLayout card = makeCard();
        int i = 0;
        for (Map.Entry<String, Object> e : colony.entrySet()) {
            addKeyValueRow(card, e.getKey(), e.getValue().toString(), i < colony.size() - 1);
            i++;
        }
        content.addView(card);

        TextView heading = new TextView(this);
        heading.setText(R.string.section_specializations);
        heading.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        heading.setTextSize(13f);
        heading.setTypeface(Typeface.DEFAULT_BOLD);
        heading.setLetterSpacing(0.05f);
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hlp.setMargins(dp(16), dp(16), dp(16), dp(8));
        heading.setLayoutParams(hlp);
        content.addView(heading);

        LinearLayout distCard = makeCard();
        distCard.setPadding(dp(12), dp(12), dp(12), dp(12));
        int max = 1;
        for (String r : ROLES) max = Math.max(max, countRole(r));

        for (String r : ROLES) {
            int n = countRole(r);
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(0, dp(6), 0, dp(6));

            TextView label = new TextView(this);
            label.setText(r);
            label.setTextSize(13f);
            label.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(dp(80),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            label.setLayoutParams(llp);
            row.addView(label);

            LinearLayout barBg = new LinearLayout(this);
            LinearLayout.LayoutParams bglp = new LinearLayout.LayoutParams(
                    0, dp(8), 1f);
            barBg.setLayoutParams(bglp);
            barBg.setBackgroundResource(R.drawable.bar_bg);

            View fill = new View(this);
            int fillW = max == 0 ? 0 : (int) (((double) n / max) * 100);
            LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(
                    0, dp(8), fillW);
            fill.setLayoutParams(flp);
            fill.setBackgroundColor(ContextCompat.getColor(this, UiUtils.roleColor(r)));

            View spacer = new View(this);
            spacer.setLayoutParams(new LinearLayout.LayoutParams(0, dp(8), 100 - fillW));
            barBg.addView(fill);
            barBg.addView(spacer);
            row.addView(barBg);

            TextView count = new TextView(this);
            count.setText(String.valueOf(n));
            count.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            count.setTextSize(13f);
            LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
                    dp(24), ViewGroup.LayoutParams.WRAP_CONTENT);
            clp.setMarginStart(dp(8));
            count.setLayoutParams(clp);
            count.setGravity(Gravity.END);
            row.addView(count);

            distCard.addView(row);
        }
        content.addView(distCard);
    }

    private int countRole(String role) {
        int n = 0;
        for (CrewMember c : allCrew()) {
            if (c.getSpecialization().equals(role)) n++;
        }
        return n;
    }

    private List<CrewMember> allCrew() {
        GameState gs = GameState.getInstance();
        List<CrewMember> all = new ArrayList<>();
        all.addAll(gs.getQuarters().listCrewMembers());
        all.addAll(gs.getSimulator().listCrewMembers());
        all.addAll(gs.getMissionControl().listCrewMembers());
        all.addAll(gs.getMedbay().listCrewMembers());
        return all;
    }

    private void renderCrew(LinearLayout content) {
        LinearLayout card = makeCard();

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setPadding(dp(16), dp(10), dp(16), dp(10));
        header.setBackgroundColor(ContextCompat.getColor(this, R.color.bg));

        TextView h1 = new TextView(this);
        LinearLayout.LayoutParams h1lp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        h1.setLayoutParams(h1lp);
        h1.setText("Name");
        styleHeader(h1);
        header.addView(h1);

        TextView h2 = new TextView(this);
        LinearLayout.LayoutParams h2lp = new LinearLayout.LayoutParams(
                dp(36), ViewGroup.LayoutParams.WRAP_CONTENT);
        h2.setLayoutParams(h2lp);
        h2.setText("Mis");
        h2.setGravity(Gravity.END);
        styleHeader(h2);
        header.addView(h2);

        TextView h3 = new TextView(this);
        LinearLayout.LayoutParams h3lp = new LinearLayout.LayoutParams(
                dp(36), ViewGroup.LayoutParams.WRAP_CONTENT);
        h3.setLayoutParams(h3lp);
        h3.setText("Win");
        h3.setGravity(Gravity.END);
        styleHeader(h3);
        header.addView(h3);

        TextView h4 = new TextView(this);
        LinearLayout.LayoutParams h4lp = new LinearLayout.LayoutParams(
                dp(44), ViewGroup.LayoutParams.WRAP_CONTENT);
        h4.setLayoutParams(h4lp);
        h4.setText("Dmg");
        h4.setGravity(Gravity.END);
        styleHeader(h4);
        header.addView(h4);

        card.addView(header);

        List<CrewMember> crew = allCrew();
        for (int i = 0; i < crew.size(); i++) {
            CrewMember c = crew.get(i);
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(16), dp(12), dp(16), dp(12));

            TextView avatar = new TextView(this);
            LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(dp(28), dp(28));
            avatar.setLayoutParams(alp);
            avatar.setBackgroundResource(R.drawable.avatar_circle);
            avatar.setGravity(Gravity.CENTER);
            avatar.setTextColor(ContextCompat.getColor(this, R.color.white));
            avatar.setTextSize(10f);
            avatar.setTypeface(Typeface.DEFAULT_BOLD);
            UiUtils.styleAvatar(avatar, c);

            LinearLayout namebox = new LinearLayout(this);
            namebox.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams nblp = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            nblp.setMarginStart(dp(8));
            namebox.setLayoutParams(nblp);

            TextView name = new TextView(this);
            name.setText(c.getName());
            name.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            name.setTextSize(13f);
            namebox.addView(name);

            TextView role = new TextView(this);
            role.setText(c.getSpecialization());
            role.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            role.setTextSize(11f);
            namebox.addView(role);

            TextView mis = new TextView(this);
            LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(
                    dp(36), ViewGroup.LayoutParams.WRAP_CONTENT);
            mis.setLayoutParams(mlp);
            mis.setText(String.valueOf(c.getMissionsCompleted()));
            mis.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            mis.setTextSize(13f);
            mis.setGravity(Gravity.END);

            TextView win = new TextView(this);
            LinearLayout.LayoutParams wlp = new LinearLayout.LayoutParams(
                    dp(36), ViewGroup.LayoutParams.WRAP_CONTENT);
            win.setLayoutParams(wlp);
            win.setText(String.valueOf(c.getMissionsWon()));
            win.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            win.setTextSize(13f);
            win.setGravity(Gravity.END);

            TextView dmg = new TextView(this);
            LinearLayout.LayoutParams dmglp = new LinearLayout.LayoutParams(
                    dp(44), ViewGroup.LayoutParams.WRAP_CONTENT);
            dmg.setLayoutParams(dmglp);
            dmg.setText(String.valueOf(c.getTotalDamageDealt()));
            dmg.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            dmg.setTextSize(13f);
            dmg.setGravity(Gravity.END);

            row.addView(avatar);
            row.addView(namebox);
            row.addView(mis);
            row.addView(win);
            row.addView(dmg);

            card.addView(row);
            if (i < crew.size() - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
                dlp.setMarginStart(dp(16));
                dlp.setMarginEnd(dp(16));
                divider.setLayoutParams(dlp);
                divider.setBackgroundResource(R.color.divider);
                card.addView(divider);
            }
        }

        if (crew.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No crew yet.");
            empty.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            empty.setPadding(dp(16), dp(32), dp(16), dp(32));
            empty.setGravity(Gravity.CENTER);
            card.addView(empty);
        }

        content.addView(card);
    }

    private void styleHeader(TextView tv) {
        tv.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tv.setTextSize(11f);
        tv.setAllCaps(true);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
    }

    private LinearLayout makeCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_bg);
        card.setElevation(dp(2));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(16), dp(16), dp(16), 0);
        card.setLayoutParams(lp);
        return card;
    }

    private void addKeyValueRow(LinearLayout parent, String key, String value, boolean divider) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(10), dp(16), dp(10));

        TextView k = new TextView(this);
        LinearLayout.LayoutParams klp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        k.setLayoutParams(klp);
        k.setText(key);
        k.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        k.setTextSize(14f);

        TextView v = new TextView(this);
        v.setText(value);
        v.setTextColor(ContextCompat.getColor(this, R.color.primary));
        v.setTextSize(14f);
        v.setTypeface(Typeface.DEFAULT_BOLD);

        row.addView(k);
        row.addView(v);
        parent.addView(row);

        if (divider) {
            View div = new View(this);
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
            dlp.setMarginStart(dp(16));
            dlp.setMarginEnd(dp(16));
            div.setLayoutParams(dlp);
            div.setBackgroundResource(R.color.divider);
            parent.addView(div);
        }
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
