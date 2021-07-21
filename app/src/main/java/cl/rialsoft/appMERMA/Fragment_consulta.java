package cl.rialsoft.appMERMA;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class Fragment_consulta extends Fragment implements View.OnClickListener{
    private TextView fecha;
    private Spinner cbx_estado,cbx_busqueda;
    private Button btn_fecha,btn_limpiar;
    private View view;
    private DatePickerDialog.OnDateSetListener fechasetlistener;
    private ArrayList<ModelComboBox> modelComboBoxes,modelComboBoxesAlm;
    private ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_consulta,null);
        fn_combobox_tip_merma();
        //fn_combobox_almacen();
        fn_combobox_estado();
        fecha = (EditText)view.findViewById (R.id.edt_fecha);
        btn_fecha = (Button)view.findViewById(R.id.btn_buscar);
        btn_fecha.setOnClickListener(this);
        btn_limpiar = (Button)view.findViewById(R.id.btn_limpiar);
        btn_limpiar.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar1);

        ArrayList<ModelComboBox> modelCbx = new ArrayList<>();
        ModelComboBox selected = new ModelComboBox("","Seleccione");
        modelCbx.add(selected);
        ArrayAdapter<ModelComboBox> arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item,modelCbx);
        cbx_busqueda = view.findViewById(R.id.cbx_busqueda);
        cbx_busqueda.setAdapter(arrayAdapter);


        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        fechasetlistener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        fn_fecha_actual();
        fechasetlistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(final DatePicker datePicker, int year, int month, int dayOfMonth) {
                //String date = Funciones.twoDigits(dayOfMonth)+"-"+Funciones.twoDigits(month+1)+"-"+year;
                String date = year+"-"+Funciones.twoDigits(month+1)+"-"+Funciones.twoDigits(dayOfMonth);
                fecha.setText(date);
                fn_buscar_mermas(date);
            }
        };
        return view;
    }
    Spinner.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String date = fecha.getText().toString();
            fn_buscar_mermas(date);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    public void fn_buscar_mermas(String date){
        String user = ((VariableGlobal) getActivity().getApplication()).getUser();
        String clave = ((VariableGlobal) getActivity().getApplication()).getClave();
        RequestParams requestParams = new RequestParams();
        requestParams.put("I_FECHA",date);
        requestParams.put("I_CHECK",((ModelComboBox)cbx_estado.getSelectedItem()).getId());
        requestParams.put("I_USER",user);
        requestParams.put("SAP_USER",user);
        requestParams.put("SAP_CLAVE",clave);

        HttpUtils.post(Rest.ZRFC_CB_MERMA,requestParams,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    ArrayList<ModelComboBox> modelCbx = new ArrayList<>();

                    JSONArray t_combo = response;

                    ModelComboBox selected = new ModelComboBox("","Seleccione");
                    modelCbx.add(selected);

                    for(int i=0;i<t_combo.length();i++){
                        ModelComboBox modelComboBox = new ModelComboBox();
                        modelComboBox.setId(t_combo.getJSONObject(i).getString("FOLIO"));
                        int folio = Integer.parseInt(t_combo.getJSONObject(i).getString("FOLIO"));
                        String text = "Folio: "+Integer.toString(folio)+
                                " Centro: "+t_combo.getJSONObject(i).getString("CENTRO")+
                                " Almacen: "+t_combo.getJSONObject(i).getString("ALMACEN")+
                                " Cl.mov: "+t_combo.getJSONObject(i).getString("CLASE_MOV")+
                                " Hora: "+t_combo.getJSONObject(i).getString("HORA");
                        modelComboBox.setNombre(text);
                        modelCbx.add(modelComboBox);
                    }
                    ArrayAdapter<ModelComboBox> arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item,modelCbx);
                    cbx_busqueda = view.findViewById(R.id.cbx_busqueda);
                    cbx_busqueda.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public void fn_fecha_actual(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        //String date = Funciones.twoDigits(day)+"-"+Funciones.twoDigits(month+1)+"-"+year;
        String date = year+"-"+Funciones.twoDigits(month+1)+"-"+Funciones.twoDigits(day);
        fecha.setText(date);
        fn_buscar_mermas(date);
    }
    @Override
    public void onClick(View v) {
        switch  (v.getId()){
            case R.id.btn_buscar:
                fn_consulta_merma();
                break;
            case R.id.btn_limpiar:
                fn_clear_list();
                limpiar_form();
                fn_fecha_actual();
                break;
        }
    }
    public void fn_consulta_merma(){
        if (fecha.getText().toString().equals("")||fecha.getText().toString().equals(null)){
            fecha.setBackgroundResource(R.drawable.edt_gris_error);
            Funciones.mensajeSweet(getContext(),"Aceptar","Ingrese la fecha",SweetAlertDialog.SUCCESS_TYPE);
        }else{
            progressBar.setVisibility(View.VISIBLE);
            fecha.setBackgroundResource(R.drawable.edt_gris);
            String user = ((VariableGlobal) getActivity().getApplication()).getUser();
            String clave = ((VariableGlobal) getActivity().getApplication()).getClave();
            RequestParams requestParams = new RequestParams();
            requestParams.add("I_FECHA",fecha.getText().toString());
            requestParams.add("I_CHECK",((ModelComboBox)cbx_estado.getSelectedItem()).getId());
            requestParams.add("I_USER",fecha.getText().toString());
            requestParams.add("I_FOLIO",((ModelComboBox)cbx_busqueda.getSelectedItem()).getId());
            requestParams.add("SAP_USER",user);
            requestParams.add("SAP_CLAVE",clave);
            HttpUtils.post(Rest.ZRFC_RD_VM,requestParams, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (statusCode==200){
                        if(response.length()>0){
                            ArrayList<ModelConsulta> mermas = new ArrayList<>();
                            fn_clear_list();
                            for(int i=0;i<response.length();i++){
                                try {
                                    ModelConsulta modelConsulta = new ModelConsulta();
                                    modelConsulta.setId(response.getJSONObject(i).getString("ID"));
                                    int folio = Integer.parseInt(response.getJSONObject(i).getString("FOLIO"));
                                    modelConsulta.setFolio(Integer.toString(folio));
                                    modelConsulta.setFecha_ing_doc(response.getJSONObject(i).getString("F_DIGIT"));
                                    modelConsulta.setUsuario_dig(response.getJSONObject(i).getString("DGTA_TEXT"));
                                    modelConsulta.setAlmacen(response.getJSONObject(i).getString("ALMACEN"));
                                    int material = Integer.parseInt(response.getJSONObject(i).getString("MATERIAL"));
                                    modelConsulta.setProducto(Integer.toString(material));
                                    modelConsulta.setCant_cajas(response.getJSONObject(i).getString("CAJAS"));
                                    modelConsulta.setCant_botellas(response.getJSONObject(i).getString("BOTELLAS"));
                                    modelConsulta.setClase_mov(response.getJSONObject(i).getString("CLSMOV"));
                                    modelConsulta.setMotivo(response.getJSONObject(i).getString("MOTIVO"));
                                    modelConsulta.setImagen(response.getJSONObject(i).getString("ARCHIVO"));
                                    modelConsulta.setPro_nombre(response.getJSONObject(i).getString("MAT_TEXT"));
                                    modelConsulta.setApro(response.getJSONObject(i).getString("APROB"));
                                    modelConsulta.setCentro(response.getJSONObject(i).getString("CENTRO"));
                                    modelConsulta.setClase_mov_nombre(response.getJSONObject(i).getString("CLSMOV")+" "+
                                            response.getJSONObject(i).getString("CLMOV_TEXT"));
                                    mermas.add(modelConsulta);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            ListView listView = (ListView)view.findViewById(R.id.lista_form);
                            String user = ((VariableGlobal) getActivity().getApplication()).getUser();
                            String clave = ((VariableGlobal) getActivity().getApplication()).getClave();
                            FormListAdapter formListAdapter = new FormListAdapter(mermas,modelComboBoxes,modelComboBoxesAlm,getContext(),user,clave);
                            listView.setAdapter(formListAdapter);
                        }else{
                            fn_clear_list();
                            Funciones.mensajeSweet(getContext(),"Aceptar","No hay resultados",SweetAlertDialog.SUCCESS_TYPE);
                        }
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Funciones.mensajeSweet(getContext(),"Aceptar","Error: "+statusCode,SweetAlertDialog.SUCCESS_TYPE);
                }
            });
        }
    }
    public void fn_clear_list(){
        ArrayList<ModelConsulta> arrayListNew = new ArrayList<>();
        ListView listView = (ListView)view.findViewById(R.id.lista_form);
        FormListAdapter formListAdapter = new FormListAdapter(arrayListNew,null,null,getContext(),"","");
        listView.setAdapter(formListAdapter);

    }
    public void limpiar_form(){
        ArrayList<ModelComboBox> modelCbx = new ArrayList<>();
        ModelComboBox selected = new ModelComboBox("","Seleccione");
        modelCbx.add(selected);

        ArrayAdapter<ModelComboBox> arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item,modelCbx);
        cbx_busqueda = view.findViewById(R.id.cbx_busqueda);
        cbx_busqueda.setAdapter(arrayAdapter);

        ((Spinner)view.findViewById(R.id.cbx_estado)).setSelection(0);
        fn_combobox_estado();
    }
    public void fn_combobox_tip_merma(){
        String user = ((VariableGlobal) getActivity().getApplication()).getUser();
        String clave = ((VariableGlobal) getActivity().getApplication()).getClave();
        RequestParams requestParams = new RequestParams();
        requestParams.put("I_OPCION",1);
        requestParams.put("SAP_USER",user);
        requestParams.put("SAP_CLAVE",clave);

        HttpUtils.post(Rest.ZRFC_MANT_VM,requestParams,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    modelComboBoxes = new ArrayList<>();

                    JSONArray t_trxal = response.getJSONArray("T_BWART");

                    ModelComboBox selected = new ModelComboBox("","Seleccione");
                    modelComboBoxes.add(selected);
                    for(int i=0;i<t_trxal.length();i++){
                        ModelComboBox modelComboBox = new ModelComboBox();
                        modelComboBox.setId(t_trxal.getJSONObject(i).getString("CLSMOV"));
                        modelComboBox.setNombre(t_trxal.getJSONObject(i).getString("CLSMOV")+" "+t_trxal.getJSONObject(i).getString("CLMOV_TEXT"));
                        modelComboBoxes.add(modelComboBox);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public void fn_combobox_estado(){
        ArrayList<ModelComboBox> cbx = new ArrayList<>();

        ModelComboBox estado1 = new ModelComboBox("0","TODOS");
        ModelComboBox estado2 = new ModelComboBox("1","PENDIENTES");
        ModelComboBox estado3 = new ModelComboBox("2","CONFIRMADAS");
        cbx.add(estado1);
        cbx.add(estado2);
        cbx.add(estado3);

        cbx_estado = view.findViewById(R.id.cbx_estado);
        ArrayAdapter<ModelComboBox> arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item,cbx);
        cbx_estado.setAdapter(arrayAdapter);
        cbx_estado.setOnItemSelectedListener(selectedListener);
    }
}
