package svrinfotech.com.flyingbird;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import svrinfotech.com.flyingbird.pojo.User;

public class Signup extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseReference;

    @BindView(R.id.sName)
    EditText username;

    @BindView(R.id.sPhone)
    EditText mobile_no;

    @BindView(R.id.sPassword)
    EditText password;

    @BindView(R.id.sConfirmPassword)
    EditText confirm_password;

    @BindView(R.id.sMailID)
    EditText mail_id;

    @BindView(R.id.sSubmit)
    Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        userDatabaseReference= FirebaseDatabase.getInstance().getReference().child("user");
        firebaseAuth=FirebaseAuth.getInstance();
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sSubmit:
                final String email=mail_id.getText().toString().toLowerCase().trim();
                final String pass=password.getText().toString().trim();
                final String name=username.getText().toString().trim();
                final String confirmPassword=confirm_password.getText().toString().trim();
                final String phoneNo=mobile_no.getText().toString().trim();

                if(!TextUtils.isEmpty(email) &&
                        !TextUtils.isEmpty(pass) &&
                        !TextUtils.isEmpty(name) &&
                        !TextUtils.isEmpty(confirmPassword) &&
                        !TextUtils.isEmpty(phoneNo)) {
                    if(phoneNo.length()<10 || phoneNo.length()>10) {
                        Toast.makeText(getApplicationContext(),
                                "Please Enter Valid 10 Digit Mobile Number",
                                Toast.LENGTH_LONG).show();
                    } else {
                        if(pass.equals(confirmPassword)) {

                            firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    User signupUser=new User();
                                    signupUser.setUsername(name);
                                    signupUser.setMail(email);
                                    signupUser.setPhone(phoneNo);
                                    signupUser.setStatus("parents");
                                    signupUser.setMobile("android");
                                    userDatabaseReference.child(name).setValue(signupUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Account Created",
                                                    Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getBaseContext(), Login.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Error : "+e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),
                                            "Error : "+e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Confirm Password Didn't Match With Password",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please Fill All Details",
                            Toast.LENGTH_LONG).show();
                }
            break;
        }
    }
}
