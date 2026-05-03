package com.example.crudapi;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class DashboardActivity extends AppCompatActivity {

    private static final class Tile {
        final String title;
        final String subtitle;
        final String badge;
        final int accentColor;
        @NonNull
        final Class<?> target;

        Tile(String title, String subtitle, String badge, int accentColor, @NonNull Class<?> target) {
            this.title = title;
            this.subtitle = subtitle;
            this.badge = badge;
            this.accentColor = accentColor;
            this.target = target;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GridLayout grid = findViewById(R.id.gridMenu);

        Tile[] tiles = new Tile[]{
                new Tile(
                        getString(R.string.menu_prodi_title),
                        getString(R.string.menu_prodi_subtitle),
                        "P",
                        R.color.poliman_blue,
                        MainActivity.class
                ),
                new Tile(
                        getString(R.string.menu_mahasiswa_title),
                        getString(R.string.menu_mahasiswa_subtitle),
                        "M",
                        R.color.poliman_teal,
                        MahasiswaActivity.class
                ),
                new Tile(
                        getString(R.string.menu_matakuliah_title),
                        getString(R.string.menu_matakuliah_subtitle),
                        "MK",
                        R.color.badge_orange,
                        MatakuliahActivity.class
                ),
                new Tile(
                        getString(R.string.menu_krs_title),
                        getString(R.string.menu_krs_subtitle),
                        "K",
                        R.color.badge_purple,
                        KrsActivity.class
                )
        };

        int margin = (int) (8 * getResources().getDisplayMetrics().density);
        for (Tile tile : tiles) {
            View row = LayoutInflater.from(this).inflate(R.layout.item_dashboard_card, grid, false);
            TextView badge = row.findViewById(R.id.txtBadge);
            TextView title = row.findViewById(R.id.txtTitle);
            TextView subtitle = row.findViewById(R.id.txtSubtitle);
            MaterialCardView card = (MaterialCardView) row;

            badge.setText(tile.badge);
            badge.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, tile.accentColor)));
            title.setText(tile.title);
            subtitle.setText(tile.subtitle);

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = GridLayout.LayoutParams.WRAP_CONTENT;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(margin, margin, margin, margin);
            card.setLayoutParams(lp);

            Intent intent = new Intent(this, tile.target);
            card.setOnClickListener(v -> startActivity(intent));

            grid.addView(card);
        }
    }
}
