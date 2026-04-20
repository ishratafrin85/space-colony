package com.isratafrin.spacecolony;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.isratafrin.spacecolony.game.GameState;
import com.isratafrin.spacecolony.ui.BaseActivity;
import com.isratafrin.spacecolony.ui.MedbayActivity;
import com.isratafrin.spacecolony.ui.MissionControlActivity;
import com.isratafrin.spacecolony.ui.QuartersActivity;
import com.isratafrin.spacecolony.ui.RecruitActivity;
import com.isratafrin.spacecolony.ui.SimulatorActivity;
import com.isratafrin.spacecolony.ui.StatisticsActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupAppBar(getString(R.string.title_home), false);
        setupBottomNav(TAB_HOME);

        findViewById(R.id.fabRecruit).setOnClickListener(v ->
                startActivity(new Intent(this, RecruitActivity.class)));

        findViewById(R.id.rowStats).setOnClickListener(v ->
                startActivity(new Intent(this, StatisticsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        GameState gs = GameState.getInstance();
        int crew = gs.totalCrew();
        int missions = gs.getStatistics().getTotalMissions();
        int success = (int) Math.round(gs.getStatistics().getSuccessRate() * 100);

        ((TextView) findViewById(R.id.statCrew)).setText(String.valueOf(crew));
        ((TextView) findViewById(R.id.statMissions)).setText(String.valueOf(missions));
        ((TextView) findViewById(R.id.statSuccess)).setText(success + "%");

        bindRow(R.id.rowQuarters, getString(R.string.title_quarters),
                gs.getQuarters().size(), QuartersActivity.class);
        bindRow(R.id.rowSimulator, getString(R.string.title_simulator),
                gs.getSimulator().size(), SimulatorActivity.class);
        bindRow(R.id.rowMission, getString(R.string.title_mission_control),
                gs.getMissionControl().size(), MissionControlActivity.class);
        bindRow(R.id.rowMedbay, getString(R.string.title_medbay),
                gs.getMedbay().size(), MedbayActivity.class);
    }

    private void bindRow(int rowId, String title, int count, Class<?> target) {
        LinearLayout row = findViewById(rowId);
        row.removeAllViews();

        TextView titleView = new TextView(this);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleView.setLayoutParams(tlp);
        titleView.setText(title);
        titleView.setTextSize(15f);
        titleView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

        TextView badge = new TextView(this);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(20));
        blp.setMarginEnd(dp(12));
        badge.setLayoutParams(blp);
        badge.setText(String.valueOf(count));
        badge.setTextColor(Color.WHITE);
        badge.setTextSize(12f);
        badge.setTypeface(Typeface.DEFAULT_BOLD);
        badge.setGravity(Gravity.CENTER);
        badge.setMinWidth(dp(28));
        badge.setPadding(dp(8), 0, dp(8), 0);
        badge.setBackgroundResource(R.drawable.count_badge);

        TextView chevron = new TextView(this);
        chevron.setText("›");
        chevron.setTextSize(18f);
        chevron.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

        row.addView(titleView);
        row.addView(badge);
        row.addView(chevron);
        row.setOnClickListener(v -> startActivity(new Intent(this, target)));
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
