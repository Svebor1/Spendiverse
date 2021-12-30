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
        String naslovTemeText = bundle.getString("nazivTeme");
        String naslovGrupe = bundle.getString("nazivGrupe");
        int redniBrojKviza = bundle.getInt("redniBrojKviza");
        naslovTeme.setText(naslovTemeText);
        Button pocetakKviza;
        pocetakKviza=findViewById(R.id.kviz_start);
        pocetakKviza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prikazPitanja(naslovTemeText, redniBrojKviza, naslovGrupe);
            }
        });



        int brojSlideova = getResources().getInteger(R.integer.lagano_tema0_brojslideova);
        ImageCarousel carousel = findViewById(R.id.carousel);

        // Register lifecycle. For activity this will be lifecycle/getLifecycle() and for fragments it will be viewLifecycleOwner/getViewLifecycleOwner().
        carousel.registerLifecycle(getLifecycle());

        List<CarouselItem> list = new ArrayList<>();


        Integer redniBrojTeme = bundle.getInt("redniBrojKviza");

        // Image drawable with caption
        for (int i = 0; i < brojSlideova; i++) {

            String idSlike = naslovGrupe + "_tema" + redniBrojTeme.toString() + "_slide" +Integer.toString(i).toString();
            int idSlikeBroj = getResources().getIdentifier("com.example.coinsmart:drawable/"+idSlike, null, null);

            String idTeksta = naslovGrupe + "_tema" + redniBrojTeme.toString() + "_text" +Integer.toString(i).toString();
            int idTekstaBroj = getResources().getIdentifier("com.example.coinsmart:string/"+idTeksta, null, null);
            list.add(
                    new CarouselItem(
                            idSlikeBroj, getResources().getString(idTekstaBroj)
                            )
            );
        }


        carousel.setData(list);
    }
    private void prikazPitanja(String naslovTeme, int redniBrojKviza, String naslovGrupe) {
        Bundle bundle = new Bundle();
        bundle.putString("nazivTeme", naslovTeme);
        bundle.putString("nazivGrupe", naslovGrupe);
        bundle.putInt("redniBrojKviza", redniBrojKviza);
        Intent intent = new Intent(this, Pitanje.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}