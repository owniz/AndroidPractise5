package es.jmoral.dam2.practicaevaluable5.models;

/**
 * Modelo de la tabla Venta que relaciona a las tablas Coche y Vendedor de la BBDD
 */

public class Venta {
    private int id_venta;
    private int id_coche;
    private int id_vendedor;
    private int precio;
    private String matricula;

    public Venta(int id_coche, int id_vendedor, int precio, String matricula) {
        this.id_coche = id_coche;
        this.id_vendedor = id_vendedor;
        this.precio = precio;
        this.matricula = matricula;
    }

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public int getId_coche() {
        return id_coche;
    }

    public void setId_coche(int id_coche) {
        this.id_coche = id_coche;
    }

    public int getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(int id_vendedor) {
        this.id_vendedor = id_vendedor;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
