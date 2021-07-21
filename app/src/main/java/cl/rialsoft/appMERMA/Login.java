package cl.rialsoft.appMERMA;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText user,clave;
    ProgressBar carga;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = findViewById(R.id.edt_usuario);
        clave = findViewById(R.id.edt_clave);
        carga = findViewById(R.id.progressBar1);
        context = this;
    }

    @Override
    public void onClick(View v) {
        switch  (v.getId()){
            case R.id.btn_entrar:
                final String sap_user = user.getText().toString();
                final String sap_clave = clave.getText().toString();
                if (sap_user.equals(null) || sap_user.equals("") || sap_clave.equals(null) || sap_clave.equals("")){
                    Funciones.mensajeSweet(context,"Aceptar","Complete los campos",SweetAlertDialog.SUCCESS_TYPE);
                }else{
                    carga.setVisibility(View.VISIBLE);
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("SAP_USER",sap_user);
                    requestParams.put("SAP_CLAVE",sap_clave);
                    HttpUtils.post(Rest.LOGIN,requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            if (statusCode == 200) {
                                try {
                                    if (response.getString("ESTADO").equals("true")){
                                        ((VariableGlobal)Login.this.getApplication()).setUser(sap_user);
                                        ((VariableGlobal)Login.this.getApplication()).setClave(sap_clave);
                                        user.setText("");
                                        clave.setText("");
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                    }else {
                                        new SweetAlertDialog(Login.this,SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Credenciales Incorrectas")
                                                .setConfirmText("Aceptar")
                                                .show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            carga.setVisibility(View.INVISIBLE);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                           carga.setVisibility(View.INVISIBLE);
                            Funciones.mensajeSweet(context,"Aceptar","Credenciales incorrectas",SweetAlertDialog.SUCCESS_TYPE);

                        }
                    });
                }
                break;
            case R.id.btn_salir:
                user.setText("");
                clave.setText("");
                break;
        }
    }
}
