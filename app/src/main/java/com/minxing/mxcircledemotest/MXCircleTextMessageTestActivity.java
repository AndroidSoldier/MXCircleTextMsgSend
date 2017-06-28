package com.minxing.mxcircledemotest;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.minxing.mxcircledemotest.retrofit.Api;
import com.minxing.mxcircledemotest.retrofit.RetrofitManager;

import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MXCircleTextMessageTestActivity extends Activity implements View.OnClickListener {

    private EditText numEdit;
    private EditText groupIdEdit;
    private Button sendBtn;
    private LinearLayout mx_log_layout;
    private ScrollView mx_scroll;

    private EditText currentUserIdEdit;
    private EditText currentUserwNetIdEdit;
    private EditText currentUserTokenEdit;
    private EditText mx_content;


    private int num = 1;
    private int defaultGroupId = -1;

    private int count = 0;


    private int currentUserID;
    private int currentUserwNetId;
    private String currentUserToken;

    private String content;
    private String[] contents;


    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mx_text_message_send_test);

        numEdit = (EditText) findViewById(R.id.mx_num);
        groupIdEdit = (EditText) findViewById(R.id.mx_groupid);
        sendBtn = (Button) findViewById(R.id.mx_send);
        mx_log_layout = (LinearLayout) findViewById(R.id.mx_log_layout);
        mx_scroll = (ScrollView) findViewById(R.id.mx_scroll);

        currentUserIdEdit = (EditText) findViewById(R.id.mx_currentUserId);
        currentUserwNetIdEdit = (EditText) findViewById(R.id.mx_currentUser_net_id);
        currentUserTokenEdit = (EditText) findViewById(R.id.mx_currentUser_token);
        mx_content = (EditText) findViewById(R.id.mx_content);

        sendBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mx_send) {
            send();
        }
    }


    private void send() {
        String numStr = numEdit.getText().toString().trim();
        if (TextUtils.isEmpty(numStr)) {
            num = 1;
        } else {
            try {
                num = Integer.parseInt(numStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String groupIdStr = groupIdEdit.getText().toString().trim();
        if (!TextUtils.isEmpty(groupIdStr)) {
            try {
                defaultGroupId = Integer.parseInt(groupIdStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (defaultGroupId == -1) {
            return;
        }

        String userID = currentUserIdEdit.getText().toString().trim();
        if (TextUtils.isEmpty(userID)) {
            currentUserID = MXContact.currentID;
        } else {
            currentUserID = Integer.valueOf(userID);
        }

        String userNetId = currentUserwNetIdEdit.getText().toString().trim();
        if (TextUtils.isEmpty(userID)) {
            currentUserwNetId = MXContact.netWorkId;
        } else {
            currentUserwNetId = Integer.valueOf(userNetId);
        }
        String token = currentUserTokenEdit.getText().toString().trim();
        if (TextUtils.isEmpty(token)) {
            currentUserToken = MXContact.accessToken;
        } else {
            currentUserToken = token;
        }

        content = mx_content.getText().toString().trim();
        if (!TextUtils.isEmpty(content)){
            contents = content.split("&");
        }

        mx_log_layout.removeAllViews();
        count = 0;
        handler.post(runnable);
    }


    private void sendTextMsg() {

        TreeMap<String, String> headers = new TreeMap<String, String>();
        headers.put("NETWORK-ID", "" + currentUserwNetId);
        headers.put("Authorization", "Bearer " + currentUserToken);

        String verName = "";
        if (verName != null && !"".equals(verName)) {
            headers.put("User-Agent", "MinxingMessenger/" + verName);
        } else {
            headers.put("User-Agent", "MinxingMessenger/1.0.0");
        }


        Retrofit retrofit = RetrofitManager.getInstance().getRetrofitWithHeader(headers);

        Api api = retrofit.create(Api.class);
        Call<JsonObject> call = api.sendTextMessage(defaultGroupId, TextUtils.isEmpty(content) ? "我是第" + count + "条消息" : contents[count]);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                int code = response.code();
                if (code == 200) {
                    TextView tv = new TextView(MXCircleTextMessageTestActivity.this);
                    if (TextUtils.isEmpty(content)){
                        tv.setText("我是第" + count + "条消息" + "    success");
                    }else {
                        tv.setText(contents[count] + "   send success");
                    }

                    mx_log_layout.addView(tv);
                    count++;
                    handler.postDelayed(runnable, 1000);
                }else {
                    try {
                        Toast.makeText(MXCircleTextMessageTestActivity.this,"====数据错误===code    ===" + code + "错误信息    ====" + response.errorBody().string(),Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (num == -1){
                if (contents != null && contents.length > 0){
                    if (count < contents.length) {
                        //执行发送
                        sendTextMsg();
                    } else {
                        TextView tv = new TextView(MXCircleTextMessageTestActivity.this);
                        tv.setText("任务结束");
                        mx_log_layout.addView(tv);
                    }
                }
            }else {
                if (count < num) {
                    //执行发送
                    sendTextMsg();
                } else {
                    TextView tv = new TextView(MXCircleTextMessageTestActivity.this);
                    tv.setText("任务结束");
                    mx_log_layout.addView(tv);
                }
            }

        }
    };
}
