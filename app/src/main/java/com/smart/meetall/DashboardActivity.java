package com.smart.meetall;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class DashboardActivity extends AppCompatActivity {

    EditText secretCodeBox;
    Button joinBtn, shareBtn;
    BottomNavigationView bottom_nav;
    FirebaseDatabase db;
    DatabaseReference reference;
    String userID;
    String x, timeText;
    ReviewManager reviewManager;
    ReviewInfo reviewInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy  'at' HH:mm:ss ");
        String currentDateandTime = sdf.format(new Date());


        secretCodeBox = findViewById(R.id.codeBox);
        joinBtn = findViewById(R.id.joinBtn);
        shareBtn = findViewById(R.id.shareBtn);
        URL serverURL;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
                txtIntent.setType("text/plain");
                txtIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Meeting Joining ID");
                txtIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Join Meet All room using this secret ID" + " "+secretCodeBox.getText().toString());
                startActivity(Intent.createChooser(txtIntent ,"Share"));
            }
        });

        db = FirebaseDatabase.getInstance();
        userID = user.getUid();
        x = userID.substring(0, 4);
        x = x + "xyz";
        reference = db.getReference().child(x + "My meet");

        try {
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        String randomUid = UUID.randomUUID().toString().replace("-","");
        secretCodeBox.setText(randomUid);

        joinBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(secretCodeBox.getText().toString())
                        .setWelcomePageEnabled(false)
                        .build();
//                saveMeetRoom();

                JitsiMeetActivity.launch(DashboardActivity.this, options);
                HashMap<String, String> meetIds = new HashMap<>();
                meetIds.put("Room_ID", secretCodeBox.getText().toString());
                meetIds.put("Room_time", currentDateandTime);
                reference.push().setValue(meetIds);


            }
        });

        bottom_nav = findViewById(R.id.bottomNavigationView);
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.home:
                        break;
                    case R.id.history:
                        Intent intent = new Intent(getApplicationContext(), history.class);
                        startActivity(intent);
                        break;

                }

                return true;
            }
        });

    }

//    private void saveMeetRoom() {
//        String roomID = secretCodeBox.getText().toString();
//        if (TextUtils.isEmpty(roomID)) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
//        } else {
//            HashMap<String, String> meetIds = new HashMap<>();
//            meetIds.put("Room", roomID);
//            reference.push().setValue(meetIds);
//
////        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout_btn:
                signout();
                return true;
            case R.id.rateus:
                initReview();
                break;
            case R.id.shareus:
                shareMe();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void shareMe() {
        final String appPackageName = this.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        this.startActivity(sendIntent);
    }

    private void initReview() {
        reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener((task -> {
            if (task.isSuccessful()){
                reviewInfo = task.getResult();
            }else{

                Toast.makeText(this, "Review failed to start", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    void startReviewFlow(){
        if (reviewInfo != null){
            Task<ReviewInfo> flow = reviewManager.requestReviewFlow();
            flow.addOnCompleteListener(task ->{
                Toast.makeText(this, "Review is Completed", Toast.LENGTH_SHORT).show();
            });

        }
    }

    private void signout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}