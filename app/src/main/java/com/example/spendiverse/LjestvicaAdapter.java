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
        TextView nazivKorisnika = listitemView.findViewById(R.id.naziv_korisnika);
        TextView brojBodova = listitemView.findViewById(R.id.broj_bodova);

        nazivKorisnika.setText(rezultatNatjecatelja.getImeKorisnika());
        brojBodova.setText(rezultatNatjecatelja.getRezultatKorisnika().toString());


        return listitemView;
    }
}
