package com.webbrowsersockettest;


import android.app.Activity;
import android.widget.Toast;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;

public class TestStompSessionHandler implements StompSessionHandler {

    private final Activity activity;

    public TestStompSessionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        activity.runOnUiThread(() -> Toast.makeText(activity, payload.toString(), Toast.LENGTH_LONG).show());
        System.out.println("WORKING");
    }


    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

        session.send("search","{\"uuid\":\"62fb14a4-8f6a-11e8-9eb6-529269fb1459\",\"query\":\"?region=52612&currency=USD&startDate=16/09/2018&endDate=21/09/2018&rooms=%5B%7B%22adults%22:2,%22children%22:%5B%5D%7D%5D\"}");

        session.subscribe("search/62fb14a4-8f6a-11e8-9eb6-529269fb1459", this);

    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();

    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        exception.printStackTrace();

    }


}
