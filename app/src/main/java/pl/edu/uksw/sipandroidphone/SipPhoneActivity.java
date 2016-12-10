package pl.edu.uksw.sipandroidphone;

import android.app.Activity;
import android.os.Bundle;


public class SipPhoneActivity extends Activity {
    //On create activity
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone);
    }

    //On start activity
    @Override
    public void onStart() {
        super.onStart();
    }

    //On destroy activity
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
