package ch.mse.mybudget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onClickSignin(View v) {
        Button button=(Button) v;
        startActivity(new Intent(getApplicationContext(), login.class));

    }
    public void onClickSignup(View v) {
        Button button=(Button) v;
        startActivity(new Intent(getApplicationContext(), signup.class));

    }
}
