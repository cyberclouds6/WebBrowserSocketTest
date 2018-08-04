package com.webbrowsersockettest;

import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.glassfish.tyrus.client.ClientManager;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class ToastModule extends ReactContextBaseJavaModule {

    private static final String DURATION_SHORT_KEY = "SHORT";
    private static final String DURATION_LONG_KEY = "LONG";
    static ReactContext reactContext;
    int count = 1;

    public ToastModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "ToastExample";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
        constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
        return constants;
    }

    @ReactMethod
    public void showToast(String message, int duration){
        Toast.makeText(getReactApplicationContext(), message, duration).show();
    }

    @ReactMethod
    public void startStompThread(){
        //new Thread(() -> this.startSession()).start();
    }

    @ReactMethod
    public void startSession() {
        ClientManager client = ClientManager.createClient();

        WebSocketClient transport = new StandardWebSocketClient(client);
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        StringMessageConverter converter = new StringMessageConverter();
        stompClient.setMessageConverter(converter);

        StompSessionHandler handler = new TestStompSessionHandler(getCurrentActivity());
        String url = "wss://alpha.locktrip.com/socket";

//        StompHeaders stompHeaders = new StompHeaders();
//        stompClient.connect(url, new WebSocketHttpHeaders(), stompHeaders, handler);

        stompClient.connect(url, new StompSessionHandler() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.send("search","{\"uuid\":\"62fb14a4-8f6a-11e8-9eb6-529269fb1459\",\"query\":\"?region=52612&currency=USD&startDate=03/08/2018&endDate=04/08/2018&rooms=%5B%7B%22adults%22:2,%22children%22:%5B%5D%7D%5D\"}");

                session.subscribe("search/62fb14a4-8f6a-11e8-9eb6-529269fb1459", this);
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {

            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                //error.invoke("went wrong");
            }

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("WORKING" + count);
                WritableMap event = Arguments.createMap();
                event.putString("message","hello");
                emitDeviceEvent("EVET_E", event);
                count ++;
            }
        });
    }

    private static void emitDeviceEvent(String eventName, @Nullable WritableMap eventData){
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, eventData);
    }
}
