package com.example.teambell_3;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.security.acl.Group;
import java.util.ArrayList;


public class Group_Fragment extends Fragment {

    EditText search;

    ArrayList<GroupData> groups;
    GroupAdapter adapter;
    ListView listview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.group_fragment, container, false);

        search = v.findViewById(R.id.group_search);

        groups = new ArrayList<>();
        groups.add(new GroupData("금천라이딩", 5, "금천구 독산1동-하남시 하남2동", "30km", "홍아무개", "2020-10-08 17:43"));

        listview = (ListView) v.findViewById(R.id.group_listview);
        adapter = new GroupAdapter(getContext(), groups);
        listview.setAdapter(adapter);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((MainActivity) activity).setActionBarTitle(R.string.title_group);
        }
    }
}