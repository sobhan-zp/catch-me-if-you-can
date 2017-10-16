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

import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Server.Result;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

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
        //array.add(Integer.toString(message.getCode()));

        // TESTING
        Result[] results = message.getResult();
        for (Result result : results) {
            array.add(Double.toString(result.getX()) + ", " + Double.toString(result.getY()));
        }
        adapter.notifyDataSetChanged();
    }

    private void updateLocation() {
        // Queries server for location updates
        JSONObject obj = new JSONObject();
        try {
            //obj.put("action", MESSAGE_COMMAND_SEND);
            //obj.put("message", "test");
            // TESTING
            obj.put("action", getResources().getInteger(R.integer.LOCATION_GET));
        } catch(Exception e) {
            e.printStackTrace();
        }
        ((FragmentCommunication) parent).onSend(obj);
    }

    public interface FragmentCommunication {

        public void onSend(JSONObject obj);

    }

}
