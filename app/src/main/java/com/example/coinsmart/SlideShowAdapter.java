package com.example.coinsmart;

import android.transition.Slide;
import android.view.ViewGroup;

import com.opensooq.pluto.base.PlutoAdapter;

import java.util.List;

public class SlideShowAdapter extends PlutoAdapter<SlideModel, SlideViewHolder> {

    public SlideShowAdapter(List<SlideModel> items) {
        super(items);
    }

    @Override
    public SlideViewHolder getViewHolder(ViewGroup viewGroup, int i) {
        return new SlideViewHolder(viewGroup, R.layout.kartica_layout);
    }

}
