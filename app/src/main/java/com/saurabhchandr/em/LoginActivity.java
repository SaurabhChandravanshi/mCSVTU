package com.saurabhchandr.em;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saurabhchandr.em.Fragment.ResetPassword;
import com.saurabhchandr.em.Model.User;


public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private AppCompatButton loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        forgetPassword();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    // Attempt login here.
                    loginBtn.setText("Please wait...");
                    loginBtn.setEnabled(false);
                    login();
                }
            }
        });
    }

    private void login() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginBtn.setText("Securely login to mCSVTU");
                        loginBtn.setEnabled(true);
                        if(task.isSuccessful()) {
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection("users").document(mAuth.getUid()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()) {
                                                User user = task.getResult().toObject(User.class);
                                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                                                editor.putString("course",user.getCourse());
                                                editor.putString("displayName",firebaseUser.getDisplayName());
                                                editor.putBoolean("launchMy",true);
                                                editor.apply();
                                                Intent intent = new Intent(LoginActivity.this,MyActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(LoginActivity.this, R.string.def_err_message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(LoginActivity.this, "Email or password mismatched, You may also reset password in case you forget.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void forgetPassword() {
        SpannableString ss = new SpannableString("If you don't know your current password then you can Reset Password.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                ResetPassword resetPassword = new ResetPassword();
                resetPassword.show(getSupportFragmentManager(),resetPassword.getTag());
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.darkBlue));
            }
        };
        ss.setSpan(clickableSpan, 53, 67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView passReset = findViewById(R.id.login_forget_pass);
        passReset.setText(ss);
        passReset.setMovementMethod(LinkMovementMethod.getInstance());
        passReset.setHighlightColor(Color.TRANSPARENT);
    }

    private void init() {
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_submit);
    }
    private boolean validateInput() {
        String regex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
        if(TextUtils.isEmpty(email.getText()) && email.getText().toString().matches(regex)) {
            email.setError("Valid email is required.");
            return false;
        } else if (TextUtils.isEmpty(password.getText())) {
            password.setError("At least 6 digit is required");
            return false;
        }
        return true;
    }
}