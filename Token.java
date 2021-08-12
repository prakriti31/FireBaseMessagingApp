package com.codewithharry.firebase.notifications;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessagingService;

public class Token {
    /*An FCM token, or much commonly known as a RegistrationToken.
    * An ID issued by the GSM connection serves to the client app that allows it to
    * receiver messages */

    String token;

    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public Token(Object tokenRefresh) {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
