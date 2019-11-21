package io.github.nearchos.projectiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class PlanetSlidePageFragment extends Fragment {

    private final Planet planet;

    PlanetSlidePageFragment(final Planet planet) {
        this.planet = planet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_planet_slide_page, container, false);
        final ImageView imageView = rootView.findViewById(R.id.imageViewPlanetSlide);
        final int identifier = getResources().getIdentifier(planet.getResourceName(), "drawable", getContext().getPackageName());
        imageView.setImageResource(identifier);
        final TextView textView = rootView.findViewById(R.id.textViewPlanetSlide);
        textView.setText(String.format(Locale.US, "%s (%.1f %s)", planet.getName(), planet.getGravity(), getString(R.string.metersPerSecondPerSecond)));
        return rootView;
    }
}