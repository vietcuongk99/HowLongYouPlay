package com.kdc.howlongyouplay.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class BackLogFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private ArrayList<Record> backlogList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_back_log, container, false);

        recyclerView = view.findViewById(R.id.recycle_view_backlog);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        backlogList = new ArrayList<>();

        getBackLogList();

        return view;
    }

    // lấy ra danh sách gamelog để hiển thị
    private void getBackLogList() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = firebaseUser.getUid();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Logs").child(user_id).child("backlog");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                backlogList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Record record = snapshot.getValue(Record.class);
                    record.setRecord_id(snapshot.getKey());
                    backlogList.add(record);

                }

                recordAdapter = new RecordAdapter(getContext(),  backlogList);
                recyclerView.setAdapter(recordAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
