package svrinfotech.com.flyingbird;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.username)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.login)
    Button login;

    @BindView(R.id.signup)
    Button signup;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        firebaseAuth=FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getBaseContext(),MainActivity.class));
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        String user=username.getText().toString().trim();
        String pass=password.getText().toString().trim();
        switch (v.getId()) {
            case R.id.login:
                if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
                    startLogin(user,pass);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please Fill All Details",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.signup:
                startActivity(new Intent(this,Signup.class));
                finish();
                break;
        }
    }

    private void startLogin(String emailValue,String passwordValue) {
        if(TextUtils.isEmpty(emailValue) || TextUtils.isEmpty(passwordValue)) {
            Toast.makeText(getApplicationContext(),"Please fill all details", Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Login Issue", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
