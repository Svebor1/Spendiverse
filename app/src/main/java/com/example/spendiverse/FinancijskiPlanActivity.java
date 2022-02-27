package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FinancijskiPlanActivity extends AppCompatActivity {
    //varijable za sve Spinnere u layoutu
    private Spinner spinnerMjeseci;
    private Spinner spinnerGodine;
    //za debugiranje
    private final String TAG = "FinancijskiPlanActivity";
    //za dobivanje trenutnog datuma
    private final Calendar myCalendar = Calendar.getInstance();
    //varijable za sve TextViewove u layoutu
    private TextView dzeparac;
    private TextView poslovi;
    private TextView pokloni;
    private TextView ostalo;
    private TextView ustedjevina;
    private TextView preostalo;
    private TextView prehrana;
    private TextView kucanstvo;
    private TextView promet;
    private TextView troskoviOstalo;
    private Integer troskovi;
    private Integer iznosPreostalo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financijski_plan);
        //pronalazi TextViewove prema id-u
        dzeparac = findViewById(R.id.dzeparac_upis);
        poslovi = findViewById(R.id.poslovi_upis);
        pokloni = findViewById(R.id.pokloni_upis);
        ostalo = findViewById(R.id.ostalo_upis);
        ustedjevina = findViewById(R.id.ustedjevina_upis);
        preostalo = findViewById(R.id.preostalo_text);
        prehrana = findViewById(R.id.prehrana_upis);
        kucanstvo = findViewById(R.id.kucanstvo_upis);
        promet = findViewById(R.id.promet_upis);
        troskoviOstalo = findViewById(R.id.troskovi_ostalo_upis);
        //pronalazi Button po id-u
        Button azurirajPlan = findViewById(R.id.azuriraj_plan);
        ImageButton expandPrihodi = findViewById(R.id.expand_prihodi);
        ImageButton expandTroskovi = findViewById(R.id.expand_troskovi);
        LinearLayout layoutPrihodi = findViewById(R.id.layout_prihodi);
        LinearLayout layoutTroskovi = findViewById(R.id.layout_troskovi);
        layoutPrihodi.setVisibility(View.GONE);
        layoutTroskovi.setVisibility(View.GONE);
        //postavljanje LinearLayouta za prihode nevidljivim ili vidljivim, klikom na ImageButton
        expandPrihodi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutPrihodi.getVisibility() == View.VISIBLE){
                    layoutPrihodi.setVisibility(View.GONE);
                    expandPrihodi.setImageResource(R.drawable.ic_baseline_expand_more_24);
                }
                else{
                    layoutPrihodi.setVisibility(View.VISIBLE);
                    expandPrihodi.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });
        //postavljanje LinearLayouta za troskove nevidljivim ili vidljivim, klikom na ImageButton
        expandTroskovi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutTroskovi.getVisibility() == View.VISIBLE){
                    layoutTroskovi.setVisibility(View.GONE);
                    expandTroskovi.setImageResource(R.drawable.ic_baseline_expand_more_24);
                }
                else{
                    layoutTroskovi.setVisibility(View.VISIBLE);
                    expandTroskovi.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });
                View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (provjeriUnos()) {
                    prikaziStanje();
                    dodajNoviPlan();

                }
            }
        };
        //postavlja novi plan na pritisak Buttona
        azurirajPlan.setOnClickListener(listener);

        //Pronalazi treutni datum
        Integer trenutnaGodina = myCalendar.get(Calendar.YEAR);
        Integer trenutniMjesec = myCalendar.get(Calendar.MONTH)+1;

        //postavljanje lista s izborima za mjesece i godine
        String mjeseci[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        spinnerMjeseci = findViewById(R.id.spinner_mjeseci);
        postaviSpinner(mjeseci, trenutniMjesec.toString(), spinnerMjeseci);

        String godine[] = {"2022","2021","2020","2019","2018"};
        spinnerGodine = findViewById(R.id.spinner_godine);
        postaviSpinner(godine, trenutnaGodina.toString(), spinnerGodine);

        //kada se promjeni text prikazuje se prikaziStanje
        TextWatcher promatrac = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                prikaziStanje();
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        dzeparac.addTextChangedListener(promatrac);
        pokloni.addTextChangedListener(promatrac);
        poslovi.addTextChangedListener(promatrac);
        ostalo.addTextChangedListener(promatrac);
        ustedjevina.addTextChangedListener(promatrac);
        prehrana.addTextChangedListener(promatrac);
        promet.addTextChangedListener(promatrac);
        kucanstvo.addTextChangedListener(promatrac);
        troskoviOstalo.addTextChangedListener(promatrac);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("ustedjevina",ustedjevina.getText().toString());
        savedInstanceState.putString("dzeparac",dzeparac.getText().toString());
        savedInstanceState.putString("pokloni",pokloni.getText().toString());
        savedInstanceState.putString("ostalo",ostalo.getText().toString());
        savedInstanceState.putString("prehrana",prehrana.getText().toString());
        savedInstanceState.putString("promet",promet.getText().toString());
        savedInstanceState.putString("kucanstvo",kucanstvo.getText().toString());
        savedInstanceState.putString("troskoviOstalo",troskoviOstalo.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState!=null){
            dzeparac.setText(savedInstanceState.getString("dzeparac"));
        }
    }




    /**
     *Postavlja izbore za Spinner i stavlja trenutni zbor kao početni.
     *
     * @param vrijednosti lista koja sadržava elemente koji se mogu izabrati
     * @param trenutniIzbor element iz liste vrijednosti koji treba izabrati u Spinneru
     * @param spinner Spinner koji učitava izbore
     */
    private void postaviSpinner(String[] vrijednosti,String trenutniIzbor, Spinner spinner){
        //Stvara Adapter za listu vrijednosti
        ArrayAdapter vrijednostiAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, vrijednosti);
        //Određuje izgled ponuđenih izbora u Spinneru
        vrijednostiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Spinner se povezuje s Adapterom
        spinner.setAdapter(vrijednostiAdapter);
        //traži izbor koji želimo postaviti unutar ponuđenih izbora u Spinneru
        int spinnerPosition = vrijednostiAdapter.getPosition(trenutniIzbor.toString());
        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Prikazuje plan za izabranu godinu i mjesec
                String godine = spinnerGodine.getSelectedItem().toString();
                String mjesec = spinnerMjeseci.getSelectedItem().toString();
                dzeparac.setError(null);
                pokloni.setError(null);
                poslovi.setError(null);
                ostalo.setError(null);
                ustedjevina.setError(null);
                prehrana.setError(null);
                kucanstvo.setError(null);
                troskoviOstalo.setError(null);
                promet.setError(null);
                nadiTroskove(mjesec,godine);
                prikazPlana(mjesec,godine);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

    }

    /**
     * Funkcija koja u bazi traži ukupan iznos troškova u određenoj godini i mjesecu za trenutno
     * prijavljenog korisnika.
     *
     * @param mjesec izabrani mjesec
     * @param godine izabrana godina
     */
    private void  nadiTroskove(String mjesec,String godine) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            troskovi = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String datumMjesec = document.getData().get("datumMjesec").toString();
                                String datumGodina = document.getData().get("datumGodina").toString();
                                Integer cijena = Integer.parseInt(document.getData().get("cijena").toString());
                                if (godine.equals(datumGodina) && mjesec.equals(datumMjesec)) {
                                    troskovi = troskovi + cijena;
                                }
                            }
                            TextView potrosenoText = findViewById(R.id.potroseno_text);
                            potrosenoText.setText(troskovi.toString() + " " + "kn");
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    /**
     * Uzima sadržaj upisan u TextViewove i dodaje ga u novi financijski plan za
     * izabranu godinu i mjesec.
     */
    private void dodajNoviPlan() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("dzeparac",dzeparac.getText().toString());
        data.put("poslovi", poslovi.getText().toString());
        data.put("pokloni", pokloni.getText().toString());
        data.put("ostalo", ostalo.getText().toString());
        data.put("mjesec", spinnerMjeseci.getSelectedItem().toString());
        data.put("godina", spinnerGodine.getSelectedItem().toString());
        data.put("preostalo",iznosPreostalo);
        data.put("ustedjevina", ustedjevina.getText().toString());
        data.put("troskovi_prehrana", prehrana.getText().toString());
        data.put("troskovi_kucanstvo", kucanstvo.getText().toString());
        data.put("troskovi_promet", promet.getText().toString());
        data.put("troskovi_ostalo", troskoviOstalo.getText().toString());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //planovi se spremaju pod nazivom plan_mjesec_godina
        db.collection("korisnici").document(firebaseUser.getUid())
                .collection("planovi").document("plan_" + spinnerMjeseci
                .getSelectedItem().toString() + "_" + spinnerGodine
                .getSelectedItem().toString())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot written with ID: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * Uzima upisane podatke iz baze i prikazuje napravljen plan za izabrani mjesec i godinu trenutno prijavljenog korisnika.
     *
     * @param mjesec izabrani mjesec
     * @param godine izabrana godina
     */
    private void prikazPlana(String mjesec,String godine) {
        ocistiTekst();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("planovi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String planMjesec = document.getData().get("mjesec").toString();
                                String planGodina = document.getData().get("godina").toString();
                                String planDzeparac = document.getData().get("dzeparac").toString();
                                String planPokloni = document.getData().get("pokloni").toString();
                                String planPoslovi = document.getData().get("poslovi").toString();
                                String planOstalo = document.getData().get("ostalo").toString();
                                String planUstedjevina = document.getData().get("ustedjevina").toString();
                                String planPromet = document.getData().get("troskovi_promet").toString();
                                String planPrehrana = document.getData().get("troskovi_prehrana").toString();
                                String planKucanstvo = document.getData().get("troskovi_kucanstvo").toString();
                                String planTroskoviOstalo = document.getData().get("troskovi_ostalo").toString();
                                if (godine.equals(planGodina) && mjesec.equals(planMjesec)) {
                                    dzeparac.setText(planDzeparac);
                                    poslovi.setText(planPoslovi);
                                    pokloni.setText(planPokloni);
                                    ostalo.setText(planOstalo);
                                    ustedjevina.setText(planUstedjevina);
                                    promet.setText(planPromet);
                                    prehrana.setText(planPrehrana);
                                    kucanstvo.setText(planKucanstvo);
                                    troskoviOstalo.setText(planTroskoviOstalo);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    /**
     * Čisti TextVieweve prije prikazivanja plana.
     */
    private void ocistiTekst(){
        dzeparac.setText("0");
        poslovi.setText("0");
        pokloni.setText("0");
        ostalo.setText("0");
        ustedjevina.setText("0");
        prehrana.setText("0");
        promet.setText("0");
        kucanstvo.setText("0");
        troskoviOstalo.setText("0");


    }

    private void prikaziStanje(){
        String poruka = "";
        String dzeparacText = dzeparac.getText().toString();
        String posloviText = poslovi.getText().toString();
        String pokloniText = pokloni.getText().toString();
        String ostaloText = ostalo.getText().toString();
        String ustedjevinaText = ustedjevina.getText().toString();
        String prehranaText = prehrana.getText().toString();
        String prometText = promet.getText().toString();
        String kucanstvoText = kucanstvo.getText().toString();
        String troskoviOstaloText = troskoviOstalo.getText().toString();
        Integer dzeparacIznos;
        Integer posloviIznos;
        Integer pokloniIznos;
        Integer ostaloIznos;
        Integer ustedjevinaIznos;
        Integer prehranaIznos;
        Integer prometIznos;
        Integer kucanstvoIznos;
        Integer troskoviOstaloIznos;

        if (dzeparacText.equals("")){
            dzeparacIznos = 0;}
        else{dzeparacIznos = Integer.parseInt(dzeparacText); }

        if (posloviText.equals("")){ posloviIznos = 0;}
        else{posloviIznos = Integer.parseInt(posloviText); }

        if (pokloniText.equals("")){ pokloniIznos = 0; }
        else{ pokloniIznos = Integer.parseInt(pokloniText);}

        if (ostaloText.equals("")){ ostaloIznos = 0;}
        else{ostaloIznos = Integer.parseInt(ostaloText);}

        if (ustedjevinaText.equals("")){ ustedjevinaIznos = 0;}
        else{ustedjevinaIznos = Integer.parseInt(ustedjevinaText);}

        if (prehranaText.equals("")){ prehranaIznos = 0;}
        else{prehranaIznos = Integer.parseInt(prehranaText);}

        if (prometText.equals("")){ prometIznos = 0;}
        else{prometIznos = Integer.parseInt(prometText);}

        if (kucanstvoText.equals("")){ kucanstvoIznos = 0;}
        else{kucanstvoIznos = Integer.parseInt(kucanstvoText);}

        if (troskoviOstaloText.equals("")){ troskoviOstaloIznos = 0;}
        else{troskoviOstaloIznos = Integer.parseInt(troskoviOstaloText);}


        Integer planiraniTroskovi = prehranaIznos + prometIznos + kucanstvoIznos + troskoviOstaloIznos;
        Integer zaradeno = dzeparacIznos + posloviIznos + pokloniIznos + ostaloIznos;
        if (troskovi==null){
            troskovi = 0;
        }
        iznosPreostalo = zaradeno - planiraniTroskovi + ustedjevinaIznos;

        if (iznosPreostalo>0){
            poruka = "Bravo! Nastavi štedjeti kao i do sada!";
        }
        if (iznosPreostalo==0){
            poruka = "Sve je potrošeno, ali nisi u minusu!";
        }
        if (iznosPreostalo<0){
            poruka = "Jao! Ovaj mjesec si u minusu!";
        }
        TextView planiranoPotroseno = findViewById(R.id.planirano_potroseno_text);
        planiranoPotroseno.setText(planiraniTroskovi.toString()+" kn");
        TextView porukaText = findViewById(R.id.poruka_text);
        porukaText.setText(poruka);
        preostalo.setText(iznosPreostalo.toString()+" kn");
    }


    private boolean provjeriUnos(){
        boolean rezultatBooleana = true;
        if (dzeparac.getText().toString().equals("")){
            rezultatBooleana = false;
            dzeparac.setError("Unesite iznos džeparca ili 0");
        }
        if (pokloni.getText().toString().equals("")){
            rezultatBooleana = false;
            pokloni.setError("Unesite iznos poklona ili 0");
        }
        if (poslovi.getText().toString().equals("")){
            rezultatBooleana = false;
            poslovi.setError("Unesite iznos dodatnih poslova ili 0");
        }
        if (ostalo.getText().toString().equals("")){
            rezultatBooleana = false;
            ostalo.setError("Unesite iznos ostalog ili 0");
        }
        if (ustedjevina.getText().toString().equals("")){
            rezultatBooleana = false;
            ustedjevina.setError("Unesite iznos ušteđevine ili 0");
        }
        if (promet.getText().toString().equals("")){
            rezultatBooleana = false;
            promet.setError("Unesite iznos troškova za promet ili 0");
        }
        if (prehrana.getText().toString().equals("")){
            rezultatBooleana = false;
            prehrana.setError("Unesite iznos troškova prehrane ili 0");
        }
        if (kucanstvo.getText().toString().equals("")){
            rezultatBooleana = false;
            kucanstvo.setError("Unesite iznos troškova za kućanstvo ili 0");
        }
        if (troskoviOstalo.getText().toString().equals("")){
            rezultatBooleana = false;
            troskoviOstalo.setError("Unesite iznos ostalih troškova ili 0");
        }
        return rezultatBooleana;
    }
    }

