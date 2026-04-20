package com.isratafrin.spacecolony.ui;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.isratafrin.spacecolony.MainActivity;
import com.isratafrin.spacecolony.R;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAB_HOME = "home";
    public static final String TAB_QUARTERS = "quarters";
    public static final String TAB_SIM = "sim";
    public static final String TAB_MISSION = "mission";
    public static final String TAB_STATS = "stats";

    protected void setupAppBar(String title, boolean showBack) {
        TextView titleView = findViewById(R.id.appbarTitle);
        View backView = findViewById(R.id.btnBack);
        if (titleView != null) titleView.setText(title);
        if (backView != null) {
            backView.setVisibility(showBack ? View.VISIBLE : View.GONE);
            backView.setOnClickListener(v -> finish());
        }
        applyWindowInsets();
    }

    protected void setupBottomNav(String current) {
        bindNav(R.id.navHome, R.id.navHomeIcon, R.id.navHomeLabel,
                TAB_HOME, current, MainActivity.class, true);
        bindNav(R.id.navQuarters, R.id.navQuartersIcon, R.id.navQuartersLabel,
                TAB_QUARTERS, current, QuartersActivity.class, false);
        bindNav(R.id.navSim, R.id.navSimIcon, R.id.navSimLabel,
                TAB_SIM, current, SimulatorActivity.class, false);
        bindNav(R.id.navMission, R.id.navMissionIcon, R.id.navMissionLabel,
                TAB_MISSION, current, MissionControlActivity.class, false);
        bindNav(R.id.navStats, R.id.navStatsIcon, R.id.navStatsLabel,
                TAB_STATS, current, StatisticsActivity.class, false);
    }

    private void bindNav(int rootId, int iconId, int labelId,
                         String tab, String current, Class<?> target, boolean clearToTop) {
        View root = findViewById(rootId);
        TextView icon = findViewById(iconId);
        TextView label = findViewById(labelId);
        if (root == null) return;

        boolean active = tab.equals(current);
        int color = ContextCompat.getColor(this,
                active ? R.color.primary : R.color.text_secondary);
        if (icon != null) icon.setTextColor(color);
        if (label != null) label.setTextColor(color);

        root.setOnClickListener(v -> {
            if (active) return;
            Intent it = new Intent(this, target);
            if (clearToTop) {
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            startActivity(it);
            if (!(this instanceof MainActivity)) finish();
        });
    }

    private void applyWindowInsets() {
        View content = findViewById(android.R.id.content);
        if (content == null) return;

        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout());

            View appbar = v.findViewById(R.id.appbar);
            if (appbar != null) {
                appbar.setPadding(
                        appbar.getPaddingLeft(),
                        bars.top,
                        appbar.getPaddingRight(),
                        appbar.getPaddingBottom());
            } else {
                v.setPadding(v.getPaddingLeft(), bars.top,
                        v.getPaddingRight(), v.getPaddingBottom());
            }

            View nav = v.findViewById(R.id.bottomNav);
            if (nav != null) {
                nav.setPadding(nav.getPaddingLeft(), nav.getPaddingTop(),
                        nav.getPaddingRight(), bars.bottom);
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(),
                        v.getPaddingRight(), 0);
            } else {
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(),
                        v.getPaddingRight(), bars.bottom);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
