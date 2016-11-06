package ch.mse.mybudget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button bLogin = (Button) findViewById(R.id.btn_continue_login);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, BalanceOverviewActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onClickSignin(View v) {
        Button button=(Button) v;
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));

    }
    public void onClickSignup(View v) {
        Button button=(Button) v;
        startActivity(new Intent(getApplicationContext(), SignupActivity.class));

    }
}
