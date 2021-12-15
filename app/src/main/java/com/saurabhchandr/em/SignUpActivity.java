package com.saurabhchandr.em;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saurabhchandr.em.Model.User;

import java.util.ArrayList;
import java.util.List;


public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private AppCompatSpinner courseSpinner;
    private EditText name,email,password;
    private AppCompatButton submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().isAnonymous()) {
            Intent intent = new Intent(SignUpActivity.this,MyActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        privacyPolicy();
        loginHere();

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("BTECH");
        spinnerArray.add("DIPLOMA");
        spinnerArray.add("MBA");
        spinnerArray.add("BPHARM");
        spinnerArray.add("MCA");
        spinnerArray.add("MTECH");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner = findViewById(R.id.sign_up_course_spinner);
        courseSpinner.setAdapter(adapter);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validInput()) {
                    submitBtn.setText("Please wait...");
                    submitBtn.setEnabled(false);
                    createAccount();
                }
            }
        });

    }

    private void init() {
        name = findViewById(R.id.sign_up_name);
        email = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.sign_up_password);
        submitBtn = findViewById(R.id.sign_up_submit);
    }

    private boolean validInput() {
        String regex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
        if(TextUtils.isEmpty(name.getText())) {
            name.setError("Name is required");
            return false;
        }
        else if(TextUtils.isEmpty(email.getText()) || !email.getText().toString().matches(regex)) {
            email.setError("Valid email is required");
            return false;
        }
        else if (TextUtils.isEmpty(password.getText()) || password.getText().length() < 6) {
            password.setError("At least 6 digit password required");
            return false;
        }
        return true;
    }


    private void createAccount() {
        if (validInput()) {
            // now proceed to create account
            AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(),password.getEditableText().toString());
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            submitBtn.setEnabled(true);
                            submitBtn.setText("Create account in mCSVTU");
                            if(task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification();
                                User user = new User(name.getText().toString(),email.getText().toString(),courseSpinner.getSelectedItem().toString());
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(user.getName())
                                        .build();
                                firebaseUser.updateProfile(request);
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this).edit();
                                editor.putString("course",user.getCourse());
                                editor.putString("displayName",user.getName());
                                editor.putBoolean("launchMy",true);
                                editor.apply();

                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                firestore.collection("users").document(mAuth.getUid()).set(user);
                                Intent intent = new Intent(SignUpActivity.this,MyActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(SignUpActivity.this, "Look like this email is already in use, Please try login instead.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void loginHere() {
        SpannableString ss = new SpannableString("If you already have account you can Login here.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.darkBlue));
            }
        };
        ss.setSpan(clickableSpan, 36, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView loginHere = findViewById(R.id.sign_up_login);
        loginHere.setText(ss);
        loginHere.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private void privacyPolicy() {
        SpannableString ss = new SpannableString("Creating account here means you accept our Privacy Policy and Terms & Conditions. We recommend to read these Policies carefully.");
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://i3developer.com/Policies/mCSVTU.htm")));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.darkBlue));
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://i3developer.com/Terms/mCSVTU.htm")));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.darkBlue));
            }
        };

        ss.setSpan(clickableSpan1, 43, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 62, 80, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView policies = findViewById(R.id.sign_up_policies);
        policies.setText(ss);
        policies.setMovementMethod(LinkMovementMethod.getInstance());
        policies.setHighlightColor(Color.TRANSPARENT);
    }
}