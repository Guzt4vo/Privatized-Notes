package gustavoposts.jimenez.privatizednotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//validates the credentials of users before allowing them to continue to the rest of the app
//users have a maximum of 3 attempts
public class loginActivity extends AppCompatActivity {

    private EditText usrNm;
    private EditText pswd;
    private Button btnLogin;
    private int counter = 3;
    boolean isValid;
    private String userName = "c1c224b03cd9bc7b6a86d77f5dace40191766c485cd55dc48caf9ac873335d6f";
    private String passWord = "e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23bd38ec221a";

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
                    MessageDigest md = null;
                    MessageDigest dm = null;
                    try {
                        md = MessageDigest.getInstance("SHA-256");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        dm = MessageDigest.getInstance("SHA-256");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    String usrHash = inputName;
                    String psdHash = inputPassword;
                    md.update(usrHash.getBytes(StandardCharsets.UTF_8));
                    dm.update(psdHash.getBytes(StandardCharsets.UTF_8));
                    byte[] digest1 = md.digest();
                    byte[] digest2 = dm.digest();
                    inputName = String.format("%064x", new BigInteger(1, digest1));
                    inputPassword = String.format("%064x", new BigInteger(1, digest2));
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
                        Toast.makeText(loginActivity.this, "Welcome Gustavo!", Toast.LENGTH_SHORT).show();

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
