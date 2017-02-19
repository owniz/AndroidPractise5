package es.jmoral.dam2.practicaevaluable5.utils;

/**
 * Constantes utilizadas en la aplicación
 */

public class Constantes {

    // conexion con la BBDD
    public static final String URL_BASE_SERVIDOR = "http://javi-luis.esy.es/";
    public static final String URL_API_COCHES = URL_BASE_SERVIDOR + "coches/";
    public static final String URL_API_VENDEDORES = URL_BASE_SERVIDOR + "vendedores/";
    public static final String URL_API_VENTAS = URL_BASE_SERVIDOR + "ventas/";

    // Broadcast
    public static final String ACTUALIZAR_COCHES = "ACTUALIZAR_COCHES";

    // tipo de consulta
    public static final int CODIGO_CONSULTA_VENDEDORES = 0;
    public static final int CODIGO_MODIFICACIÓN_VENDEDOR = 1;
    public static final int CODIGO_BORRADO_VENDEDOR = 2;
    public static final int CODIGO_CONSULTA_COCHES = 3;
    public static final int CODIGO_INSERCIÓN_COCHE = 4;
    public static final int CODIGO_BORRADO_COCHE = 5;
    public static final int CODIGO_CONSULTA_VENTAS = 6;
    public static final int CODIGO_INSERCIÓN_VENTA = 7;

}
