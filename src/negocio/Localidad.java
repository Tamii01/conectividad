package negocio;

import java.util.Objects;
import interfaz.VisualizadorMapa;
public class Localidad {
	private String nombre;
    private Provincia provincia;
    private double latitud;
    private double longitud;

    public Localidad(String nombre, Provincia provincia, double latitud, double longitud) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la localidad no puede estar vacío.");
        }
        if (provincia == null) {
            throw new IllegalArgumentException("La provincia no puede ser nula.");
        }
        

        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("Latitud inválida.");
        }
        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("Longitud inválida.");
        }

        this.nombre = nombre;
        this.provincia = provincia;
        this.latitud = latitud;
        this.longitud = longitud;
    }


    public String getNombre() { return nombre; }
    public Provincia getProvincia() { return provincia; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Localidad localidad = (Localidad) o;
        return nombre.equalsIgnoreCase(localidad.nombre) && provincia == localidad.provincia;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase(), provincia);
    }
    
    public void dibujarEnMapa(VisualizadorMapa visualizador) {
        visualizador.dibujarPunto(this.latitud, this.longitud, this.nombre);
    }
}
