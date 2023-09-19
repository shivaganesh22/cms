package com.complaint.management.system;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link user_complaints#newInstance} factory method to
 * create an instance of this fragment.
 */
public class user_complaints extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    DatabaseReference database;
    MyAdapter myAdapter;
    RecyclerView recyclerView;
    ArrayList<complaint_class> list;
    String user;
    FirebaseAuth mAuth;
    SearchView searchView;

    public user_complaints() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment user_complaints.
     */
    // TODO: Rename and change types and number of parameters
    public static user_complaints newInstance(String param1, String param2) {
        user_complaints fragment = new user_complaints();
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
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v1=inflater.inflate(R.layout.fragment_user_complaints, container, false);
        recyclerView=v1.findViewById(R.id.userComplaints);
//        recyclerView.hasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        database= FirebaseDatabase.getInstance().getReference("Complaints");
        list=new ArrayList<>();
        myAdapter=new MyAdapter(getContext(),list);
        recyclerView.setAdapter(myAdapter);

        //database
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    complaint_class com=dataSnapshot.getValue(complaint_class.class);
                    com.setKey(dataSnapshot.getKey());
                    if(com.getId().equalsIgnoreCase(FirebaseAuth.getInstance().getUid()))
                    list.add(com);
                }

                Collections.sort(list, new Comparator<complaint_class>() {
                    @Override
                    public int compare(complaint_class o1, complaint_class o2) {
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

        //search
        searchView=v1.findViewById(R.id.searchView);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterList(newText);
                return true;
            }
        });




        return v1;
    }

    private void filterList(String text) {
        ArrayList<complaint_class> filtered =new ArrayList<>();
        for(complaint_class item:list){
            if(item.getDate().toLowerCase().contains(text.toLowerCase())){
                filtered.add(item);
            }
        }

        myAdapter.setFilteredList(filtered);


    }
}