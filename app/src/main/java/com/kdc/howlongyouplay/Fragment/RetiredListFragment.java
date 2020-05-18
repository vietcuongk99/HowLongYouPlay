package com.kdc.howlongyouplay.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Adapter.RecordAdapter;
import com.kdc.howlongyouplay.R;
import com.kdc.howlongyouplay.Record;

import java.util.ArrayList;

public class RetiredListFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private ArrayList<Record> retiredList;
    private RelativeLayout loading_state_view;
    private RelativeLayout empty_state_view;
    private RelativeLayout error_state_view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_retired_list, container, false);

        recyclerView = view.findViewById(R.id.recycle_view);
        loading_state_view = view.findViewById(R.id.loading);
        empty_state_view = view.findViewById(R.id.empty_list);
        error_state_view = view.findViewById(R.id.error);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        retiredList = new ArrayList<>();

        getRetiredList();

        /*
        if (isNetworkConnected()) {
            getRetiredList();
        }
        else {
            loading_state_view.setVisibility(View.GONE);
            empty_state_view.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            error_state_view.setVisibility(View.VISIBLE);
        }

         */


        return view;
    }

    private void getRetiredList() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Logs").child(user_id).child("retired");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                retiredList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Record record = snapshot.getValue(Record.class);
                    record.setRecord_id(snapshot.getKey());
                    retiredList.add(record);

                }

                if (retiredList.size() != 0) {
                    loading_state_view.setVisibility(View.GONE);
                    recordAdapter = new RecordAdapter(getContext(),  retiredList);
                    recyclerView.setAdapter(recordAdapter);

                }
                else {
                    loading_state_view.setVisibility(View.GONE);
                    empty_state_view.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private boolean isNetworkConnected() {
        boolean connected = false;
        if (getActivity() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }

        return connected;
    }
}
