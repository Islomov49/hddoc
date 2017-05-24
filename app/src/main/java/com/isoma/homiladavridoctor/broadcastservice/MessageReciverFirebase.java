package com.isoma.homiladavridoctor.broadcastservice;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.TempRoomAndPushedValue;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by developer on 27.04.2017.
 */

public class MessageReciverFirebase extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {




            switch (remoteMessage.getNotification().getTag()) {
                case "newMessage":
                    EventBus.getDefault().post(new EventMessage(new TempRoomAndPushedValue(remoteMessage.getNotification().getTitleLocalizationKey(),remoteMessage.getNotification().getBodyLocalizationKey()),"newMessage","all"));
                    break;
                case "newAnswer":
                    EventBus.getDefault().post(new EventMessage(null,"newAnswer","all"));
                    break;
                case "trueAnswer":
                    EventBus.getDefault().post(new EventMessage(null,"trueAnswer","all"));
                    break;


        }

    }


    /**

     * Handle time allotted to BroadcastReceivers.

     */


}
