package cl.rialsoft.appMERMA;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView registro,consulta;
    ViewPager viewPager;
    PagerViewAdapter pagerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_bar);
        registro = (TextView)findViewById(R.id.tab_registro);
        consulta = (TextView)findViewById(R.id.tab_consulta);
        viewPager = (ViewPager)findViewById(R.id.fragment_content);

        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerViewAdapter);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                onChangeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onChangeTab(int position) {
        if (position==0){
            registro.setBackgroundResource(R.drawable.tab_btn_blanco);
            registro.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorrojo));
            registro.setCompoundDrawablesWithIntrinsicBounds(R.drawable.imagen_mas, 0, 0, 0);

            consulta.setBackgroundResource(R.drawable.tab_btn);
            consulta.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorText));
            consulta.setCompoundDrawablesWithIntrinsicBounds(R.drawable.imagen_lupa_blanca, 0, 0, 0);
        }
        if (position==1){
            registro.setBackgroundResource(R.drawable.tab_btn);
            registro.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorText));
            registro.setCompoundDrawablesWithIntrinsicBounds(R.drawable.imagen_mas_blanco, 0, 0, 0);

            consulta.setBackgroundResource(R.drawable.tab_btn_blanco);
            consulta.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorrojo));
            consulta.setCompoundDrawablesWithIntrinsicBounds(R.drawable.imagen_lupa_roja, 0, 0, 0);
        }
    }
}
