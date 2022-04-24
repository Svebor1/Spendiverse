package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UnosTroskovaActivity extends AppCompatActivity {
    private final String TAG = "Unos troskova activity";
    private Spinner spinner;
    private Spinner spinnerValuta;
    private final Calendar myCalendar = Calendar.getInstance();
    private TextView nazivTroska;
    private Button dodatiTrosak;
    private ImageButton dodatiRacun;
    private Integer dan;
    private Integer mjesec;
    private Integer godina;
    private Bitmap photo;
    private ImageView slikaRacuna;
    private TextView cijenaTroska;
    private TextView datumTroska;
    private ArrayAdapter arrayAdapter;
    private ArrayAdapter arrayAdapterValute;
    private String firebaseIdTroska;
    private ImageButton buttonIzbrisiSliku;
    private Integer postojanjeBedzaZaTrosak;
    private ImageButton buttonDownloadSlike;
    private ImageButton buttonUploadSlike;
    String zadaneKategorije[] = {"prehrana", "kućanstvo", "promet"};
    String zadaneValute[] = {"HRK", "USD", "EUR", "GBP"};
    ArrayList<String> kategorije = new ArrayList<>(Arrays.asList(zadaneKategorije));
    ArrayList<String> valute = new ArrayList<>(Arrays.asList(zadaneValute));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unos_troskova);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        spinner = findViewById(R.id.spinner);
        spinnerValuta = findViewById(R.id.spinner_valuta);
        slikaRacuna = findViewById(R.id.slika_racuna);
        datumTroska = findViewById(R.id.datum_troska);
        dodatiTrosak = findViewById(R.id.dodati_trosak);
        dodatiRacun = findViewById(R.id.dodati_racun);
        nazivTroska = findViewById(R.id.naziv_troska);
        cijenaTroska = findViewById(R.id.cijena_troska);
        buttonDownloadSlike = findViewById(R.id.download_slike);
        buttonIzbrisiSliku = findViewById(R.id.izbrisi_sliku_racuna);
        buttonUploadSlike = findViewById(R.id.upload_slike);

        db.collection("korisnici").document(firebaseUser.getUid()).collection("valute")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String nazivValute = document.getData().get("naziv").toString();
                                valute.add(nazivValute);
                            }
                            arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, kategorije);
                            arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinner.setAdapter(arrayAdapter); //postavljanje niz mogućih kategorija u izbornik za kategorije

                            arrayAdapterValute = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, valute);
                            arrayAdapterValute.setDropDownViewResource(R.layout.spinner_item);
                            spinnerValuta.setAdapter(arrayAdapterValute);
                            postavljanjeUnosaTroska();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

        db.collection("korisnici").document(firebaseUser.getUid()).collection("kategorije")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String nazivKategorije = document.getData().get("naziv").toString();
                                kategorije.add(nazivKategorije);
                            }
                            arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, kategorije);
                            arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinner.setAdapter(arrayAdapter); //postavljanje niz mogućih kategorija u izbornik za kategorije

                            arrayAdapterValute = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, valute);
                            arrayAdapterValute.setDropDownViewResource(R.layout.spinner_item);
                            spinnerValuta.setAdapter(arrayAdapterValute);
                            postavljanjeUnosaTroska();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

        slikaRacuna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikazSlikeRacuna();
            }
        });
        buttonIzbrisiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                izbrisiSlikuRacuna();
                slikaRacuna.setImageResource(R.drawable.ic_racun);
            }
        });
        buttonDownloadSlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeImage(photo);
            }
        });
        buttonUploadSlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,2);
            }
        });
    }


    private void storeImage(Bitmap image) {
        String fileName = Calendar.getInstance().getTime().toString().replace(" ","").replace("+","").replace(":","").replace("-","");
        MediaStore.Images.Media.insertImage(getContentResolver(), image, "spendiverse" + fileName +".jpg", "");
        Toast.makeText(this, "Slika je spremljena u galeriju", Toast.LENGTH_LONG).show();
    }


    private void izbrisiSlikuRacuna(){
        photo = null;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference slikaRef = storageRef.child(firebaseIdTroska+".jpg");
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

    private void prikazSlikeRacuna() {
        if (photo != null) {
            Bundle bundle = new Bundle();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap smallPhoto = resize(photo,720,720);
            smallPhoto.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bundle.putByteArray("slika", stream.toByteArray());
            Intent intent = new Intent(this, PrikazSlikeRacunaActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void postavljanjeUnosaTroska() {
        Bundle bundle = getIntent().getExtras();
        dodatiRacun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            }
        });
        if (bundle == null) { //ako je to upis novog troška

            prikaziDatum();
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (provjeriUnos()) {
                        dodajNoviTrosak();
                    }
                }
            };
            dodatiTrosak.setOnClickListener(listener);
        } else {
            //uzimaju se podaci starog troška i prikazuju se na ekranu
            String nazivTroskaText = bundle.getString("naziv");
            nazivTroska.setText(nazivTroskaText);
            Double cijenaTroskaText = bundle.getDouble("cijena");
            cijenaTroska.setText(cijenaTroskaText.toString());
            String kategorijaTroska = bundle.getString("kategorija");
            int spinnerPosition = arrayAdapter.getPosition(kategorijaTroska);
            spinner.setSelection(spinnerPosition);

            String valutaTroska = bundle.getString("valuta");
            int spinnerPositionValuta = arrayAdapterValute.getPosition(valutaTroska);
            spinnerValuta.setSelection(spinnerPositionValuta);
            firebaseIdTroska = bundle.getString("firebaseId");

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference pathReference = storageRef.child(firebaseIdTroska+".jpg");

            final long ONE_MEGABYTE = 1024 * 1024 * 10;
            pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    slikaRacuna.setImageBitmap(photo);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });


            Integer datumDan = bundle.getInt("datumDan");
            Integer datumMjesec = bundle.getInt("datumMjesec") - 1;
            Integer datumGodina = bundle.getInt("datumGodina");
            postaviDatum(datumDan, datumMjesec, datumGodina);
            prikaziDatum();
            dodatiTrosak.setText("promijeni trošak");
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (provjeriUnos()) {
                        promijeniTrosak(firebaseIdTroska);
                        submit(photo,firebaseIdTroska+".jpg");
                    }
                }
            };
            dodatiTrosak.setOnClickListener(listener);

        }
    }
    public void showDatePickerDialog(View v) {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                prikaziDatum();
            }

        };
        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * ova metoda postavlja datum troška i prima tri parametra
     *
     * @param dan    dan troška
     * @param mjesec mjesec troška
     * @param godina godina troška
     */
    private void postaviDatum(Integer dan, Integer mjesec, Integer godina) {
        myCalendar.set(Calendar.YEAR, godina);
        myCalendar.set(Calendar.MONTH, mjesec);
        myCalendar.set(Calendar.DAY_OF_MONTH, dan);
    }

    /**
     * ova metoda prikazuje datum troška
     */
    private void prikaziDatum() {
        godina = myCalendar.get(Calendar.YEAR);
        mjesec = myCalendar.get(Calendar.MONTH) + 1;
        dan = myCalendar.get(Calendar.DAY_OF_MONTH);
        datumTroska.setText(Integer.toString(dan) + "." + Integer.toString(mjesec) + "." + Integer.toString(godina));
    }

    /**
     * ova metoda uzima sve podatke vezane uz trošak i sprema ih u bazu
     */
    private void dodajNoviTrosak() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("cijena", cijenaTroska.getText().toString());
        data.put("naziv", nazivTroska.getText().toString());
        data.put("kategorija", spinner.getSelectedItem().toString());
        data.put("valuta", spinnerValuta.getSelectedItem().toString());
        data.put("datumDan", dan);
        data.put("datumMjesec", mjesec);
        data.put("datumGodina", godina);
        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        submit(photo, documentReference.getId()+".jpg");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        postojanjeBedzaZaTrosak = 0;
        Context context = this;
        db.collection("korisnici").document(firebaseUser.getUid()).collection("bedzevi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals("prvi_trosak")){
                                    postojanjeBedzaZaTrosak = 1;
                                }
                            }
                            if (postojanjeBedzaZaTrosak==0){
                                db.collection("korisnici").document(firebaseUser.getUid()).collection("bedzevi")
                                        .document("prvi_trosak").set(new HashMap<>());
                                db.collection("ljestvica").document(firebaseUser.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    String popisBedzeva = task.getResult().getString("bedzevi") + " prvi_trosak";

                                                    db.collection("ljestvica").document(firebaseUser.getUid()).update("bedzevi", popisBedzeva);
                                                    Toast.makeText(context, "Osvojili ste bedž za prvi trošak!", Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        finish();
    }

    /**
     * ova metoda uzima sve podatke vezane uz promijenjeni trošak i mijenja trošak u bazi
     */
    private void promijeniTrosak(String firebaseId) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("cijena", cijenaTroska.getText().toString());
        data.put("naziv", nazivTroska.getText().toString());
        data.put("kategorija", spinner.getSelectedItem().toString());
        data.put("valuta", spinnerValuta.getSelectedItem().toString());
        data.put("datumDan", dan);
        data.put("datumMjesec", mjesec);
        data.put("datumGodina", godina);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi").document(firebaseId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        finish();
    }

    /**
     * ova metoda provjerava je su li sva potrebna polja ispunjena
     *
     * @return
     */
    private boolean provjeriUnos() {
        boolean rezultatBooleana = true;

        if (cijenaTroska.getText().toString().replace(" ","").equals("")) {
            cijenaTroska.setError("Unesite cijenu troška");
            rezultatBooleana = false;
        }
        if (cijenaTroska.getText().toString().length()>8){
            cijenaTroska.setError("Trošak ne može biti veći od 99999999");
            rezultatBooleana = false;
        }
        if (nazivTroska.getText().toString().replace(" ","").equals("")) {
            nazivTroska.setError("Unesite naziv troška");
            rezultatBooleana = false;
        }

        return rezultatBooleana;
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

    public Bitmap getPicture(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            slikaRacuna.setImageBitmap(photo);
        }
        if (requestCode==2 && resultCode==RESULT_OK){
            Uri selectedImage = data.getData();

            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                photo = BitmapFactory.decodeStream(imageStream);
                slikaRacuna.setImageBitmap(photo);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            /*photo = (Bitmap) getPicture(data.getData());
            slikaRacuna.setImageBitmap(getPicture(data.getData()));*/
        }

    }

    public void submit(Bitmap photo, String imeSlike) {
        if (photo != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference racunRef = storageRef.child(imeSlike);
            byte[] b = stream.toByteArray();

            UploadTask uploadTask = racunRef.putBytes(b);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("unos troskova", exception.toString());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("unos troskova", "uspjeh");
                }
            });
        }
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
}
