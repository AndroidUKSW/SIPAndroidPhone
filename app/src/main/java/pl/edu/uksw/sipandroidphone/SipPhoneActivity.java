package pl.edu.uksw.sipandroidphone;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ToggleButton;

import java.text.ParseException;


public class SipPhoneActivity extends Activity implements View.OnTouchListener {

    public SipManager sipManager = null;
    public SipProfile sipProfile = null;

    //On create activity
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone);

        create();
    }

    //On start activity
    @Override
    public void onStart() {
        super.onStart();

        initializeManager();
    }

    public void create(){
        ToggleButton pushToTalkButton = (ToggleButton) findViewById(R.id.pushToTalk);
        pushToTalkButton.setOnTouchListener(this);

        initializeManager();
    }

    //New instance of sip manager
    public void initializeManager() {
        if(sipManager == null) {
            sipManager = SipManager.newInstance(this);
        }

        initializeLocalProfile();
    }

    public void initializeLocalProfile() {
        if (sipManager == null) {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String username = prefs.getString("namePref", "");
        String domain = prefs.getString("domainPref", "");
        String password = prefs.getString("passPref", "");
        String authname = prefs.getString("authnamePref", "");
        String proxy = prefs.getString("proxyPref", "");


        if (username.length() == 0 || domain.length() == 0 || password.length() == 0) {
            return;
        }

        try {
            SipProfile.Builder builder = new SipProfile.Builder(username, domain);
            builder.setPassword(password);
            if(!authname.isEmpty()) {
                builder.setAuthUserName(authname);
            }
            if(!proxy.isEmpty()) {
                builder.setOutboundProxy(proxy);
            }

            sipProfile = builder.build();

            Intent i = new Intent();
            i.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
            sipManager.open(sipProfile, pi, null);
        } catch (ParseException pe) {

        } catch (SipException se) {

        }
    }

    //On destroy activity
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}