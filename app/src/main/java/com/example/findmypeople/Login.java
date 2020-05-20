package com.example.findmypeople;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;


public class Login extends AppCompatActivity {
    private TextView txtRegister2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRegister2 = (TextView) findViewById(R.id.txtRegister2);
        txtRegister2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openRegisterActivity();
            }
        });


    }
    //To open Register Activity when yellow text is clicked
    public void openRegisterActivity(){
        Intent intent = new Intent(this, Register_Activity.class);
        startActivity(intent);
    }


}
