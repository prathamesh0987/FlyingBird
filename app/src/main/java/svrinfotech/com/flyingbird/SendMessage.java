package svrinfotech.com.flyingbird;


import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import svrinfotech.com.flyingbird.pojo.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendMessage extends Fragment {

    /*@BindView(R.id.contacts)
    MultiAutoCompleteTextView contacts;*/

    @BindView(R.id.message)
    EditText message_body;

    @BindView(R.id.send)
    Button send;

    DatabaseReference userReference= FirebaseDatabase.getInstance().getReference().child("user");
    DatabaseReference messageReference=FirebaseDatabase.getInstance().getReference().child("message");

    Context context;

//    ArrayAdapter<String> userAdapter;


    public SendMessage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_send_message, container, false);
        ButterKnife.bind(this,rootView);
        context=getActivity().getBaseContext();
        /*final ArrayList<String> userList=new ArrayList<>();
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot userData=iterator.next();
                    User user=userData.getValue(User.class);
                    Toast.makeText(context,"Name : "+user.getUsername(),Toast.LENGTH_LONG).show();
                    userList.add(user.getUsername());
                }
                userAdapter=new ArrayAdapter<>(context,android.R.layout.simple_dropdown_item_1line,userList);
                contacts.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                contacts.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,
                        "Error : "+databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });*/

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String msg=message_body.getText().toString();
                //final String contact_no=contacts.getText().toString();
                //String[] users=contact_no.split(",");
                userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                            while (iterator.hasNext()) {
                                DataSnapshot userData=iterator.next();
                                User user=userData.getValue(User.class);
                                if(user.getMobile().equals("symbian")) {
                                    SmsManager smsManager=SmsManager.getDefault();
                                    String phn_no=user.getPhone();
                                    smsManager.sendTextMessage(phn_no,null,msg,null,null);
                                } else {
                                    messageReference.child(user.getUsername()).setValue(msg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context,
                                                    "Message Sent",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
        });
    }
}