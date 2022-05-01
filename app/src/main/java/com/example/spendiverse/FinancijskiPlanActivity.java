package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

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
    private Integer postojanjeBedzaZaPlan;
    private TextView troskoviOstalo;
    private Double troskovi;
    private Double iznosPreostalo;
    private Spinner spinnerZaValute;

    HashMap<String, Double> ukupnoPoValutama = new HashMap<>();
    String[] zadaneValute = {"HRK", "USD", "EUR", "GBP"};
    ArrayList<String> valute = new ArrayList<>(Arrays.asList(zadaneValute));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financijski_plan);
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        spinnerZaValute = findViewById(R.id.odabir_valute);
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

        String[] godine = new String[9];
        Integer ponudenaGodina = trenutnaGodina-4;
        for (int i=0; i<9; i++){
            godine[i] = ponudenaGodina.toString();
            ponudenaGodina++;
        }
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
        nadiValute();
        //postavljanje alerta za objasnjenje dinancijskog plana
        ImageView pomocFinancijskiPlan = findViewById(R.id.help_financijski_plan);
        AlertDialog alertDialogPomoc =
                new AlertDialog.Builder(this)
                        .setTitle("Financijski plan")
                        .setMessage("Financijski plan možemo pregledati ili urediti za određeni mjesec i godinu upisivanjem ušteđevine iz prošlog mjeseca. " +
                                "te planiranih prihoda i troškova u određenoj valuti koju možemo izabrati. Kada spremimo plan možemo ga preračunati u drugu valutu. " +
                                "Plan nam služi kako bismo procijenili uštedu u nekom razdoblju.\n" +
                                "Ispod dijela za unos piše nekoliko vrijednosti:  \n" +
                                "◼Planirani ukupni troškovi koje smo upisali u različitim kategorijama \n" +
                                "◼Stvarni ukupni troškovi koje smo do sad upisali u aplikaciju za mjesec na koji se odnosi plan \n" +
                                "◼Ukupna planirana ušteda koja se dobiva zbrajanjem ušteđevine od prošlog mjeseca i planiranih prihoda ovaj mjesec te oduzimanjem planiranih troškova")
                        .setPositiveButton("zatvori", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_baseline_help_24)
                        .create();

        View.OnClickListener listenerPomoc = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogPomoc.show();
                TextView msgTxt = (TextView) alertDialogPomoc.findViewById(android.R.id.message);
                msgTxt.setMovementMethod(LinkMovementMethod.getInstance());
            }
        };
        pomocFinancijskiPlan.setOnClickListener(listenerPomoc);

    }

    public interface ExchangeService {
        @GET("v6/ec3b22d3e9c864306c37e179/latest/{valuta}")
        Call<JsonObject> listRepos(@Path("valuta") String valuta);
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


    private void postaviSpinnerZaValute(){
        //Stvara Adapter za listu vrijednosti
        ArrayAdapter arrayAdapterValute = new ArrayAdapter(this, android.R.layout.simple_spinner_item, valute);
        //Određuje izgled ponuđenih izbora u Spinneru
        arrayAdapterValute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Spinner se povezuje s Adapterom
        spinnerZaValute.setAdapter(arrayAdapterValute);
        //traži izbor koji želimo postaviti unutar ponuđenih izbora u Spinneru

        int spinnerPosition = arrayAdapterValute.getPosition("HRK");
        spinnerZaValute.setSelection(spinnerPosition);

        spinnerZaValute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                dohvatiKonverziju();
                String godine = spinnerGodine.getSelectedItem().toString();
                String mjesec = spinnerMjeseci.getSelectedItem().toString();
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
    private void nadiTroskove(String mjesec,String godine) {
        ukupnoPoValutama.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            troskovi = 0.0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String datumMjesec = document.getData().get("datumMjesec").toString();
                                String datumGodina = document.getData().get("datumGodina").toString();
                                String valuta = document.getData().get("valuta").toString();
                                Double cijena = Double.parseDouble(document.getData().get("cijena").toString());
                                if (godine.equals(datumGodina) && mjesec.equals(datumMjesec)) {
                                    troskovi = troskovi + cijena;
                                    if (ukupnoPoValutama.containsKey(valuta)) {
                                        ukupnoPoValutama.put(valuta, ukupnoPoValutama.get(valuta) + cijena);
                                    }
                                    else {
                                        ukupnoPoValutama.put(valuta, cijena);
                                    }
                                }


                            }
                            dohvatiKonverziju();

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

        data.put("dzeparac",dzeparac.getText().toString().replace(',','.'));
        data.put("poslovi", poslovi.getText().toString().replace(',','.'));
        data.put("pokloni", pokloni.getText().toString().replace(',','.'));
        data.put("ostalo", ostalo.getText().toString().replace(',','.'));
        data.put("mjesec", spinnerMjeseci.getSelectedItem().toString());
        data.put("godina", spinnerGodine.getSelectedItem().toString());
        data.put("preostalo",iznosPreostalo);
        data.put("ustedjevina", ustedjevina.getText().toString().replace(',','.'));
        data.put("troskovi_prehrana", prehrana.getText().toString().replace(',','.'));
        data.put("troskovi_kucanstvo", kucanstvo.getText().toString().replace(',','.'));
        data.put("troskovi_promet", promet.getText().toString().replace(',','.'));
        data.put("troskovi_ostalo", troskoviOstalo.getText().toString().replace(',','.'));
        data.put("valuta", spinnerZaValute.getSelectedItem().toString());
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
        postojanjeBedzaZaPlan = 0;
        Context context = this;
        db.collection("korisnici").document(firebaseUser.getUid()).collection("bedzevi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals("prvi_plan")){
                                    postojanjeBedzaZaPlan = 1;
                                }
                            }
                            if (postojanjeBedzaZaPlan==0){
                                db.collection("korisnici").document(firebaseUser.getUid()).collection("bedzevi")
                                        .document("prvi_plan").set(new HashMap<>());
                                db.collection("ljestvica").document(firebaseUser.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    String popisBedzeva = task.getResult().getString("bedzevi") + " prvi_plan";

                                                    db.collection("ljestvica").document(firebaseUser.getUid()).update("bedzevi", popisBedzeva);
                                                    Toast.makeText(context, "Osvojili ste bedž za prvi financijski plan!", Toast.LENGTH_LONG).show();
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
                                Double planDzeparac = Double.parseDouble(document.getData().get("dzeparac").toString().replace(',','.'));
                                Double planPokloni = Double.parseDouble(document.getData().get("pokloni").toString().replace(',','.'));
                                Double planPoslovi = Double.parseDouble(document.getData().get("poslovi").toString().replace(',','.'));
                                Double planOstalo = Double.parseDouble(document.getData().get("ostalo").toString().replace(',','.'));
                                Double planUstedjevina = Double.parseDouble(document.getData().get("ustedjevina").toString().replace(',','.'));
                                Double planPromet = Double.parseDouble(document.getData().get("troskovi_promet").toString().replace(',','.'));
                                Double planPrehrana = Double.parseDouble(document.getData().get("troskovi_prehrana").toString().replace(',','.'));
                                Double planKucanstvo = Double.parseDouble(document.getData().get("troskovi_kucanstvo").toString().replace(',','.'));
                                Double planTroskoviOstalo = Double.parseDouble(document.getData().get("troskovi_ostalo").toString().replace(',','.'));
                                String planValuta = document.getData().get("valuta").toString();
                                if (godine.equals(planGodina) && mjesec.equals(planMjesec)) {
                                    dohvatiKonverzijuPlana(planDzeparac, planPokloni, planPoslovi, planOstalo, planUstedjevina, planPromet, planPrehrana, planKucanstvo, planTroskoviOstalo, planValuta);
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
        String dzeparacText = dzeparac.getText().toString().replace(',','.');
        String posloviText = poslovi.getText().toString().replace(',','.');
        String pokloniText = pokloni.getText().toString().replace(',','.');
        String ostaloText = ostalo.getText().toString().replace(',','.');
        String ustedjevinaText = ustedjevina.getText().toString().replace(',','.');
        String prehranaText = prehrana.getText().toString().replace(',','.');
        String prometText = promet.getText().toString().replace(',','.');
        String kucanstvoText = kucanstvo.getText().toString().replace(',','.');
        String troskoviOstaloText = troskoviOstalo.getText().toString().replace(',','.');
        Double dzeparacIznos;
        Double posloviIznos;
        Double pokloniIznos;
        Double ostaloIznos;
        Double ustedjevinaIznos;
        Double prehranaIznos;
        Double prometIznos;
        Double kucanstvoIznos;
        Double troskoviOstaloIznos;

        if (dzeparacText.equals("")){
            dzeparacIznos = 0.0;}
        else{dzeparacIznos = Double.parseDouble(dzeparacText); }

        if (posloviText.equals("")){ posloviIznos = 0.0;}
        else{posloviIznos = Double.parseDouble(posloviText); }

        if (pokloniText.equals("")){ pokloniIznos = 0.0; }
        else{ pokloniIznos = Double.parseDouble(pokloniText);}

        if (ostaloText.equals("")){ ostaloIznos = 0.0;}
        else{ostaloIznos = Double.parseDouble(ostaloText);}

        if (ustedjevinaText.equals("")){ ustedjevinaIznos = 0.0;}
        else{ustedjevinaIznos = Double.parseDouble(ustedjevinaText);}

        if (prehranaText.equals("")){ prehranaIznos = 0.0;}
        else{prehranaIznos = Double.parseDouble(prehranaText);}

        if (prometText.equals("")){ prometIznos = 0.0;}
        else{prometIznos = Double.parseDouble(prometText);}

        if (kucanstvoText.equals("")){ kucanstvoIznos = 0.0;}
        else{kucanstvoIznos = Double.parseDouble(kucanstvoText);}

        if (troskoviOstaloText.equals("")){ troskoviOstaloIznos = 0.0;}
        else{troskoviOstaloIznos = Double.parseDouble(troskoviOstaloText);}


        Double planiraniTroskovi = prehranaIznos + prometIznos + kucanstvoIznos + troskoviOstaloIznos;
        Double zaradeno = dzeparacIznos + posloviIznos + pokloniIznos + ostaloIznos;
        if (troskovi==null){
            troskovi = 0.0;
        }
        DecimalFormat myFormatter = new DecimalFormat("#.##");
        iznosPreostalo = zaradeno - planiraniTroskovi + ustedjevinaIznos;
        String iznosPreostaloZaokruzeno = myFormatter.format(iznosPreostalo);
        if (iznosPreostalo>0){
            poruka = "Bravo! Plan pokazuje da ćeš uštedjeti!";
        }
        if (iznosPreostalo==0){
            poruka = "Prema planu, sve ćeš potrošiti, ali nećeš biti u minusu!";
        }
        if (iznosPreostalo<0){
            poruka = "Jao! Plan pokazuje da ćeš ovaj mjesec biti u minusu!";
        }
        TextView planiranoPotroseno = findViewById(R.id.planirano_potroseno_text);
        String valuta = "";
        if (spinnerZaValute.getSelectedItem() != null) {
            valuta = spinnerZaValute.getSelectedItem().toString();
        }
        planiranoPotroseno.setText(planiraniTroskovi.toString()+" " + valuta);
        TextView porukaText = findViewById(R.id.poruka_text);
        porukaText.setText(poruka);
        preostalo.setText(iznosPreostaloZaokruzeno+" " + valuta);
    }

    private void nadiValute() {

        valute = new ArrayList<>(Arrays.asList(zadaneValute));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Context context = this;
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

                            postaviSpinnerZaValute();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }



                    }
                });

    }

    private void dohvatiKonverziju() {

        if (spinnerZaValute.getSelectedItem()==null) {
            return;
        }
        String valuta = spinnerZaValute.getSelectedItem().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExchangeService service = retrofit.create(ExchangeService.class);
        Call<JsonObject> valute = service.listRepos(valuta);
        Context context = this;
        valute.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Toast t = new Toast(context);
                JsonObject objekt = response.body();
                JsonElement konverzija;
                konverzija = objekt.get("conversion_rates");
                troskovi = 0.0;

                for(Map.Entry<String, Double> entry : ukupnoPoValutama.entrySet()) {
                    JsonElement iznos = konverzija.getAsJsonObject().get(entry.getKey());
                    Double omjer = iznos.getAsDouble();
                    Double cijenaPoValuti = entry.getValue() / omjer;
                    troskovi += cijenaPoValuti;
                }

                troskovi /= 2; //dijelimo s dva zato jer oba spinnera dodaju troškove u mapu
                DecimalFormat myFormatter = new DecimalFormat("#.##");
                String output = myFormatter.format(troskovi);

                TextView potrosenoText = findViewById(R.id.potroseno_text);
                potrosenoText.setText(output + " " + spinnerZaValute.getSelectedItem().toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("TAG", "onFailure: "+t.toString() );
            }
        });

    }
    private void dohvatiKonverzijuPlana(Double planDzeparac, Double planPoslovi, Double planPokloni, Double planOstalo, Double planUstedjevina, Double planPromet, Double planPrehrana, Double planKucanstvo, Double planTroskoviOstalo, String planValuta) {

        if (spinnerZaValute.getSelectedItem()==null) {
            return;
        }
        String valuta = spinnerZaValute.getSelectedItem().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExchangeService service = retrofit.create(ExchangeService.class);
        Call<JsonObject> valute = service.listRepos(valuta);
        Context context = this;

        valute.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Toast t = new Toast(context);
                JsonObject objekt = response.body();
                JsonElement konverzija;
                konverzija = objekt.get("conversion_rates");
                JsonElement iznos = konverzija.getAsJsonObject().get(planValuta);
                Double omjer = iznos.getAsDouble();
                DecimalFormat myFormatter = new DecimalFormat("#.##");
                String planDzeparacPreracunato = myFormatter.format(planDzeparac / omjer);
                String planPosloviPreracunato = myFormatter.format(planPoslovi / omjer);
                String planPokloniPreracunato = myFormatter.format(planPokloni / omjer);
                String planOstaloPreracunato = myFormatter.format(planOstalo / omjer);
                String planUstedjevninaPreracunato = myFormatter.format(planUstedjevina / omjer);
                String planPrometPreracunato = myFormatter.format(planPromet / omjer);
                String planPrehranaPreracunato = myFormatter.format(planPrehrana / omjer);
                String planKucanstvoPraracunato = myFormatter.format(planKucanstvo / omjer);
                String planTroskoviOstaloPreracunato = myFormatter.format(planTroskoviOstalo / omjer);

                dzeparac.setText(planDzeparacPreracunato);
                poslovi.setText(planPosloviPreracunato);
                pokloni.setText(planPokloniPreracunato);
                ostalo.setText(planOstaloPreracunato);
                ustedjevina.setText(planUstedjevninaPreracunato);
                promet.setText(planPrometPreracunato);
                prehrana.setText(planPrehranaPreracunato);
                kucanstvo.setText(planKucanstvoPraracunato);
                troskoviOstalo.setText(planTroskoviOstaloPreracunato);
                /*
                TextView potrosenoText = findViewById(R.id.potroseno_text);
                potrosenoText.setText(output + " " + spinnerZaValute.getSelectedItem().toString());
                 */
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("TAG", "onFailure: "+t.toString() );
            }
        });

    }

    private boolean provjeriUnos(){
        boolean rezultatBooleana = true;
        if (dzeparac.getText().toString().equals("")){
            rezultatBooleana = false;
            dzeparac.setError("Unesite iznos džeparca ili 0");
        }
        else{
            String greska = PomocneFunkcije.provjeraDuljineUnosa(dzeparac.getText().toString());
            if (!greska.equals("")){
                rezultatBooleana = false;
                dzeparac.setError(greska);
            }
        }
        if (pokloni.getText().toString().equals("")){
            rezultatBooleana = false;
            pokloni.setError("Unesite iznos poklona ili 0");
        }
        else{
            String greska = PomocneFunkcije.provjeraDuljineUnosa(pokloni.getText().toString());
            if (!greska.equals("")){
                rezultatBooleana = false;
                pokloni.setError(greska);
            }
        }
        if (poslovi.getText().toString().equals("")){
            rezultatBooleana = false;
            poslovi.setError("Unesite iznos dodatnih poslova ili 0");
        }
        else{
            String greska = PomocneFunkcije.provjeraDuljineUnosa(poslovi.getText().toString());
            if (!greska.equals("")){
                rezultatBooleana = false;
                poslovi.setError(greska);
            }
        }
        if (ostalo.getText().toString().equals("")){
            rezultatBooleana = false;
            ostalo.setError("Unesite iznos ostalog ili 0");
        }
        else{
            String greska = PomocneFunkcije.provjeraDuljineUnosa(ostalo.getText().toString());
            if (!greska.equals("")){
                rezultatBooleana = false;
                ostalo.setError(greska);
            }
        }
        if (ustedjevina.getText().toString().equals("")){
            rezultatBooleana = false;
            ustedjevina.setError("Unesite iznos ušteđevine ili 0");
        }
        else{
            String greska = PomocneFunkcije.provjeraDuljineUnosa(ustedjevina.getText().toString());
            if (!greska.equals("")){
                rezultatBooleana = false;
                ustedjevina.setError(greska);
            }
        }
        if (promet.getText().toString().equals("")){
            rezultatBooleana = false;
            promet.setError("Unesite iznos troškova za promet ili 0");
        }
        else{
            String greska = PomocneFunkcije.provjeraDuljineUnosa(promet.getText().toString());
            if (!greska.equals("")){
                rezultatBooleana = false;
                promet.setError(greska);
            }
        }
        if (prehrana.getText().toString().equals("")){
            rezultatBooleana = false;
            prehrana.setError("Unesite iznos troškova prehrane ili 0");
        }
        else{
            String greska = PomocneFunkcije.provjeraDuljineUnosa(prehrana.getText().toString());
            if (!greska.equals("")){
                rezultatBooleana = false;
                prehrana.setError(greska);
            }
        }
        if (kucanstvo.getText().toString().equals("")){
            rezultatBooleana = false;
            kucanstvo.setError("Unesite iznos troškova za kućanstvo ili 0");
        }
        else{
            String greska = PomocneFunkcije.provjeraDuljineUnosa(kucanstvo.getText().toString());
            if (!greska.equals("")){
                rezultatBooleana = false;
                kucanstvo.setError(greska);
            }
        }
        if (troskoviOstalo.getText().toString().equals("")){
            rezultatBooleana = false;
            troskoviOstalo.setError("Unesite iznos ostalih troškova ili 0");
        }
        else{
            String greska = PomocneFunkcije.provjeraDuljineUnosa(troskoviOstalo.getText().toString());
            if (!greska.equals("")){
                rezultatBooleana = false;
                troskoviOstalo.setError(greska);
            }
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

}

