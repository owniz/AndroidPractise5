package es.jmoral.dam2.practicaevaluable5.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Clase para comprobar la conexión a internet y además tiene las Constanetes de los servicios RESTful
 */

public class InternetUtils {
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_DELETE = "DELETE";
    public static final String HTTP_PUT = "PUT";

    public static boolean isInternetAvailable(@NonNull Context context) {
        NetworkInfo ni = ((ConnectivityManager) context
                .getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return ni != null && ni.isConnected() && ni.isAvailable();
    }
}
