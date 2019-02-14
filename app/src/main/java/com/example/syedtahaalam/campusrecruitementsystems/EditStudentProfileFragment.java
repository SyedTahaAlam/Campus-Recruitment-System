package com.example.syedtahaalam.campusrecruitementsystems;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
 A simple {@link Fragment} subclass.
*/

public class EditStudentProfileFragment extends Fragment implements View.OnClickListener {

    private String mUserID;
    private EditText mNameEditText,mPhoneEditText;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mStudentDatabaseReference;
    private ChildEventListener mChildEventListener;

    public EditStudentProfileFragment(){
        // Required empty public constructor

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_student_profile,container,false);

        mAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mNameEditText = (EditText) rootView.findViewById(R.id.editTextName);
        mPhoneEditText = (EditText) rootView.findViewById(R.id.editTextPhone);
        mProgressBar =(ProgressBar) rootView.findViewById(R.id.progressbar);

        Button updateButton = (Button) rootView.findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(this);


        return  rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();

    }

    private void detachDatabaseReadListener() {

        if (mChildEventListener != null) {
            mStudentDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.buttonUpdate:

                if (mAuth.getCurrentUser() != null) {

                    attachDatabaseReadListener();

                }
                 break;
        }
    }

    private void attachDatabaseReadListener() {

        mUserID = mAuth.getUid();
        mStudentDatabaseReference = mFirebaseDatabase.getReference().child("Users").child("Student");

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if(dataSnapshot==null) {
                        return;
                    }


                    String name = mNameEditText.getText().toString().trim();
                    String phone = mPhoneEditText.getText().toString().trim();

                    if (name.isEmpty()){

                        mNameEditText.setError("Enter your Name!");
                        mNameEditText.requestFocus();
                        return;
                    }
                    if (phone.isEmpty()){

                        mPhoneEditText.setError("Enter your Phone!");
                        mPhoneEditText.requestFocus();
                        return;
                    }
                    if (phone.length() != 11){

                        mPhoneEditText.setError("Invalid Number!");
                        mPhoneEditText.requestFocus();
                        return;
                    }

                    mProgressBar.setVisibility(View.VISIBLE);


                        if (dataSnapshot.getKey().equals(mUserID)) {


                            User currentStudent = dataSnapshot.getValue(User.class);
                            currentStudent.setName(name);
                            currentStudent.setPhone(phone);

                            mStudentDatabaseReference.child(mUserID).setValue(currentStudent).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    mProgressBar.setVisibility(View.GONE);

                                    if (task.isSuccessful()) {

                                        mNameEditText.setText("");
                                        mPhoneEditText.setText("");

                                        Toast.makeText(getActivity(), "Profile Edit Successfully", Toast.LENGTH_SHORT).show();
                                    } else {

                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
        mStudentDatabaseReference.addChildEventListener(mChildEventListener);

    }
    }
