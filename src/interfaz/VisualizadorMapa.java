package interfaz;

public interface VisualizadorMapa {
    void limpiarMapa();
    void dibujarPunto(double latitud, double longitud, String etiqueta);
    void dibujarLinea(double latOrigen, double lonOrigen, double latDestino, double lonDestino);
}