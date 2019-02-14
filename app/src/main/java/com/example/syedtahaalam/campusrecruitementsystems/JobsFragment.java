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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class JobsFragment extends Fragment {

    private ListView mJobsListView;
    private ArrayAdapter<String> mJobAdapter;
    private List<String> jobID;
    private List<String> companyID;
    private List<Job> jobs;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mJobDatabaseReference,mStudentDatabaseReference,mCandidateDatabaseReference;
    private ChildEventListener mChildEventListener,mStudentEventListener;
    private String mUserID;

    public JobsFragment(){
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_view,container,false);

        mJobsListView= (ListView) rootView.findViewById(R.id.list);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mUserID= mAuth.getUid();

        if (mAuth.getCurrentUser() != null) {

            jobID = new ArrayList<String>();
            companyID = new ArrayList<String>();
            jobs = new ArrayList<Job>();
            List<String> jobsNames = new ArrayList<String>();
            mJobAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item,R.id.list_item_textView,jobsNames);

           attachDatabaseReadListener();

        }


        return rootView;
    }
    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        mJobAdapter.clear();
    }

    private void detachDatabaseReadListener() {

        if (mChildEventListener != null) {
            mJobDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        if (mStudentEventListener != null) {
            mStudentDatabaseReference.removeEventListener(mStudentEventListener);
            mStudentEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {

        mJobsListView.setAdapter(mJobAdapter);
        mJobDatabaseReference = mFirebaseDatabase.getReference().child("Jobs");

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (dataSnapshot == null) {
                        return;
                    }

                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                        Job currentJob = postSnapShot.getValue(Job.class);

                        companyID.add(currentJob.getCompanyID());
                        jobID.add(postSnapShot.getKey().toString());
                        jobs.add(currentJob);

                        String jobTitle = currentJob.getTitle();
                        mJobAdapter.add(jobTitle);

                        if (getActivity() instanceof AdminActivity) {

                            mJobsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    deleteJob(i);
                                    return true;
                                }
                            });

                            mJobsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    displayJobInformation(i);

                                }
                            });
                        } else if (getActivity() instanceof StudentActivity) {

                            mJobsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                                    displayJobInformation(i);

                                }
                            });
                        }
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
        mJobDatabaseReference.addChildEventListener(mChildEventListener);

    }

    private void deleteJob(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getLayoutInflater();

        final View dialogueView = inflater.inflate(R.layout.delete_dialogue,null);

        builder.setView(dialogueView);

        builder.setTitle(R.string.delete);

        final AlertDialog deleteDialogue = builder.create();
        deleteDialogue.show();

        Button yesButton = (Button) dialogueView.findViewById(R.id.yes_button);
        Button noButton = (Button)  dialogueView.findViewById(R.id.no_button);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String JobId = jobID.get(position);
                String CompanyId = companyID.get(position);

                DatabaseReference selectedJob = mJobDatabaseReference.child(CompanyId).child(JobId);

                selectedJob.removeValue();

                mJobAdapter.clear();
                mJobsListView.setAdapter(null);
                attachDatabaseReadListener();

                deleteDialogue.dismiss();


                Toast.makeText(getActivity(),"Job Deleted Successfully!",Toast.LENGTH_SHORT).show();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteDialogue.dismiss();
            }
        });

    }
    private void displayJobInformation(final int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getLayoutInflater();

        final View dialogueView = inflater.inflate(R.layout.jobs_information_dialogue,null);

        builder.setView(dialogueView);

        builder.setTitle(R.string.job);


        final AlertDialog jobDialogue = builder.create();
        jobDialogue.show();

        TextView titleTextView = (TextView) dialogueView.findViewById(R.id.user_title_textView);
        TextView qualificaitonTextView = (TextView) dialogueView.findViewById(R.id.user_qualification_textView);
        TextView experienceTextView = (TextView) dialogueView.findViewById(R.id.user_experience_textView);
        TextView salaryTextView = (TextView) dialogueView.findViewById(R.id.user_salary_textView);
        TextView locationTextView = (TextView) dialogueView.findViewById(R.id.user_location_textView);
        TextView deadlineTextView = (TextView) dialogueView.findViewById(R.id.user_deadline_textView);
        TextView emailTextView = (TextView) dialogueView.findViewById(R.id.user_email_textView);

        Button applyButton = (Button) dialogueView.findViewById(R.id.apply_button);

        final Job selectedJob =jobs.get(position);

        titleTextView.setText(selectedJob.getTitle());
        qualificaitonTextView.setText(selectedJob.getQualification());
        experienceTextView.setText(selectedJob.getExperience());
        salaryTextView.setText(selectedJob.getSalary());
        locationTextView.setText(selectedJob.getLocation());
        deadlineTextView.setText(selectedJob.getDeadline());
        emailTextView.setText(selectedJob.getEmail());

        if (getActivity() instanceof StudentActivity){


            applyButton.setVisibility(View.VISIBLE);

            applyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                 mCandidateDatabaseReference = mFirebaseDatabase.getReference().child("Candidates").child(selectedJob.getCompanyID());

                    createCandidate(position);
                    jobDialogue.dismiss();
                    Toast.makeText(getActivity(),"Successfully Applied for Job!",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    private void createCandidate(final int position) {

       mStudentDatabaseReference = mFirebaseDatabase.getReference().child("Users").child("Student");

            mStudentEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if(dataSnapshot==null) {
                        return;
                    }

                    if(dataSnapshot.getKey().equals(mUserID)) {

                        User student = dataSnapshot.getValue(User.class);

                        String studentName = student.getName();
                        String studentEmail = student.getEmail();
                        String studentPhone = student.getPhone();

                        Job selectedJob = jobs.get(position);
                        String jobTitle = selectedJob.getTitle();

                        Candidate currentCandidate = new Candidate(studentName, studentEmail, studentPhone, jobTitle);

                        mCandidateDatabaseReference.push().setValue(currentCandidate);

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

            mStudentDatabaseReference.addChildEventListener(mStudentEventListener);


       /* FirebaseUser user = mAuth.getCurrentUser();

        String studentName = user.getDisplayName();
        String studentEmail = user.getEmail();
        String studentPhone = user.getPhoneNumber();

        Job selectedJob = jobs.get(position);
        String jobTitle = selectedJob.getTitle();

        Candidate currentCandidate = new Candidate(studentName, studentEmail, studentPhone, jobTitle);

        mCandidateDatabaseReference.push().setValue(currentCandidate);
*/
    }
}

