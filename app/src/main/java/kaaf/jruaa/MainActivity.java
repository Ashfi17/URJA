package kaaf.jruaa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView.Adapter;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private TextView mNavName;
    private  TextView mNAvLocation;


    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mRef,mSuppRef;
    private RecyclerView mUsersList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Android





        //FIREBASE
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();



        if (mAuth.getCurrentUser() != null) {
            mRef = database.getReference().child("Customer").child(mCurrentUser.getUid());

        }
        else
        {

            Intent i = new Intent(MainActivity.this,CustLoginActivity.class);
            startActivity(i);
        }

        mUsersList = findViewById(R.id.UsersListRec);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));




       /* else {

            Intent fint = new Intent(MainActivity.this, CustLoginActivity.class);
            startActivity(fint);
            Toast.makeText(MainActivity.this, "You need to login first", Toast.LENGTH_LONG).show();
            finish();

        }*/





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mNavName = navigationView.getHeaderView(0).findViewById(R.id.nav_UserName);
        mNAvLocation = navigationView.getHeaderView(0).findViewById(R.id.nav_UserLocation);


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String name = dataSnapshot.child("name").getValue().toString();
                final String location = dataSnapshot.child("location").getValue().toString();
                Log.e("ashfi",name);
                mNavName.setText(name);
                mNAvLocation.setText(location);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        if (id == R.id.nav_settings) {
            Intent setInt = new Intent(MainActivity.this,CustSettingsActivity.class);
            startActivity(setInt);
        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            Intent startIntent = new Intent(MainActivity.this, CustLoginActivity.class);
            startActivity(startIntent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent Lint = new Intent(MainActivity.this, CustLoginActivity.class);
            startActivity(Lint);
            Toast.makeText(MainActivity.this, "You need to login first", Toast.LENGTH_LONG).show();
            finish();
        }


        mSuppRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        FirebaseRecyclerAdapter <Users, UsersViewHolder> UserRBA = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.single_user_layout,
                UsersViewHolder.class,
                mSuppRef
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, Users model, int position) {
                final String list_user_id = mSuppRef.getRef().getKey();
                mSuppRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String usrName = dataSnapshot.child("name").getValue().toString();
                        final String usrLoc = dataSnapshot.child("location").getValue().toString();

                        viewHolder.setName(usrName);
                        viewHolder.setLocation(usrLoc);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        mUsersList.setAdapter(UserRBA);


    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public  void setName(String name){

            TextView mname = mView.findViewById(R.id.User_name);
            mname.setText(name);
        }

        public void setLocation(String location){

            TextView mLoc = mView.findViewById(R.id.User_address);
            mLoc.setText(location);
        }
    }



}
