package com.ctk.spendiverse;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

//ovaj adapter koristimo za prikaz ljestvice bodova na ekranu za prikaz ljestvice
public class LjestvicaAdapter extends ArrayAdapter<RezultatNatjecatelja> {
    private final String TAG = "LjestvicaAdapter";

    public LjestvicaAdapter(@NonNull Context context, int resource, @NonNull ArrayList<RezultatNatjecatelja> objects) {
        super(context, resource, objects);
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

        ImageView ljestvicaPlanBedz = listitemView.findViewById(R.id.ljestvica_plan_bedz);
        ImageView ljestvicaTrosakBedz = listitemView.findViewById(R.id.ljestvica_trosak_bedz);
        ImageView ljestvicaLaganoBedz = listitemView.findViewById(R.id.ljestvica_lagano_bedz);
        ImageView ljestvicaSrednjeBedz = listitemView.findViewById(R.id.ljestvica_srednje_bedz);
        ImageView ljestvicaTeskoBedz = listitemView.findViewById(R.id.ljestvica_tesko_bedz);

        TextView nazivKorisnika = listitemView.findViewById(R.id.naziv_korisnika);
        TextView brojBodova = listitemView.findViewById(R.id.broj_bodova);
        TextView mjestoKorisnika = listitemView.findViewById(R.id.mjesto_korisnika);
        nazivKorisnika.setText(rezultatNatjecatelja.getImeKorisnika());
        brojBodova.setText(bodovi.toString());
        String[] bedzevi = rezultatNatjecatelja.getListaBedzeva();

        ljestvicaPlanBedz.setVisibility(View.GONE);
        ljestvicaTrosakBedz.setVisibility(View.GONE);
        ljestvicaLaganoBedz.setVisibility(View.GONE);
        ljestvicaSrednjeBedz.setVisibility(View.GONE);
        ljestvicaTeskoBedz.setVisibility(View.GONE);

        for(String bedz : bedzevi){
            if (bedz.equals("prvi_plan")){
                ljestvicaPlanBedz.setVisibility(View.VISIBLE);
            }
            if (bedz.equals("prvi_trosak")){
                ljestvicaTrosakBedz.setVisibility(View.VISIBLE);
            }
            if (bedz.equals("bedz_lagani_kvizovi")){
                ljestvicaLaganoBedz.setVisibility(View.VISIBLE);
            }
            if (bedz.equals("bedz_srednji_kvizovi")){
                ljestvicaSrednjeBedz.setVisibility(View.VISIBLE);
            }
            if (bedz.equals("bedz_teski_kvizovi")){
                ljestvicaTeskoBedz.setVisibility(View.VISIBLE);
            }
        }
        Integer pozicija = rezultatNatjecatelja.getPozicija();
        mjestoKorisnika.setText(Integer.toString(pozicija));

        return listitemView;
    }
}
