package com.compscieddy.meetinthemiddle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.meetinthemiddle.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ambar on 6/15/16.
 */
public class AuthenticationActivity extends Activity {

  private static final Lawg lawg = Lawg.newInstance(AuthenticationActivity.class.getSimpleName());
  private static final int RC_SIGN_IN = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //Checking if the user is already logged in
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null) {
      Intent intent = new Intent(this, HomeActivity.class);
      startActivity(intent);
      finish();
    }

    startActivityForResult(
        AuthUI.getInstance().createSignInIntentBuilder()
            .setLogo(R.mipmap.ic_launcher)
            .setProviders(AuthUI.EMAIL_PROVIDER,
                AuthUI.FACEBOOK_PROVIDER,
                AuthUI.GOOGLE_PROVIDER)
            .build(),
        RC_SIGN_IN);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN) {
      if (resultCode == RESULT_OK) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

        // User creation is occurring! Tada! Welcome to being trapped to the most addictive map app ever

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String encodedEmail = Etils.encodeEmail(firebaseUser.getEmail());
        String name = firebaseUser.getDisplayName();

        DatabaseReference userReference = database.getReference("users").child(encodedEmail);
        User user = new User(encodedEmail, name);
        userReference.setValue(user);

        startActivity(new Intent(this, HomeActivity.class));
        finish();
      } else {
        // user is not signed in. Maybe just wait for the user to press
        // "sign in" again, or show a message
        Log.d("FAIL", "FAIL");
      }
    }
  }
}