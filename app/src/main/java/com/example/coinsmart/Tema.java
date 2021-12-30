package com.example.coinsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tema extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tema);
        TextView naslovTeme = findViewById(R.id.naslov_teme);
        Bundle bundle = getIntent().getExtras();
        String naslovGrupe = bundle.get("nazivGrupe").toString();
        String naslovTemeText = bundle.get("nazivTeme").toString();
        int redniBrojKviza = Integer.getInteger(bundle.get("redniBrojKviza").toString());
        naslovTeme.setText(naslovTemeText);
        Button pocetakKviza;
        pocetakKviza=findViewById(R.id.kviz_start);
        pocetakKviza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prikazPitanja(naslovTemeText, redniBrojKviza, naslovGrupe);

            }
        });




        ImageCarousel carousel = findViewById(R.id.carousel);

// Register lifecycle. For activity this will be lifecycle/getLifecycle() and for fragments it will be viewLifecycleOwner/getViewLifecycleOwner().
        carousel.registerLifecycle(getLifecycle());

        List<CarouselItem> list = new ArrayList<>();
// Image URL with caption
        list.add(
                new CarouselItem(
                        "https://images.unsplash.com/photo-1532581291347-9c39cf10a73c?w=1080",
                        "Photo by Aaron Wu on Unsplash"
                )
        );

// Just image URL
        list.add(
                new CarouselItem(
                        "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080"
                )
        );

// Image URL with header
        Map<String, String> headers = new HashMap<>();
        headers.put("header_key", "header_value");

        list.add(
                new CarouselItem(
                        "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080",
                        headers
                )
        );

// Image drawable with caption
        list.add(
                new CarouselItem(
                        R.drawable.kasica_ljudi_novac,
                        "Photo by Kimiya Oveisi on Unsplash"
                )
        );

// Just image drawable
        list.add(
                new CarouselItem(
                        R.drawable.rich_bankar
                )
        );

// ...

        carousel.setData(list);
    }
    private void prikazPitanja(String naslovTeme, int redniBrojKviza, String naslovGrupe) {
        Bundle bundle = new Bundle();
        bundle.putString("naslovGrupe", naslovGrupe);
        bundle.putString("naslovTeme", naslovTeme);
        bundle.putInt("redniBrojKviza", redniBrojKviza);
        Intent intent = new Intent(this, Pitanje.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}