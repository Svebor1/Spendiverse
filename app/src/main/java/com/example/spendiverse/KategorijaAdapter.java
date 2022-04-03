package com.example.spendiverse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class KategorijaAdapter extends ArrayAdapter<String> {
    private Context context;
    public KategorijaAdapter(@NonNull Context context, ArrayList<String> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
        this.context = context;
    }
    String TAG = "KategorijaAdapter";
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.kategorija_adapter_item, parent, false);
        }

        String kategorija = getItem(position);
        TextView naziv = listitemView.findViewById(R.id.naziv);
        naziv.setText(kategorija);

        ImageButton kanta = listitemView.findViewById(R.id.kanta_kategorija);
        ImageButton edit = listitemView.findViewById(R.id.edit_kategorija);
        if (position < 3) {
            kanta.setVisibility(View.INVISIBLE);
            edit.setVisibility(View.INVISIBLE);
        }
        kanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("korisnici").document(firebaseUser.getUid())
                        .collection("troskovi")
                        .whereEqualTo("kategorija", kategorija)
                        .get()
                        .addOnCompleteListener(
                                new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot trosak: task.getResult()) {
                                                trosak.getReference().delete();
                                            }
                                        }
                                    }
                                }
                        )
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
                db.collection("korisnici").document(firebaseUser.getUid())
                        .collection("kategorije")
                        .whereEqualTo("naziv", kategorija)
                        .get()
                        .addOnCompleteListener(
                                new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot kategorija: task.getResult()) {
                                                kategorija.getReference().delete();
                                            }
                                        }
                                    }
                                }
                        )
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
                remove(kategorija);
            }

        });
        return listitemView;
    };
}
