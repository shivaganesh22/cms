package com.complaint.management.system;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link complaint#newInstance} factory method to
 * create an instance of this fragment.
 */
public class complaint extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  FirebaseAuth mAuth;
    private FirebaseUser user;
    String mProblem,sProblem;
    EditText cname,cemail,cphone,cdescription;
    Button csubmit;
    TextView cDate;
    Spinner cMain,cSub;
    TextView x;
    String[] mainProblems={"1)Sexual harassment", "2)Depression and anxiety" ,"3)Infrastructure" ,"4)Child marriages" ,"5)Teaching and  syllabus", "6)Sports facilities"   };
    String[] Infrastructure={"3.1)Mid day meals","3.2)Facilities provided by hostels","3.3)Proper schedule","3.4)Monthly facility reviews by student","3.5)A correct guidance a basics of guidance"};
    String[] Sports={"6.1)Indoor","6.2)Outdoor"};
    public complaint() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment complaaint.
     */
    // TODO: Rename and change types and number of parameters
    public static complaint newInstance(String param1, String param2) {
        complaint fragment = new complaint();
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
        View view=inflater.inflate(R.layout.fragment_complaint, container, false);
        mProblem="";sProblem="";
        cname=view.findViewById(R.id.compaintName);
        csubmit=view.findViewById(R.id.complaintSubmit);
        cemail=view.findViewById(R.id.complaintEmail);
        cphone=view.findViewById(R.id.complaintNumber);
        cDate=view.findViewById(R.id.complaintDate);
        cdescription=view.findViewById(R.id.complaintDescription);
        cMain=view.findViewById(R.id.complaintMain);
        cSub=view.findViewById(R.id.complaintSub);


        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

// Format the date as a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(date);
        cDate.setText("Complaint Date :"+dateString);


        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,mainProblems);
        cMain.setAdapter(arrayAdapter);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int i = bundle.getInt("key");
            cMain.setSelection(i);
        }
        cMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mProblem=mainProblems[i];
//                Toast.makeText(getContext(),mProblem,Toast.LENGTH_SHORT).show();
                if (mProblem.equals(mainProblems[2])){

                    ArrayAdapter subAdapter=new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,Infrastructure);
                    cSub.setVisibility(View.VISIBLE);
                    cSub.setAdapter(subAdapter);
                    cSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            sProblem=Infrastructure[i];
//                            Toast.makeText(getContext(),mProblem,Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                else if(mProblem.equals(mainProblems[5])) {
                    ArrayAdapter subAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, Sports);
                    cSub.setVisibility(View.VISIBLE);
                    cSub.setAdapter(subAdapter);
                    cSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            sProblem=Sports[i];
//                            Toast.makeText(getContext(),mProblem,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
                else{
                    cSub.setVisibility(View.INVISIBLE);
                    sProblem="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        //data retriving
        FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Map map = (Map) snapshot.getValue();
                    if (snapshot.getKey().matches(user.getUid())) {
                        cname.setText(map.get("Username").toString());
                        cemail.setText(map.get("Email").toString());
                        try{
                           cphone.setText(map.get("Mobile").toString());
                        }
                        catch (Exception e){

                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //submit
        csubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=cname.getText().toString();
                String email=cemail.getText().toString();
                String number=cphone.getText().toString();
                String description=cdescription.getText().toString();
                if (name.isEmpty() || email.isEmpty()||number.isEmpty()||description.isEmpty()){
                    Toast.makeText(getContext(),"Fill all fields",Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("Username",name);
                    map.put("Email",email);
                    map.put("Mobile",number);
                    map.put("Main_problem",mProblem);
                    map.put("Sub_problem",sProblem);
                    map.put("Description",description);
                    map.put("Date",dateString);
                    map.put("Status","Pending");
                    map.put("Id",user.getUid());
                    FirebaseDatabase.getInstance().getReference().child("Complaints").push().setValue(map);
                    ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Sending complaint..");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 500);
                    Toast.makeText(getContext(),"Sent",Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager =getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new user_complaints());
                    fragmentTransaction.commit();


                }
            }
        });



        return view;
    }
}