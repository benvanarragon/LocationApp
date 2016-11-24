package com.vanarragon.ben.locationapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.vanarragon.ben.locationapp.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by jamin on 2016-11-23.
 */

public class Login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "coffeemate";
    //permissions stuff
    private static final int PERMISSION_REQUEST_CODE=0;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Build GoogleApiClient with access to basic profile
        Base.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.PLUS_ME))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        setContentView(R.layout.activity_login);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION,0);
        loadPermissions(Manifest.permission.READ_CONTACTS,123);
        loadPermissions(Manifest.permission.GET_ACCOUNTS,123);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Base.mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }

        if (v.getId() == R.id.disconnect_button)
        {
            if(Base.mGoogleApiClient.isConnected() && !Base.signedIn) {
                Toast.makeText(this, "Disconencting Account....", Toast.LENGTH_SHORT).show();
                Log.v(TAG,"Logging out from: " + Base.mGoogleApiClient);
                Plus.AccountApi.clearDefaultAccount(Base.mGoogleApiClient);
                Plus.AccountApi.revokeAccessAndDisconnect(Base.mGoogleApiClient);
                Base.mGoogleApiClient.disconnect();
                Base.googleToken = "";
                Base.signedIn = Base.mGoogleApiClient.isConnected();
                Log.v(TAG,"Signed In is: " + Base.signedIn);
                Base.mGoogleApiClient.connect();
            }
            else
                Toast.makeText(this, "No Account to Disconenct....", Toast.LENGTH_SHORT).show();
        }
    }

    private void startHomeScreen() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        Login.this.startActivity(intent);
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        Base.mGoogleApiClient.connect();
        //mStatusTextView.setText(R.string.signing_in);
        //startHomeScreen();
        if(!Base.signedIn)
            Toast.makeText(this, "Attempting to Sign in...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            Base.mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.v(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.v(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    Base.mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                //showErrorDialog(connectionResult);
                Toast.makeText(this, "Error Signing in to Google " + connectionResult, Toast.LENGTH_LONG).show();
                Log.v(TAG, "ConnectionResult : " + connectionResult);
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
            Base.signedIn = Base.mGoogleApiClient.isConnected();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.v(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
        //showSignedInUI();
        if(!Base.signedIn) {
            if (Plus.PeopleApi.getCurrentPerson(Base.mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(Base.mGoogleApiClient);
                String personPhotoUrl = currentPerson.getImage().getUrl();
                Base.googleName = currentPerson.getDisplayName();


                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + 100;

                Base.googlePhotoURL = personPhotoUrl;
                Base.googleToken = currentPerson.getId();
                Base.signedIn = Base.mGoogleApiClient.isConnected();

                loadPermissions(Manifest.permission.GET_ACCOUNTS,PERMISSION_REQUEST_CODE);
                if(!checkPermission())
                    requestPermission();
                else{
                Base.googleMail = Plus.AccountApi.getAccountName(Base.mGoogleApiClient);
                // Show a message to the user that we are signing in.
                Toast.makeText(this, "Signing in " + Base.googleName, Toast.LENGTH_SHORT).show();
                startHomeScreen();}
            }
        }
    }

    //https://ddrohan.github.io/mad-2016/topic04-google-services/talk-3-google-3/01.maps.pdf
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //https://ddrohan.github.io/mad-2016/topic04-google-services/talk-3-google-3/01.maps.pdf
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

    @Override
    public void onConnectionSuspended(int cause) {
        // TODO Auto-generated method stub

    }

    private void loadPermissions(String perm,int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

}
