package io.github.nearchos.projectiles;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PlanetPagerAdapter extends FragmentStatePagerAdapter {

    private final Planet [] planets;

    PlanetPagerAdapter(@NonNull final FragmentManager fragmentManager, final Planet [] planets) {
        super(fragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.planets = planets;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new PlanetSlidePageFragment(planets[position]);
    }

    @Override
    public int getCount() {
        return planets.length;
    }
}
