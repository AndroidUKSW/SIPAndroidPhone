package pl.edu.uksw.sipandroidphone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.net.sip.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.ParseException;

public class SipPhoneActivity extends Activity implements View.OnTouchListener {

    public String address = null; //sip address
    public SipManager sipManager = null;
    public SipProfile sipProfile = null;
    public SipAudioCall sipAudioCall = null;
    private static final int CALL = 1; //call button id
    private static final int SET_AUTH_INFO = 2; //set auth data button id
    private static final int SETTINGS = 3; //set settings button id
    private static final int END_CALL = 4; //end call button id

    //Load layout
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone);

        create();
    }

    //On push to talk button initialize
    //Register on incoming call receiver
    //Keep screen on
    //Initialize manager
    public void create(){
        ToggleButton pushToTalkButton = (ToggleButton) findViewById(R.id.pushToTalk);
        pushToTalkButton.setOnTouchListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.SipDemo.INCOMING_CALL");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initializeManager();
    }

    //On start activity
    @Override
    public void onStart() {
        super.onStart();
        initializeManager();
    }

    //On destroy activity
    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy();
    }

    //Stop call
    //unregister incoming call
    public void destroy(){
        if (sipAudioCall != null) {
            sipAudioCall.close();
        }

        closeLocalProfile();
    }

    //New instance of sip manager
    public void initializeManager() {
        if(sipManager == null) {
            sipManager = SipManager.newInstance(this);
        }

        initializeLocalProfile();
    }


    //Register into SIP provider
    //Register device as the location to send SIP calls to for your SIP address
    public void initializeLocalProfile() {
        if (sipManager == null) {
            return;
        }

        if (sipProfile != null) {
            closeLocalProfile();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String username = prefs.getString("namePref", "");
        String domain = prefs.getString("domainPref", "");
        String password = prefs.getString("passPref", "");
        String authname = prefs.getString("authnamePref", "");
        String proxy = prefs.getString("proxyPref", "");


        if (username.length() == 0 || domain.length() == 0 || password.length() == 0) {
            showDialog(SETTINGS);
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

            sipManager.setRegistrationListener(sipProfile.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
                    updateStatus("Registering with SIP Server...");
                }

                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    updateStatus("Ready");
                }

                public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                 String errorMessage) {
                    updateStatus("Registration failed.  Please check settings.");
                }
            });
        } catch (ParseException pe) {
            updateStatus("Connection Error.");
        } catch (SipException se) {
            updateStatus("Connection error.");
        }
    }

    //Closes out your local profile
    //Freeing associated objects into memory and unregistering your device from the server.
    public void closeLocalProfile() {
        if (sipManager == null) {
            return;
        }
        try {
            if (sipProfile != null) {
                sipManager.close(sipProfile.getUriString());
            }
        } catch (Exception ee) {
        }
    }

    //Make an outgoing sipAudioCall
    public void initiateCall() {

        updateStatus(address);

        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing sipAudioCall, don't
                // forget to set up a listener to set things up once the sipAudioCall is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    call.startAudio();
                    call.setSpeakerMode(true);
                    if(!call.isMuted()) {
                        call.toggleMute();
                    }
                    updateStatus(call);
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    updateStatus("Ready.");
                }
            };

            sipAudioCall = sipManager.makeAudioCall(sipProfile.getUriString(), address, listener, 30);

        }
        catch (Exception e) {
            if (sipProfile != null) {
                try {
                    sipManager.close(sipProfile.getUriString());
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            if (sipAudioCall != null) {
                sipAudioCall.close();
            }
        }
    }

    //Updates the status box at the top of the UI with a messege of your choice.
    public void updateStatus(final String status) {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
        this.runOnUiThread(new Runnable() {
            public void run() {
                TextView labelView = (TextView) findViewById(R.id.sipLabel);
                labelView.setText(status);
            }
        });
    }

    //Updates the status box with the SIP address of the current sipAudioCall.
    public void updateStatus(SipAudioCall call) {
        String useName = call.getPeerProfile().getDisplayName();
        if(useName == null) {
            useName = call.getPeerProfile().getUserName();
        }
        updateStatus(useName + "@" + call.getPeerProfile().getSipDomain());
    }

    //Updates whether or not the user's voice is muted, depending on whether the button is pressed.
    public boolean onTouch(View v, MotionEvent event) {
        if (sipAudioCall == null) {
            return false;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN && sipAudioCall != null && sipAudioCall.isMuted()) {
            sipAudioCall.toggleMute();
        } else if (event.getAction() == MotionEvent.ACTION_UP && !sipAudioCall.isMuted()) {
            sipAudioCall.toggleMute();
        }
        return false;
    }

    //On create menu
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CALL, 0, "Call someone");
        menu.add(0, SET_AUTH_INFO, 0, "Edit your SIP Info.");
        menu.add(0, END_CALL, 0, "End Current Call.");

        return true;
    }

    //On menu option selected
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CALL:
                showDialog(CALL);
                break;
            case SET_AUTH_INFO:
                updatePreferences();
                break;
            case END_CALL:
                if(sipAudioCall != null) {
                    try {
                        sipAudioCall.endCall();
                        destroy();
                        create();
                    } catch (SipException se) {
                        Log.d("WTA",
                                "Error ending sipAudioCall.", se);
                    }
                    sipAudioCall.close();
                }
                break;
        }
        return true;
    }

    //On create dialog
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CALL:

                LayoutInflater factory = LayoutInflater.from(this);
                final View textBoxView = factory.inflate(R.layout.address, null);
                return new AlertDialog.Builder(this)
                        .setTitle("Call Someone.")
                        .setView(textBoxView)
                        .setPositiveButton(
                                android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        EditText textField = (EditText)
                                                (textBoxView.findViewById(R.id.calladdress_edit));
                                        address = textField.getText().toString();
                                        initiateCall();

                                    }
                                })
                        .setNegativeButton(
                                android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {}
                                })
                        .create();

            case SETTINGS:
                return new AlertDialog.Builder(this)
                        .setMessage("Please update your SIP Account Settings.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                updatePreferences();
                            }
                        })
                        .setNegativeButton(
                                android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {}
                                })
                        .create();
        }
        return null;
    }

    //Load preferences activity
    public void updatePreferences() {
        Intent settingsActivity = new Intent(getBaseContext(),
                SipSettings.class);
        startActivity(settingsActivity);
    }
}
