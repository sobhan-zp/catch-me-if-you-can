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
import android.widget.ListView;

import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private static final Integer GAME_NOTIFICATION_SEND = 721;
    private static final Integer GAME_NOTIFICATION_RECEIVE = 722;

    private static final Integer MESSAGE_COMMAND_SEND = 606;
    private static final Integer MESSAGE_COMMAND_SUCCESS = 607;
    private static final Integer MESSAGE_COMMAND_FAIL = 608;
    private static final Integer MESSAGE_COMMAND_RECEIVE = 609;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listViewMessages = (ListView) view.findViewById(R.id.list_of_messages);
        array = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(
                parent,
                R.layout.list_one_item,
                array
        );
        listViewMessages.setAdapter(adapter);

        updateLocation();
    }

    public void onResponse(final Message message) {
        // Repopulates list
        array.add(Integer.toString(message.getCode()));
        adapter.notifyDataSetChanged();
    }

    private void updateLocation() {
        // Queries server for location updates
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", MESSAGE_COMMAND_SEND);
            obj.put("message", "test");
        } catch(Exception e) {
            e.printStackTrace();
        }
        ((FragmentCommunication) parent).onSend(obj);
    }

    public interface FragmentCommunication {

        public void onSend(JSONObject obj);

    }

}
