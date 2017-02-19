package es.jmoral.dam2.practicaevaluable5.activities;

import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import es.jmoral.dam2.practicaevaluable5.R;
import es.jmoral.dam2.practicaevaluable5.fragments.FragmentMain;
import es.jmoral.dam2.practicaevaluable5.fragments.FragmentoAnyadir;
import es.jmoral.dam2.practicaevaluable5.fragments.FragmentoBorrar;

/**
 * Clase principal encargada de gestionar las pestañas de la aplicación
 */

public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    // inflamos los componentes de la APP
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setDialogoAyuda();
    }

    // menú desplegable
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // definimos el comportamiento al pulsar sobre el menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // abre el dialogo de ayuda
        if (id == R.id.action_settings) {
            setDialogoAyuda();
        }

        return true;
    }

    // Clase para la gestion de las pestañas
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // asignamos un fragmento para cada pestaña
        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return FragmentMain.newInstance();
                case 1:
                    return FragmentoBorrar.newInstance();
                case 2:
                    return FragmentoAnyadir.newInstance();
                default:
                    return new Fragment();
            }
        }

        // asignamos el número de pestañas
        @Override
        public int getCount() {
            return 3;
        }

        // título de las pestañas
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.vende_coche);
                case 1:
                    return getString(R.string.borrar_coche);
                case 2:
                    return getString(R.string.anyadir_coche);
            }
            return null;
        }
    }

    // método que muestra el dialogo de ayuda
    public void setDialogoAyuda() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.ayuda)
                .setMessage(R.string.mensaje_ayuda)
                .setPositiveButton(android.R.string.yes, null)
                .setCancelable(false)
                .show();
    }
}
