package com.Zakovskiy.lwaf.authorization;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.MD5Hash;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.HashMap;
public class RegisterNicknamePasswordFragment extends Fragment implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private EditText loginEdit;
    private EditText passwordEdit;
    private NavController navController;
    private FragmentActivity mActivity;

    public RegisterNicknamePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View binding = inflater.inflate(R.layout.fragment_reg_nickname_password, container, false);
        //this.socketHelper.subscribe(this);
        this.loginEdit = binding.findViewById(R.id.editTextLogin);
        this.passwordEdit = binding.findViewById(R.id.editTextPassword);
        binding.findViewById(R.id.textAlreadyHaveAccount).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        binding.findViewById(R.id.buttonRegistration).setOnClickListener(v -> {
            String login = this.loginEdit.getText().toString();
            String password = this.passwordEdit.getText().toString();
            Logs.info(login+password);
            if (login.isEmpty() || password.isEmpty()) {
                new DialogTextBox(getContext(), getString(R.string.without_parametrs)).show();
                return;
            }
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ACCOUNT_SIGNUP);
            data.put(PacketDataKeys.NICKNAME, login);
            data.put(PacketDataKeys.PASSWORD, MD5Hash.md5Salt(password));
            data.put(PacketDataKeys.DEVICE, Config.getDeviceID(getContext()));
            this.socketHelper.sendData(new JSONObject(data));
        });

        return binding;
    }

    @Override
    public void onViewCreated(View view, Bundle instance) {
        super.onViewCreated(view, instance);
        this.navController = Navigation.findNavController(view);
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (navController == null) {
            navController = Navigation.findNavController(requireView().findViewById(R.id.fragmentContainerView));
        }
    }

    public void onBackPressed() {
        this.socketHelper.unsubscribe(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.socketHelper.unsubscribe(this);
    }

    @Override
    public void onReceive(JsonNode json) {
        if (json.has(PacketDataKeys.TYPE_EVENT)){
            String event = json.get(PacketDataKeys.TYPE_EVENT).asText();
            if (event.equals(PacketDataKeys.ACCOUNT_SIGNUP)) {
                getActivity().runOnUiThread(() -> {
                    this.navController.navigate(R.id.action_reg_nickname_password_to_registerSexFragment);
                });
                Application.lwafCurrentUser = (User) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.ACCOUNT), User.class);
                SharedPreferences sPref = getContext().getSharedPreferences("lwaf_user", 0);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("access_token", Application.lwafCurrentUser.accessToken);
                ed.apply();
            }
        }
    }

    @Override
    public void onReceiveError(String str) {

    }
}