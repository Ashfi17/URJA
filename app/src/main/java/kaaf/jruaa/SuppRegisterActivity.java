package kaaf.jruaa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SuppRegisterActivity extends AppCompatActivity {

    private EditText mRegName, mEmail, mPassword;
    private Button mCreateAccbtnBtn;
    private TextView logintv;
    private ImageButton Mapbtn;

    private Toolbar mtoolbar;

    private ProgressDialog mRegprogressDialog;

    //firebasae
    private FirebaseAuth mAuth;
    private DatabaseReference mRef,mSuppRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser mCurrentUser;

    int PLACE_PICKER_REQUEST = 1;

    public String address = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supp_register);
        //Toolbar
        /*mtoolbar =   findViewById(R.id.Supp_Reg_Toolbar);
        setSupportActionBar(mtoolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("S Register");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        //FIREBASE

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        /*mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();*/




        mRegName = findViewById(R.id.reg_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateAccbtnBtn = findViewById(R.id.reg_btn);
        logintv = findViewById(R.id.Login_textview);
        Mapbtn = findViewById(R.id.mapImg);



        logintv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logintv_intent = new Intent(SuppRegisterActivity.this, CustLoginActivity.class);
                logintv_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logintv_intent);
            }
        });

        Mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent Placeintent;
                try{

                    Placeintent = builder.build(SuppRegisterActivity.this);
                    startActivityForResult(Placeintent,PLACE_PICKER_REQUEST);

                }
                catch (GooglePlayServicesRepairableException e){
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


        mRegprogressDialog = new ProgressDialog(this);


    }

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            //String DbAddress = null;


            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                address = String.format("%s", place.getAddress()
                );
            }

            /*mRef = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            mRef.child("Users").child(mCurrentUser.getUid()).child("location").setValue(address);*/



        }
    }

    public void onRegisterButtonClicked(View view) {

        final String name, email, password;
        name = mRegName.getText().toString().trim();
        email = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();





        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mRegprogressDialog.setTitle("Registering User");
            mRegprogressDialog.setMessage("Creating account,Please Wait");
            mRegprogressDialog.setCanceledOnTouchOutside(false);
            mRegprogressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String uId;
                        assert mCurrentUser != null;
                        uId = mCurrentUser.getUid();


                        mRef = firebaseDatabase.getReference().child("Users").child(uId);
                        mRef.child("name").setValue(name);
                        mRef.child("location").setValue(address);
                        final String UserKey = mRef.getKey();

                        mRegprogressDialog.dismiss();

                        // String name_value = mRegName.getText().toString();
                        Toast.makeText(SuppRegisterActivity.this, "Sign in successfully ",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        // If sign in fails, display a message to the user.
                        mRegprogressDialog.hide();

                        Toast.makeText(SuppRegisterActivity.this, "Sign in error ",
                                Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }
    }
}
