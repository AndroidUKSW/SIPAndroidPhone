package pl.edu.uksw.sipandroidphone;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Sip Settings Activity
 */
public class SipSettings extends PreferenceActivity {

    /**
     * Load preferences
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
