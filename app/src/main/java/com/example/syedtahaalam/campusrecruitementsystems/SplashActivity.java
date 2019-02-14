package com.example.syedtahaalam.campusrecruitementsystems;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildEventListener;

    private String mUserID;

    private TextView textView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();


        textView = (TextView) findViewById(R.id.text_view);
        imageView = (ImageView) findViewById(R.id.image_view);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        textView.startAnimation(myanim);
        imageView.startAnimation(myanim);
        final Intent intent = new Intent(this,MainActivity.class);

        Thread timer = new Thread() {

            public void run() {

                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                   if (mAuth.getCurrentUser()!= null) {

                       attachDatabaseReadListener();

                   }else{

                        startActivity(intent);
                        finish();

                    }
                }
            }

        };

        timer.start();

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

   private void attachDatabaseReadListener() {


        mUserID = mAuth.getUid();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("Users");

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (dataSnapshot == null) {
                        return;
                    }

                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {


                        if (postSnapShot.getKey().equals(mUserID)) {

                            User CurrentUser = postSnapShot.getValue(User.class);

                            if (CurrentUser.getType().equals(getResources().getString(R.string.student))) {

                                Intent studentIntent = new Intent(SplashActivity.this, StudentActivity.class);
                                studentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(studentIntent);
                                finish();

                            } else if (CurrentUser.getType().equals(getResources().getString(R.string.admin))) {

                                Intent adminIntent = new Intent(SplashActivity.this, AdminActivity.class);
                                adminIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(adminIntent);
                                finish();

                            } else {

                                Intent companyIntent = new Intent(SplashActivity.this, CompanyActivity.class);
                                companyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(companyIntent);
                                finish();

                            }
                            return;
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
        mUserDatabaseReference.addChildEventListener(mChildEventListener);

    }
}

