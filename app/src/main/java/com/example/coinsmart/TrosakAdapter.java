package com.example.coinsmart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TrosakAdapter extends ArrayAdapter<Trosak> {
    private final String TAG = "ProductAdapter";
    public TrosakAdapter(@NonNull Context context, ArrayList<Trosak> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
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

        nameTV.setText(trosak.getNaziv());
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
