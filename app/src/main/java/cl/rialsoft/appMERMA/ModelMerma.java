package cl.rialsoft.appMERMA;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelMerma {
    private String producto;
    private String cant_caja;
    private String cant_bote;
    private String clave_mov;
    private String motivo;
    private String image;
    private String img_name;


    public ModelMerma(String producto, String cant_caja, String cant_bote, String clave_mov,
                      String motivo,String image,String img_name) {
        this.producto = producto;
        this.cant_caja = cant_caja;
        this.cant_bote = cant_bote;
        this.clave_mov = clave_mov;
        this.motivo = motivo;
        this.image = image;
        this.img_name = img_name;
    }

    public String getImg_name() {
        return img_name;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCant_caja() {
        return cant_caja;
    }

    public void setCant_caja(String cant_caja) {
        this.cant_caja = cant_caja;
    }

    public String getCant_bote() {
        return cant_bote;
    }

    public void setCant_bote(String cant_bote) {
        this.cant_bote = cant_bote;
    }

    public String getClave_mov() {
        return clave_mov;
    }

    public void setClave_mov(String clave_mov) {
        this.clave_mov = clave_mov;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public JSONObject toJSON(){

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("PRODUCTO", getProducto());
            jsonObject.put("CANT_CAJA", getCant_caja());
            jsonObject.put("CANT_BOTE", getCant_bote());
            jsonObject.put("CLASE_MOV", getClave_mov());
            jsonObject.put("MOTIVO", getMotivo());
            jsonObject.put("IMAGE", getImage());
            jsonObject.put("IMG_NAME",getImg_name());
            return jsonObject;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }
}
