package cl.rialsoft.appMERMA;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

import static cl.rialsoft.appMERMA.Funciones.botonesSweet;
import static cl.rialsoft.appMERMA.Funciones.mensajeSweet;

public class ModalModificar extends DialogFragment {
    private int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 222;
    private ImageView imageView;
    private Button cerrar, adj_img, btn_modificar,btn_img_limpiar;
    private ModelConsulta modelConsulta;
    private ArrayList<ModelComboBox> almacen;
    private ArrayList<ModelComboBox> clase_mov;
    private EditText clase_mov_spinner;
    private View view;
    private Context context;
    private ProgressBar progressBar;
    private EditText almacen_spinner;
    public void setData(ModelConsulta modelConsulta, ArrayList<ModelComboBox> almacen, ArrayList<ModelComboBox> clase_mov) {
        this.modelConsulta = modelConsulta;
        this.almacen = almacen;
        this.clase_mov = clase_mov;
    }
    public ModelConsulta getModelConsulta() {
        return modelConsulta;
    }
    public void setModelConsulta(ModelConsulta modelConsulta) {
        this.modelConsulta = modelConsulta;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.form_modificar, container, false);
        cerrar = view.findViewById(R.id.btn_salir);
        imageView = view.findViewById(R.id.edt_adj_img);
        adj_img = view.findViewById(R.id.btn_adj_imagen);
        clase_mov_spinner = (EditText)view.findViewById(R.id.edt_tipo_merma_con);
        almacen_spinner = view.findViewById(R.id.edt_producto);
        btn_modificar = view.findViewById(R.id.btn_modificar);
        btn_modificar.setOnClickListener(clickModificar);
        progressBar = view.findViewById(R.id.progressBar1);
        context = getContext();
        init();
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        adj_img.setOnClickListener(clickImagen);
        btn_img_limpiar = view.findViewById(R.id.limpiar_adj_imagen);
        btn_img_limpiar.setBackgroundResource(R.drawable.btn_gris);
        btn_img_limpiar.setOnClickListener(null);

        return view;
    }
    View.OnClickListener limpiar_img = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
            sweetAlertDialog.setTitleText("Información");
            sweetAlertDialog.setContentText("¿Desea eliminar el adjunto?");
            sweetAlertDialog.setConfirmText("SI");
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    imageView.setImageDrawable(null);
                    btn_img_limpiar.setBackgroundResource(R.drawable.btn_gris);
                    btn_img_limpiar.setOnClickListener(null);
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
    View.OnClickListener clickImagen = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    };
    View.OnClickListener clickModificar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int validar = 0;
            EditText pro = ((EditText) view.findViewById(R.id.btn_scanear));
            EditText caja = ((EditText) view.findViewById(R.id.edt_cantidad));
            EditText bot = ((EditText) view.findViewById(R.id.edt_unidad));
            EditText mot = ((EditText) view.findViewById(R.id.edt_motivo));
            final EditText alma = ((EditText) view.findViewById(R.id.edt_producto));
            //Spinner clase = ((Spinner) view.findViewById(R.id.edt_tipo_merma_con));

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
            if (mot.getText().toString().equals("") || mot.getText().toString().equals(null)) {
                validar = 1;
                marcar_borde(mot, true);
            } else {
                validar = validar == 0 ? 0 : validar--;
                marcar_borde(mot, false);
            }
           /* if (clase.getSelectedItemPosition() == 0) {
                validar = 1;
                clase.setBackgroundResource(R.drawable.edt_gris_error);
            } else {
                clase.setBackgroundResource(R.drawable.edt_blanco);
                validar = validar == 0 ? 0 : validar--;
            }*/
            boolean valida = false;
            if (validar == 0) {
                int cant = 0;
                if (Integer.parseInt(bot.getText().toString())==0 && Integer.parseInt(caja.getText().toString())==0){
                    cant++;
                    marcar_borde(caja,true);
                    marcar_borde(bot, true);
                } else {
                    cant = cant == 0 ? 0 : cant--;
                    marcar_borde(bot, false);
                    marcar_borde(caja,false);
                }
                if (cant>0){
                    valida=false;
                    mensajeSweet(getContext(),"Aceptar","Las cantidades deben ser mayor a 0",SweetAlertDialog.SUCCESS_TYPE);
                }else{
                    valida=true;
                }

            }
            if (valida) {
                String user = ((VariableGlobal) getActivity().getApplication()).getUser();
                String clave = ((VariableGlobal) getActivity().getApplication()).getClave();
                int cod = Integer.parseInt(((EditText) view.findViewById(R.id.btn_scanear)).getText().toString());
                RequestParams requestParams = new RequestParams();
                requestParams.put("I_MATNR", cod);
                requestParams.put("SAP_USER", user);
                requestParams.put("SAP_CLAVE", clave);

                HttpUtils.post(Rest.ZRFC_VL_MERMA_UNIT, requestParams, new BaseJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                        String res = "";
                        try {
                            JSONObject jsonObject = new JSONObject(rawJsonResponse);
                            res = jsonObject.getString("E_RETURN");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (res.equals("0")) {
                            SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                            alertDialog.setTitleText("Información");
                            alertDialog.setContentText("¿Desea modificar el registro?");
                            alertDialog.setConfirmText("SI");
                            alertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    RequestParams requestParams = new RequestParams();
                                    String id = ((EditText) view.findViewById(R.id.edt_fol_sol)).getTag().toString();
                                    final String pro = ((EditText) view.findViewById(R.id.btn_scanear)).getText().toString();
                                    final String caja = ((EditText) view.findViewById(R.id.edt_cantidad)).getText().toString();
                                    final String bote = ((EditText) view.findViewById(R.id.edt_unidad)).getText().toString();
                                    final String motivo = ((EditText) view.findViewById(R.id.edt_motivo)).getText().toString();
                                    final String clase =""; //((ModelComboBox) ((Spinner) clase_mov_spinner).getSelectedItem()).getId();
                                    final String almacen = alma.getText().toString();
                                    final String clase_mov_nombre = clase_mov_spinner.getText().toString();//((ModelComboBox) ((Spinner) clase_mov_spinner).getSelectedItem()).getNombre();
                                    BitmapDrawable drawable = (BitmapDrawable) ((ImageView) view.findViewById(R.id.edt_adj_img)).getDrawable();
                                    String imageString="";
                                    String img_nombre="";
                                    try {
                                        Bitmap image = drawable.getBitmap();
                                        if (image==null){
                                            img_nombre="";
                                        }else{
                                            String user = ((VariableGlobal) getActivity().getApplication()).getUser();
                                            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                                            Date date = new Date();
                                            String fecha_hora = dateFormat.format(date);
                                            img_nombre = almacen + "_" + user + "_" + fecha_hora;

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            Bitmap bitmap = drawable.getBitmap();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] imageBytes = baos.toByteArray();
                                            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                        }
                                    }catch (Exception e){
                                        img_nombre = "";
                                    }

                                    final String img_name = img_nombre;

                                     String user = ((VariableGlobal) getActivity().getApplication()).getUser();
                                    String clave = ((VariableGlobal) getActivity().getApplication()).getClave();
                                    requestParams.add("ID", id);
                                    requestParams.add("ALMACEN", almacen);
                                    requestParams.add("MOTIVO", motivo);
                                    requestParams.add("PRODUCTO", pro);
                                    requestParams.add("CANT_CAJA", caja);
                                    requestParams.add("CANT_BOTE", bote);
                                    requestParams.add("CLASE_MOV", clase);
                                    requestParams.add("IMAGE", imageString);
                                    requestParams.add("IMG_NAME", img_nombre);
                                    requestParams.add("SAP_USER", user);
                                    requestParams.add("SAP_CLAVE",clave);

                                    HttpUtils.post(Rest.ZRFC_MD_MERMA, requestParams, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            try {
                                                if (response.getString("E_RETURN").equals("0")) {
                                                    modelConsulta.setAlmacen(almacen);
                                                    modelConsulta.setClase_mov(clase);
                                                    modelConsulta.setClase_mov_nombre(clase);
                                                    modelConsulta.setMotivo(motivo);
                                                    modelConsulta.setProducto(pro);
                                                    modelConsulta.setCant_cajas(caja);
                                                    modelConsulta.setCant_botellas(bote);
                                                    modelConsulta.setClase_mov_nombre(clase_mov_nombre);
                                                    String img = "/ImagesFolder/" + img_name + ".jpg";
                                                    modelConsulta.setImagen(img);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    mensajeSweet(context,"Aceptar","Actualizado",SweetAlertDialog.SUCCESS_TYPE);
                                                }else{
                                                    mensajeSweet(context,"Aceptar",response.getString("E_MESSAGE"),SweetAlertDialog.SUCCESS_TYPE);
                                                }
                                                dismiss();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            mensajeSweet(context,"Aceptar","Error: "+statusCode,SweetAlertDialog.SUCCESS_TYPE);
                                        }
                                    });
                                    sDialog.dismissWithAnimation();
                                }
                            });
                            alertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            });
                            alertDialog.show();
                            botonesSweet(alertDialog);
                        }else{
                            mensajeSweet(context,"Aceptar","El producto no existe",SweetAlertDialog.ERROR_TYPE);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                    }

                    @Override
                    protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                        return null;
                    }
                });
            }else{
                if(validar>0){
                    mensajeSweet(getContext(), "Aceptar", "Complete los campos", SweetAlertDialog.ERROR_TYPE);
                }
            }
        }
    };
    public void init() {
     /*   ArrayAdapter<ModelComboBox> arrayAdapter =
                new ArrayAdapter<>(getContext(), R.layout.spinner_item, clase_mov);
        clase_mov_spinner.setAdapter(arrayAdapter);
        int index = 0;
        String selected = modelConsulta.getClase_mov();
        for (int i = 0; i < clase_mov.size(); i++) {
            if (clase_mov.get(i).getId().equals(selected)) {
                index = i;
                break;
            }
        }
        clase_mov_spinner.setSelection(index);*/
        clase_mov_spinner.setText(modelConsulta.getClase_mov_nombre());
        almacen_spinner.setText(modelConsulta.getAlmacen());

        btn_img_limpiar = view.findViewById(R.id.limpiar_adj_imagen);
        new DownloadImageFromInternet(imageView,btn_img_limpiar).execute(Rest.BASE_RECURSO + modelConsulta.getImagen());

        ((EditText) view.findViewById(R.id.edt_centro)).setText(modelConsulta.getCentro());
        ((EditText) view.findViewById(R.id.edt_fol_sol)).setTag(modelConsulta.getId());
        ((EditText) view.findViewById(R.id.edt_fol_sol)).setText(modelConsulta.getFolio());
        ((EditText) view.findViewById(R.id.btn_scanear)).setText(modelConsulta.getProducto());
        ((EditText) view.findViewById(R.id.edt_pro_nombre)).setText(modelConsulta.getPro_nombre());
        ((EditText) view.findViewById(R.id.edt_cantidad)).setText(modelConsulta.getCant_cajas());
        ((EditText) view.findViewById(R.id.edt_unidad)).setText(modelConsulta.getCant_botellas());
        ((EditText) view.findViewById(R.id.edt_motivo)).setText(modelConsulta.getMotivo());
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

                btn_img_limpiar.setOnClickListener(limpiar_img);
                btn_img_limpiar.setBackgroundResource(R.drawable.btn_rojo_form);
            }
        }
    }
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Button button;

        public DownloadImageFromInternet(ImageView imageView,Button button) {
            this.imageView = imageView;
            this.button = button;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            if(bimage==null){
                button.setOnClickListener(null);
                button.setBackgroundResource(R.drawable.btn_gris);
            }else{
                button.setOnClickListener(limpiar_img);
                button.setBackgroundResource(R.drawable.btn_rojo_form);
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
    public void marcar_borde(EditText editText, boolean val) {
        if (val) {
            editText.setBackgroundResource(R.drawable.edt_gris_error);
        } else {
            editText.setBackgroundResource(R.drawable.edt_blanco);
        }
    }


}
