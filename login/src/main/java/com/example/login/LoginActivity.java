package com.example.login;

import android.os.Bundle;

import com.example.annotation.Path;
import com.example.arouter.Router;
import com.example.base.LiveDataBus;
import com.example.base.LiveDataBus_;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.view.View;
import android.widget.Toast;

@Path(path = "/login/login")
public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LiveDataBus.getInstance().with("login",String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(LoginActivity.this,s,Toast.LENGTH_SHORT).show();
            }
        });

        LiveDataBus_.getInstance().with_("login/msg",String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(LoginActivity.this,s,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void member(View view){
        Router.getsInstance().build("/member/info").navigation();
    }

    public void postValue(View view) {
        LiveDataBus.getInstance().with("member",String.class).postValue("先post：LoginActivity -- > MemberActivity");
    }

    public void msg(View view) {
        Router.getsInstance().build("/login/msg").navigation();
    }

    public void postValue_(View view) {
        LiveDataBus_.getInstance().with_("msg/login",String.class).postValue("先post：LoginActivity -- > MsgActivity");
    }
}
