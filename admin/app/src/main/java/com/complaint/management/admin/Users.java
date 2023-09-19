package com.complaint.management.admin;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Users#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Users extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DatabaseReference database;
    userAdapter myAdapter;
    RecyclerView recyclerView;
    ArrayList<userGetter> list;
    String user,childId="";
    FirebaseAuth mAuth;

    public Users() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Users.
     */
    // TODO: Rename and change types and number of parameters
    public static Users newInstance(String param1, String param2) {
        Users fragment = new Users();
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
        View v1=inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView=v1.findViewById(R.id.allUsers);
//        recyclerView.hasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        database= FirebaseDatabase.getInstance().getReference("Users");
        list=new ArrayList<>();
        childId= "";
        myAdapter=new userAdapter(getContext(),list);
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

                        userGetter com=dataSnapshot.getValue(userGetter.class);
                        com.setKey(dataSnapshot.getKey());
                        if(com.getRole().equalsIgnoreCase("user"))
                        list.add(com);



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