package com.example.spendiverse;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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
        AlertDialog alertDialogBrisanje =
                //ako korisnik hoće izbrisati kategoriju prvo će se otvoriti prozor za potvrdu
                new AlertDialog.Builder(context)
                        .setTitle("Brisanje kategorije")
                        .setMessage("Jeste li sigurni da želite izbrisati kategoriju?" +
                                "\nBiti će izbrisani svi troškovi koji pripadaju toj kategoriji")
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                izbrisiKategoriju(kategorija); //ako je korisnik potvrdio brisanje kategorije poziva se metoda brisanja kategorije
                            }
                        })
                        .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_baseline_help_24)
                        .create();

        kanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBrisanje.show();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promijeniKategorijuDialog(kategorija);
            }
        });
        return listitemView;
    };
    void promijeniKategoriju(String kategorija, String novaKategorija) {
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
                                        trosak.getReference().update("kategorija", novaKategorija)
                                                .addOnCompleteListener(
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                Log.i(TAG, "azurirana je kategorija");
                                                            }
                                                        }
                                                )
                                                .addOnFailureListener(
                                                        new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                                Log.i(TAG, "nije azurirana kategorija");
                                                            }
                                                        }

                                                );
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
                                        kategorija.getReference().update("naziv", novaKategorija);
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
    }
    void promijeniKategorijuDialog(String kategorija) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Unesite novi nadimak");

        //postavljanje unosa
        final EditText input = new EditText(context);
        //postavljanjje tipa unosa na text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        //postavljanje gumba
        builder.setPositiveButton("potvrdi", null);
        builder.setNegativeButton("odustani", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String novaKategorija;
                if (input.getText().toString().replace(" ","").length()==0){
                    input.setError("Nadimak ne smije biti prazan");
                }
                else{
                    novaKategorija = input.getText().toString();
                    promijeniKategoriju(kategorija, novaKategorija);
                    dialog.dismiss();
                }

            }
        });
    }
    void izbrisiKategoriju(String kategorija) {
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
}
