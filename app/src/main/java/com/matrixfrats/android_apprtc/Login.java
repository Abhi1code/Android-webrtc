package com.matrixfrats.android_apprtc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.matrixfrats.android_apprtc.database.FireDatabase;
import com.matrixfrats.android_apprtc.util.DataPacks;
import com.matrixfrats.android_apprtc.util.Login_session;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Login extends AppCompatActivity {

    private EditText name;
    private Button login;
    private Login_session login_session;
    private String user_name;
    private ProgressDialog progressDialog;
    private static boolean conn_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = findViewById(R.id.name);
        login = findViewById(R.id.login);
        login_session = new Login_session(this);
        prepare_progressdialog();

        login.setOnClickListener(view -> {
            user_name = this.name.getText().toString().trim();
            if (TextUtils.isEmpty(user_name) || user_name == null){
                return;
            }
            progressDialog.show();
            login_session.setName(user_name);
            login_session.setSession();
            FireDatabase.getInstance(this).initUser(user_name);

        });
    }

    public void prepare_progressdialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("please wait...");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DataPacks.LoginDataPack loginDataPack) {
        progressDialog.cancel();
        if (loginDataPack.status) {
            Intent intent = new Intent(this, VideoRoom.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}