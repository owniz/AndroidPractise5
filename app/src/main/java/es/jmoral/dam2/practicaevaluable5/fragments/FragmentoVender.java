package es.jmoral.dam2.practicaevaluable5.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import es.dmoral.toasty.Toasty;
import es.jmoral.dam2.practicaevaluable5.R;
import es.jmoral.dam2.practicaevaluable5.dialogs.DialogoModVendedor;
import es.jmoral.dam2.practicaevaluable5.models.Vendedor;
import es.jmoral.dam2.practicaevaluable5.utils.Constantes;
import es.jmoral.dam2.practicaevaluable5.utils.InternetUtils;
import es.jmoral.dam2.practicaevaluable5.utils.TareaRest;

/**
 * Fragmento que muestra una lista de vendedores disponibles y nos permite modificar sus datos o borrarlos
 */

public class FragmentoVender extends Fragment implements TareaRest.TareaRestListener, AdapterView.OnItemClickListener,
                                                    AdapterView.OnItemLongClickListener, TextWatcher,
                                                    DialogoModVendedor.OnDialogoModVendedor {
    private EditText editTextFiltradoVendedor;
    private ListView listViewVendedores;
    private ArrayAdapter<Vendedor> arrayAdapter;
    private List<Vendedor> vendedores;

    // método que devuelve el fragmento
    public static Fragment newInstance() {
        return new FragmentoVender();
    }

    // inicializamos y seteamos los componentes
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_fragmento_vender, container, false);

        // si hay internet los inicializamos y llamamos a la BBDD para obtener los vendedores
        if (InternetUtils.isInternetAvailable(getActivity())) {
            setUpViews(v);
            setListeners();

            TareaRest tarea = new TareaRest(getContext(), Constantes.CODIGO_CONSULTA_VENDEDORES,
                    InternetUtils.HTTP_GET, Constantes.URL_API_VENDEDORES, null, this);
            tarea.execute();
        } else {
            Toasty.error(getActivity(), getString(R.string.no_red)).show();
        }

        return v;
    }

    // inicializamos los componentes
    private void setUpViews(View v) {
        editTextFiltradoVendedor = (EditText) v.findViewById(R.id.editTextFiltradoNombre);
        listViewVendedores = (ListView) v.findViewById(R.id.listViewVendedores);
    }

    // seteamos los listeners
    private void setListeners() {
        listViewVendedores.setOnItemClickListener(this);
        listViewVendedores.setOnItemLongClickListener(this);
    }

    // comportamiento al finalizar la tarea de TareaRest
    @Override
    public void onTareaRestFinalizada(int codigoOperacion, int codigoRespuestaHttp, String respuestaJson) {

        // si la respuesta del servidor es correcta
        if(codigoRespuestaHttp == 200 || respuestaJson != null && !respuestaJson.isEmpty()) {

            // al consultar los vendedores procesaos la lista paara añadirla al ListView
            if(codigoOperacion == Constantes.CODIGO_CONSULTA_VENDEDORES) {
                vendedores = procesarListaVendedores(respuestaJson);

                if(vendedores != null) {
                    arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, vendedores);
                    listViewVendedores.setAdapter(arrayAdapter);
                }

            // si modificamos un vendedor actualizamos el ListView
            } else if(codigoOperacion == Constantes.CODIGO_MODIFICACIÓN_VENDEDOR) {
                arrayAdapter.notifyDataSetChanged();
                Toasty.success(getActivity(), getString(R.string.vendedor_mod_ok)).show();

            // si borramos un vendedor actualizamos el ListView
            } else if(codigoOperacion == Constantes.CODIGO_BORRADO_VENDEDOR) {
                arrayAdapter.notifyDataSetChanged();
                Toasty.success(getActivity(), getString(R.string.vendedor_borrado_ok)).show();
            }
        }
    }

    // procesamos los datos recibidos en JSON
    private List<Vendedor> procesarListaVendedores(String objetoJSON) {
        Gson gson = new Gson();

        try {
            Type tipoLista = new TypeToken<List<Vendedor>>() {
            }.getType();
            return gson.fromJson(objetoJSON, tipoLista);
        } catch(Exception e) {
            Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    // al modificar un vendedor accedemos a la base de datos para realizar los cambios
    @Override
    public void onModificarVendedor(String parametroJSON, Vendedor vendedor) {
        if(InternetUtils.isInternetAvailable(getActivity())) {
            TareaRest tarea = new TareaRest(getContext(), Constantes.CODIGO_MODIFICACIÓN_VENDEDOR,
                    InternetUtils.HTTP_PUT, Constantes.URL_API_VENDEDORES + vendedor.getId_vendedor(), parametroJSON, this);
            tarea.execute();
        } else {
            Toasty.error(getActivity(), getString(R.string.no_red)).show();
        }
    }

    // al borrar un vendedor accedemos a la base de datos para realizar los cambios y lo borramos
    // del ArrayAdapter
    @Override
    public void onBorrarVendedor(Vendedor vendedor) {
        if(InternetUtils.isInternetAvailable(getActivity())) {
            TareaRest tarea = new TareaRest(getContext(), Constantes.CODIGO_BORRADO_VENDEDOR,
                    InternetUtils.HTTP_DELETE, Constantes.URL_API_VENDEDORES + vendedor.getId_vendedor(),
                    null, this);
            tarea.execute();

            vendedores.remove(vendedor);
        } else {
            Toasty.error(getActivity(), getString(R.string.no_red)).show();
        }
    }

    // al pulsar sobre un vendedor accedemos al FragmentoDatosVendedor
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Vendedor vendedor = (Vendedor) adapterView.getItemAtPosition(i);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, FragmentoDatosVendedor.newInstance(vendedor));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    // si mantenemos pulsado sobre un vendedor mostramos un dialogo con sus datos para
    // modificarlo o borrarlo
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Vendedor vendedor = (Vendedor) adapterView.getItemAtPosition(i);
        DialogoModVendedor dialogoModVendedor = DialogoModVendedor.newInstance(vendedor);
        dialogoModVendedor.setOnDialogoModVendedorListener(this);
        dialogoModVendedor.setCancelable(false);
        dialogoModVendedor.show(getFragmentManager(), null);

        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    // filtra los datos del EditText para hacer busqueda de vendedores
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        arrayAdapter.getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    // al volver al fragmento si no esta null el listener, lo setea
    @Override
    public void onResume() {
        super.onResume();
        if(editTextFiltradoVendedor != null)
            editTextFiltradoVendedor.addTextChangedListener(this);
    }

    // al pausar al fragmento si no esta null el listener, lo setea a null para evitar errores
    // a la hora de cerrar la aplicación si no esta el fragmento en memoria
    @Override
    public void onPause() {
        super.onPause();
        if(editTextFiltradoVendedor != null)
            editTextFiltradoVendedor.addTextChangedListener(null);
    }
}
