package ch.mse.mybudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickSignup(View v) {
        Button button=(Button) v;
        startActivity(new Intent(getApplicationContext(), SignupActivity.class));

    }

    public void onClickSignin(View v) {
        Button button=(Button) v;
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));

    }
}
