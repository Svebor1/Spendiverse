package com.example.spendiverse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TrosakAdapter extends ArrayAdapter<Trosak> {
    private final String TAG = "TrosakAdapter";
    private Context context;
    public TrosakAdapter(@NonNull Context context, ArrayList<Trosak> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
        this.context = context;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.trosak_adapter_item, parent, false);
        }

        Trosak trosak = getItem(position);

        TextView nameTV = listitemView.findViewById(R.id.naziv_tv);
        TextView datum = listitemView.findViewById(R.id.datum_tv);
        TextView cijena = listitemView.findViewById(R.id.cijena_tv);
        nameTV.setText(trosak.getNaziv());
        datum.setText(trosak.getDatumDan()+"."+trosak.getDatumMjesec()+"."+trosak.getDatumGodina()+".");
        DecimalFormat myFormatter = new DecimalFormat("#.##");
        String zaokruzenaCijena = myFormatter.format(trosak.getCijena());
        cijena.setText(zaokruzenaCijena + " "+  trosak.getValuta());
        ImageButton kanta;
        ImageButton edit;
        edit = listitemView.findViewById(R.id.edit);
        kanta = listitemView.findViewById(R.id.kanta);
        kanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("korisnici").document(firebaseUser.getUid())
                        .collection("troskovi").document(trosak.getFirebaseId()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "DocumentSnapshot deleted with ID: " + trosak.getFirebaseId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
                remove(trosak);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference slikaRef = storageRef.child(trosak.getFirebaseId() +".jpg");
                slikaRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                    }
                });
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, UnosTroskovaActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("naziv", trosak.getNaziv());
                bundle.putString("kategorija", trosak.getKategorija());
                bundle.putString("valuta", trosak.getValuta());
                bundle.putInt("datumDan", trosak.getDatumDan());
                bundle.putInt("datumMjesec", trosak.getDatumMjesec());
                bundle.putInt("datumGodina", trosak.getDatumGodina());
                bundle.putDouble("cijena", trosak.getCijena());
                bundle.putString("firebaseId", trosak.getFirebaseId());
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });

        /*
        kanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(firebaseUser.getUid()).document("data")
                        .collection("products").document(product.getFirebaseId()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "DocumentSnapshot deleted with ID: " + product.getFirebaseId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
                remove(product);
            } //
        });
        */
        return listitemView;
    }
}
