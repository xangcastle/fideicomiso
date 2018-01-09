package com.deltacopiers.banpro.fideicomiso;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.deltacopiers.banpro.fideicomiso.AppController;
public class MainActivity extends AppCompatActivity {
    private Button btn_login;
    private EditText txt_usuario;
    private EditText txt_password;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_login = (Button)findViewById(R.id.btnLogin);
        txt_usuario = (EditText)findViewById(R.id.email);
        txt_password = (EditText)findViewById(R.id.password);
        session = new SessionManager(getApplicationContext());

        btn_login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String usuario = txt_usuario.getText().toString();
                String password = txt_password.getText().toString();
                checkLogin(usuario, password);
            }
        });
    }

    private void checkLogin(final String usuario, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://192.168.0.17:8000/banpro/login/", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);

                    session.setLogin(true);

                    // Now store the user in SQLite
                    String uid = jObj.getString("id");
                    String uusername = jObj.getString("username");
                    String unombre = jObj.getString("nombre");

                    String[][] data = new String[3][2];
                    data[0][0] = "id";
                    data[0][1] = uid;
                    data[1][0] = "username";
                    data[1][1] = uusername;
                    data[2][0] = "nombre";
                    data[2][1] = unombre;

                    Conexion conexion = new Conexion(getApplicationContext(), "Delta", null, 3);
                    long respuesta = conexion.insertRegistration("usuarios", data);

                    // Launch main activity
                    Intent intent = new Intent(MainActivity.this, dashboard.class);
                    intent.putExtra("id", uid);
                    intent.putExtra("username", uusername);
                    intent.putExtra("nombre", unombre);
                    startActivity(intent);
                    finish();


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            R.string.message_error, Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        R.string.message_error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuario", usuario);
                params.put("password", password);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
