package com.complaint.management.system;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_chat extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String Chat="";
    DatabaseReference database;
    adminAdapter myAdapter;
    RecyclerView recyclerView;
    ArrayList<adminGetter> list;
    String user;
    FirebaseAuth mAuth;
    private ValueEventListener senderListener;

    public fragment_chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_chat.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_chat newInstance(String param1, String param2) {
        fragment_chat fragment = new fragment_chat();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v1=inflater.inflate(R.layout.fragment_chat, container, false);
        senderListener = new ValueEventListener() {
            private boolean chatDisabled=false;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminGetter senderdata = dataSnapshot.getValue(adminGetter.class);


                if (senderdata != null) {

                    if(senderdata.getChat().equalsIgnoreCase("false")&& !chatDisabled ){
                        Toast.makeText(getContext(),"Chat is disabled",Toast.LENGTH_SHORT).show();
                        chatDisabled = true;
                        startActivity(new Intent(getContext(),MainActivity.class));

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(senderListener);

        recyclerView=v1.findViewById(R.id.allUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        database= FirebaseDatabase.getInstance().getReference("Users");
        list=new ArrayList<>();
        myAdapter=new adminAdapter(getContext(),list);
        recyclerView.setAdapter(myAdapter);
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                    adminGetter com=dataSnapshot.getValue(adminGetter.class);
                    com.setKey(dataSnapshot.getKey());

                    if (com.getRole().equalsIgnoreCase("admin")&&com.getState().equalsIgnoreCase("true")&& !dataSnapshot.getKey().equalsIgnoreCase(FirebaseAuth.getInstance().getUid())){
                        list.add(com);


                    }
                }
                myAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });









        // Inflate the layout for this fragment
        return v1;
    }
}