package es.jmoral.dam2.practicaevaluable5;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Clase base que mantiene el estado de la aplicación, utilizada para seleccionar
 * el modo noche en la aplicación
 */

public class PracticaEvaluable5Application extends Application {
    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
