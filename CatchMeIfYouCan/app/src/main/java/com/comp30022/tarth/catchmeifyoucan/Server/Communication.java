// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.Server;

/**
 * Communication.java
 * Interface used by activities communicating with the WebSocketClient
 */
public interface Communication {

    /**
     * Method invoked when the WebSocketClient receives a message
     * @param message : Message received from server
     */
    void onResponse(final Message message);

}

