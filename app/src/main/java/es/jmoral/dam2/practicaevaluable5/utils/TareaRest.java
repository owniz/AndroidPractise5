package es.jmoral.dam2.practicaevaluable5.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import es.jmoral.dam2.practicaevaluable5.R;

/**
 * Clase con la tarea encargada de la conexión con l aBBDD
 */

public class TareaRest extends AsyncTask <Void, String, String> {
    private Context contexto;
    private int codigoOperacion;
    private String operacionREST;
    private String urlRecurso;
    private String parametro;
    private TareaRestListener actividadOyente;
    private ProgressDialog progressDialog;
    private int codigoRespuestaHttp;

    // declaramos los componentes
    public TareaRest(Context contexto, int codigoOperacion, String operacionREST, String urlRecurso, String parametro, TareaRestListener actividadOyente) {
        this.contexto=contexto;
        this.codigoOperacion=codigoOperacion;
        this.operacionREST = operacionREST;
        this.urlRecurso =urlRecurso;
        this.parametro = parametro;
        this.actividadOyente=actividadOyente;

        progressDialog = new ProgressDialog(contexto);
    }

    // interfaz para que puedan definir el comportamiento cuando finalice la Tarea
    public interface TareaRestListener {
        void onTareaRestFinalizada(int codigoOperacion, int codigoRespuestaHttp, String respuestaJson);
    }

    // dialogo mostrando una espera mientras conecta
    protected void onPreExecute() {
        progressDialog.setMessage(contexto.getString(R.string.cargando));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    // conexión con la BBDD
    protected String doInBackground(Void... vacio) {
        BufferedReader bufferLectura = null;
        HttpURLConnection conexion = null;
        String cuerpoRecibido = null;

        try {
            URL url = new URL(urlRecurso);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setConnectTimeout(5000);
            conexion.setRequestMethod(operacionREST);

            if(operacionREST.equals(InternetUtils.HTTP_POST) || operacionREST.equals(InternetUtils.HTTP_PUT)){
                conexion.setRequestProperty("Content-Type", "application/json");
                conexion.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conexion.getOutputStream());
                outputStreamWriter.write(parametro);
                outputStreamWriter.flush();
                outputStreamWriter.close();
            }

            codigoRespuestaHttp = conexion.getResponseCode();
            bufferLectura = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferLectura.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            cuerpoRecibido = stringBuilder.toString();
        } catch (Exception ex) {
            publishProgress(ex.toString());
        } finally {
            conexion.disconnect();
            if (bufferLectura != null) {
                try {
                    bufferLectura.close();
                } catch (Exception ex) {
                    publishProgress(contexto.getString(R.string.error_buffer) + ex.toString());
                }
            }

            return cuerpoRecibido;
        }
    }

    // cierra el dialogo avisando de que la conexión ha sido correcta
    protected void onPostExecute(String cuerpoRecibido) {
        progressDialog.dismiss();
        actividadOyente.onTareaRestFinalizada(codigoOperacion,codigoRespuestaHttp,cuerpoRecibido);
    }
}
