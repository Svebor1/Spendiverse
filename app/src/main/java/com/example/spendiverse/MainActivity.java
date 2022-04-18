package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private Context context;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dodavanje mogućnosti prijave s Google accountom
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();

        context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                "dark_mode", Context.MODE_PRIVATE);
        int defaultValue = getResources().getInteger(R.integer.zadani_status_dark_modea);
        int darkModeStanje = sharedPref.getInt("dark_mode", defaultValue);
        if (darkModeStanje==0){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //ugašen night mode
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //ukljucen night mode
        }

    }

    /**
     * Ova metoda služi za započeti funkcionalnost activity-a
     */
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        //FirebaseAuth.getInstance().signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * metoda za prijelaz u MojProfil activity
     */
    private void mojProfil() {
        Intent intent = new Intent(this, MojProfil.class);
        startActivity(intent);
    }

    /**
     * metoda za prijelaz u Info activity
     */
    private void info() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
    /**
     * Ova metoda premiješta korisnika u drugi activity u slučaju odabira menu-a moj profil ili profil_info
     * Također ona poziva metoda za odjavu kada je pritisnut sign out
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profil:
                mojProfil(); //ako je odabran moj profil poziva se metoda za prijelaz u MojProfil Activity
                return true;
            case R.id.profil_sign_out:
                AlertDialog alertDialogSignOut = //ovdje se stvara prozor s pitanjem "Jeste li sigurni da se želite odjaviti?"
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.odjava_alert)
                                .setPositiveButton(R.string.odgovor_da, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        signOut(); //kada je korisnik odabrao "Da" poziva se metoda za odjavu korinika
                                    }
                                })
                                .setNegativeButton(R.string.odgovor_ne, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        //kada je korisnik odabrao "Ne" ništa se ne događa
                                    }
                                })
                                .create();
                alertDialogSignOut.show();
                return true;
            case R.id.profil_info:
                info(); //ako je odabran info poziva se metoda za prijelaz u Info Activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google prijava je prošla uspješna, slijedi autentikacija s Firebase-om
                GoogleSignInAccount account = (GoogleSignInAccount) ((Task<?>) task).getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                //Google prijava je prošla neuspješno
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    /**
     * Ova metoda služi za autentikaciju korisnika i stvaranje korisničnog računa
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew) {
                                createNewUser(); //ako je novi korisnik stvara se novi korisnički račun
                            }
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user); //prijelaz ekrana za prijavu u glavni ekran

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * Ova metoda dodaje sve početne podatke korisnika u bazu
     */
    private void createNewUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance(); //podaci za spajanje na bazu

        HashMap data = new HashMap();
        String email = firebaseUser.getEmail();
        data.put("email", email);
        Integer pozicijaAt = email.indexOf("@");
        String nadimak = email.substring(0,pozicijaAt);
        data.put("nadimak", nadimak);
        data.put("bodovi", 0);
        data.put("prikaz", false);
        //u mapi data se nalaze svi potrebni podaci za ljestvicu - nadimak, bodovi, i boolean prikaz koji znači hoće li se korisnik prikazivati na ljestvici
        db.collection("ljestvica").document(firebaseUser.getUid()).set(data) //stvara se novi collection ljestvica i u njega se dodaju podaci
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                }
            });

    }

    /**
     * metoda za prijavu korisnika
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * metoda za odjavu korisnika
     */
    private void signOut() {
        mAuth.signOut(); //Firebase odjava
        getSupportActionBar().hide();

        mGoogleSignInClient.signOut().addOnCompleteListener(this, //Google odjava
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null); //za prijelaz glavnog ekrana u ekran za prijvau
                    }
                });

    }


    /**
     * ova metoda postavlja sve funkcionalnosti vezane uz početni ekran
     * @param currentUser trenutni korisnik, ako je currentUser null znači da ne postoji trenutnog korisnika
     */
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) { //ako korisnik nije prijavljen
            setContentView(R.layout.sign_in); //postavlja se ekran activity-a
            Button button;
            button = findViewById(R.id.signInButton);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn(); //ako je odabrana prijava poziva se metoda signin za prijavu
                }
            };
            button.setOnClickListener(listener);
            TextView poslovica;
            poslovica=findViewById(R.id.textcitat);
            String textposlovica;
            Random r=new Random();
            String[] sveposlovice={"Štedjeti danas znači imati za sutra.",
                    "Tko ne zna štedjeti, brzo će mu ponestati.",
                    "Novac je lako steći, ali ga je teško sačuvati."};
            textposlovica=sveposlovice[r.nextInt(3)];
            poslovica.setText(textposlovica); //postavljanje nasumične poslovice
            String link = "<a href=\"https://www.carnet.hr/usluga/google-workspace\">https://www.carnet.hr/usluga/google-workspace</a>";
            String poruka = getString(R.string.pomocni_text);
            ImageButton pomoc = findViewById(R.id.help_button);
            AlertDialog alertDialogPomoc = //ako je odabran upitnik za pomoć otvara se prozor s informacijama za prijavu
                    new AlertDialog.Builder(this)
                            .setTitle("Prijava")
                            .setMessage((Spanned)Html.fromHtml(poruka + link))
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
            pomoc.setOnClickListener(listenerPomoc);

        }
        else { //ako je korisnik prijavljen
            setContentView(R.layout.activity_main); //postavlja se ekran activity-a
            Button prikazTroskovaButton;
            Button financijskaPismenost;
            financijskaPismenost = findViewById(R.id.financijska_pismenost);
            financijskaPismenost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    financijskaPismenost(); //ako je pritisnuta financijska pismenost
                }
            });
            prikazTroskovaButton = findViewById(R.id.prikaz_troskova);
            View.OnClickListener listenerPrikazTroskova = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    prikazTroskova(); //ako je pritisnut prikaz troškova
                }
            };
            prikazTroskovaButton.setOnClickListener(listenerPrikazTroskova);
            getSupportActionBar().show();
        }
    }

    /**
     * metoda za prijelaz u prikaz troškova
     */
    private void prikazTroskova() {
        Intent intent = new Intent(this, PrikazTroskovaActivity.class);
        startActivity(intent);
    }

    /**
     * metoda za prijelaz u finanancijsku pismenost
     */
    private void financijskaPismenost() {
        Intent intent = new Intent(this, FinancijskaPismenostActivity.class);
        startActivity(intent);
    }
}