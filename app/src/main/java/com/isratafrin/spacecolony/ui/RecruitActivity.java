package com.isratafrin.spacecolony.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.isratafrin.spacecolony.R;
import com.isratafrin.spacecolony.game.GameState;
import com.isratafrin.spacecolony.model.CrewMember;
import com.isratafrin.spacecolony.model.Engineer;
import com.isratafrin.spacecolony.model.Medic;
import com.isratafrin.spacecolony.model.Pilot;
import com.isratafrin.spacecolony.model.Scientist;
import com.isratafrin.spacecolony.model.Soldier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class RecruitActivity extends BaseActivity {

    private static final String[] ROLES =
            {"Pilot", "Engineer", "Medic", "Scientist", "Soldier"};

    private final Map<String, RadioButton> radios = new LinkedHashMap<>();
    private String selectedRole = "Pilot";
    private EditText inputName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);
        setupAppBar(getString(R.string.title_recruit), true);
        setupBottomNav(TAB_HOME);

        inputName = findViewById(R.id.inputName);
        inputName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) { updatePreview(); }
            public void afterTextChanged(Editable s) {}
        });

        buildRoleList();
        updatePreview();

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnCreate).setOnClickListener(v -> submit());
    }

    private void buildRoleList() {
        LinearLayout container = findViewById(R.id.roleList);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < ROLES.length; i++) {
            final String role = ROLES[i];
            View row = inflater.inflate(R.layout.item_role, container, false);

            RadioButton radio = row.findViewById(R.id.radio);
            TextView avatar = row.findViewById(R.id.avatar);
            TextView name = row.findViewById(R.id.roleName);
            TextView stats = row.findViewById(R.id.roleStats);

            UiUtils.styleAvatar(avatar, "··", role);
            name.setText(role);
            CrewMember sample = createByRole(role, role);
            stats.setText("Skill " + sample.getSkill() +
                    " · Resilience " + sample.getResilience() +
                    " · Energy " + sample.getMaxEnergy());

            radios.put(role, radio);
            row.setOnClickListener(v -> {
                selectedRole = role;
                refreshRadios();
                updatePreview();
            });
            container.addView(row);

            if (i < ROLES.length - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
                dlp.setMarginStart(dp(16));
                dlp.setMarginEnd(dp(16));
                divider.setLayoutParams(dlp);
                divider.setBackgroundResource(R.color.divider);
                container.addView(divider);
            }
        }
        refreshRadios();
    }

    private void refreshRadios() {
        for (Map.Entry<String, RadioButton> e : radios.entrySet()) {
            e.getValue().setChecked(e.getKey().equals(selectedRole));
        }
    }

    private void updatePreview() {
        String name = inputName.getText().toString().trim();
        if (name.isEmpty()) name = "New Recruit";
        CrewMember cm = createByRole(name, selectedRole);

        TextView avatar = findViewById(R.id.previewAvatar);
        UiUtils.styleAvatar(avatar, cm);
        ((TextView) findViewById(R.id.previewName)).setText(name);
        ((TextView) findViewById(R.id.previewRole)).setText(selectedRole);
        ((TextView) findViewById(R.id.statSkill)).setText(String.valueOf(cm.getSkill()));
        ((TextView) findViewById(R.id.statResilience)).setText(String.valueOf(cm.getResilience()));
        ((TextView) findViewById(R.id.statEnergy)).setText(String.valueOf(cm.getMaxEnergy()));
    }

    private void submit() {
        String name = inputName.getText().toString().trim();
        if (name.isEmpty()) {
            name = "Recruit " + (100 + new Random().nextInt(900));
        }
        CrewMember cm = createByRole(name, selectedRole);
        GameState.getInstance().recruit(cm);
        finish();
    }

    private CrewMember createByRole(String name, String role) {
        switch (role) {
            case "Engineer":  return new Engineer(name);
            case "Medic":     return new Medic(name);
            case "Scientist": return new Scientist(name);
            case "Soldier":   return new Soldier(name);
            default:          return new Pilot(name);
        }
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
