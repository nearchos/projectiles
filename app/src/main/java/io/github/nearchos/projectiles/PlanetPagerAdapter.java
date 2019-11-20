package io.github.nearchos.projectiles;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PlanetPagerAdapter extends FragmentStatePagerAdapter {

    public PlanetPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new PlanetSlidePageFragment(Planet.PLANETS[position]);
    }

    @Override
    public int getCount() {
        return Planet.PLANETS.length;
    }
}
