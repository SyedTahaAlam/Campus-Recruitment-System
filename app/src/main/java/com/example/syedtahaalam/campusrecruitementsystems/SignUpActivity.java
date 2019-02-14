package com.example.syedtahaalam.campusrecruitementsystems;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private String mName;
    private String mEmail;
    private String mPassword;
    private String mPhone;
    private String mType;
    private String mUserID;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;

    private TextView mLoginTextView;
    private Button mSignUpButton;
    private ProgressBar mProgressBar;
    private EditText mNameEditText,mEmailEditText,mPasswordEditText,mPhoneEditText;
    private RadioButton mStudentRadioButton, mAdminRadioButton, mCompanyRadioButton;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth= FirebaseAuth.getInstance();

         mFirebaseDatabase = FirebaseDatabase.getInstance();
         mUserDatabaseReference = mFirebaseDatabase.getReference().child("Users");

         mLoginTextView = (TextView) findViewById(R.id.textViewLogin);
        mEmailEditText = (EditText) findViewById(R.id.editTextEmail);
        mPasswordEditText = (EditText) findViewById(R.id.editTextPassword);
        mNameEditText = (EditText) findViewById(R.id.editTextName);
        mPhoneEditText = (EditText) findViewById(R.id.editTextPhone);
        mProgressBar =(ProgressBar) findViewById(R.id.progressbar);
        mSignUpButton = (Button) findViewById(R.id.buttonSignUp);
        mStudentRadioButton = (RadioButton) findViewById(R.id.student_radio_button);
        mAdminRadioButton = (RadioButton) findViewById(R.id.admin_radio_button);
        mCompanyRadioButton = (RadioButton) findViewById(R.id.company_radio_button);

        mStudentRadioButton.setOnClickListener(this);
        mAdminRadioButton.setOnClickListener(this);
        mCompanyRadioButton.setOnClickListener(this);
        mLoginTextView.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.textViewLogin:

                Intent loginIntent = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(loginIntent);
                finish();
                break;

            case R.id.buttonSignUp:

                registerUser();
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

    private void registerUser() {

        mName = mNameEditText.getText().toString().trim();
        mEmail = mEmailEditText.getText().toString().trim();
        mPassword = mPasswordEditText.getText().toString().trim();
        mPhone = mPhoneEditText.getText().toString().trim();

        if (mName.isEmpty()){

            mNameEditText.setError("Enter your Name!");
            mNameEditText.requestFocus();
            return;
        }

        if (mEmail.isEmpty()){

            mEmailEditText.setError("Enter your Email!");
            mEmailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()){

            mEmailEditText.setError("Invalid Email!");
            mEmailEditText.requestFocus();
            return;
        }

        if (mPassword.isEmpty()){
            mPasswordEditText.setError("Enter your Password!");
            mPasswordEditText.requestFocus();
            return;
        }

        if (mPassword.length()<6){
            mPasswordEditText.setError("Minimum Password Limit is 6");
            mPasswordEditText.requestFocus();
            return;
        }

        if (mPhone.isEmpty()){

            mPhoneEditText.setError("Enter your Phone!");
            mPhoneEditText.requestFocus();
            return;
        }
        if (mPhone.length() != 11){

            mPhoneEditText.setError("Invalid Number!");
            mPhoneEditText.requestFocus();
            return;
        }
        if ((!mStudentRadioButton.isChecked()) && (!mAdminRadioButton.isChecked()) && (!mCompanyRadioButton.isChecked())){

            Toast.makeText(SignUpActivity.this,"Choose the Account Category!",Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(mEmail,mPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        mProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){

                            mUserID = mAuth.getUid();

                            User currentUser = new User (mName,mEmail,mPhone,mType);

                            Toast.makeText(SignUpActivity.this, "Sign Up Successfull!", Toast.LENGTH_SHORT).show();

                            if (mType.equals(getResources().getString(R.string.student))) {

                                mUserDatabaseReference = mUserDatabaseReference.child("Student").child(mUserID);
                                mUserDatabaseReference.setValue(currentUser);

                                Intent studentIntent = new Intent(SignUpActivity.this, StudentActivity.class);
                                studentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(studentIntent);
                                finish();
                            }
                            else if (mType.equals(getResources().getString(R.string.admin))){

                                mUserDatabaseReference = mUserDatabaseReference.child("Admin").child(mUserID);
                                mUserDatabaseReference.setValue(currentUser);

                                Intent adminIntent = new Intent(SignUpActivity.this, AdminActivity.class);
                                adminIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(adminIntent);
                                finish();

                            }
                            else{
                                mUserDatabaseReference = mUserDatabaseReference.child("Company").child(mUserID);
                                mUserDatabaseReference.setValue(currentUser);

                                Intent companyIntent = new Intent(SignUpActivity.this, CompanyActivity.class);
                                companyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(companyIntent);
                                finish();

                            }



                        }else{

                            if (task.getException() instanceof FirebaseAuthUserCollisionException){

                                Toast.makeText(getApplicationContext(),"Already Signed Up with this Email!",Toast.LENGTH_SHORT).show();
                            }else{

                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
}
