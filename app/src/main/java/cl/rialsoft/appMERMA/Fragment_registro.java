package cl.rialsoft.appMERMA;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.login.LoginException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

import static cl.rialsoft.appMERMA.Funciones.botonesSweet;
import static cl.rialsoft.appMERMA.Funciones.mensajeSweet;
import static cl.rialsoft.appMERMA.Funciones.stateconexion;

public class Fragment_registro extends Fragment implements View.OnClickListener {
    private View view;
    private int cont = 0;
    private TextView fecha;
    private Button btn_agregar, btn_enviar_data, btn_cancelar;
    private DatePickerDialog.OnDateSetListener fechasetlistener;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_SCANNER_REQUEST_CODE = 0x0000c0de;
    private Button button, btn_delete, btn_edit, btn_scanear, btn_limpia_img;
    private ImageView imageView;
    private TableLayout table;
    private JSONArray arraymerma;
    private ProgressBar progressBar,loadigcentro,loadingalmacen,loadingclase;
    private Boolean mIsSpinnerFirstCall = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registro, null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        fecha = (EditText) view.findViewById(R.id.edt_fecha);
        btn_agregar = (Button) view.findViewById(R.id.btn_agregar);
        btn_agregar.setOnClickListener(this);
        btn_enviar_data = (Button) view.findViewById(R.id.btn_enviar_data);
        btn_enviar_data.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);
        table = (TableLayout) view.findViewById(R.id.table_producto);
        btn_limpia_img = view.findViewById(R.id.limpiar_adj_imagen);
        btn_limpia_img.setOnClickListener(null);
        btn_limpia_img.setBackgroundResource(R.drawable.btn_gris);


        btn_cancelar = (Button) view.findViewById(R.id.btn_cancelar);
        btn_cancelar.setOnClickListener(this);
        button = view.findViewById(R.id.btn_adj_imagen);
        btn_scanear = view.findViewById(R.id.btn_scanear);
        btn_scanear.setOnClickListener(this);

        loadigcentro = view.findViewById(R.id.progressBarcentro);
        loadingalmacen = view.findViewById(R.id.progressBaralmacen);
        loadingclase = view.findViewById(R.id.progressBarclase);

        /**fecha.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
        getContext(),
        android.R.style.Theme_Holo_Dialog_MinWidth,
        fechasetlistener, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        }
        });
         fechasetlistener = new DatePickerDialog.OnDateSetListener() {
        @Override public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        fecha.setText(Funciones.twoDigits(dayOfMonth) + "-" + Funciones.twoDigits(month + 1) + "-" + year);
        }
        };**/
        fn_inicio();
        stateconexion(getContext());
        imageView = (ImageView) view.findViewById(R.id.edt_imagen);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        fn_fecha_actual();
        return view;
    }

    View.OnClickListener eliminar_adj = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
            sweetAlertDialog.setTitleText("Información");
            sweetAlertDialog.setContentText("¿Desea eliminar el adjunto?");
            sweetAlertDialog.setConfirmText("SI");
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    ((ImageView) view.findViewById(R.id.edt_imagen)).setImageDrawable(null);
                    btn_limpia_img.setOnClickListener(null);
                    btn_limpia_img.setBackgroundResource(R.drawable.btn_gris);
                    sDialog.dismissWithAnimation();
                }
            });
            sweetAlertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();
                }
            });
            sweetAlertDialog.show();
            botonesSweet(sweetAlertDialog);
        }
    };

    public void fn_fecha_actual() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        //fecha.setText(Funciones.twoDigits(day) + "-" + Funciones.twoDigits(month + 1) + "-" + year);
        fecha.setText( year+ "-" +Funciones.twoDigits(month + 1) + "-"+Funciones.twoDigits(day));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_agregar:
                fn_add_merma();
                break;
            case R.id.btn_enviar_data:
                try {
                    fn_send_merma();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cancelar:
                fn_limpiar_form(false);
                break;
            case R.id.btn_scanear:
                IntentIntegrator
                        .forSupportFragment(this)
                        .setOrientationLocked(false)
                        .initiateScan();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);
                imageView.setImageBitmap(bitmap);
                btn_limpia_img.setOnClickListener(eliminar_adj);
                btn_limpia_img.setBackgroundResource(R.drawable.btn_rojo_form);
            }
        }
        if (requestCode == CAPTURE_SCANNER_REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result.getContents() != null) {
                if (!stateconexion(getContext())) {
                    mensajeSweet(getContext(), "Aceptar", "No tiene conexión disponible, debe realizar ingreso manual", SweetAlertDialog.SUCCESS_TYPE);

                } else {
                    cargarCodigoProducto(result.getContents());
                }

            }
        }
    }

    public void cargarCodigoProducto(final String codigo) {
        String user = ((VariableGlobal) getActivity().getApplication()).getUser();
        String clave = ((VariableGlobal) getActivity().getApplication()).getClave();

        RequestParams requestParams = new RequestParams();
        requestParams.put("I_SCAN", codigo);
        requestParams.put("SAP_USER", user);
        requestParams.put("SAP_CLAVE", clave);

        HttpUtils.post(Rest.ZRFC_SC_MERMA, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 200) {
                    try {
                        if (response.getString("E_RETURN").equals("0")) {
                            String E_MATNR = response.getString("E_MATNR");
                            int material = Integer.parseInt(E_MATNR);
                            EditText editText = view.findViewById(R.id.edt_producto);
                            editText.setText(material + "");
                        } else {
                            mensajeSweet(getContext(), "Aceptar", response.getString("E_MESSAGE"), SUCCESS_MESSAGE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("xxx", "onSuccess" + statusCode);
            }

        });
    }

    public void fn_vacio_cambo() {
        Spinner spinnerAlmacen = view.findViewById(R.id.edt_almacen);
        Spinner spinnerCentro = view.findViewById(R.id.edt_centro);
        Spinner spinnerClaseMov = view.findViewById(R.id.edt_tipo_merma);
        ArrayList<ModelComboBox> modelComboBoxes = new ArrayList<>();
        ModelComboBox selected = new ModelComboBox("", "Seleccione");
        modelComboBoxes.add(selected);
        ArrayAdapter<ModelComboBox> arrayAdapter =
                new ArrayAdapter<ModelComboBox>(getContext(), R.layout.spinner_item, modelComboBoxes);
        spinnerAlmacen.setAdapter(arrayAdapter);
        spinnerCentro.setAdapter(arrayAdapter);
        spinnerClaseMov.setAdapter(arrayAdapter);
    }

    public void fn_inicio() {
        fn_combobox_tip_merma();
        fn_combobox_centro();
        fn_vacio_cambo();
        getData();
    }

    public void fn_combobox_centro() {
        loadigcentro.setVisibility(View.VISIBLE);
        String user = ((VariableGlobal) getActivity().getApplication()).getUser();
        String clave = ((VariableGlobal) getActivity().getApplication()).getClave();

        RequestParams requestParams = new RequestParams();
        requestParams.put("I_OPCION", 2);
        requestParams.put("SAP_USER", user);
        requestParams.put("SAP_CLAVE", clave);

        HttpUtils.post(Rest.ZRFC_MANT_VM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 200) {
                    try {
                        ArrayList<ModelComboBox> modelComboBoxes = new ArrayList<>();

                        JSONArray t_trxal = response.getJSONArray("T_USERS");

                        ModelComboBox selected = new ModelComboBox("", "Seleccione");
                        modelComboBoxes.add(selected);

                        for (int i = 0; i < t_trxal.length(); i++) {
                            ModelComboBox modelComboBox = new ModelComboBox();
                            modelComboBox.setId(t_trxal.getJSONObject(i).getString("CENTRO"));
                            modelComboBox.setNombre(t_trxal.getJSONObject(i).getString("CENTRO"));
                            modelComboBoxes.add(modelComboBox);
                        }
                        Spinner spinnercentro = view.findViewById(R.id.edt_centro);
                        ArrayAdapter<ModelComboBox> arrayAdapter =
                                new ArrayAdapter<ModelComboBox>(getContext(), R.layout.spinner_item, modelComboBoxes);
                        spinnercentro.setAdapter(arrayAdapter);
                        if (modelComboBoxes.size() == 2) {
                            spinnercentro.setSelection(1);
                        }
                        spinnercentro.setOnItemSelectedListener(selectedListener);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadigcentro.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("xxx", "onSuccess" + statusCode);
                loadigcentro.setVisibility(View.INVISIBLE);
            }

        });
    }

    public void fn_combobox_almacen() {
        loadingalmacen.setVisibility(View.VISIBLE);
        final Spinner spinnerAlmacen = view.findViewById(R.id.edt_almacen);
        ArrayList<ModelComboBox> modelComboBoxes = new ArrayList<>();
        ModelComboBox selected = new ModelComboBox("", "Seleccione");
        modelComboBoxes.add(selected);
        ArrayAdapter<ModelComboBox> arrayAdapter =
                new ArrayAdapter<ModelComboBox>(getContext(), R.layout.spinner_item, modelComboBoxes);
        spinnerAlmacen.setAdapter(arrayAdapter);

        Spinner spinnerCentro = view.findViewById(R.id.edt_centro);

        if (spinnerCentro.getSelectedItemPosition() > 0) {
            String user = ((VariableGlobal) getActivity().getApplication()).getUser();
            String clave = ((VariableGlobal) getActivity().getApplication()).getClave();

            RequestParams requestParams = new RequestParams();
            requestParams.put("I_OPCION", 4);
            requestParams.put("I_WERKS", ((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_centro)).getSelectedItem()).getId());
            requestParams.put("SAP_USER", user);
            requestParams.put("SAP_CLAVE", clave);

            HttpUtils.post(Rest.ZRFC_MANT_VM, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (statusCode == 200) {
                        try {
                            ArrayList<ModelComboBox> modelComboBoxes = new ArrayList<>();
                            JSONArray t_trxal = response.getJSONArray("T_TRXAL");
                            ModelComboBox selected = new ModelComboBox("", "Seleccione");
                            modelComboBoxes.add(selected);
                            for (int i = 0; i < t_trxal.length(); i++) {
                                ModelComboBox modelComboBox = new ModelComboBox();
                                modelComboBox.setId(t_trxal.getJSONObject(i).getString("ALMACEN"));
                                modelComboBox.setNombre(t_trxal.getJSONObject(i).getString("ALMACEN"));
                                modelComboBoxes.add(modelComboBox);
                            }
                            ArrayAdapter<ModelComboBox> arrayAdapter =
                                    new ArrayAdapter<ModelComboBox>(getContext(), R.layout.spinner_item, modelComboBoxes);
                            spinnerAlmacen.setAdapter(arrayAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    loadingalmacen.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.e("xxx", "onSuccess" + statusCode);
                    loadingalmacen.setVisibility(View.INVISIBLE);
                }

            });
        }
    }

    public void fn_combobox_tip_merma() {
        loadingclase.setVisibility(View.VISIBLE);
        String user = ((VariableGlobal) getActivity().getApplication()).getUser();
        String clave = ((VariableGlobal) getActivity().getApplication()).getClave();

        RequestParams requestParams = new RequestParams();
        requestParams.put("I_OPCION", 1);
        requestParams.put("SAP_USER", user);
        requestParams.put("SAP_CLAVE", clave);

        HttpUtils.post(Rest.ZRFC_MANT_VM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ArrayList<ModelComboBox> modelComboBoxes = new ArrayList<>();

                    JSONArray t_trxal = response.getJSONArray("T_BWART");

                    ModelComboBox selected = new ModelComboBox("", "Seleccione");
                    modelComboBoxes.add(selected);
                    for (int i = 0; i < t_trxal.length(); i++) {
                        ModelComboBox modelComboBox = new ModelComboBox();
                        modelComboBox.setId(t_trxal.getJSONObject(i).getString("CLSMOV"));
                        modelComboBox.setNombre(t_trxal.getJSONObject(i).getString("CLSMOV") + " " + t_trxal.getJSONObject(i).getString("CLMOV_TEXT"));
                        modelComboBoxes.add(modelComboBox);
                    }

                    Spinner spinnerAlmacen = (Spinner) view.findViewById(R.id.edt_tipo_merma);
                    ArrayAdapter<ModelComboBox> arrayAdapter =
                            new ArrayAdapter<ModelComboBox>(getContext(),
                                    R.layout.spinner_item,
                                    modelComboBoxes);
                    arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                    spinnerAlmacen.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadingclase.setVisibility(View.INVISIBLE);

            }
        });
    }

    public void marcar_borde(EditText editText, boolean val) {
        if (val) {
            editText.setBackgroundResource(R.drawable.edt_gris_error);
        } else {
            editText.setBackgroundResource(R.drawable.edt_blanco);
        }
    }

    public void fn_add_merma() {
        int validar = 0;
        EditText pro = ((EditText) view.findViewById(R.id.edt_producto));
        EditText caja = ((EditText) view.findViewById(R.id.edt_cantidad));
        EditText bot = ((EditText) view.findViewById(R.id.edt_unidad));
        EditText mot = ((EditText) view.findViewById(R.id.edt_motivo_merma));
        EditText fech = ((EditText) view.findViewById(R.id.edt_fecha));
        Spinner alma = ((Spinner) view.findViewById(R.id.edt_almacen));
        Spinner clase = ((Spinner) view.findViewById(R.id.edt_tipo_merma));
        Spinner centro = ((Spinner)view.findViewById(R.id.edt_centro));
        if (pro.getText().toString().equals("") || pro.getText().toString().equals(null)) {
            validar = 1;
            marcar_borde(pro, true);
        } else {
            marcar_borde(pro, false);
            validar = validar == 0 ? 0 : validar--;
        }
        if (caja.getText().toString().equals("") || caja.getText().toString().equals(null)) {
            validar = 1;
            marcar_borde(caja, true);
        } else {
            validar = validar == 0 ? 0 : validar--;
            marcar_borde(caja, false);
        }
        if (bot.getText().toString().equals("") || bot.getText().toString().equals(null)) {
            validar = 1;
            marcar_borde(bot, true);
        } else {
            validar = validar == 0 ? 0 : validar--;
            marcar_borde(bot, false);
        }
       /* if (mot.getText().toString().equals("") || mot.getText().toString().equals(null)) {
            validar = 1;
            marcar_borde(mot, true);
        } else {
            validar = validar == 0 ? 0 : validar--;
            marcar_borde(mot, false);
        }*/
        if (fech.getText().toString().equals("") || fech.getText().toString().equals(null)) {
            validar = 1;
            fech.setBackgroundResource(R.drawable.edt_gris_error);
        } else {
            validar = validar == 0 ? 0 : validar--;
            fech.setBackgroundResource(R.drawable.edt_gris);
        }
        if (alma.getSelectedItemPosition() == 0) {
            validar = 1;
            alma.setBackgroundResource(R.drawable.edt_gris_error);
        } else {
            alma.setBackgroundResource(R.drawable.edt_gris);
            validar = validar == 0 ? 0 : validar--;
        }
        if (clase.getSelectedItemPosition() == 0) {
            validar = 1;
            clase.setBackgroundResource(R.drawable.edt_gris_error);
        } else {
            clase.setBackgroundResource(R.drawable.edt_gris);
            validar = validar == 0 ? 0 : validar--;
        }
        if (centro.getSelectedItemPosition() == 0) {
            validar = 1;
            centro.setBackgroundResource(R.drawable.edt_gris_error);
        } else {
            centro.setBackgroundResource(R.drawable.edt_gris);
            validar = validar == 0 ? 0 : validar--;
        }
        boolean valida = false;
        if (validar == 0) {
            int cant = 0;
            if (Integer.parseInt(bot.getText().toString()) == 0 && Integer.parseInt(caja.getText().toString()) == 0) {
                cant++;
                marcar_borde(caja, true);
                marcar_borde(bot, true);
            } else {
                cant = cant == 0 ? 0 : cant--;
                marcar_borde(bot, false);
                marcar_borde(caja, false);
            }
            if (cant > 0) {
                valida = false;
                mensajeSweet(getContext(), "Aceptar", "Las cantidades deben ser mayor a 0", SweetAlertDialog.SUCCESS_TYPE);
            } else {
                valida = true;
            }
        }
        if (valida) {
            if (btn_agregar.getText().toString().equalsIgnoreCase("AGREGAR")) {
                cont++;
                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.raleway_mdium);
                Resources resources = getResources();

                TableRow row = new TableRow(getContext());

                row.setPadding(5, 5, 5, 5);
                if (cont % 2 == 0) {
                    row.setBackgroundResource(R.drawable.table_row);
                } else {
                    row.setBackgroundResource(R.drawable.table_body_zebrado);
                }

                TableLayout.LayoutParams botones =
                        new TableLayout.LayoutParams
                                (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                botones.width = 40;
                botones.height = 40;
                botones.setMarginEnd(20);
                botones.setMarginStart(20);

                Button btn_borrar = new Button(getContext());
                btn_borrar.setBackgroundResource(R.drawable.eliminar_btn);
                btn_borrar.setOnClickListener(btnClick);
                btn_borrar.setLayoutParams(botones);


                Button btn_modificar = new Button(getContext());
                btn_modificar.setBackgroundResource(R.drawable.editar);
                btn_modificar.setOnClickListener(btnClickModificar);
                btn_modificar.setLayoutParams(botones);

                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.addView(btn_borrar);
                linearLayout.addView(btn_modificar);

                row.addView(linearLayout);

                TextView produc = new TextView(getContext());
                produc.setTextColor(Color.BLACK);
                produc.setPadding(5, 0, 5, 0);
                produc.setGravity(Gravity.CENTER);
                produc.setTypeface(typeface);
                produc.setText(((EditText) view.findViewById(R.id.edt_producto)).getText().toString());
                row.addView(produc);

                TextView cant = new TextView(getContext());
                cant.setTextColor(Color.BLACK);
                cant.setPadding(5, 0, 5, 0);
                cant.setGravity(Gravity.CENTER);
                cant.setTypeface(typeface);
                cant.setText(((EditText) view.findViewById(R.id.edt_cantidad)).getText().toString());
                row.addView(cant);

                TextView uni = new TextView(getContext());
                uni.setTextColor(Color.BLACK);
                uni.setPadding(5, 0, 5, 0);
                uni.setGravity(Gravity.CENTER);
                uni.setTypeface(typeface);
                uni.setText(((EditText) view.findViewById(R.id.edt_unidad)).getText().toString());
                row.addView(uni);

                TextView tip_merma = new TextView(getContext());
                tip_merma.setTextColor(Color.BLACK);
                tip_merma.setPadding(5, 0, 5, 0);
                tip_merma.setGravity(Gravity.CENTER);
                tip_merma.setTypeface(typeface);
                tip_merma.setText(((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_tipo_merma)).getSelectedItem()).getId());
                tip_merma.setTag(((Spinner) view.findViewById(R.id.edt_tipo_merma)).getSelectedItemPosition());
                row.addView(tip_merma);

                TextView motivo = new TextView(getContext());
                motivo.setVisibility(View.GONE);
                motivo.setText(((EditText) view.findViewById(R.id.edt_motivo_merma)).getText().toString());
                row.addView(motivo);

                ImageView imageView = new ImageView(getContext());
                imageView.setImageDrawable(((ImageView) view.findViewById(R.id.edt_imagen)).getDrawable());
                imageView.setVisibility(View.GONE);

                row.addView(imageView);
                row.setId(cont);
                table.addView(row);
                guardarData();
                view.findViewById(R.id.edt_centro).setEnabled(false);
                view.findViewById(R.id.edt_almacen).setEnabled(false);
                view.findViewById(R.id.edt_tipo_merma).setEnabled(false);
                fn_limpiar_form(false);

                mensajeSweet(getContext(), "Aceptar", "Producto Agregado", SweetAlertDialog.SUCCESS_TYPE);
            } else {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertDialog.setTitleText("Información");
                sweetAlertDialog.setContentText("¿Esta seguro de modificar el registro?");
                sweetAlertDialog.setConfirmText("SI");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        int id = Integer.parseInt(btn_agregar.getTag().toString());
                        TableRow ve = new TableRow(getContext());
                        for (int i = 0; i < table.getChildCount(); i++) {
                            if (table.getChildAt(i).getId() == id) {
                                ve = (TableRow) table.getChildAt(i);
                                break;
                            }
                        }
                        TextView pro = (TextView) ve.getChildAt(1);
                        TextView caj = (TextView) ve.getChildAt(2);
                        TextView bot = (TextView) ve.getChildAt(3);
                        TextView tip = (TextView) ve.getChildAt(4);
                        TextView mot = (TextView) ve.getChildAt(5);
                        ImageView adj = (ImageView) ve.getChildAt(6);

                        pro.setText(((EditText) view.findViewById(R.id.edt_producto)).getText().toString());
                        caj.setText(((EditText) view.findViewById(R.id.edt_cantidad)).getText().toString());
                        bot.setText(((EditText) view.findViewById(R.id.edt_unidad)).getText().toString());
                        tip.setText(((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_tipo_merma)).getSelectedItem()).getId());
                        tip.setTag(((Spinner) view.findViewById(R.id.edt_tipo_merma)).getSelectedItemPosition());
                        mot.setText(((EditText) view.findViewById(R.id.edt_motivo_merma)).getText().toString());
                        adj.setImageDrawable(((ImageView) view.findViewById(R.id.edt_imagen)).getDrawable());
                        guardarData();
                        fn_limpiar_form(false);
                        mensajeSweet(getContext(), "Aceptar", "Registro Modificado", SweetAlertDialog.SUCCESS_TYPE);
                        sDialog.dismissWithAnimation();
                    }
                });
                sweetAlertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        fn_limpiar_form(false);
                        sDialog.dismissWithAnimation();
                    }
                });
                sweetAlertDialog.show();
                botonesSweet(sweetAlertDialog);
            }
        } else {
            if (validar > 0) {
                mensajeSweet(getContext(), "Aceptar", "Complete los campos", SweetAlertDialog.ERROR_TYPE);
            }
        }
    }

    public void fn_send_merma() throws JSONException {
        if (stateconexion(getContext())) {
            table = (TableLayout) view.findViewById(R.id.table_producto);
            EditText fech = ((EditText) view.findViewById(R.id.edt_fecha));
            Spinner alma = ((Spinner) view.findViewById(R.id.edt_almacen));
            Spinner clase = ((Spinner) view.findViewById(R.id.edt_tipo_merma));
            Spinner centro = ((Spinner)view.findViewById(R.id.edt_centro));
            int validar = 0;
            if (fech.getText().toString().equals("") || fech.getText().toString().equals(null)) {
                validar = 1;
                fech.setBackgroundResource(R.drawable.edt_gris_error);
            } else {
                validar = validar == 0 ? 0 : validar--;
                fech.setBackgroundResource(R.drawable.edt_gris);
            }
            if (alma.getSelectedItemPosition() == 0) {
                validar = 1;
                alma.setBackgroundResource(R.drawable.edt_gris_error);
            } else {
                alma.setBackgroundResource(R.drawable.edt_gris);
                validar = validar == 0 ? 0 : validar--;
            }
            if (clase.getSelectedItemPosition() == 0) {
                validar = 1;
                clase.setBackgroundResource(R.drawable.edt_gris_error);
            } else {
                clase.setBackgroundResource(R.drawable.edt_gris);
                validar = validar == 0 ? 0 : validar--;
            }
            if (centro.getSelectedItemPosition() == 0) {
                validar = 1;
                centro.setBackgroundResource(R.drawable.edt_gris_error);
            } else {
                centro.setBackgroundResource(R.drawable.edt_gris);
                validar = validar == 0 ? 0 : validar--;
            }

            if (validar > 0) {
                mensajeSweet(getContext(), "Aceptar", "Ingrese campos requeridos", SweetAlertDialog.ERROR_TYPE);
            } else {
                if (table.getChildCount() > 1) {
                    progressBar.setVisibility(View.VISIBLE);
                    btn_enviar_data.setEnabled(false);
                    arraymerma = new JSONArray();
                    final ArrayList<ModelMerma> productos = new ArrayList<>();

                    for (int i = 1; i < table.getChildCount(); i++) {
                        TableRow row = (TableRow) table.getChildAt(i);

                        String product = ((TextView) row.getChildAt(1)).getText().toString();
                        String cant_caja = ((TextView) row.getChildAt(2)).getText().toString();
                        String cant_bote = ((TextView) row.getChildAt(3)).getText().toString();
                        String clave_mov = ((TextView) row.getChildAt(4)).getText().toString();
                        String motivo = ((TextView) row.getChildAt(5)).getText().toString();
                        BitmapDrawable drawable = (BitmapDrawable) ((ImageView) row.getChildAt(6)).getDrawable();
                        String imageString = "";
                        String img_name = "";
                        if (drawable != null) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            Bitmap bitmap = drawable.getBitmap();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageBytes = baos.toByteArray();
                            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                            String id_almacen = ((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_almacen)).getSelectedItem()).getId();
                            String user = ((VariableGlobal) getActivity().getApplication()).getUser();
                            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                            Date date = new Date();
                            String fecha_hora = dateFormat.format(date);
                            img_name = id_almacen + "_" + user + "_" + fecha_hora + "_" + i;
                        }


                        ModelMerma modelMerma = new ModelMerma(product, cant_caja, cant_bote, clave_mov, motivo, imageString, img_name);
                        productos.add(modelMerma);
                        try {
                            arraymerma.put(i - 1, modelMerma.toJSON());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONArray list = new JSONArray();

                    for (int i = 1; i < table.getChildCount(); i++) {
                        TableRow row = (TableRow) table.getChildAt(i);
                        TextView pro = (TextView) row.getChildAt(1);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("I_MATNR", pro.getText().toString());
                        list.put(jsonObject);
                    }
                    String user = ((VariableGlobal) getActivity().getApplication()).getUser();
                    String clave = ((VariableGlobal) getActivity().getApplication()).getClave();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("I_MATNR_LIST", list);
                    jsonObject.put("SAP_USER", user);
                    jsonObject.put("SAP_CLAVE", clave);

                    StringEntity entity = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
                    HttpUtils.postJson(getContext(), Rest.ZRFC_VL_MERMA, entity, "application/json", new BaseJsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                            JSONArray jsonObject = null;
                            boolean resultado = false;
                            try {
                                jsonObject = new JSONArray(rawJsonResponse);

                                for (int i = 0; i < jsonObject.length(); i++) {
                                    int result = Integer.parseInt(jsonObject.getJSONObject(i).getString("E_RETURN"));
                                    if (result == 1) {
                                        resultado = true;
                                        TableRow row = (TableRow) table.getChildAt((i + 1));
                                        if ((i + 1) % 2 == 0) {
                                            row.setBackgroundResource(R.drawable.table_row_rojo);
                                        } else {
                                            row.setBackgroundResource(R.drawable.table_body_zebrado_rojo);
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                btn_enviar_data.setEnabled(true);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                            if (resultado) {
                                btn_enviar_data.setEnabled(true);
                                progressBar.setVisibility(View.INVISIBLE);
                                mensajeSweet(getContext(), "Aceptar", "¡Error de productos ingresados!", SweetAlertDialog.ERROR_TYPE);
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                btn_enviar_data.setEnabled(false);
                                try {
                                    String user = ((VariableGlobal) getActivity().getApplication()).getUser();
                                    String clave = ((VariableGlobal) getActivity().getApplication()).getClave();

                                    JSONObject objJsonMERMA = new JSONObject();
                                    objJsonMERMA.put("FECHA", ((EditText) view.findViewById(R.id.edt_fecha)).getText().toString());
                                    objJsonMERMA.put("ALMACEN", ((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_almacen)).getSelectedItem()).getNombre());
                                    objJsonMERMA.put("CENTRO", ((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_centro)).getSelectedItem()).getId());
                                    objJsonMERMA.put("CLASE_MOV", ((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_tipo_merma)).getSelectedItem()).getId());
                                    objJsonMERMA.put("MERMAS", arraymerma);
                                    objJsonMERMA.put("SAP_USER", user);
                                    objJsonMERMA.put("SAP_CLAVE", clave);

                                    StringEntity entity = new StringEntity(objJsonMERMA.toString(), ContentType.APPLICATION_JSON);
                                    HttpUtils.postJson(getContext(), Rest.ZRFC_CR_MERMA, entity, "application/json", new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            try {
                                                if (response.getString("E_RETURN").equals("0")) {
                                                    mensajeSweet(getContext(), "Aceptar", "¡Registros Guardados!", SweetAlertDialog.ERROR_TYPE);
                                                }else{
                                                    mensajeSweet(getContext(),"Aceptar",response.getString("E_MESSAGE"),SweetAlertDialog.SUCCESS_TYPE);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            progressBar.setVisibility(View.INVISIBLE);
                                            btn_enviar_data.setEnabled(true);
                                            fn_limpiar_form(true);
                                            view.findViewById(R.id.edt_centro).setEnabled(true);
                                            view.findViewById(R.id.edt_almacen).setEnabled(true);
                                            view.findViewById(R.id.edt_tipo_merma).setEnabled(true);
                                            fn_limpiar_cache();
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                            mensajeSweet(getContext(), "Aceptar", "Error: " + statusCode, SweetAlertDialog.ERROR_TYPE);
                                            btn_enviar_data.setEnabled(true);
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });

                                } catch (JSONException e) {
                                    btn_enviar_data.setEnabled(true);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                            progressBar.setVisibility(View.INVISIBLE);
                            btn_enviar_data.setEnabled(true);
                        }

                        @Override
                        protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                            return null;
                        }
                    });

                } else {
                    mensajeSweet(getContext(), "Aceptar", "Ingrese productos", SweetAlertDialog.ERROR_TYPE);
                }
            }
        }

    }

    public void fn_limpiar_form(boolean si) {
        ((EditText) view.findViewById(R.id.edt_producto)).setText("");
        ((EditText) view.findViewById(R.id.edt_cantidad)).setText("0");
        ((EditText) view.findViewById(R.id.edt_unidad)).setText("0");
        ((EditText) view.findViewById(R.id.edt_motivo_merma)).setText("");
        ((ImageView) view.findViewById(R.id.edt_imagen)).setImageDrawable(null);

        ((EditText) view.findViewById(R.id.edt_producto)).setBackgroundResource(R.drawable.edt_blanco);
        ((EditText) view.findViewById(R.id.edt_cantidad)).setBackgroundResource(R.drawable.edt_blanco);
        ((EditText) view.findViewById(R.id.edt_unidad)).setBackgroundResource(R.drawable.edt_blanco);
        ((EditText) view.findViewById(R.id.edt_motivo_merma)).setBackgroundResource(R.drawable.edt_blanco);
        ((ImageView) view.findViewById(R.id.edt_imagen)).setBackgroundResource(R.drawable.edt_blanco);

        btn_agregar.setText("agregar");
        btn_agregar.setBackgroundResource(R.drawable.btn_verde);

        btn_limpia_img.setBackgroundResource(R.drawable.btn_gris);
        btn_limpia_img.setOnClickListener(null);
        if (si) {
            table = (TableLayout) view.findViewById(R.id.table_producto);
            int childCount = table.getChildCount();
            if (childCount > 1) {
                table.removeViews(1, childCount - 1);
            }
        }
    }

    public void guardarData() {
        arraymerma = new JSONArray();
        final ArrayList<ModelMermaData> productos = new ArrayList<>();
        if (table.getChildCount() > 1) {
            try {
                for (int i = 1; i < table.getChildCount(); i++) {
                    TableRow row = (TableRow) table.getChildAt(i);

                    String product = ((TextView) row.getChildAt(1)).getText().toString();
                    String cant_caja = ((TextView) row.getChildAt(2)).getText().toString();
                    String cant_bote = ((TextView) row.getChildAt(3)).getText().toString();
                    String clave_mov = ((TextView) row.getChildAt(4)).getText().toString();
                    int index_clase_mov = Integer.parseInt(((TextView) row.getChildAt(4)).getTag().toString());
                    String motivo = ((TextView) row.getChildAt(5)).getText().toString();
                    BitmapDrawable drawable = (BitmapDrawable) ((ImageView) row.getChildAt(6)).getDrawable();
                    String imageString = "";
                    String img_name = "";
                    if (drawable != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bitmap bitmap = drawable.getBitmap();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        String id_almacen = ((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_almacen)).getSelectedItem()).getId();
                        String user = ((VariableGlobal) getActivity().getApplication()).getUser();
                        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        Date date = new Date();
                        String fecha_hora = dateFormat.format(date);
                        img_name = id_almacen + "_" + user + "_" + fecha_hora + "_" + i;
                    }


                    ModelMermaData modelMerma = new ModelMermaData(product, cant_caja, cant_bote, clave_mov, motivo, imageString, img_name, index_clase_mov);
                    productos.add(modelMerma);

                    arraymerma.put(i - 1, modelMerma.toJSON());

                }
                SharedPreferences myPreferences
                        = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = myPreferences.edit();
                JSONObject objJsonMERMA = new JSONObject();
                objJsonMERMA.put("FECHA", ((EditText) view.findViewById(R.id.edt_fecha)).getText().toString());
                objJsonMERMA.put("ALMACEN", ((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_almacen)).getSelectedItem()).getNombre());
                objJsonMERMA.put("CENTRO", ((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_centro)).getSelectedItem()).getNombre());
                objJsonMERMA.put("CLASE_MOV", ((ModelComboBox) ((Spinner) view.findViewById(R.id.edt_tipo_merma)).getSelectedItem()).getNombre());
                objJsonMERMA.put("MERMAS", arraymerma);
                objJsonMERMA.put("SAP_USER", ((VariableGlobal) getActivity().getApplication()).getUser());
                editor.putString("DATA", objJsonMERMA.toString());
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            view.findViewById(R.id.edt_centro).setEnabled(true);
            view.findViewById(R.id.edt_almacen).setEnabled(true);
            view.findViewById(R.id.edt_tipo_merma).setEnabled(true);
            fn_limpiar_cache();
        }
    }

    public void getData() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences myPreferences
                        = PreferenceManager.getDefaultSharedPreferences(getContext());
                final String data = myPreferences.getString("DATA", "");
                if (!data.equals("")) {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setTitleText("EXISTE UNA SOLICITUD EN CURSO");
                    sweetAlertDialog.setContentText("Desea continuar trabajando en la solicitud");
                    sweetAlertDialog.setConfirmText("SI");
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                JSONArray jsonArray = jsonObject.getJSONArray("MERMAS");
                                String centro = jsonObject.getString("CENTRO");
                                String almacen = jsonObject.getString("ALMACEN");
                                String cls_mov = jsonObject.getString("CLASE_MOV");
                                fn_selected_cls_mov(cls_mov);
                                fn_selected_centro(centro);
                                fn_selected_almacen(almacen, centro);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.raleway_mdium);
                                    Resources resources = getResources();

                                    TableRow row = new TableRow(getContext());

                                    row.setPadding(5, 5, 5, 5);
                                    if (i % 2 == 0) {
                                        row.setBackgroundResource(R.drawable.table_row);
                                    } else {
                                        row.setBackgroundResource(R.drawable.table_body_zebrado);
                                    }

                                    TableLayout.LayoutParams botones =
                                            new TableLayout.LayoutParams
                                                    (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                                    botones.width = 40;
                                    botones.height = 40;
                                    botones.setMarginEnd(20);
                                    botones.setMarginStart(20);

                                    Button btn_borrar = new Button(getContext());
                                    btn_borrar.setBackgroundResource(R.drawable.eliminar_btn);
                                    btn_borrar.setOnClickListener(btnClick);
                                    btn_borrar.setLayoutParams(botones);


                                    Button btn_modificar = new Button(getContext());
                                    btn_modificar.setBackgroundResource(R.drawable.editar);
                                    btn_modificar.setOnClickListener(btnClickModificar);
                                    btn_modificar.setLayoutParams(botones);

                                    LinearLayout linearLayout = new LinearLayout(getContext());
                                    linearLayout.addView(btn_borrar);
                                    linearLayout.addView(btn_modificar);

                                    row.addView(linearLayout);

                                    TextView produc = new TextView(getContext());
                                    produc.setTextColor(Color.BLACK);
                                    produc.setPadding(5, 0, 5, 0);
                                    produc.setGravity(Gravity.CENTER);
                                    produc.setTypeface(typeface);
                                    produc.setText(jsonArray.getJSONObject(i).getString("PRODUCTO"));
                                    row.addView(produc);

                                    TextView cant = new TextView(getContext());
                                    cant.setTextColor(Color.BLACK);
                                    cant.setPadding(5, 0, 5, 0);
                                    cant.setGravity(Gravity.CENTER);
                                    cant.setTypeface(typeface);
                                    cant.setText(jsonArray.getJSONObject(i).getString("CANT_CAJA"));
                                    row.addView(cant);

                                    TextView uni = new TextView(getContext());
                                    uni.setTextColor(Color.BLACK);
                                    uni.setPadding(5, 0, 5, 0);
                                    uni.setGravity(Gravity.CENTER);
                                    uni.setTypeface(typeface);
                                    uni.setText(jsonArray.getJSONObject(i).getString("CANT_BOTE"));
                                    row.addView(uni);

                                    TextView tip_merma = new TextView(getContext());
                                    tip_merma.setTextColor(Color.BLACK);
                                    tip_merma.setPadding(5, 0, 5, 0);
                                    tip_merma.setGravity(Gravity.CENTER);
                                    tip_merma.setTypeface(typeface);
                                    tip_merma.setText(jsonArray.getJSONObject(i).getString("CLASE_MOV"));
                                    tip_merma.setTag(jsonArray.getJSONObject(i).getString("CLASE_MOV_INDEX"));
                                    row.addView(tip_merma);

                                    TextView motivo = new TextView(getContext());
                                    motivo.setVisibility(View.GONE);
                                    motivo.setText(jsonArray.getJSONObject(i).getString("MOTIVO"));
                                    row.addView(motivo);

                                    ImageView imageView = new ImageView(getContext());
                                    imageView.setImageDrawable(null);
                                    imageView.setVisibility(View.GONE);

                                    row.addView(imageView);
                                    row.setId(i);
                                    table.addView(row);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            sDialog.dismissWithAnimation();
                        }
                    });
                    sweetAlertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            fn_limpiar_cache();
                            sDialog.dismissWithAnimation();
                        }
                    });
                    sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            fn_limpiar_cache();
                            dialog.cancel();
                        }
                    });
                    sweetAlertDialog.show();
                    botonesSweet(sweetAlertDialog);
                }
            }
        });
    }

    void fn_limpiar_cache() {
        SharedPreferences myPreferences
                = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("DATA", "");
        editor.commit();
    }

    void fn_selected_centro(final String centro) {
        loadigcentro.setVisibility(View.VISIBLE);
        String user = ((VariableGlobal) getActivity().getApplication()).getUser();
        String clave = ((VariableGlobal) getActivity().getApplication()).getClave();

        RequestParams requestParams = new RequestParams();
        requestParams.put("I_OPCION", 2);
        requestParams.put("SAP_USER", user);
        requestParams.put("SAP_CLAVE", clave);

        HttpUtils.post(Rest.ZRFC_MANT_VM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 200) {
                    try {
                        ArrayList<ModelComboBox> modelComboBoxes = new ArrayList<>();

                        JSONArray t_trxal = response.getJSONArray("T_USERS");

                        ModelComboBox selected = new ModelComboBox("", "Seleccione");
                        modelComboBoxes.add(selected);

                        for (int i = 0; i < t_trxal.length(); i++) {
                            ModelComboBox modelComboBox = new ModelComboBox();
                            modelComboBox.setId(t_trxal.getJSONObject(i).getString("CENTRO"));
                            modelComboBox.setNombre(t_trxal.getJSONObject(i).getString("CENTRO"));
                            modelComboBoxes.add(modelComboBox);
                        }

                        Spinner spinnercentro = view.findViewById(R.id.edt_centro);

                        ArrayAdapter<ModelComboBox> arrayAdapter =
                                new ArrayAdapter<ModelComboBox>(getContext(), R.layout.spinner_item, modelComboBoxes);
                        spinnercentro.setAdapter(arrayAdapter);
                        if (modelComboBoxes.size() == 2) {
                            spinnercentro.setSelection(1);
                        }
                        spinnercentro.setOnItemSelectedListener(selectedListener);
                        int posicion = 0;
                        for (int i = 0; i < arrayAdapter.getCount(); i++) {
                            ModelComboBox comboBox = (ModelComboBox) arrayAdapter.getItem(i);
                            if (comboBox.getNombre().equalsIgnoreCase(centro)) {
                                posicion = i;
                                break;
                            }
                        }
                        mIsSpinnerFirstCall = false;
                        spinnercentro.setSelection(posicion);
                        spinnercentro.setEnabled(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadigcentro.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("xxx", "onSuccess" + statusCode);
                loadigcentro.setVisibility(View.INVISIBLE);
            }

        });
    }

    void fn_selected_cls_mov(final String cls_mov) {
        loadingclase.setVisibility(View.VISIBLE);
        String user = ((VariableGlobal) getActivity().getApplication()).getUser();
        String clave = ((VariableGlobal) getActivity().getApplication()).getClave();

        RequestParams requestParams = new RequestParams();
        requestParams.put("I_OPCION", 1);
        requestParams.put("SAP_USER", user);
        requestParams.put("SAP_CLAVE", clave);

        HttpUtils.post(Rest.ZRFC_MANT_VM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ArrayList<ModelComboBox> modelComboBoxes = new ArrayList<>();

                    JSONArray t_trxal = response.getJSONArray("T_BWART");

                    ModelComboBox selected = new ModelComboBox("", "Seleccione");
                    modelComboBoxes.add(selected);
                    for (int i = 0; i < t_trxal.length(); i++) {
                        ModelComboBox modelComboBox = new ModelComboBox();
                        modelComboBox.setId(t_trxal.getJSONObject(i).getString("CLSMOV"));
                        modelComboBox.setNombre(t_trxal.getJSONObject(i).getString("CLSMOV") + " " + t_trxal.getJSONObject(i).getString("CLMOV_TEXT"));
                        modelComboBoxes.add(modelComboBox);
                    }

                    Spinner spinnerAlmacen = (Spinner) view.findViewById(R.id.edt_tipo_merma);
                    ArrayAdapter<ModelComboBox> arrayAdapter =
                            new ArrayAdapter<ModelComboBox>(getContext(),
                                    R.layout.spinner_item,
                                    modelComboBoxes);
                    arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                    spinnerAlmacen.setAdapter(arrayAdapter);
                    int posicion = 0;
                    for (int i = 0; i < arrayAdapter.getCount(); i++) {
                        ModelComboBox comboBox = (ModelComboBox) arrayAdapter.getItem(i);
                        if (comboBox.getNombre().equalsIgnoreCase(cls_mov)) {
                            posicion = i;
                            break;
                        }
                    }
                    spinnerAlmacen.setSelection(posicion);
                    spinnerAlmacen.setEnabled(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadingclase.setVisibility(View.INVISIBLE);
            }
        });
    }

    void fn_selected_almacen(final String almacen, String centro) {
        loadingalmacen.setVisibility(View.VISIBLE);
        String user = ((VariableGlobal) getActivity().getApplication()).getUser();
        String clave = ((VariableGlobal) getActivity().getApplication()).getClave();

        RequestParams requestParams = new RequestParams();
        requestParams.put("I_OPCION", 4);
        requestParams.put("I_WERKS", centro);
        requestParams.put("SAP_USER", user);
        requestParams.put("SAP_CLAVE", clave);

        HttpUtils.post(Rest.ZRFC_MANT_VM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 200) {
                    try {
                        ArrayList<ModelComboBox> modelComboBoxes = new ArrayList<>();
                        JSONArray t_trxal = response.getJSONArray("T_TRXAL");
                        ModelComboBox selected = new ModelComboBox("", "Seleccione");
                        modelComboBoxes.add(selected);
                        for (int i = 0; i < t_trxal.length(); i++) {
                            ModelComboBox modelComboBox = new ModelComboBox();
                            modelComboBox.setId(t_trxal.getJSONObject(i).getString("ALMACEN"));
                            modelComboBox.setNombre(t_trxal.getJSONObject(i).getString("ALMACEN"));
                            modelComboBoxes.add(modelComboBox);
                        }
                        Spinner spinnerAlmacen = view.findViewById(R.id.edt_almacen);
                        ArrayAdapter<ModelComboBox> arrayAdapter =
                                new ArrayAdapter<ModelComboBox>(getContext(), R.layout.spinner_item, modelComboBoxes);
                        spinnerAlmacen.setAdapter(arrayAdapter);
                        int posicion = 0;
                        for (int i = 0; i < modelComboBoxes.size(); i++) {
                            if (modelComboBoxes.get(i).getNombre().equalsIgnoreCase(almacen)) {
                                posicion = i;
                                break;
                            }
                        }
                        spinnerAlmacen.setSelection(posicion, false);
                        spinnerAlmacen.setEnabled(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadingalmacen.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("xxx", "onSuccess" + statusCode);
                loadingalmacen.setVisibility(View.INVISIBLE);
            }

        });

    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            Context context = getContext();
            btn_delete = (Button) v;
            if (btn_agregar.getText().toString().equalsIgnoreCase("modificar")) {
                SweetAlertDialog confirm = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                confirm.setTitleText("Información");
                confirm.setContentText("Debe cancelar o modificar actual registro en edición");
                confirm.setConfirmText("ACEPTAR");
                confirm.show();
                botonesSweet(confirm);
            } else {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertDialog.setTitleText("Información");
                sweetAlertDialog.setContentText("¿Desea eliminar el registro?");
                sweetAlertDialog.setConfirmText("SI");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Button btn = (Button) btn_delete;
                        btn_delete.setBackgroundResource(R.drawable.btn_gris);
                        btn_delete.setOnClickListener(null);
                        LinearLayout ln = (LinearLayout) btn.getParent();
                        TableRow ve = (TableRow) ln.getParent();
                        TableLayout tableLayout = (TableLayout) ve.getParent();
                        tableLayout.removeView(ve);
                        if (table.getChildCount() == 1) {
                            view.findViewById(R.id.edt_centro).setEnabled(true);
                        }
                        guardarData();
                        sDialog.dismissWithAnimation();
                    }
                });
                sweetAlertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });
                sweetAlertDialog.show();
                botonesSweet(sweetAlertDialog);
            }
        }
    };
    private View.OnClickListener btnClickModificar = new View.OnClickListener() {
        public void onClick(View v) {
            Context context = getContext();
            btn_edit = (Button) v;
            if (btn_agregar.getText().toString().equalsIgnoreCase("modificar")) {
                SweetAlertDialog confirm = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                confirm.setTitleText("Información");
                confirm.setContentText("Debe cancelar o modificar actual registro en edición");
                confirm.setConfirmText("ACEPTAR");
                confirm.show();
                botonesSweet(confirm);
            } else {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertDialog.setTitleText("Información");
                sweetAlertDialog.setContentText("¿Desea modificar el registro?");
                sweetAlertDialog.setConfirmText("SI");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        fn_limpiar_form(false);
                        btn_agregar.setText("Modificar");
                        btn_agregar.setBackgroundResource(R.drawable.btn_naranjo);
                        Button btn = (Button) btn_edit;
                        LinearLayout ln = (LinearLayout) btn.getParent();
                        TableRow ve = (TableRow) ln.getParent();
                        btn_agregar.setTag(ve.getId());
                        TextView pro = (TextView) ve.getChildAt(1);
                        TextView caj = (TextView) ve.getChildAt(2);
                        TextView bot = (TextView) ve.getChildAt(3);
                        TextView tip = (TextView) ve.getChildAt(4);
                        TextView mot = (TextView) ve.getChildAt(5);
                        ImageView adj = (ImageView) ve.getChildAt(6);

                        ((EditText) view.findViewById(R.id.edt_producto)).setText(pro.getText().toString());
                        ((EditText) view.findViewById(R.id.edt_cantidad)).setText(caj.getText().toString());
                        ((EditText) view.findViewById(R.id.edt_unidad)).setText(bot.getText().toString());
                        ((Spinner) view.findViewById(R.id.edt_tipo_merma)).setSelection(Integer.parseInt(tip.getTag().toString()));
                        ((EditText) view.findViewById(R.id.edt_motivo_merma)).setText(mot.getText().toString());
                        if (adj.getDrawable() == null) {
                            btn_limpia_img.setOnClickListener(null);
                            btn_limpia_img.setBackgroundResource(R.drawable.btn_gris);
                        } else {
                            btn_limpia_img.setOnClickListener(eliminar_adj);
                            btn_limpia_img.setBackgroundResource(R.drawable.btn_rojo_form);
                        }
                        ((ImageView) view.findViewById(R.id.edt_imagen)).setImageDrawable(adj.getDrawable());

                        sDialog.dismissWithAnimation();
                    }
                });
                sweetAlertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        fn_limpiar_form(false);
                        sDialog.dismissWithAnimation();
                    }
                });
                sweetAlertDialog.show();
                botonesSweet(sweetAlertDialog);
            }
        }
    };
    Spinner.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mIsSpinnerFirstCall) {
                if (position > 0) {
                    fn_combobox_almacen();
                }
            } else {
                mIsSpinnerFirstCall = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
