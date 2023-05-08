package com.Zakovskiy.lwaf.authorization;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.api.LWAFApi;
import com.Zakovskiy.lwaf.api.VKApi;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.FileUtils;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterAvatarFragment extends Fragment {

    private CircleImageView circleImageView;

    public RegisterAvatarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {}
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg_avatar, container, false);
        this.circleImageView = view.findViewById(R.id.imageSetAvatar);
        view.findViewById(R.id.imageSetAvatar).setOnClickListener(v -> {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};
            chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            chooseFile = Intent.createChooser(chooseFile, getString(R.string.pick_avatar));
            startActivityForResult(chooseFile, 100);
        });
        view.findViewById(R.id.buttonEndRegistration).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DashboardFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver contentResolver = getContext().getContentResolver();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Application.lwafServerConfig.restHost).build();
            LWAFApi lwafApi = retrofit.create(LWAFApi.class);
            InputStream inputStream = null;
            try {
                inputStream = contentResolver.openInputStream(uri);
            } catch (FileNotFoundException e) {
                new DialogTextBox(getContext(), getString(R.string.error_upload_photo)).show();
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            RequestBody requestBody = null;
            try {
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), FileUtils.getBytes(inputStream));
            } catch (IOException e) {
                new DialogTextBox(getContext(), getString(R.string.error_upload_photo)).show();
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", FileUtils.getFileName(getContext(), uri), requestBody);
            lwafApi.uploadPhoto(Application.lwafCurrentUser.accessToken, filePart).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    String responseString = null;
                    try {
                        responseString = response.body().string();
                        Logs.debug(responseString);
                        JsonNode json = JsonUtils.convertJsonStringToJsonNode(responseString);
                        if (json.has("error")) {
                            new DialogTextBox(getContext(), getString(R.string.error_upload_photo)).show();
                        } else {
                            circleImageView.setImageURI(uri);
                        }
                    } catch (IOException e) {
                        new DialogTextBox(getContext(), getString(R.string.error_upload_photo)).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Logs.debug(t.getMessage().toString());
                }
            });
        }
    }
}