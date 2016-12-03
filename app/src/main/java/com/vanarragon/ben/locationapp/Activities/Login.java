package com.vanarragon.ben.locationapp.Activities;



import com.android.volley.toolbox.ImageLoader;
import com.vanarragon.ben.locationapp.R;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import android.widget.Toast;

//DONT CALL ON STOP HERE IT BREAKS THE APPLICATION SO MUCH, SPENT 24 HOURS STRAIGHT TRYING TO FIX THAT STUPID BUG...
//I WILL REMEMBER THIS NIGHT! UP UNTIL 9:30AM DEBUGGING, SLEPT IN UNTIL 6PM, AND SOLVED IT AT 9:00...that'll go down in my history
//books for coding :( do not have an on stop callback...please...do not!
public class Login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    /* Request code used to invoke sign in

     user interactions. */



    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    //permissions stuff
    private static final int PERMISSION_REQUEST_CODE=0;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    //Image Loader
    private ImageLoader imageLoader;
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        Base.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                /*.enableAutoManage(this *//* FragmentActivity *//*, this *//* OnConnectionFailedListener *//*)*/
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]


        
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION,0);
        loadPermissions(Manifest.permission.READ_CONTACTS,123);
        loadPermissions(Manifest.permission.GET_ACCOUNTS,123);


    }




    @Override
    public void onStart() {
        super.onStart();
        //connect to the api
        Base.mGoogleApiClient.connect();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(Base.mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }

    }



    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]


    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);


            loadPermissions(Manifest.permission.GET_ACCOUNTS,PERMISSION_REQUEST_CODE);
            if(!checkPermission())
                requestPermission();
            else{
                Base.googleToken = acct.getIdToken();

                //Base.signedIn = Base.mGoogleApiClient.isConnected();
                Base.googleName = acct.getDisplayName();
                Base.googleMail = acct.getEmail();
                Base.googlePhotoURL = acct.getPhotoUrl().toString();
                // Show a message to the user that we are signing in.
                Toast.makeText(this, "Signing in " + Base.googleName, Toast.LENGTH_LONG).show();
                startMainActivity();
            }



        } else {
            // Signed out, show unauthenticated UI.
            //Toast.makeText(this, "Error signing in.", Toast.LENGTH_LONG).show();
            //updateUI(false);
        }
    }
    // [END handleSignInResult]



    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(Base.mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]


    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(Base.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    //start main activity
    private void startMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        Login.this.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                //signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Base.signedIn = Base.mGoogleApiClient.isConnected();

        Toast.makeText(this, "Connected: " + Base.signedIn, Toast.LENGTH_SHORT);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //https://ddrohan.github.io/mad-2016/topic04-google-services/talk-3-google-3/01.maps.pdf
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void loadPermissions(String perm,int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted
                    Toast.makeText(this,"Permissions Granted", Toast.LENGTH_SHORT).show();
                }
                else{
                    // no granted
                }
                return;
            }

        }

    }
}
