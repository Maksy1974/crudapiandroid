package com.example.crudapi;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class DashboardActivity extends AppCompatActivity {

    private static final class Tile {
        final String title;
        final String subtitle;
        @DrawableRes
        final int iconRes;
        final int accentColor;
        final Class<?> target;

        Tile(String title, String subtitle, int iconRes, int accentColor, Class<?> target) {
            this.title = title;
            this.subtitle = subtitle;
            this.iconRes = iconRes;
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
                        R.drawable.ic_entity_prodi,
                        R.color.poliman_blue,
                        MainActivity.class
                ),
                new Tile(
                        getString(R.string.menu_mahasiswa_title),
                        getString(R.string.menu_mahasiswa_subtitle),
                        R.drawable.ic_entity_mahasiswa,
                        R.color.poliman_teal,
                        MahasiswaActivity.class
                ),
                new Tile(
                        getString(R.string.menu_matakuliah_title),
                        getString(R.string.menu_matakuliah_subtitle),
                        R.drawable.ic_entity_matakuliah,
                        R.color.badge_orange,
                        MatakuliahActivity.class
                ),
                new Tile(
                        getString(R.string.menu_krs_title),
                        getString(R.string.menu_krs_subtitle),
                        R.drawable.ic_entity_krs,
                        R.color.badge_purple,
                        KrsActivity.class
                ),
                new Tile(
                        getString(R.string.menu_dosen_title),
                        getString(R.string.menu_dosen_subtitle),
                        R.drawable.ic_entity_dosen,
                        R.color.poliman_blue_dark,
                        DosenActivity.class
                ),
                new Tile(
                        getString(R.string.menu_lab_title),
                        getString(R.string.menu_lab_subtitle),
                        R.drawable.ic_entity_lab,
                        R.color.poliman_teal,
                        LaboratoriumActivity.class
                )
        };

        int margin = (int) (8 * getResources().getDisplayMetrics().density);
        for (Tile tile : tiles) {
            View row = LayoutInflater.from(this).inflate(R.layout.item_dashboard_card, grid, false);
            FrameLayout iconBg = row.findViewById(R.id.frameIconBg);
            ImageView imgIcon = row.findViewById(R.id.imgMenuIcon);
            TextView title = row.findViewById(R.id.txtTitle);
            TextView subtitle = row.findViewById(R.id.txtSubtitle);
            MaterialCardView card = (MaterialCardView) row;

            imgIcon.setImageResource(tile.iconRes);
            iconBg.setBackgroundResource(R.drawable.bg_dashboard_badge);
            iconBg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, tile.accentColor)));

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
