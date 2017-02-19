package es.jmoral.dam2.practicaevaluable5.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import es.dmoral.toasty.Toasty;
import es.jmoral.dam2.practicaevaluable5.R;
import es.jmoral.dam2.practicaevaluable5.models.Coche;
import es.jmoral.dam2.practicaevaluable5.utils.Constantes;
import es.jmoral.dam2.practicaevaluable5.utils.InternetUtils;
import es.jmoral.dam2.practicaevaluable5.utils.TareaRest;

/**
 * Fragmento para el borrado de coches en la BBDD
 */

public class FragmentoBorrar extends Fragment implements TareaRest.TareaRestListener, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Spinner spinnerCoches;
    private Button botonBorrarCoche;
    private ArrayAdapter arrayAdapterCoches;
    private List<Coche> coches;
    private Coche cocheSeleccionado;
    private BroadcastReceiver broadcastReceiver;

    // método que devuelve el fragmento
    public static Fragment newInstance() {
        return new FragmentoBorrar();
    }

    // inicializamos y seteamos los componentes
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_fragmento_borrar, container, false);

        setUpViews(v);
        setListeners();
        consultaListadoCoches(); // consulta los coches en la BBDD
        setBroadcastListener();

        return v;
    }

    // inicializa los componentes
    private void setUpViews(View v) {
        spinnerCoches = (Spinner) v.findViewById(R.id.spinnerCoche);
        botonBorrarCoche = (Button) v.findViewById(R.id.botonBorrarCoche);
    }

    // setea los listeners
    private void setListeners() {
        spinnerCoches.setOnItemSelectedListener(this);
        botonBorrarCoche.setOnClickListener(this);
    }

    // comprueba si hay conexión a internet para obtener una lista de los coches disponibles en la BBDD
    private void consultaListadoCoches() {
        if(InternetUtils.isInternetAvailable(getActivity())) {
            TareaRest tarea = new TareaRest(getContext(), Constantes.CODIGO_CONSULTA_COCHES,
                    InternetUtils.HTTP_GET, Constantes.URL_API_COCHES, null, this);
            tarea.execute();
        } else {
            Toasty.error(getActivity(), getString(R.string.no_red)).show();
        }
    }

    // escucha el Broadcast que puede enviar el FragmentoAnyadir para actualizar la lista de coches
    private void setBroadcastListener() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constantes.ACTUALIZAR_COCHES);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                consultaListadoCoches();
            }
        };

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    // al destruir el fragmento cancela el Broadcast para liberar recursos
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    // comportamiento al finalizar la tarea de TareaRest
    @Override
    public void onTareaRestFinalizada(int codigoOperacion, int codigoRespuestaHttp, String respuestaJson) {

        // si se ha realizaco correctamente
        if(codigoRespuestaHttp == 200 || respuestaJson != null && !respuestaJson.isEmpty()) {

            // si es una consulta de coches los añadimos al Spinner
            if(codigoOperacion == Constantes.CODIGO_CONSULTA_COCHES){
                coches = procesarListaCoches(respuestaJson);

                // si no hay coches desactivamos el botón de borrado
                if (coches.size() > 0)
                    botonBorrarCoche.setEnabled(true);

                // si hay coches los añadimos
                if(coches != null){
                    arrayAdapterCoches = new ArrayAdapter<>(getActivity(), android.R.layout.simple_selectable_list_item, coches);
                    spinnerCoches.setAdapter(arrayAdapterCoches);
                }

            // si borramos un coche lo notificamos al spinner para que lo borre
            } else if(codigoOperacion == Constantes.CODIGO_BORRADO_COCHE) {
                coches.remove(coches.indexOf(cocheSeleccionado));

                if(coches.size() > 0)
                    cocheSeleccionado = coches.get(0);
                else
                    botonBorrarCoche.setEnabled(false);

                arrayAdapterCoches.notifyDataSetChanged();
                Toasty.success(getActivity(), getString(R.string.borrado_coche_ok)).show();
            }
        }
    }

    // procesamos la lista de coches recibida en JSON
    private List<Coche> procesarListaCoches (String objetoJSON){
        Gson gson = new Gson();
        Type tipoLista = new TypeToken<List<Coche>>(){}.getType();
        return gson.fromJson(objetoJSON,tipoLista);
    }

    // comportamiento del botón, mostramos un dialogo advirtiendo que está apunto de borrar un
    // coche de la BBDD, para que pueda aceptar o cancelar el borrado
    @Override
    public void onClick(View view) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.alerta)
                .setMessage(getString(R.string.confirmacion_borrado, cocheSeleccionado))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(InternetUtils.isInternetAvailable(getActivity())) {
                            TareaRest tarea = new TareaRest(getContext(), Constantes.CODIGO_BORRADO_COCHE,
                                    InternetUtils.HTTP_DELETE, Constantes.URL_API_COCHES + cocheSeleccionado.getId_coche(),
                                    null, FragmentoBorrar.this);
                            tarea.execute();
                        } else {
                            Toasty.error(getActivity(), getString(R.string.no_red)).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setCancelable(false)
                .show();
    }

    // recuperamos el coche seleccionado
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        cocheSeleccionado = coches.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}