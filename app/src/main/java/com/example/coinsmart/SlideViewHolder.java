package com.example.coinsmart;

import android.view.ViewGroup;
import android.widget.TextView;

import com.opensooq.pluto.base.PlutoViewHolder;

public class SlideViewHolder extends PlutoViewHolder<SlideModel> {

    TextView opis;
    public SlideViewHolder(ViewGroup parent, int itemLayoutId) {
        super(parent, itemLayoutId);
        opis = getView(R.id.opis);
    }

    @Override
    public void set(SlideModel slideModel, int i) {
        opis.setText(slideModel.getText());
    }
}
