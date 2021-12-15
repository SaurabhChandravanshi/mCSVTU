package com.saurabhchandr.em.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.saurabhchandr.em.R;

public class ResetPassword extends BottomSheetDialogFragment {

    private AppCompatButton sendLinkBtn;
    private EditText email;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_pass,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendLinkBtn = view.findViewById(R.id.reset_pass_submit);
        email = view.findViewById(R.id.reset_pass_email);

        sendLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
                if (TextUtils.isEmpty(email.getText()) || !email.getText().toString().matches(regex)) {
                    email.setError("Valid email is required");
                } else {
                    sendLinkBtn.setText("Please wait...");
                    sendLinkBtn.setEnabled(false);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendLinkBtn.setText("Send password reset link");
                                    sendLinkBtn.setEnabled(true);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(view.getContext(), "Password reset link has been sent to "+email.getText().toString(), Toast.LENGTH_LONG).show();
                                        dismiss();
                                    } else {
                                        Toast.makeText(view.getContext(), "Look like email is not registered, Please try sign-up instead.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}
