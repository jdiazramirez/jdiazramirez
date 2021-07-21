package cl.rialsoft.appMERMA;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerViewAdapter extends FragmentPagerAdapter {
    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new Fragment_registro();
            break;
            case 1:
                fragment = new Fragment_consulta();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
