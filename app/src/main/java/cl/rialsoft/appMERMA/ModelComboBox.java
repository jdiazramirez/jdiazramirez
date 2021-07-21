package cl.rialsoft.appMERMA;

public class ModelComboBox {
    private String id;
    private String nombre;

    public ModelComboBox(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public ModelComboBox() { }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
