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

public class CompaniesFragment extends Fragment {

    private ListView mCompaniesListView;
    private ArrayAdapter<String> mCompanyAdapter;
    private List<String> companyID;
    private List<User> companies;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildEventListener;

    public CompaniesFragment(){
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_view,container,false);

        mCompaniesListView = (ListView) rootView.findViewById(R.id.list);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() != null) {

            companies = new ArrayList<User>();
            companyID = new ArrayList<String>();
            List<String> companiesNames = new ArrayList<String>();
            mCompanyAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item,R.id.list_item_textView,companiesNames);

                attachDatabaseReadListener();

        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        mCompanyAdapter.clear();
    }

    private void detachDatabaseReadListener() {

        if (mChildEventListener != null) {
            mUserDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

   private void attachDatabaseReadListener() {

        mCompaniesListView.setAdapter(mCompanyAdapter);

        mUserDatabaseReference = mFirebaseDatabase.getReference().child("Users").child("Company");

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if(dataSnapshot==null) {
                        return;
                    }

                        User currentCompany = dataSnapshot.getValue(User.class);

                            companyID.add(dataSnapshot.getKey().toString());
                            companies.add(currentCompany);

                            String companyName = currentCompany.getName();
                            mCompanyAdapter.add(companyName);

                                if (getActivity() instanceof AdminActivity) {

                                    mCompaniesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                        @Override
                                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                                            deleteCompany(i);
                                             return true;
                                        }
                                    });

                                    mCompaniesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                            displayCompanyInformation(i);
                                        }
                                        });
                                } else if (getActivity() instanceof StudentActivity) {

                                    mCompaniesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                            displayCompanyInformation(i);

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

    private void deleteCompany(final int position) {

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

                String companyId = companyID.get(position);
                DatabaseReference selectedCompany = mUserDatabaseReference.child(companyId);

                selectedCompany.removeValue();

                mCompanyAdapter.clear();
                mCompaniesListView.setAdapter(null);
                attachDatabaseReadListener();

                deleteDialogue.dismiss();

                Toast.makeText(getActivity(),"Company Deleted Successfully!",Toast.LENGTH_SHORT).show();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteDialogue.dismiss();
            }
        });

    }

    private void displayCompanyInformation(int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getLayoutInflater();

        final View dialogueView = inflater.inflate(R.layout.users_information_dialogue,null);

        builder.setView(dialogueView);

        builder.setTitle(R.string.company);

        final AlertDialog companyDialogue = builder.create();
        companyDialogue.show();

        TextView nameTextView = (TextView) dialogueView.findViewById(R.id.user_name_textView);
        TextView emailTextView = (TextView) dialogueView.findViewById(R.id.user_email_textView);
        TextView phoneTextView = (TextView) dialogueView.findViewById(R.id.user_phone_textView);

        User selectedCompany =companies.get(position);

        nameTextView.setText(selectedCompany.getName());
        emailTextView.setText(selectedCompany.getEmail());
        phoneTextView.setText(selectedCompany.getPhone());

    }

}
