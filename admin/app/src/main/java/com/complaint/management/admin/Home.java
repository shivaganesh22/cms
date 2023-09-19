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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    DatabaseReference database;
    complaintAdapter myAdapter;
    RecyclerView recyclerView;
    ArrayList<complaintsGetter> list;
    String user,childId;
    FirebaseAuth mAuth;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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
        View v1=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=v1.findViewById(R.id.userComplaints);
//        recyclerView.hasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        database= FirebaseDatabase.getInstance().getReference("Complaints");
        list=new ArrayList<>();

        myAdapter=new complaintAdapter(getContext(),list);
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

                    complaintsGetter com=dataSnapshot.getValue(complaintsGetter.class);
                    com.setKey(dataSnapshot.getKey());
                    list.add(com);

                }

                Collections.sort(list, new Comparator<complaintsGetter>() {
                    @Override
                    public int compare(complaintsGetter o1, complaintsGetter o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });

                // Reverse the order of the list
                Collections.reverse(list);

                progressDialog.dismiss();
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Inflate the layout for this fragment
        return v1;
    }
}