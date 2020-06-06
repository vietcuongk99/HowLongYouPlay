package com.kdc.howlongyouplay.Fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.kdc.howlongyouplay.GameRecord;
import com.kdc.howlongyouplay.R;

import java.util.ArrayList;

public class FinishedListFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private ArrayList<GameRecord> finishedList;
    private RelativeLayout loading_state_view;
    private RelativeLayout empty_state_view;
    private RelativeLayout error_state_view;
    private Resources resource;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finished_list, container, false);

        recyclerView = view.findViewById(R.id.recycle_view);
        loading_state_view = view.findViewById(R.id.loading);
        empty_state_view = view.findViewById(R.id.empty_list);
        error_state_view = view.findViewById(R.id.error);
        recyclerView.setHasFixedSize(true);

        resource = recyclerView.getResources();

        if (resource.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        finishedList = new ArrayList<>();
        getFinishedList();

        return view;
    }

    // lấy ra danh sách gamelog để hiển thị
    private void getFinishedList() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Logs").child(user_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                finishedList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if(snapshot.hasChild("records")) {
                        for (DataSnapshot snapshot1 : snapshot.child("records").getChildren()) {
                            GameRecord gameRecord = snapshot1.getValue(GameRecord.class);
                            gameRecord.setGame_id(snapshot.getKey());
                            if (gameRecord.getStatus().contains("finished")) {
                                gameRecord.setRecord_id(snapshot1.getKey());
                                finishedList.add(gameRecord);
                            }
                        }
                    }

                }

                if (finishedList.size() != 0) {
                    loading_state_view.setVisibility(View.GONE);
                    recordAdapter = new RecordAdapter(getContext(),  finishedList);
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
