package com.example.callblockingbrodcastreciever;

public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}