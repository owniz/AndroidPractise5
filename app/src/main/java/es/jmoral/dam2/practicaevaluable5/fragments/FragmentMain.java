package es.jmoral.dam2.practicaevaluable5.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.jmoral.dam2.practicaevaluable5.R;

/**
 * Fragmento principal de la pestaña para vender coches
 */

public class FragmentMain extends Fragment {

    // método que devuelve el fragmento
    public static Fragment newInstance() {
        return new FragmentMain();
    }

    // ponemos el primer fragmento (FragmentoVender) al iniciar la aplicación
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        FragmentoVender fragmentoVender = (FragmentoVender) FragmentoVender.newInstance();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frameLayout, fragmentoVender);
        ft.commit();

        return v;
    }
}
