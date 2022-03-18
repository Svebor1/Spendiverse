package com.example.spendiverse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

//ovaj adapter koristimo za prikaz ljestvice bodova na ekranu za prikaz ljestvice
public class LjestvicaAdapter extends ArrayAdapter<RezultatNatjecatelja> {
    private final String TAG = "LjestvicaAdapter";
    private Integer brojac; //broji količinu prethodnih uzastopnih korisnika s istim brojem bodova
    private Integer prosliBodovi = null; //bodovi prošlog natjecatelja
    public LjestvicaAdapter(@NonNull Context context, int resource, @NonNull ArrayList<RezultatNatjecatelja> objects) {
        super(context, resource, objects);
        brojac = 0; //u samom početku je 0 uzastopnih korisnika s istim brojem bodova
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.ljestvica_adapter, parent, false);
        }

        RezultatNatjecatelja rezultatNatjecatelja = getItem(position);

        Integer bodovi = rezultatNatjecatelja.getRezultatKorisnika(); //bodovi natjecatelja

        TextView nazivKorisnika = listitemView.findViewById(R.id.naziv_korisnika);
        TextView brojBodova = listitemView.findViewById(R.id.broj_bodova);
        TextView mjestoKorisnika = listitemView.findViewById(R.id.mjesto_korisnika);
        nazivKorisnika.setText(rezultatNatjecatelja.getImeKorisnika());

        brojBodova.setText(bodovi.toString());
        Integer pozicija = position + 1;
        if (prosliBodovi != null) {
            if (bodovi.equals(prosliBodovi)) {
                //ako je broj bodova sadašnjeg i prošlog natjecatelja isti
                mjestoKorisnika.setText(Integer.toString(pozicija-brojac));
                brojac++;
            }
            else {
                //ako su broj bodova sadašnjeg i prošlog natjecatelja različiti
                mjestoKorisnika.setText(pozicija.toString());
                brojac = 1;
            }
        }
        else {
            //ako je to trenutni natjecatelj prvi u listi i nema prošlog
            mjestoKorisnika.setText(pozicija.toString());
            brojac = 1;
        }
        prosliBodovi = bodovi;
        return listitemView;
    }
}
