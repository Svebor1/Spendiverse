package com.ctk.spendiverse;

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
import java.util.Arrays;

public class KategorijaAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> dataModalArrayList;
    public KategorijaAdapter(@NonNull Context context, ArrayList<String> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
        this.context = context;
        this.dataModalArrayList = dataModalArrayList;
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
            //kategorije prehrana, kućanstvo i promet se ne mogu uređivati niti brisati
            kanta.setVisibility(View.INVISIBLE);
            edit.setVisibility(View.INVISIBLE);
        }
        else {
            kanta.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
        }
        AlertDialog alertDialogBrisanje =
                //ako korisnik hoće izbrisati kategoriju prvo će se otvoriti prozor za potvrdu
                new AlertDialog.Builder(context)
                        .setTitle(R.string.brisanje_kategorije_naslov_alert)
                        .setMessage(R.string.brisanje_kategorije_alert)
                        .setPositiveButton(R.string.odgovor_da, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                izbrisiKategoriju(kategorija); //ako je korisnik potvrdio brisanje kategorije poziva se metoda brisanja kategorije
                            }
                        })
                        .setNegativeButton(R.string.odgovor_ne, new DialogInterface.OnClickListener() {
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
                //ako korisnik hoće urediti trošak
                promijeniKategorijuDialog(kategorija, position);
            }
        });
        return listitemView;
    };

    /**
     * metoda za uređivanje kategorije
     * @param kategorija naziv kategorije koju hoćemo promijeniti
     * @param novaKategorija novi naziv kategorije
     * @param position pozicija kategorije koju hoćemo promijeniti
     */
    void promijeniKategoriju(String kategorija, String novaKategorija, int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //ažuriramo zadanu kategoriju u troškovima u bazi
        db.collection("korisnici").document(firebaseUser.getUid())
                .collection("troskovi")
                .whereEqualTo("kategorija", kategorija) //uređujemo troškove samo s zadanom kategorijom
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot trosak: task.getResult()) {
                                        //ažurirana je zadana kategorija u trošku
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
        //ažuriramo zadanu kategoriju u bazi
        db.collection("korisnici").document(firebaseUser.getUid())
                .collection("kategorije")
                .whereEqualTo("naziv", kategorija) //ažuriramo samo zadanu kategoriju
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot kategorija: task.getResult()) {
                                        //kategorija je ažurirana
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
        dataModalArrayList.set(position, novaKategorija); //ažurirana kategorija u nizu
        notifyDataSetChanged(); //ažuriranje kategorije u listview
    }

    /**
     * metoda za unos novog naziva kategorije i njegovu promijenu
     * @param kategorija naziv kategorije koju hoćemo promijeniti
     * @param position pozicija kategorije koju hoćemo promijeniti
     */
    void promijeniKategorijuDialog(String kategorija, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Unesite novi naziv kategorije");

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
                else {
                    boolean rezultatBooleana = true;
                    //provjera postoji li već upisana kategorija u nizu s kategorijama
                    if (dataModalArrayList.stream().anyMatch(str -> str.toLowerCase().equals(input.getText().toString().toLowerCase()))) {
                        input.setError("Upisana kategorija već postoji");
                        //ako već postoji varijabla rezultatBoleana će biti false i neće omogućiti da se promijeni trošak na novi naziv
                        rezultatBooleana = false;
                    }
                    if (rezultatBooleana) {
                        novaKategorija = input.getText().toString(); //novi naziv kategorije
                        //metoda za promijenu kategorije
                        promijeniKategoriju(kategorija, novaKategorija, position);
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    /**
     * metoda za brisanje kategorije
     * @param kategorija
     */
    void izbrisiKategoriju(String kategorija) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //brišemo troškove s zadanom kategorijom u bazi
        db.collection("korisnici").document(firebaseUser.getUid())
                .collection("troskovi")
                .whereEqualTo("kategorija", kategorija) //biramo samo troškove s zadanom kategorijom
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot trosak: task.getResult()) {
                                        //brisanje troškova
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
        //brišemo kategoriju u bazi
        db.collection("korisnici").document(firebaseUser.getUid())
                .collection("kategorije")
                .whereEqualTo("naziv", kategorija) //biramo samo zadanu kategoriju
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot kategorija: task.getResult()) {
                                        //brisanje kategorije
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
        remove(kategorija); //brisanje kategorije u listview
    }
}
