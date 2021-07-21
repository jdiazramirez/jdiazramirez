package cl.rialsoft.appMERMA;

import android.app.Application;

public class VariableGlobal extends Application {
    private String user;
    private String clave;

    public String getUser() {
        return user.toUpperCase();
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
