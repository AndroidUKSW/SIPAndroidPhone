package pl.edu.uksw.sipandroidphone;


import android.text.TextUtils;

import java.util.regex.Pattern;

public class SIPAddressValidator {
    public static final Pattern SIP_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static boolean isValidSipAddress(String sipAddress){
        return SIP_ADDRESS_PATTERN.matcher(sipAddress).matches();
    }
}
