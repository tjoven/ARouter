package com.example.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.annotation.Path;
import com.example.base.LiveDataBus_;

@Path(path = "/login/msg")
public class MsgActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        LiveDataBus_.getInstance().with_("msg/login",String.class).observe(this, false, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(MsgActivity.this,s,Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void postValue(View view) {
        LiveDataBus_.getInstance().with_("login/msg",String.class).postValue("后post：Msg -- 》LoginActivity");
    }
}
