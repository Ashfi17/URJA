package kaaf.jruaa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustSettingsActivity extends AppCompatActivity {

    private ImageButton DisplayPicture,map_button;
    private TextView settingsLocation;
    //Maps PlacePicker
    int PLACE_PICKER_REQUEST = 1;


    //Firebase

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef;
    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_settings);

        DisplayPicture = findViewById(R.id.imageButton);
        map_button = findViewById(R.id.map_button);
        settingsLocation = findViewById(R.id.Settings_Location);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        String uId = mCurrentUser.getUid();

        mRef = FirebaseDatabase.getInstance().getReference().child("Customer").child(uId);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String Loc = dataSnapshot.child("location").getValue().toString();
                settingsLocation.setText(Loc);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent Placeintent;
                try{

                    Placeintent = builder.build(CustSettingsActivity.this);
                    startActivityForResult(Placeintent,PLACE_PICKER_REQUEST);

                }
                catch (GooglePlayServicesRepairableException e){
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            //String DbAddress = null;

            String address = null;
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                address = String.format("%s", place.getAddress()
                );
            }

            mRef = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            mRef.child("Customer").child(mCurrentUser.getUid()).child("location").setValue(address);

            settingsLocation.setText(address);

        }
    }
}
