package gustavoposts.jimenez.privatizednotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class loginActivity extends AppCompatActivity {

    private EditText usrNm;
    private EditText pswd;
    private Button btnLogin;
    private int counter = 3;
    boolean isValid;
    private String userName = "Admin";
    private String passWord = "Password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usrNm = findViewById(R.id.usr);
        pswd = findViewById(R.id.psd);
        btnLogin = findViewById(R.id.btn);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputName = usrNm.getText().toString();
                String inputPassword = pswd.getText().toString();

                if(inputName.isEmpty() || inputPassword.isEmpty())
                {
                    Toast.makeText(loginActivity.this, "No username or password entered", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    isValid = validate(inputName, inputPassword);

                    if(!isValid)
                    {
                        counter--;

                        Toast.makeText(loginActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();

                        if(counter == 0)
                        {
                            Toast.makeText(loginActivity.this, "You have run out of login attempts", Toast.LENGTH_SHORT).show();
                            btnLogin.setEnabled(false);
                        }
                    }
                    else
                    {
                        Toast.makeText(loginActivity.this, "Welcome " + inputName + "!", Toast.LENGTH_SHORT).show();

                        Intent myIntent = new Intent(loginActivity.this, MainActivity.class);
                        startActivity(myIntent);
                    }
                }
            }
        });
    }

    private boolean validate(String name, String password)
    {
        if(name.equals(userName) && password.equals(passWord))
        {
            return true;
        }

        return false;
    }

}