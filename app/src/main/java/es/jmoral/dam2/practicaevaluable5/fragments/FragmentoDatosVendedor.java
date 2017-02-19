package es.jmoral.dam2.practicaevaluable5.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import es.dmoral.toasty.Toasty;
import es.jmoral.dam2.practicaevaluable5.R;
import es.jmoral.dam2.practicaevaluable5.models.Coche;
import es.jmoral.dam2.practicaevaluable5.models.Vendedor;
import es.jmoral.dam2.practicaevaluable5.models.Venta;
import es.jmoral.dam2.practicaevaluable5.utils.Constantes;
import es.jmoral.dam2.practicaevaluable5.utils.InternetUtils;
import es.jmoral.dam2.practicaevaluable5.utils.TareaRest;

/**
 * Fragemento encargado de mostrar los datos del vendedor seleccionado y las ventas que ha realizado
 * y además nos permite realizar la venta de coches
 */

public class FragmentoDatosVendedor extends Fragment implements View.OnClickListener, TareaRest.TareaRestListener, AdapterView.OnItemSelectedListener {
    private TextView tvNombre;
    private TextView tvApellidos;
    private TextView tvEdad;
    private TextView tvTelefono;
    private TextView tvVentas;
    private EditText etPrecio;
    private EditText etMatricula;
    private Vendedor vendedor;
    private Spinner spinnerCochesVenta;
    private Button botonRealizarVenta;
    private Button botonVolver;
    private ArrayAdapter arrayAdapterCoches;
    private List<Coche> coches;
    private Coche cocheSeleccionado;

    // método que devuelve el fragmento
    public static Fragment newInstance(Vendedor vendedor) {
        FragmentoDatosVendedor fragmentoDatosVendedor = new FragmentoDatosVendedor();
        fragmentoDatosVendedor.vendedor = vendedor;
        return fragmentoDatosVendedor;
    }

    // inicializamos y seteamos los componentes
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_fragmento_datos_vendedor, container, false);

        setUpViews(v);
        setListeners();
        consultaListadoCoches();
        consultarVentasVendedor();

        return v;
    }

    // inicializamos loa componentes
    private void setUpViews(View v) {
        tvNombre = (TextView) v.findViewById(R.id.textViewNombre);
        tvApellidos = (TextView) v.findViewById(R.id.textViewApellidos);
        tvEdad = (TextView) v.findViewById(R.id.textViewEdad);
        tvTelefono = (TextView) v.findViewById(R.id.textViewTelefono);
        tvVentas = (TextView) v.findViewById(R.id.textViewVentas);
        etPrecio = (EditText) v.findViewById(R.id.editTextPrecio);
        etMatricula = (EditText) v.findViewById(R.id.editTextMatricula);
        spinnerCochesVenta = (Spinner) v.findViewById(R.id.spinnerCocheVendedor);
        botonRealizarVenta = (Button) v.findViewById(R.id.botonRealizarVenta);
        botonVolver = (Button) v.findViewById(R.id.botonVolver);

        // recuperamos los datos del vendedor para mostrarlos
        tvNombre.setText(vendedor.getNombre());
        tvApellidos.setText(vendedor.getApellidos());
        tvEdad.setText(String.valueOf(vendedor.getEdad()));
        tvTelefono.setText(String.valueOf(vendedor.getTelefono()));
    }

    // seteamos los listeners
    public void setListeners() {
        botonRealizarVenta.setOnClickListener(this);
        botonVolver.setOnClickListener(this);
        spinnerCochesVenta.setOnItemSelectedListener(this);
    }

    // consulta en la BBDD los coches disponibles para vender si hay conexión a internet
    private void consultaListadoCoches() {
        if(InternetUtils.isInternetAvailable(getActivity())) {
            TareaRest tarea = new TareaRest(getContext(), Constantes.CODIGO_CONSULTA_COCHES,
                    InternetUtils.HTTP_GET, Constantes.URL_API_COCHES, null, this);
            tarea.execute();
        } else {
            Toasty.error(getActivity(), getString(R.string.no_red)).show();
        }
    }

    // consulta en la BBDD las ventas realizadas por el vendedor si hay conexión a internet
    private void consultarVentasVendedor() {
        if(InternetUtils.isInternetAvailable(getActivity())) {
            TareaRest tarea = new TareaRest(getContext(), Constantes.CODIGO_CONSULTA_VENTAS,
                    InternetUtils.HTTP_GET, Constantes.URL_API_VENTAS + vendedor.getId_vendedor(), null, this);
            tarea.execute();
        } else {
            Toasty.error(getActivity(), getString(R.string.no_red)).show();
        }
    }

    // dfinimos el comportamiento de los botones
    @Override
    public void onClick(View view) {
        int opccionElegida = view.getId();

        switch(opccionElegida) {

            // botón para realizar las ventas
            case R.id.botonRealizarVenta:
                String precio = etPrecio.getText().toString();
                String matricula = etMatricula.getText().toString();

                // si hay internet creamos una nueva instancia de Venta para mandarla al servidor de la BBDD
                if(InternetUtils.isInternetAvailable(getActivity())) {
                    if(!precio.isEmpty() && !matricula.isEmpty()) {
                        Venta venta = new Venta(cocheSeleccionado.getId_coche(), vendedor.getId_vendedor(), Integer.valueOf(precio), matricula);

                        Gson gson = new Gson();
                        String paramatrosJSON = gson.toJson(venta);

                        TareaRest tarea = new TareaRest(getContext(), Constantes.CODIGO_INSERCIÓN_VENTA,
                                InternetUtils.HTTP_POST, Constantes.URL_API_VENTAS, paramatrosJSON, this);
                        tarea.execute();
                    } else {
                        Toasty.info(getActivity(), getString(R.string.venta_no_realizada)).show();
                    }
                } else {
                    Toasty.error(getActivity(), getString(R.string.no_red)).show();
                }
                break;

            // botón para volver al FragmentoVender (el anterior)
            case R.id.botonVolver:
                volverFragmentoVender();
                break;
        }
    }

    // comportamiento al finalizar la tarea de TareaRest
    @Override
    public void onTareaRestFinalizada(int codigoOperacion, int codigoRespuestaHttp, String respuestaJson) {

        // si la respuesta del servidor es correcta
        if(codigoRespuestaHttp == 200 || respuestaJson != null && !respuestaJson.isEmpty()) {

            // si la consulta es correcta procesamos la respuesta en JSON para añadirla al Spinner
            if(codigoOperacion == Constantes.CODIGO_CONSULTA_COCHES) {
                coches = procesarListaCoches(respuestaJson);

                if(coches != null) {
                    arrayAdapterCoches = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, coches);
                    spinnerCochesVenta.setAdapter(arrayAdapterCoches);
                }

            // si hemos realizado una venta mostramos un mensaje avisandolo y volvemos al fragmento anterior
            } else if(codigoOperacion == Constantes.CODIGO_INSERCIÓN_VENTA) {
                Toasty.success(getActivity(), getString(R.string.venta_realizada)).show();
                volverFragmentoVender();

            // al consultar las ventas
            } else if(codigoOperacion == Constantes.CODIGO_CONSULTA_VENTAS) {

                // si hay respuesta del servidor mostramos las ventas realionadas con el vendedor
                if(!respuestaJson.isEmpty()) {
                    List<Venta> ventas = procesarListaVentas(respuestaJson);

                    if(ventas != null) {
                        String ventasAMostrar = "";

                        for(int i = 0; i < ventas.size(); i++) {
                            ventasAMostrar += (i + 1) +getString(R.string.id_venta_) + ventas.get(i).getId_venta()
                                    + getString(R.string.precio_) + ventas.get(i).getPrecio()
                                    + getString(R.string.matricula_) + ventas.get(i).getMatricula() + "\n";
                        }
                        tvVentas.setText(ventasAMostrar);
                    }
                }
            }

        // si no hay ventas lo mostramos también
        } else if(codigoRespuestaHttp == 404) {
            tvVentas.setText(R.string.sin_ventas);
        }
    }

    // procesa la respuesta en JSON de los coches
    private List<Coche> procesarListaCoches (String objetoJSON){
        Gson gson = new Gson();
        Type tipoLista = new TypeToken<List<Coche>>(){}.getType();
        return gson.fromJson(objetoJSON,tipoLista);
    }

    // procesa la respuesta en JSON de las ventas
    private List<Venta> procesarListaVentas (String objetoJSON){
        Gson gson = new Gson();
        Type tipoLista = new TypeToken<List<Venta>>(){}.getType();
        return gson.fromJson(objetoJSON,tipoLista);
    }

    // reemplazamos el fragmento para volver al anterior (FragmentoVender)
    private void volverFragmentoVender() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, FragmentoVender.newInstance());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    // recuperamos el eoche seleccionado
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        cocheSeleccionado = coches.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
