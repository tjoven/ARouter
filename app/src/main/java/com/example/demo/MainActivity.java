package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.annotation.Path;
import com.example.arouter.Router;
import com.example.login.LoginActivity;

@Path(path = "/main/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view) {
        Log.d("tzw","MainActivity");
        Router.getsInstance().build("/login/login").navigation();
    }
}
