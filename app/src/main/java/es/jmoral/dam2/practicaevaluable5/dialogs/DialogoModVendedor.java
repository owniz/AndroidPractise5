package es.jmoral.dam2.practicaevaluable5.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import es.jmoral.dam2.practicaevaluable5.R;
import es.jmoral.dam2.practicaevaluable5.models.Vendedor;

/**
 * Dialogo personalizado para modificar o borrar vendedores
 */

public class DialogoModVendedor extends DialogFragment implements DialogInterface.OnClickListener {

    // listener del dialogo
    private OnDialogoModVendedor esuchador;

    // componentes del dialogo
    private EditText etNombre;
    private EditText etApellidos;
    private EditText etEdad;
    private EditText etTelefono;
    private Vendedor vendedor;

    // método a llamar para crear el dialogo
    public static DialogoModVendedor newInstance(Vendedor vendedor) {
        DialogoModVendedor dialogoModVendedor = new DialogoModVendedor();
        dialogoModVendedor.vendedor = vendedor;
        return dialogoModVendedor;
    }

    // inflamos los componentes del dialogo y lo devolvemos
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.layout_dialogo_mod_vendedor, null);

        final Dialog dialog =  new AlertDialog.Builder(getActivity())
                .setTitle(R.string.mod_vendedor)
                .setView(v)
                .setCancelable(false)
                .setPositiveButton(R.string.modificar, this)
                .setNegativeButton(R.string.borrar, this)
                .setNeutralButton(R.string.cancelar, null)
                .create();

        // inicializamos los componentes
        etNombre = (EditText) v.findViewById(R.id.editTextNombre);
        etApellidos = (EditText) v.findViewById(R.id.editTextApellidos);
        etEdad = (EditText) v.findViewById(R.id.editTextEdad);
        etTelefono = (EditText) v.findViewById(R.id.editTextTelefono);

        // recuperemos la información del vendedor para colocarla en los EditText
        etNombre.setText(vendedor.getNombre());
        etNombre.setSelection(etNombre.getText().length()); // ponemos el cursor a la derecha
        etApellidos.setText(vendedor.getApellidos());
        etEdad.setText(String.valueOf(vendedor.getEdad()));
        etTelefono.setText(String.valueOf(vendedor.getTelefono()));

        return dialog;
    }

    // comportamiento de los botones
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch(i) {

            // modifica el vendedor
            case DialogInterface.BUTTON_POSITIVE:
                String nombre = etNombre.getText().toString();
                String apellidos = etApellidos.getText().toString();
                String edad = etEdad.getText().toString();
                String telefono = etTelefono.getText().toString();

                // si no hay dato vacio modifica el vendedor
                if(!nombre.isEmpty() && !apellidos.isEmpty() && !edad.isEmpty() && !telefono.isEmpty()) {
                    vendedor.setNombre(nombre);
                    vendedor.setApellidos(apellidos);
                    vendedor.setEdad(Integer.valueOf(edad));
                    vendedor.setTelefono(Integer.valueOf(telefono));

                    Gson gson = new Gson();
                    String parametroJSON = gson.toJson(vendedor);

                    esuchador.onModificarVendedor(parametroJSON, vendedor);

                // si hay algún dato vacio nos lo muestra con un Toast
                } else {
                    dialogInterface.dismiss();
                    Toasty.info(getActivity(), getString(R.string.mod_vendedor_sin_datos)).show();
                }

                break;

            // llama al método que borra el vendedor
            case DialogInterface.BUTTON_NEGATIVE:
                esuchador.onBorrarVendedor(vendedor);
                break;
        }
    }

    // interfaz para el comportamiento de los botones
    public interface OnDialogoModVendedor {
        void onModificarVendedor(String parametroJSON, Vendedor vendedor);
        void onBorrarVendedor(Vendedor vendedor);
    }

    // seteamos el listener del dialogo
    public void setOnDialogoModVendedorListener(OnDialogoModVendedor escuchador) {
        this.esuchador = escuchador;
    }
}
