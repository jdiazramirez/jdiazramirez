package cl.rialsoft.appMERMA;

public class ModelConsulta {
    private String id;
    private String folio;
    private String fecha_ing_doc;
    private String usuario_dig;
    private String almacen;
    private String producto;
    private String cant_cajas;
    private String cant_botellas;
    private String clase_mov;
    private String clase_mov_nombre;
    private String motivo;
    private String imagen;
    private String hora;
    private String pro_nombre;
    private String apro;
    private String centro;

    public String getClase_mov_nombre() {
        return clase_mov_nombre;
    }

    public void setClase_mov_nombre(String clase_mov_nombre) {
        this.clase_mov_nombre = clase_mov_nombre;
    }
    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public String getApro() {
        return apro;
    }

    public void setApro(String apro) {
        this.apro = apro;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getPro_nombre() {
        return pro_nombre;
    }

    public void setPro_nombre(String pro_nombre) {
        this.pro_nombre = pro_nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ModelConsulta(String folio, String fecha_ing_doc, String usuario_dig, String almacen, String producto, String cant_cajas, String cant_botellas, String clase_mov, String motivo,String imagen,String hora,String pro_nombre
    ,String apro) {
        this.folio = folio;
        this.fecha_ing_doc = fecha_ing_doc;
        this.usuario_dig = usuario_dig;
        this.almacen = almacen;
        this.producto = producto;
        this.cant_cajas = cant_cajas;
        this.cant_botellas = cant_botellas;
        this.clase_mov = clase_mov;
        this.motivo = motivo;
        this.imagen = imagen;
        this.hora = hora;
        this.pro_nombre = pro_nombre;
        this.apro = apro;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getFecha_ing_doc() {
        return fecha_ing_doc;
    }

    public void setFecha_ing_doc(String fecha_ing_doc) {
        this.fecha_ing_doc = fecha_ing_doc;
    }

    public String getUsuario_dig() {
        return usuario_dig;
    }

    public void setUsuario_dig(String usuario_dig) {
        this.usuario_dig = usuario_dig;
    }

    public ModelConsulta() {
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCant_cajas() {
        return cant_cajas;
    }

    public void setCant_cajas(String cant_cajas) {
        this.cant_cajas = cant_cajas;
    }

    public String getCant_botellas() {
        return cant_botellas;
    }

    public void setCant_botellas(String cant_botellas) {
        this.cant_botellas = cant_botellas;
    }

    public String getClase_mov() {
        return clase_mov;
    }

    public void setClase_mov(String clase_mov) {
        this.clase_mov = clase_mov;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public String toString() {
        return "ModelConsulta{" +
                "folio='" + folio + '\'' +
                ", fecha_ing_doc='" + fecha_ing_doc + '\'' +
                ", usuario_dig='" + usuario_dig + '\'' +
                ", almacen='" + almacen + '\'' +
                ", producto='" + producto + '\'' +
                ", cant_cajas='" + cant_cajas + '\'' +
                ", cant_botellas='" + cant_botellas + '\'' +
                ", clase_mov='" + clase_mov + '\'' +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}
