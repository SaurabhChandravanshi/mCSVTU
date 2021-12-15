package com.saurabhchandr.em;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saurabhchandr.em.Model.Branch;
import com.saurabhchandr.em.Model.BranchNames;
import com.saurabhchandr.em.Model.CTPaper;
import com.saurabhchandr.em.Model.College;

import java.util.ArrayList;
import java.util.List;

import kotlin.coroutines.Continuation;

public class UploadActivity extends AppCompatActivity {

    private Spinner semSpinner,branchSpinner;
    private EditText title;
    private RadioGroup rgExam,rgCT;
    private AutoCompleteTextView college;
    private AppCompatButton selectBtn,sendBtn;
    private String EXAM_TYPE = "CT";
    private String CT_NAME = "CT 1";
    private String FILE_URL;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        if(mAuth.getCurrentUser() == null) {
            finish();
        }

        init();
        setUpSpinners();

        rgExam.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.upload_exam_ct) {
                    EXAM_TYPE = "CT";
                } else {
                    EXAM_TYPE = "ESE";
                }
            }
        });
        rgCT.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.upload_ct_1)
                    CT_NAME = "CT 1";
                else if (checkedId == R.id.upload_ct_2)
                    CT_NAME = "CT 2";
            }
        });

        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Uri pdfUri = data.getData();
                            selectBtn.setEnabled(false);
                            selectBtn.setText("Uploading...");

                            String timestamp = String.valueOf(System.currentTimeMillis());
                            StorageReference reference = FirebaseStorage.getInstance().getReference(EXAM_TYPE);
                            StorageReference filePath = reference.child(timestamp+".pdf");
                            filePath.putFile(pdfUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> taskDl) {
                                                if (task.isSuccessful()) {
                                                    FILE_URL = taskDl.getResult().toString();
                                                    selectBtn.setText(" Document uploaded");
                                                    sendBtn.setEnabled(true);
                                                } else {
                                                    Toast.makeText(UploadActivity.this, R.string.def_err_message, Toast.LENGTH_SHORT).show();
                                                    selectBtn.setEnabled(true);
                                                    selectBtn.setText("Select document");
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(UploadActivity.this, R.string.def_err_message, Toast.LENGTH_SHORT).show();
                                        selectBtn.setEnabled(true);
                                        selectBtn.setText("Select document");
                                    }
                                }
                            });
                        }
                    }
                });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                someActivityResultLauncher.launch(intent);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IsInputValid() && EXAM_TYPE.equals("CT")) {
                    sendBtn.setEnabled(false);
                    sendBtn.setText("Please wait...");
                    CTPaper ctPaper = new CTPaper(title.getText().toString(),branchSpinner.getSelectedItem().toString(),
                            semSpinner.getSelectedItem().toString(),college.getText().toString(),CT_NAME,FILE_URL,"private",mAuth.getCurrentUser().getUid());
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("ctPapers").document("BTECH").update("papers", FieldValue.arrayUnion(ctPaper))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        sendBtn.setText("Completed.");
                                        confirmationDialog();
                                    } else {
                                        sendBtn.setText("Send for review");
                                        sendBtn.setEnabled(true);
                                        Toast.makeText(UploadActivity.this, R.string.def_err_message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void confirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("Document has been successfully sent for review.");
        builder.setPositiveButton("ADD OTHER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(UploadActivity.this,UploadActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }

    private boolean IsInputValid() {
        if(TextUtils.isEmpty(title.getText())) {
            title.setError("Enter subject name");
            return false;
        }
        else if(TextUtils.isEmpty(college.getText())) {
            college.setError("Enter college name");
            return false;
        }
        return true;
    }

    private void init() {
        rgExam = findViewById(R.id.upload_exam_rg);
        rgCT = findViewById(R.id.upload_ct_rg);
        semSpinner = findViewById(R.id.upload_semester_spinner);
        branchSpinner = findViewById(R.id.upload_branch_spinner);
        college = findViewById(R.id.upload_college_name);
        title = findViewById(R.id.upload_title);
        selectBtn = findViewById(R.id.upload_select_btn);
        sendBtn = findViewById(R.id.upload_submit_btn);
    }
    private void setUpSpinners() {
        // Setting up semester spinner.
        String[] semesters = new String[]{"Semester 1","Semester 2","Semester 3","Semester 4","Semester 5","Semester 6","Semester 7","Semester 8"};
        ArrayAdapter<String> semAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, semesters);
        semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semSpinner.setAdapter(semAdapter);

        // Setting up branch spinner.
        List<String> branches = new BranchNames().getBTechBranches();
        ArrayAdapter<String> branchAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,branches);
        semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);

        List<String> collegeNames = new ArrayList<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("colleges").document("BTECH").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        College.CollegeList collegeList = documentSnapshot.toObject(College.CollegeList.class);
                        List<College> colleges = new ArrayList<>();
                        if(collegeList != null) {
                            colleges.addAll(collegeList.getColleges());
                        }
                        for (College college:colleges)
                            collegeNames.add(college.getName()+", "+college.getId());

                        ArrayAdapter collegeAdapter = new ArrayAdapter(UploadActivity.this,android.R.layout.simple_list_item_1,collegeNames);
                        college.setAdapter(collegeAdapter);
                    }
                });
    }


}