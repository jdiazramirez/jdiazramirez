package cl.rialsoft.appMERMA;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.channels.AlreadyBoundException;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLDisplay;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class FormListAdapter extends BaseAdapter {
    private int posicion;
    private ImageView imagen;
    private String id;
    private ArrayList<ModelConsulta> mermas;
    private ArrayList<ModelComboBox> comboBoxes, comboAlamacen;
    private LayoutInflater inflter;
    private Context context;
    private EditText producto, cant_caja, cant_bote, motivo, folio, pro_nombre;
    private Spinner clase_mov, almacen;
    private Button btn_delete, btn_modificar;
    private BaseAdapter baseAdapter = this;
    private String user;
    private String clave;

    public FormListAdapter(ArrayList<ModelConsulta> mermas, ArrayList<ModelComboBox> comboBoxes, ArrayList<ModelComboBox> comboAlamacen, Context context,String user,String clave) {
        this.mermas = mermas;
        this.comboBoxes = comboBoxes;
        this.comboAlamacen = comboAlamacen;
        this.context = context;
        this.user = user;
        this.clave = clave;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return mermas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.list_view_row, null);

        EditText centro = convertView.findViewById(R.id.edt_centro);
        EditText almacen = convertView.findViewById(R.id.edt_producto);
        producto = convertView.findViewById(R.id.btn_scanear);
        cant_caja = convertView.findViewById(R.id.edt_cantidad);
        cant_bote = convertView.findViewById(R.id.edt_unidad);
        EditText clase_mov = convertView.findViewById(R.id.edt_tipo_merma_con);
        motivo = convertView.findViewById(R.id.edt_motivo);
        btn_delete = convertView.findViewById(R.id.btn_eliminar);
        btn_modificar = convertView.findViewById(R.id.btn_modificar);
        imagen = convertView.findViewById(R.id.edt_adj);
        folio = convertView.findViewById(R.id.edt_fol_sol);
        pro_nombre = convertView.findViewById(R.id.edt_pro_nombre);

        String selected = mermas.get(position).getClase_mov_nombre();
        clase_mov.setText(selected);
        clase_mov.setEnabled(false);

        String selectedAlm = mermas.get(position).getAlmacen();
        String selectedCentro = mermas.get(position).getCentro();

        almacen.setText(selectedAlm);
        almacen.setEnabled(false);
        centro.setText(selectedCentro);
        centro.setEnabled(false);
        btn_delete.setTag(position);
        btn_modificar.setTag(position);

        producto.setText(mermas.get(position).getProducto());
        cant_caja.setText(mermas.get(position).getCant_cajas());
        cant_bote.setText(mermas.get(position).getCant_botellas());
        motivo.setText(mermas.get(position).getMotivo());
        pro_nombre.setText(mermas.get(position).getPro_nombre());
        folio.setText(mermas.get(position).getFolio());
        folio.setTag(mermas.get(position).getId());

        try {
            new DownloadImageFromInternet(imagen).execute(Rest.BASE_RECURSO + mermas.get(position).getImagen());
        } catch (Exception e) {
            Log.e("xx", e.getMessage());
        }
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog settingsDialog = new Dialog(context);
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(inflter.inflate(R.layout.image_view, null));
                settingsDialog.show();

                ImageView img = (ImageView) settingsDialog.findViewById(R.id.adj_img);
                img.setImageDrawable(imagen.getDrawable());

            }
        });
        if(!mermas.get(position).getApro().equalsIgnoreCase("C")){
            btn_delete.setOnClickListener(btn_eliminar);
            btn_modificar.setOnClickListener(btn_modificarClick);
        }else{
            btn_delete.setOnClickListener(btn_mensaje);
            btn_modificar.setOnClickListener(btn_mensaje);
        }
        EditText estado = convertView.findViewById(R.id.edt_estado);
        inputEstado(estado,mermas.get(position).getApro());
        return convertView;
    }
    public void inputEstado(EditText editText,String estado){
        //C = CONFIRMADOS / T = PARCIAL / N = PENDIENTE
        switch (estado){
            case "C":
                editText.setText("CONFIRMADO");
                editText.setBackgroundResource(R.drawable.estado_confirm);
                break;
            case "T":
                editText.setText("PARCIAL");
                editText.setTextColor(Color.BLACK);
                break;
            case "N":
                editText.setText("PENDIENTE");
                editText.setBackgroundResource(R.drawable.edt_rojo);
                break;
        }
    }
    public View.OnClickListener btn_mensaje = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Funciones.mensajeSweet(context, "Aceptar", "No puede modificar una solicitud confirmada", SweetAlertDialog.ERROR_TYPE);
        }
    };
    public View.OnClickListener btn_modificarClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            posicion = ((int) v.getTag());
            FragmentActivity activity = (FragmentActivity) (context);
            FragmentManager fm = activity.getSupportFragmentManager();
            final ModalModificar alertDialog = new ModalModificar();
            alertDialog.setData(mermas.get(posicion), comboAlamacen, comboBoxes);
            alertDialog.show(fm, "fragment_alert");
            fm.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                    super.onFragmentViewDestroyed(fm, f);
                    mermas.set(posicion,alertDialog.getModelConsulta());
                    baseAdapter.notifyDataSetChanged();
                    fm.unregisterFragmentLifecycleCallbacks(this);
                }
            }, false);
        }
    };
    public View.OnClickListener btn_eliminar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            posicion = ((int) v.getTag());
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
            sweetAlertDialog.setTitleText("Información");
            sweetAlertDialog.setContentText("¿Desea eliminar este registro");
            sweetAlertDialog.setConfirmText("SI");
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(final SweetAlertDialog sDialog) {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("I_IDENT", mermas.get(posicion).getId());
                    requestParams.add("SAP_USER",user);
                    requestParams.add("SAP_CLAVE",clave);
                    HttpUtils.post(Rest.ZRFC_DL_MERMA, requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String RESULT = response.getString("E_RETURN");
                                String MENSAJE = response.getString("E_MESSAGE");
                                if (RESULT.equals("0")) {
                                    Funciones.mensajeSweet(context, "Aceptar", "Eliminado", SweetAlertDialog.ERROR_TYPE);
                                }else{
                                    Funciones.mensajeSweet(context, "Aceptar",MENSAJE , SweetAlertDialog.ERROR_TYPE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Funciones.mensajeSweet(context, "Aceptar", "Error: " + statusCode, SweetAlertDialog.ERROR_TYPE);
                        }
                    });
                    mermas.remove(posicion);
                    baseAdapter.notifyDataSetChanged();
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
            Funciones.botonesSweet(sweetAlertDialog);
        }
    };

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
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
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
