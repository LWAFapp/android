package com.Zakovskiy.lwaf;

import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.AppCompatButton;

import com.Zakovskiy.lwaf.authorization.LoginActivity;
import com.Zakovskiy.lwaf.utils.Logs;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiConfig;
import com.vk.api.sdk.auth.VKAuthenticationResult;
import com.vk.api.sdk.auth.VKScope;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends ABCActivity implements View.OnClickListener {

    private ActivityResultLauncher authLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ((AppCompatButton)findViewById(R.id.buttonLogInVK)).setOnClickListener(this);
        this.authLauncher = VK.login(this, new ActivityResultCallback<VKAuthenticationResult>() {
            @Override
            public void onActivityResult(VKAuthenticationResult result) {
                if (result instanceof VKAuthenticationResult.Success) {
                    Logs.debug(((VKAuthenticationResult.Success) result).getToken().getAccessToken());
                }
                if (result instanceof VKAuthenticationResult.Failed) {
                    Logs.debug(((VKAuthenticationResult.Failed) result).getException().getMessage());
                }
            }
        });

    }

    public void onLogin(View v) {
        newActivity(LoginActivity.class);
    }

    public void onRegistration(View v) {
        newActivity(LoginActivity.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonLogInVK) {
            List<VKScope> scopes = new ArrayList<VKScope>();
            scopes.add(VKScope.AUDIO);
            scopes.add(VKScope.OFFLINE);
            scopes.add(VKScope.STATS);
            this.authLauncher.launch(scopes);
        }
    }
}
