package pl.edu.uksw.sipandroidphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.*;

public class IncomingCallReceiver extends BroadcastReceiver {
    //On receive incoming call
    @Override
    public void onReceive(Context context, Intent intent) {
        SipAudioCall incomingCall = null;
        try {

            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    try {
                        call.answerCall(30);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            SipPhoneActivity sipPhoneActivity = (SipPhoneActivity) context;

            incomingCall = sipPhoneActivity.sipManager.takeAudioCall(intent, listener);
            incomingCall.answerCall(30);
            incomingCall.startAudio();
            incomingCall.setSpeakerMode(true);
            if(!incomingCall.isMuted()) {
                incomingCall.toggleMute();
            }

            sipPhoneActivity.sipAudioCall = incomingCall;

            sipPhoneActivity.updateStatus(incomingCall);

        } catch (Exception e) {
            if (incomingCall != null) {
                incomingCall.close();
            }
        }
    }
}
