package com.smart.meetall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                checkUser();
            }
        },2000);



    }

    private void checkUser() {
        if (user != null){
            Intent intent = new Intent(getApplicationContext(),DashboardActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }
    }

//    bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//            switch (item.getItemId()) {
//                case R.id.home_btn:
//                    loadFragment(new HomeH());
//                    textView.setText("Home");
//                    break;
//                case R.id.search_btn:
//                    loadFragment(new search());
//                    textView.setText("Search");
//                    break;
//                case R.id.Course_btn:
//                    loadFragment(new myCourse());
//                    textView.setText("My Course");
//                    break;
//                case R.id.profile_btn:
//                    loadFragment(new Home());
//                    textView.setText("Profile");
//                    break;
//            }
//
//            return true;
//        }
//    });
}