package com.Zakovskiy.lwaf;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.Zakovskiy.lwaf.api.VKApi;
import com.Zakovskiy.lwaf.models.ApiResponse;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DialogVKLogIn extends Dialog {

    private final Integer action;
    private Context context;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    public DialogVKLogIn(Context context, Integer action) {
        super(context);
        this.context = context;
        this.action = action;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_vk_login);
        setCancelable(true);
        TextInputLayout phoneLayout = findViewById(R.id.editPhone);
        TextInputLayout passwordLayout = findViewById(R.id.editPassword);
        findViewById(R.id.button_submit).setOnClickListener(view -> {
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://oauth.vk.com/").build();
            VKApi vkApi = retrofit.create(VKApi.class);
            vkApi.logIn(phoneLayout.getEditText().getText().toString(), passwordLayout.getEditText().getText().toString(), "", "").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Logs.debug(response.message());
                        int code = response.code();
                        if (response.body() != null && code == 200) {
                            JSONObject json = new JSONObject(response.body().string());
                            String accessToken = json.optString("access_token", "");
                            String secret = json.optString("secret", "");
                            HashMap<String, Object> data = new HashMap<>();
                            if(action == 0) {
                                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ACCOUNT_SIGN_VK);
                                data.put(PacketDataKeys.DEVICE, Config.getDeviceID(DialogVKLogIn.this.context));
                            } else if(action == 1) {
                                data.put(PacketDataKeys.VK_SECRET, secret);
                                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.VK_TOKEN_CHANGE);
                            }
                            data.put(PacketDataKeys.VK_TOKEN, accessToken);
                            socketHelper.sendData(new JSONObject(data));
                        } else if (code == 401) {
                            DialogVKLogIn.this.dismiss();
                            new DialogTextBox(DialogVKLogIn.this.context, context.getString(R.string.invalid_data)).show();
                            return;
                        }
                    } catch (NullPointerException | IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    DialogVKLogIn.this.dismiss();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Logs.error(t.getMessage());
                    t.printStackTrace();
                    DialogVKLogIn.this.dismiss();
                }
            });
            DialogVKLogIn.this.dismiss();
        });
    }
}