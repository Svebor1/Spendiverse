package com.example.spendiverse;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.spendiverse.databinding.ActivityMainBinding;
import com.example.spendiverse.databinding.SlideBinding;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Tema extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tema);
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView naslovTeme = findViewById(R.id.naslov_teme);
        Bundle bundle = getIntent().getExtras();
        String naslovTemeText = bundle.getString("nazivTeme");
        String naslovGrupe = bundle.getString("nazivGrupe").replace("š","s");
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


        String imeBrojaSlideova = naslovGrupe + "_tema" + redniBrojKviza + "_brojslideova";
        int brojSlideovaId = getResources().getIdentifier("com.example.coinsmart:integer/"+imeBrojaSlideova,null,null);
        int brojSlideova = getResources().getInteger(brojSlideovaId);
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
        carousel.setCarouselListener(new CarouselListener() {
            @Override
            public void onLongClick(int i, CarouselItem carouselItem) {

            }

            @Override
            public ViewBinding onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
                return SlideBinding.inflate(layoutInflater,viewGroup,false);
            }

            @Override
            public void onClick(int i, CarouselItem carouselItem) {

            }

            @Override
            public void onBindViewHolder(ViewBinding viewBinding, CarouselItem carouselItem, int i) {
                SlideBinding slideBinding=(SlideBinding) viewBinding;
                slideBinding.slideText.setText(carouselItem.getCaption());
                Utils.setImage(slideBinding.slideSlika,carouselItem,carouselItem.getImageDrawable());
            }
        });
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}