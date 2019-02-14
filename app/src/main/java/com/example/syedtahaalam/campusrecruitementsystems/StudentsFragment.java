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

public class StudentsFragment extends Fragment {

    private ListView mStudentsListView;
    private ArrayAdapter<String> mStudentAdapter;
    private List<String> studentID;
    private List<User> students;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildEventListener;


    public StudentsFragment(){
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_view,container,false);


        mStudentsListView = (ListView) rootView.findViewById(R.id.list);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() != null) {

            students = new ArrayList<User>();
            studentID = new ArrayList<String>();
            List<String> studentsNames = new ArrayList<String>();
            mStudentAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item,R.id.list_item_textView,studentsNames);

            attachDatabaseReadListener();

        }

        return rootView;
        }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        mStudentAdapter.clear();

    }

    private void detachDatabaseReadListener() {

        if (mChildEventListener != null) {
            mUserDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {

        mStudentsListView.setAdapter(mStudentAdapter);

        mUserDatabaseReference = mFirebaseDatabase.getReference().child("Users").child("Student");

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if(dataSnapshot==null) {
                        return;
                    }


                        User currrentStudent = dataSnapshot.getValue(User.class);

                            studentID.add(dataSnapshot.getKey().toString());
                            students.add(currrentStudent);

                            String  studentName = currrentStudent.getName();
                            mStudentAdapter.add(studentName);

                            if (getActivity() instanceof AdminActivity) {

                                mStudentsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        deleteStudent(i);
                                        return true;
                                    }
                                });

                               mStudentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                   @Override
                                   public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                       displayStudentInformation(i);

                                   }
                               });
                            } else if (getActivity() instanceof CompanyActivity) {

                                mStudentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        displayStudentInformation(i);


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
        mUserDatabaseReference.addChildEventListener(mChildEventListener);

    }

    private void deleteStudent(final int position) {

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

                String studentId = studentID.get(position);
                DatabaseReference selectedStudent = mUserDatabaseReference.child(studentId);

                selectedStudent.removeValue();

                mStudentAdapter.clear();
                mStudentsListView.setAdapter(null);
               attachDatabaseReadListener();

                deleteDialogue.dismiss();

                Toast.makeText(getActivity(),"Student Deleted Successfully!",Toast.LENGTH_SHORT).show();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteDialogue.dismiss();
            }
        });

        }

    private void displayStudentInformation(int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getLayoutInflater();

        final View dialogueView = inflater.inflate(R.layout.users_information_dialogue,null);

        builder.setView(dialogueView);

        builder.setTitle(R.string.student);

        final AlertDialog companyDialogue = builder.create();
        companyDialogue.show();

        TextView nameTextView = (TextView) dialogueView.findViewById(R.id.user_name_textView);
        TextView emailTextView = (TextView) dialogueView.findViewById(R.id.user_email_textView);
        TextView phoneTextView = (TextView) dialogueView.findViewById(R.id.user_phone_textView);

        User selectedStudent =students.get(position);

        nameTextView.setText(selectedStudent.getName());
        emailTextView.setText(selectedStudent.getEmail());
        phoneTextView.setText(selectedStudent.getPhone());

    }

}
