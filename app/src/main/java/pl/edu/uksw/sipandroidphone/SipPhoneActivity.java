package pl.edu.uksw.sipandroidphone;

import android.app.Activity;
import android.net.sip.SipManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ToggleButton;


public class SipPhoneActivity extends Activity implements View.OnTouchListener {

    public SipManager sipManager = null;

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