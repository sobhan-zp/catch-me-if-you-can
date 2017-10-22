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
import android.widget.ListView;

import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Server.Result;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * OptionsFragment.java
 * Enables target to send messages to all searchers in the game
 */
public class ChatFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_chat, container, false);
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

        final ListView listViewMessages = (ListView) view.findViewById(R.id.list_of_messages);
        array = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                parent,
                R.layout.list_one_item,
                array
        );
        listViewMessages.setAdapter(adapter);
    }

    /**
     * Method invoked when the WebSocketClient receives a message
     * @param message
     */
    public void onResponse(final Message message) {
        Result[] results = message.getResult();
        for (Result result : results) {
            array.add(Double.toString(result.getX()) + ", " + Double.toString(result.getY()));
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Callback interface for communicating with parent activity
     */
    public interface FragmentCommunication {

        /**
         * Sends a message to the server
         * @param obj
         */
        void onSend(JSONObject obj);

    }

}
