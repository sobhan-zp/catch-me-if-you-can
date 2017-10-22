// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

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

import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Server.Result;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * OptionsFragment.java
 * Displays game options and the list of participants
 */
public class OptionsFragment extends Fragment {

    private Activity parent;

    private ArrayAdapter<String> adapter;
    private List<String> array;

    /**
     * Called once the fragment is associated with its activity
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            parent = (Activity) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tells the fragment that its activity has completed its own Activity.onCreate()
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtains list of users
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.GAME_GET_USER));
        } catch(Exception e) {
            e.printStackTrace();
        }
        ((FragmentCommunication) parent).onSend(obj);
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_options, container, false);
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned,
     * but before any saved state has been restored in to the view
     * @param view
     * @param savedInstanceState
     */
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
        array = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                parent,
                R.layout.list_one_item_friends,
                array
        );
        listViewPlayers.setAdapter(adapter);
    }

    /**
     * Method invoked when the WebSocketClient receives a message
     * @param message
     */
    public void onResponse(final Message message) {
        // Repopulates list
        Result[] results = message.getResult();
        array.clear();
        for (Result result : results) {
            array.add(
                    "Account ID: " + result.getAccount_id()
                    + " (owner: " + result.getIs_owner() + ")"
            );
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Callback interface for communicating with parent activity
     */
    public interface FragmentCommunication {

        /**
         * User exits the game
         */
        void onExit();

        /**
         * Sends a message to the server
         * @param obj
         */
        void onSend(JSONObject obj);

    }

}
