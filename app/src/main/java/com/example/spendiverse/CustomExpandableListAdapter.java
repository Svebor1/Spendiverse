package com.example.spendiverse;


import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//adapter koji se koristi za kategorije tema
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> imenaKategorija; //lista imena kategorija
    private HashMap<String, List<String>> imenaTema; //lista imena tema u kategorijama
    private List<String> rjeseniKvizovi; //lista s rjesenim kvizovima


    //konstruktor
    public CustomExpandableListAdapter(Context context, List<String> imenaKategorija,
                                       HashMap<String, List<String>> imenaTema, List<String> rjeseniKvizovi) {
        this.context = context;
        this.imenaKategorija = imenaKategorija;
        this.imenaTema = imenaTema;
        this.rjeseniKvizovi = rjeseniKvizovi;
    }

    @Override
    public Object getChild(int pozicijaKategorije, int pozicijaTeme) {
        String trazenaKategorija = this.imenaKategorija.get(pozicijaKategorije); //dobivanje imena kategorije
        List<String> teme = this.imenaTema.get(trazenaKategorija); //dobivanje liste tema u toj kategoriji
        return teme.get(pozicijaTeme); //vraća temu
    }

    @Override
    public long getChildId(int pozicijaKategorije, int pozicijaTeme) {
        return pozicijaTeme;
    }

    @Override
    public View getChildView(int pozicijaKategorije, final int pozicijaTeme,
                             boolean isLastChild, View temaView, ViewGroup grupa) {
        final String imeTeme = (String) getChild(pozicijaKategorije, pozicijaTeme);
        String imeKategorije = "";
        if (pozicijaKategorije==0){
            imeKategorije = "lagano";
        }
        if (pozicijaKategorije==1){
            imeKategorije = "srednje";
        }
        if (pozicijaKategorije==2){
            imeKategorije = "tesko";
        }

        if (temaView == null) {
            //stvaramo view uz pomoć layoutInflatera
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            temaView = layoutInflater.inflate(R.layout.list_item, null);
        }
        ImageView kvacicaImage = temaView.findViewById(R.id.kvacica_image);
        String pomocnaVarijabla = imeKategorije + "_" + imeTeme;
        if (rjeseniKvizovi.contains(imeKategorije + "_" + imeTeme)){
            kvacicaImage.setVisibility(View.GONE);
        }
        else{
            kvacicaImage.setVisibility(View.VISIBLE);

        }
        TextView naslovTemeView = temaView.findViewById(R.id.naslovTeme);
        naslovTemeView.setText(imeTeme); //prikazujemo tekst teme
        return temaView;
    }

    @Override
    public int getChildrenCount(int pozicijaKategorije) {
        String trazenaKategorija = this.imenaKategorija.get(pozicijaKategorije);
        return this.imenaTema.get(trazenaKategorija).size();
    }

    @Override
    public Object getGroup(int pozicijaKategorije) {
        return this.imenaKategorija.get(pozicijaKategorije);
    }

    @Override
    public int getGroupCount() {
        return this.imenaKategorija.size();
    }

    @Override
    public long getGroupId(int pozicijaKategorije) {
        return pozicijaKategorije;
    }

    @Override
    public View getGroupView(int pozicijaKategorije, boolean isExpanded,
                             View kategorijaView, ViewGroup grupa) {
        String nazivKategorije = (String) getGroup(pozicijaKategorije);
        if (kategorijaView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            kategorijaView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) kategorijaView
                .findViewById(R.id.naslovKategorije);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(nazivKategorije);
        return kategorijaView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int pozicijaKategorije, int pozicijaTeme) {
        return true;
    }
}
