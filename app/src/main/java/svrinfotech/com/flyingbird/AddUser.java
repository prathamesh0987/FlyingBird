package svrinfotech.com.flyingbird;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import svrinfotech.com.flyingbird.pojo.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddUser extends Fragment {

    @BindView(R.id.parentsName)
    EditText fullName;

    @BindView(R.id.parentsMobile)
    EditText mobile_no;

    @BindView(R.id.parentsEmail)
    EditText mail_id;

    @BindView(R.id.saveUser)
    Button save;

    Context context;

    DatabaseReference userReference;

    public AddUser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_add_user, container, false);
        ButterKnife.bind(this,rootView);
        context=getActivity().getBaseContext();
        userReference=FirebaseDatabase.getInstance().getReference().child("user");
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=fullName.getText().toString().trim();
                String mobile=mobile_no.getText().toString().trim();
                String mail=mail_id.getText().toString().trim();
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(mail)) {
                    User user=new User();
                    user.setMobile("symbian");
                    user.setMail(mail);
                    user.setPhone(mobile);
                    user.setStatus("parents");
                    user.setUsername(name);
                    userReference.child(name).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context,
                                    "User Added to the database",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(context,MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,
                                    "Error : "+e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(context,"Fill All Details",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
