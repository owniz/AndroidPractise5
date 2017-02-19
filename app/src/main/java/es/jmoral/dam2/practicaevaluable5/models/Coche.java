package es.jmoral.dam2.practicaevaluable5.models;

/**
 * Modelo de la tabla Coche de la BBDD
 */

public class Coche {
    private int id_coche;
    private String marca;
    private String modelo;
    private int activo;

    public Coche(int id_coche) {
        this.id_coche = id_coche;
    }

    public Coche(String marca, String modelo, int activo) {
        this.marca = marca;
        this.modelo = modelo;
        this.activo = activo;
    }

    public int getId_coche() {
        return id_coche;
    }

    public void setId_coche(int id_coche) {
        this.id_coche = id_coche;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return this.marca + " " + this.modelo;
    }
}
