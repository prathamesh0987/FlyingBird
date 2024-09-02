package svrinfotech.com.flyingbird;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import svrinfotech.com.flyingbird.pojo.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    Fragment fragment;

    NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    Intent intent;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager=getSupportFragmentManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Context context=getBaseContext();
        intent=new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent=PendingIntent.getActivity(context,0,intent,0);
        final String CHANNEL_ID="CHAT_NOTIFICATIONS";

        builder=new NotificationCompat.Builder(context,CHANNEL_ID);
        final String email= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot userData=iterator.next();
                    User user=userData.getValue(User.class);
                    if(user.getMail().equals(email)) {
                        FirebaseDatabase.getInstance().getReference().child("message").child(user.getUsername()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String msg=dataSnapshot.getValue(String.class);
                                Toast.makeText(getApplicationContext(),
                                        "Msg : "+msg,
                                        Toast.LENGTH_LONG).show();
                                builder.setContentTitle("SVR")
                                        .setContentText(msg)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                            "SVR NOTIFICATIONS",
                                            NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationManager.createNotificationChannel(channel);
                                }

                                notificationManager.notify(0,builder.build());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(),
                                        "Error : "+databaseError.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),
                        "Error : "+databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.add_user:
                fragment=new AddUser();
                setTitle(R.string.addUser);
                break;
            case R.id.sentMessage:
                setTitle(R.string.sendMessage);
                fragment=new SendMessage();
                break;
        }

        if(fragment!=null) {
            fragmentManager.beginTransaction().replace(R.id.relativeLayout,fragment).commitAllowingStateLoss();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
