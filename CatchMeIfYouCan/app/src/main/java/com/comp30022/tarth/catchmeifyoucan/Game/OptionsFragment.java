package com.comp30022.tarth.catchmeifyoucan.Game;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OptionsFragment extends Fragment {

    private static final Integer GAME_GET_USER = 723;
    private static final Integer GAME_GET_USER_SUCCESS = 724;
    private static final Integer GAME_GET_USER_FAIL = 725;

    private Activity parent;

    private ArrayAdapter<String> adapter;
    private List<String> array;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            parent = (Activity) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtains list of users
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", GAME_GET_USER);
        } catch(Exception e) {
            e.printStackTrace();
        }
        ((FragmentCommunication) parent).onSend(obj);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_options, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button exitButton = (Button) view.findViewById(R.id.buttonExit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((FragmentCommunication) parent).onExit();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });

        final ListView listViewPlayers = (ListView) view.findViewById(R.id.listViewPlayers);
        array = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(
                parent,
                R.layout.list_one_item,
                array
        );
        listViewPlayers.setAdapter(adapter);
    }

    public void onResponse(final Message message) {
        // Repopulates friendlist
        Participant[] participants = message.getResult();
        array.clear();
        for (Participant participant : participants) {
            array.add(participant.toString());
        }
        adapter.notifyDataSetChanged();
    }

    public interface FragmentCommunication {

        public void onExit();

        public void onSend(JSONObject obj);

    }

}
