package com.isratafrin.spacecolony.ui;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.isratafrin.spacecolony.R;
import com.isratafrin.spacecolony.model.CrewMember;

public final class UiUtils {
    private UiUtils() {}

    public static String initials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length && sb.length() < 2; i++) {
            if (!parts[i].isEmpty()) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        return sb.toString();
    }

    public static int roleColor(String role) {
        switch (role) {
            case "Pilot":     return R.color.role_pilot;
            case "Engineer":  return R.color.role_engineer;
            case "Medic":     return R.color.role_medic;
            case "Scientist": return R.color.role_scientist;
            case "Soldier":   return R.color.role_soldier;
            default:          return R.color.primary;
        }
    }

    public static void styleAvatar(TextView view, CrewMember cm) {
        styleAvatar(view, cm.getName(), cm.getSpecialization());
    }

    public static void styleAvatar(TextView view, String name, String role) {
        view.setText(initials(name));
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(ContextCompat.getColor(view.getContext(), roleColor(role)));
        view.setBackground(bg);
    }

    public static void setBar(ProgressBar bar, int value, int max) {
        bar.setMax(Math.max(1, max));
        bar.setProgress(Math.max(0, Math.min(max, value)));
        double pct = max == 0 ? 0 : (double) value / max;
        int colorRes;
        if (pct > 0.5) colorRes = R.color.ok;
        else if (pct > 0.25) colorRes = R.color.warn;
        else colorRes = R.color.bad;
        int color = ContextCompat.getColor(bar.getContext(), colorRes);
        bar.setProgressTintList(ColorStateList.valueOf(color));
    }
}
