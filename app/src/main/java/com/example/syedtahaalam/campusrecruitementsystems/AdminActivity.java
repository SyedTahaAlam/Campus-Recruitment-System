package com.example.syedtahaalam.campusrecruitementsystems;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        AdminAdapter adminAdapter = new AdminAdapter(this,getSupportFragmentManager());
        viewPager.setAdapter(adminAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuLogout:
                
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(AdminActivity.this,MainActivity.class));
                break;
        }
        return true;
    }
}
