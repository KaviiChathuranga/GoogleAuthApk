package com.example.googleauthapk;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    static final int GOOGLE_SIGN = 123;

    private FirebaseAuth mAuth;
    Button loginBtn , logoutBtn;

    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.login_button);
        logoutBtn = findViewById(R.id.logout);

        logoutBtn.setOnClickListener(logOutAction);
        loginBtn.setOnClickListener(loginAction);

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("793762306140-8qsafvpnabpkk183hadqfu4scmm8o8t7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        if(mAuth.getCurrentUser() != null){
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null){
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String photo = String.valueOf(currentUser.getPhotoUrl());

            System.out.println(name +"  "+email);
        }else{

        }
    }


    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                System.out.println("Google sign in failed "+e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        System.out.println("firebaseAuthWithGoogle: "+acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCredential:failure "+task.getException());
                            System.out.println("Authentication Failed.");
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    View.OnClickListener logOutAction = new View.OnClickListener() {
        public void onClick(View v) {
            FirebaseAuth.getInstance().signOut();
//            googleSignInClient.signOut()
//                    .addOnCompleteListener(this, task -> {
                            updateUI(null);
//                    });
        }
    };

    View.OnClickListener loginAction = new View.OnClickListener() {
        public void onClick(View v) {
            signIn();
        }
    };

}

