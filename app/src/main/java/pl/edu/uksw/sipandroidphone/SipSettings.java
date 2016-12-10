package pl.edu.uksw.sipandroidphone;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SipSettings extends PreferenceActivity {

    //Load preferences
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
