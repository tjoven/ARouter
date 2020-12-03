package com.example.member;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.annotation.Path;
import com.example.base.LiveDataBus;

@Path(path = "/member/info")
public class MemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        LiveDataBus.getInstance().with("member",String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(MemberActivity.this,s,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postValue(View view) {
        LiveDataBus.getInstance().with("login",String.class).postValue("后post：MemberActivity -- 》LoginActivity");
    }
}
