package com.Zakovskiy.lwaf.authorization;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.MD5Hash;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.HashMap;

public class RegisterSexFragment extends Fragment implements SocketHelper.SocketListener{

    private AutoCompleteTextView autoCompleteTextView;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private NavController navController;

    private int selectedSex = 0;

    public RegisterSexFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View binding =  inflater.inflate(R.layout.fragment_reg_sex, container, false);
        String[] sexes = getResources().getStringArray(R.array.sexes);
        ArrayAdapter<String> sexesAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_sex, sexes);
        this.autoCompleteTextView = (AutoCompleteTextView) binding.findViewById(R.id.autoCompleteSex);
        this.autoCompleteTextView.setAdapter(sexesAdapter);
        this.autoCompleteTextView.setOnItemClickListener((parent, view, position, rowId) -> this.selectedSex = position);
        binding.findViewById(R.id.buttonNext).setOnClickListener(v -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.SEX_CHANGE);
            data.put(PacketDataKeys.SEX, selectedSex);
            this.socketHelper.sendData(new JSONObject(data));
        });
        this.socketHelper.subscribe(this);
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
    public void onReceive(JsonNode json) {
        if (navController == null) {
            navController = Navigation.findNavController(requireView().findViewById(R.id.fragmentContainerView));
        }
        if (json.has(PacketDataKeys.TYPE_EVENT)){
            String event = json.get(PacketDataKeys.TYPE_EVENT).asText();
            if(event.equals(PacketDataKeys.SEX_CHANGE)) {
                getActivity().runOnUiThread(() -> {
                    this.socketHelper.unsubscribe(this);
                    this.navController.navigate(R.id.action_registerSexFragment_to_registerAvatarFragment);
                });
            }
        }
    }

    @Override
    public void onReceiveError(String str) {

    }
}