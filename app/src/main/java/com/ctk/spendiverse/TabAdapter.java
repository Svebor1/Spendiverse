package com.ctk.spendiverse;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class TabAdapter extends FragmentStateAdapter {

    public TabAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new KamateFragment();
        } else if (position == 1) {
            return new PopustiFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
