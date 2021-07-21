package cl.rialsoft.appMERMA;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.widget.Button;
import android.widget.TableLayout;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Funciones {
    Context context;

    public static String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }
    public static void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }
    public static void botonesSweet(SweetAlertDialog sweetAlertDialog){
        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
        Button btn2 = (Button) sweetAlertDialog.findViewById(R.id.cancel_button);
        btn.setBackgroundResource(R.drawable.btn_rojo_form);
        btn2.setBackgroundResource(R.drawable.btn_gris);
        TableLayout.LayoutParams latLayoutParams =
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

        TableLayout.LayoutParams latLayoutParams2 =
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

        latLayoutParams.leftMargin = 10;
        btn.setPadding(0, 20, 0, 20);
        btn.setLayoutParams(latLayoutParams);
        btn2.setPadding(0, 20, 0, 20);
        btn2.setLayoutParams(latLayoutParams2);
    }
    public static void mensajeSweet(Context context,String btnText,String title,int type){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setConfirmText(btnText);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.show();
        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
        btn.setBackgroundResource(R.drawable.btn_rojo_form);
    }
    public static boolean stateconexion(Context context) {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public void mensaje() {
        // Import library

// 1. Success message
        new SweetAlertDialog(context)
                .setTitleText("Here's a message!")
                .show();

// 2. Confirmation message
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("You won't be able to recover this file!")
                .setConfirmText("Delete!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

// 3. Error message
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show();

// 4. Loading message
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

// 5. Confirm success
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("Yes,delete it!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog
                                .setTitleText("Deleted!")
                                .setContentText("Your imaginary file has been deleted!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();
    }
}
