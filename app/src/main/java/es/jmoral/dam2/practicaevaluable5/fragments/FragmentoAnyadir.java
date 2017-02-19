package es.jmoral.dam2.practicaevaluable5.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import es.jmoral.dam2.practicaevaluable5.R;
import es.jmoral.dam2.practicaevaluable5.models.Coche;
import es.jmoral.dam2.practicaevaluable5.utils.Constantes;
import es.jmoral.dam2.practicaevaluable5.utils.InternetUtils;
import es.jmoral.dam2.practicaevaluable5.utils.TareaRest;

/**
 * Fragmento utilziado para añadir coches a la BBDD
 */

public class FragmentoAnyadir extends Fragment implements TareaRest.TareaRestListener, AdapterView.OnClickListener {
    private EditText etMarca;
    private EditText etModelo;
    private Button botonAnyadir;
    private Coche coche;

    // método que devuelve el fragmento
    public static Fragment newInstance() {
        return new FragmentoAnyadir();
    }

    // inicializamos y seteamos los componentes
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_fragmento_anyadir, container, false);

        setUpViews(v);
        setListeners();

        return v;
    }

    // inicializa los componentes
    private void setUpViews(View v) {
        etMarca = (EditText) v.findViewById(R.id.editTextMarca);
        etModelo = (EditText) v.findViewById(R.id.editTextModelo);
        botonAnyadir = (Button) v.findViewById(R.id.botonAnyadirCoche);
    }

    // seteamos el listener del botón
    private void setListeners() {
        botonAnyadir.setOnClickListener(this);
    }

    // comportamiento al finalizar la tarea de TareaRest
    @Override
    public void onTareaRestFinalizada(int codigoOperacion, int codigoRespuestaHttp, String respuestaJson) {

        // Si el servidor responde correctamente mostramos un Toast avisando del coche añadido
        if (codigoRespuestaHttp == 200 || respuestaJson != null && !respuestaJson.isEmpty()) {
            if (codigoOperacion == Constantes.CODIGO_INSERCIÓN_COCHE) {
                Toasty.success(getActivity(), getString(R.string.coche_anyadido)).show();
                enviarBroadcastCoche();
                coche = null;
            }
        }
    }

    // enviamos un BroadCcast paa notiicar la inserción del coche al fragento que borra los coches
    // para que actualice su Spinner
    private void enviarBroadcastCoche() {
        final Intent updateIntent = new Intent(Constantes.ACTUALIZAR_COCHES);
        getActivity().sendBroadcast(updateIntent);
    }

    // comportamiento del botón de "BORRAR"
    @Override
    public void onClick(View view) {
        String marca = etMarca.getText().toString();
        String modelo = etModelo.getText().toString();

        // si no hay datos lo notifica con un Toast
        if(marca.isEmpty() || modelo.isEmpty()) {
            Toasty.info(getActivity(), getString(R.string.rellenar_campos)).show();

        // si hay datos llama a TareaRest para la inserción del coche en la BBDD
        // parseando los datos a JSON
        } else {
            coche = new Coche(marca, modelo, 1);

            Gson gson = new Gson();
            String paramatrosJSON = gson.toJson(coche);

            // comprueba la conexión antes del envio para notificar en caso de que no haya
            if(InternetUtils.isInternetAvailable(getActivity())) {
                TareaRest tarea = new TareaRest(getContext(), Constantes.CODIGO_INSERCIÓN_COCHE ,
                        InternetUtils.HTTP_POST, Constantes.URL_API_COCHES, paramatrosJSON, this);
                tarea.execute();
            } else {
                Toasty.error(getActivity(), getString(R.string.no_red)).show();
            }
        }
    }
}