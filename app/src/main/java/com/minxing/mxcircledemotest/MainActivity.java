package com.minxing.mxcircledemotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minxing.mxcircledemotest.retrofit.Api;
import com.minxing.mxcircledemotest.retrofit.RetrofitManager;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends Activity {

    private EditText serverAddress;
    private EditText pushAddress;
    private EditText useNameEdit;
    private EditText passwordEdit;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverAddress = (EditText) findViewById(R.id.server_address);
        pushAddress = (EditText) findViewById(R.id.push_address);
        useNameEdit = (EditText) findViewById(R.id.username);
        passwordEdit = (EditText) findViewById(R.id.password);

        loginBtn = (Button) findViewById(R.id.login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }


    private void login(){
        MXContact.serverAddress = serverAddress.getText().toString().trim();
        MXContact.pushAddress = pushAddress.getText().toString().trim();

        String username = useNameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();


        TreeMap<String, String> headers = new TreeMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        String clientCheck = null;
        try {
            String serverUrl = MXContact.serverAddress;
            URI uri = new URI(serverUrl);
            if (uri != null) {
                clientCheck = SHA1Util.SHA1(uri.getHost() + ":" + username);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (clientCheck != null) {
            headers.put("X-CLIENT-CHECKSUM", clientCheck);
        }
        String verName = "";
        if (verName != null && !"".equals(verName)) {
            headers.put("User-Agent","MinxingMessenger/" + verName);
        } else {
            headers.put("User-Agent","MinxingMessenger/1.0.0");
        }

        Retrofit retrofit = RetrofitManager.getInstance().getRetrofitWithHeader(headers);

        Api api = retrofit.create(Api.class);

        String nonce = AESUtil.getRawKey();

//        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String device_uuid = "9883af08c4de3b14fba9e2acf65e7a0c025a1da2";
        Call<JsonObject> call = api.oauth2("password",
                username,
                nonce,
                AESUtil.encrypt(nonce, password),
                "2",
                "true",
                device_uuid,
                "true");


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                int code = response.code();
                if (code == 200){
                    JsonObject result = response.body();

                    try {
                        JsonElement elementToken = result.get("access_token");
                        MXContact.accessToken = elementToken.getAsString();
                        Log.e("Tag","====elementToken====" + elementToken);
                        JsonElement elementDafaultNetID = result.get("default_network_id");
                        MXContact.default_network_id = elementDafaultNetID.getAsInt();
                        Log.e("Tag","====elementDefaultNetID====" + elementDafaultNetID);

                        JsonObject userObject = result.getAsJsonObject("user_info");
                        JsonArray identityArray = userObject.getAsJsonArray("users");

                        if (identityArray != null && identityArray.size() != 0) {
                            JsonElement element = identityArray.get(0);
                            JsonObject jo = element.getAsJsonObject();
                            MXContact.currentID = jo.get("id").getAsInt();
                            Log.e("Tag","====MXContact.currentID====" + MXContact.currentID);
                            MXContact.netWorkId = jo.get("network_id").getAsInt();
                            Log.e("Tag","==== MXContact.netWorkId====" +  MXContact.netWorkId);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(MainActivity.this, MXCircleTextMessageTestActivity.class);
                    startActivity(intent);

                }else {
                    Toast.makeText(MainActivity.this,"登录出错  code === " + code,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this,"网络出错    " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}