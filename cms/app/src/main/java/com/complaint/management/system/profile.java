package com.complaint.management.system;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView pun;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView run,remail,save,pemail;
    TextView logout;
    private EditText pname,paddr,pDOB,pno;
    String chat;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ValueEventListener userValueEventListener;

    public profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile.
     */
    // TODO: Rename and change types and number of parameters
    public static profile newInstance(String param1, String param2) {
        profile fragment = new profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        if(user==null){
            getActivity().getFragmentManager().popBackStack();
            startActivity(new Intent(getContext(),login.class));


        }
        run = view.findViewById(R.id.username);
        remail=view.findViewById(R.id.useremail);
        pname=view.findViewById(R.id.UserName);
        pemail=view.findViewById(R.id.userEmail);
        paddr=view.findViewById(R.id.userAddress);
        pno=view.findViewById(R.id.userMobile);
        pDOB=view.findViewById(R.id.userDOB);
        save=view.findViewById(R.id.saveProfile);
        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminGetter userdata = dataSnapshot.getValue(adminGetter.class);

                run.setText(userdata.getUsername());
                pname.setText(userdata.getUsername());
                try{

                    pno.setText(userdata.getMobile());

                }
                catch (Exception e){

                }
                try{
                    paddr.setText(userdata.getAddress());

                }
                catch (Exception e){

                }
                try{
                    pDOB.setText(userdata.getDOB());
                }
                catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(userValueEventListener);






        remail.setText(user.getEmail());
        pemail.setText(user.getEmail());
        logout=view.findViewById(R.id.Signout);
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        view.findViewById(R.id.changePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Sending recovery mail...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 1000);
                mAuth.sendPasswordResetEmail(user.getEmail());
                Toast.makeText(getContext(),"Recovery mail sent",Toast.LENGTH_SHORT).show();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(getContext(),"Logout successful",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), login.class));
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = user.getEmail();
                String address = paddr.getText().toString();
                String mobile = pno.getText().toString();
                String name = pname.getText().toString();
                String DOB = pDOB.getText().toString();
                if (address.isEmpty() || mobile.isEmpty() || name.isEmpty() || DOB.isEmpty()) {
                    Toast.makeText(getContext(), "Fill all details", Toast.LENGTH_SHORT).show();
                }
                else if (mobile.length()!=10){
                    Toast.makeText(getContext(), "Enter valid number", Toast.LENGTH_SHORT).show();
                }
                else {

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Username", name.toUpperCase());
                    map.put("Mobile", mobile);
                    map.put("Address", address);
                    map.put("DOB", DOB);


                    FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).updateChildren(map);


                    progressDialog.setMessage("Saving...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);

                    Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();

                }
            }
        });



        return view;
    }


}