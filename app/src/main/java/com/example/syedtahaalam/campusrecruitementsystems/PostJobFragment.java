package com.example.syedtahaalam.campusrecruitementsystems;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
 A simple {@link Fragment} subclass.
*/

public class PostJobFragment extends Fragment implements View.OnClickListener {

    private String mTitle;
    private String mEmail;
    private String mQualification;
    private String mExperience;
    private String mSalary;
    private String mLocation;
    private String mDeadline;
    private  String mUserID;

    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;

    private Button mPostButton;
    private EditText mTitleEditText,mQualificationEditText,mExperienceEditText,mSalaryEditText,
                     mLocationEditText,mDeadlineEditText,mEmailEditText;

    public PostJobFragment(){
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.post_job,container,false);

        mAuth= FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mPostButton= (Button) rootView.findViewById(R.id.buttonPost);
        mProgressBar =(ProgressBar) rootView.findViewById(R.id.progressbar);

        mTitleEditText = (EditText) rootView.findViewById(R.id.editTextTitle);
        mQualificationEditText = (EditText) rootView.findViewById(R.id.editTextQualification);
        mExperienceEditText =(EditText) rootView.findViewById(R.id.editTextExperience);
        mSalaryEditText = (EditText) rootView.findViewById(R.id.editTextSalary);
        mLocationEditText = (EditText) rootView.findViewById(R.id.editTextLocation);
        mDeadlineEditText = (EditText) rootView.findViewById(R.id.editTextDeadline);
        mEmailEditText = (EditText) rootView.findViewById(R.id.editTextEmail);

        mPostButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.buttonPost:

                saveJobPost();
                break;
        }
    }

    private void saveJobPost() {

        mTitle = mTitleEditText.getText().toString().trim();
        mEmail = mEmailEditText.getText().toString().trim();
        mQualification = mQualificationEditText.getText().toString().trim();
        mExperience = mExperienceEditText.getText().toString().trim();
        mSalary = mSalaryEditText.getText().toString().trim();
        mDeadline = mDeadlineEditText.getText().toString().trim();
        mLocation = mLocationEditText.getText().toString().trim();

        if (mTitle.isEmpty()){

            mTitleEditText.setError("Enter Job Title!");
            mTitleEditText.requestFocus();
            return;
        }
        if (mQualification.isEmpty()){
            mQualificationEditText.setError("Enter Required Qualification!");
            mQualificationEditText.requestFocus();
            return;
        }

        if (mExperience.isEmpty()){
            mExperienceEditText.setError("Enter Required Experience!");
            mExperienceEditText.requestFocus();
            return;
        }

        if (mSalary.isEmpty()){
            mSalaryEditText.setError("Enter Salary!");
            mSalaryEditText.requestFocus();
            return;
        }

        if (mLocation.isEmpty()){
            mLocationEditText.setError("Enter Location!");
            mLocationEditText.requestFocus();
            return;
        }
        if (mDeadline.isEmpty()){
            mDeadlineEditText.setError("Enter Deadline!");
            mDeadlineEditText.requestFocus();
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
        mProgressBar.setVisibility(View.VISIBLE);

        mUserID = mAuth.getUid();

        Job job = new Job(mTitle,mEmail,mExperience,mDeadline,mSalary,mLocation,mQualification,mUserID);

        mUserDatabaseReference = mFirebaseDatabase.getReference().child("Jobs").child(mUserID);
        mUserDatabaseReference.push().setValue(job).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                mProgressBar.setVisibility(View.GONE);

                if(task.isSuccessful()){

                    mTitleEditText.setText("");
                    mQualificationEditText.setText("");
                    mExperienceEditText.setText("");
                    mSalaryEditText.setText("");
                    mLocationEditText.setText("");
                    mDeadlineEditText.setText("");
                    mEmailEditText.setText("");

                    Toast.makeText(getActivity(),"Job has been Post!",Toast.LENGTH_SHORT).show();
                }
                else{

                    Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
