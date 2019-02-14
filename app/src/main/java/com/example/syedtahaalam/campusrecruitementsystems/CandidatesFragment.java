package com.example.syedtahaalam.campusrecruitementsystems;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/*
 A simple {@link Fragment} subclass.
*/


public class CandidatesFragment extends Fragment {

    private ListView mCandidatesListView;
    private ArrayAdapter<String> mCandidateAdapter;
    private List<Candidate> candidates;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCandidateDatabaseReference;
    private ChildEventListener mChildEventListener;

    private String mUserID;

    public CandidatesFragment(){
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_view,container,false);


        mCandidatesListView = (ListView) rootView.findViewById(R.id.list);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();


        if (mAuth.getCurrentUser() != null) {

            candidates = new ArrayList<Candidate>();
            List<String> candidatesNames = new ArrayList<String>();
            mCandidateAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item,R.id.list_item_textView,candidatesNames);

            attachDatabaseReadListener();

        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        mCandidateAdapter.clear();
    }

    private void detachDatabaseReadListener() {

        if (mChildEventListener != null) {
            mCandidateDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {

        mCandidatesListView.setAdapter(mCandidateAdapter);

        mUserID = mAuth.getUid();
        mCandidateDatabaseReference = mFirebaseDatabase.getReference().child("Candidates").child(mUserID);

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (dataSnapshot == null) {
                        return;
                    }

                    Candidate currentCandidate = dataSnapshot.getValue(Candidate.class);

                    candidates.add(currentCandidate);

                    String candidateName = currentCandidate.getName();
                    mCandidateAdapter.add(candidateName);

                    mCandidatesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            displayCandidateInformation(i);

                        }
                    });

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
        mCandidateDatabaseReference.addChildEventListener(mChildEventListener);

    }

    private void displayCandidateInformation(int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getLayoutInflater();

        final View dialogueView = inflater.inflate(R.layout.users_information_dialogue,null);

        builder.setView(dialogueView);

        builder.setTitle(R.string.candidate);

        final AlertDialog candidateDialogue = builder.create();
        candidateDialogue.show();

        TextView nameTextView = (TextView) dialogueView.findViewById(R.id.user_name_textView);
        TextView emailTextView = (TextView) dialogueView.findViewById(R.id.user_email_textView);
        TextView phoneTextView = (TextView) dialogueView.findViewById(R.id.user_phone_textView);
        View linearLayout = (View) dialogueView.findViewById(R.id.linear_layout);
        linearLayout.setVisibility(View.VISIBLE);
        TextView jobTitleTextView = (TextView) dialogueView.findViewById(R.id.job_title);

        Candidate selectedCandidate =candidates.get(position);

        nameTextView.setText(selectedCandidate.getName());
        emailTextView.setText(selectedCandidate.getEmail());
        phoneTextView.setText(selectedCandidate.getPhone());
        jobTitleTextView.setText(selectedCandidate.getJobTitle());

    }

}
