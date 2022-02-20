package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
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
    private void mojProfil() {
        Intent intent = new Intent(this, MojProfil.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profil:
                mojProfil();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//import class a ne create class
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = (GoogleSignInAccount) ((Task<?>) task).getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                //samo mi se prikazao TextView
            }
        } //Bok
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew) {
                                createNewUser();
                            }
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    private void createNewUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap data = new HashMap();
        data.put("prikaz", false);
        db.collection("korisnici").document(firebaseUser.getUid()).collection("postavke").document("postavke_za_ljestvicu").set(data)
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
    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        getSupportActionBar().hide();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }




    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            setContentView(R.layout.sign_in);
            Button button;
            button = findViewById(R.id.signInButton);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
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
            poslovica.setText(textposlovica);

        }
        else {
            setContentView(R.layout.activity_main);
            Button signOutButton;
            Button prikazTroskovaButton;
            Button financijskaPismenost;
            financijskaPismenost = findViewById(R.id.financijska_pismenost);
            financijskaPismenost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    financijskaPismenost();
                }
            });
            signOutButton = findViewById(R.id.sign_out);
            prikazTroskovaButton = findViewById(R.id.prikaz_troskova);

            View.OnClickListener listener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    signOut();
                }
            };
            signOutButton.setOnClickListener(listener);

            View.OnClickListener listenerPrikazTroskova = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    prikazTroskova();
                }
            };
            prikazTroskovaButton.setOnClickListener(listenerPrikazTroskova);
            getSupportActionBar().show();
        }
    }
    private void prikazTroskova() {
        Intent intent = new Intent(this, PrikazTroskovaActivity.class);
        startActivity(intent);
    }
    private void financijskaPismenost() {
        Intent intent = new Intent(this, FinancijskaPismenostActivity.class);
        startActivity(intent);
    }


}