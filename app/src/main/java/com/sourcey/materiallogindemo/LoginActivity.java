package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    private static int RC_SIGN_IN = 0;
    public String Username;
    public String Emailxyz;


    public void signIn(){

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }

    private static String TAG = "LOGIN_ACTIVITY";
   private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        // ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        {



            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            findViewById(R.id.sign_in_button).setOnClickListener(this);
            //findViewById(R.id.sign_out_button).setOnClickListener(this);


       /* _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        }*/

            ;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess()) {
                final GoogleSignInAccount account = result.getSignInAccount();


                firebaseAuthWithGoogle(account);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference Users = database.getReference("Users");
                // DatabaseReference Volunteer = database.getReference("Name");
                Username = account.getDisplayName();


                Emailxyz=account.getEmail();
                Users.child("Volunteer").child("Num").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String str = dataSnapshot.getValue(String.class);
                        long l = Long.parseLong(str);
                        long t;
                        for(t=0;t<l;t++) {
                            String xyz = Long.toString(t);
                            if ((Users.child("Volunteer").child(xyz).child("Email")).equals(Emailxyz)) {
                                break;
                            }
                        }
                            if(t==l){
                            Users.child("Volunteer").child(str).child("Name").setValue(Username);
                            Users.child("Volunteer").child(str).child("Age").setValue(19);
                            Users.child("Volunteer").child(str).child("Email").setValue(account.getEmail());
                            Users.child("Volunteer").child(str).child("GoogleProfileId").setValue(account.getId());
                            Users.child("Volunteer").child(str).child("ProfilePic").setValue(account.getPhotoUrl().toString());
                            Users.child("Volunteer").child(str).child("Interests").child("Animal Welfare").setValue(true);
                            Users.child("Volunteer").child(str).child("Interests").child("Blood Donation").setValue(false);
                            str = str.valueOf(++l);
                            Users.child("Volunteer").child("Num").setValue(str);
                        }

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

                Intent intent = new Intent(this, Navigation.class);
                startActivity(intent);
                ;
                //l=Long.parseLong(Users.child("Volunteer").child("Num").getKey());
                //String str="";

            }

            }
            else if (resultCode == RESULT_CANCELED){
                //User not Authenticated
                Log.d("AUTH", "NOT AUTHENTICATED");
                finish();
            }
            else{
                Log.d(TAG, "Google Login Failed");
            }


        }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("AUTH", "signInWithCredential:oncomplete: " + task.isSuccessful());
                    }
                });
    }
    /*
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
    */
    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.sign_in_button:
                signIn();
                break;

        }
    }
}
