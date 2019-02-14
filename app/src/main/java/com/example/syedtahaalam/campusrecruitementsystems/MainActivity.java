package com.example.syedtahaalam.campusrecruitementsystems;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildEventListener;

    private String mUserID;
    private String mType;

    private TextView mSignUpTextview;
    private ProgressBar mProgressBar;
    private Button mLoginButton;
    private EditText mEmailEditText,mPasswordEditText;
    private RadioButton mStudentRadioButton, mAdminRadioButton, mCompanyRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mSignUpTextview =(TextView) findViewById(R.id.textViewSignup);
        mEmailEditText = (EditText) findViewById(R.id.editTextEmail);
        mPasswordEditText = (EditText) findViewById(R.id.editTextPassword);
        mProgressBar =(ProgressBar) findViewById(R.id.progressbar);
        mLoginButton = (Button) findViewById(R.id.buttonLogin);
        mStudentRadioButton = (RadioButton) findViewById(R.id.student_radio_button);
        mAdminRadioButton = (RadioButton) findViewById(R.id.admin_radio_button);
        mCompanyRadioButton = (RadioButton) findViewById(R.id.company_radio_button);

        mStudentRadioButton.setOnClickListener(this);
        mAdminRadioButton.setOnClickListener(this);
        mCompanyRadioButton.setOnClickListener(this);
        mSignUpTextview.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {

        if (mChildEventListener != null) {
            mUserDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.textViewSignup:

                Intent signuUpIntent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(signuUpIntent);
                finish();
                break;

            case R.id.buttonLogin:

                Login();
                break;

            case R.id.student_radio_button:

                mType = getResources().getString(R.string.student);
                break;

            case R.id.admin_radio_button:
                mType = getResources().getString(R.string.admin);
                break;

            case R.id.company_radio_button:
                mType= getResources().getString(R.string.company);
                break;

        }
    }
    private void Login() {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        if (email.isEmpty()){

            mEmailEditText.setError("Enter your Email!");
            mEmailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            mEmailEditText.setError("Invalid Email!");
            mEmailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()){
            mPasswordEditText.setError("Enter your Password!");
            mPasswordEditText.requestFocus();
            return;
        }

        if (password.length()<6){
            mPasswordEditText.setError("Minimum Password Limit is 6");
            mPasswordEditText.requestFocus();
            return;
        }

        if ((!mStudentRadioButton.isChecked()) && (!mAdminRadioButton.isChecked()) && (!mCompanyRadioButton.isChecked())){

            Toast.makeText(MainActivity.this,"Choose the Account Category!",Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        mProgressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()){

                            attachDatabaseReadListener();

                        }else{

                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    private void attachDatabaseReadListener() {

        mUserID = mAuth.getUid();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("Users").child(mType);

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if(dataSnapshot.getKey().equals(mUserID)) {

                    User currentUser = dataSnapshot.getValue(User.class);

                        Toast.makeText(MainActivity.this, "Login Successfull!", Toast.LENGTH_SHORT).show();

                        if (currentUser.getType().equals(getResources().getString(R.string.student))) {

                            Intent studentIntent = new Intent(MainActivity.this, StudentActivity.class);
                            studentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(studentIntent);
                            finish();

                        } else if (currentUser.getType().equals(getResources().getString(R.string.admin))) {

                            Intent adminIntent = new Intent(MainActivity.this, AdminActivity.class);
                            adminIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(adminIntent);
                            finish();

                        } else {

                            Intent companyIntent = new Intent(MainActivity.this, CompanyActivity.class);
                            companyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(companyIntent);
                            finish();

                        }
                        return;

                    }
                    }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
        }
        mUserDatabaseReference.addChildEventListener(mChildEventListener);

    }
}
