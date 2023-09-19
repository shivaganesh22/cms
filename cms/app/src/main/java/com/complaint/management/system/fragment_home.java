package com.complaint.management.system;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Bundle bundle = new Bundle();


    public fragment_home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_home.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_home newInstance(String param1, String param2) {
        fragment_home fragment = new fragment_home();
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

        bundle.putInt("key", 0);
        v1.findViewById(R.id.complaintInfra).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bundle.putInt("key", 2);
                gotoComplaint();


            }
        });
       v1.findViewById(R.id.sexual).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               bundle.putInt("key",0);
               gotoComplaint();
           }
       });
        v1.findViewById(R.id.depression).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt("key",1);
                gotoComplaint();
            }
        });
        v1.findViewById(R.id.child).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt("key",3);
                gotoComplaint();
            }
        });
        v1.findViewById(R.id.teaching).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt("key",4);
                gotoComplaint();
            }
        });
        v1.findViewById(R.id.sports).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt("key",5);
                gotoComplaint();
            }
        });






        return v1;
    }

    private void gotoComplaint() {
        FragmentManager fragmentManager=getParentFragmentManager();
        complaint fragment=new complaint();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment).commit();

    }
}