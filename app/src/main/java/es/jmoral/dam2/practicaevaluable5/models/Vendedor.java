package es.jmoral.dam2.practicaevaluable5.models;

/**
 * Modelo de la tabla Vendedor de la BBDD
 */

public class Vendedor {
    private int id_vendedor;
    private String nombre;
    private String apellidos;
    private int edad;
    private int telefono;
    private int activo;

    public Vendedor (int id_vendedor, String nombre, String apellidos, int edad, int telefono, int activo) {
        this.id_vendedor = id_vendedor;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
        this.telefono = telefono;
        this.activo = activo;
    }

    public int getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(int id_vendedor) {
        this.id_vendedor = id_vendedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return this.nombre + " " + this.apellidos;
    }
}
