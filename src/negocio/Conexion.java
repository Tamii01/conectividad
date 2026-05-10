package negocio;

public class Conexion implements Comparable<Conexion> {
    private Localidad origen;
    private Localidad destino;
    private double distanciaKm;
    private double costoTotal;

    public Conexion(Localidad origen, Localidad destino, ParametroCosto parametros) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Las localidades de origen y destino no pueden ser nulas.");
        }
        if (origen.equals(destino)) {
            throw new IllegalArgumentException("Una localidad no puede conectarse consigo misma.");
        }
        if (parametros == null) {
            throw new IllegalArgumentException("Los parámetros de costo no pueden ser nulos.");
        }

        this.origen = origen;
        this.destino = destino;
        this.distanciaKm = calcularDistanciaHaversine(
                origen.getLatitud(), origen.getLongitud(),
                destino.getLatitud(), destino.getLongitud()
        );
        this.costoTotal = calcularCosto(parametros);
    }

    private double calcularCosto(ParametroCosto parametros) {
        double costoBase = this.distanciaKm * parametros.getCostoPorKm();
        double costoFinal = costoBase;

        if (this.distanciaKm > 300) {
            costoFinal += costoBase * parametros.getPorcentajeAumentoMas300Km();
        }
        if (this.origen.getProvincia() != this.destino.getProvincia()) {
            costoFinal += parametros.getCostoFijoInterprovincial();
        }
        return costoFinal;
    }

    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int RADIO_TIERRA_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIO_TIERRA_KM * c;
    }

    public Localidad getOrigen() { return origen; }
    public Localidad getDestino() { return destino; }
    public double getDistanciaKm() { return distanciaKm; }
    public double getCostoTotal() { return costoTotal; }

    @Override
    public int compareTo(Conexion otra) {
        return Double.compare(this.costoTotal, otra.costoTotal);
    }

    public boolean esIgualA(Conexion otra) {
        boolean igualDerecho = this.origen.equals(otra.origen) && this.destino.equals(otra.destino);
        boolean igualAlReves = this.origen.equals(otra.destino) && this.destino.equals(otra.origen);
        return igualDerecho || igualAlReves;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Conexion)) return false;
        return this.esIgualA((Conexion) obj);
    }

    @Override
    public int hashCode() {
        // Suma para que sea simétrico: hash(A,B) == hash(B,A)
        return origen.hashCode() + destino.hashCode();
    }

    @Override
    public String toString() {
        return origen.getNombre() + " <---> " + destino.getNombre() +
               " (" + String.format("%.1f", distanciaKm) + " km) - Costo: $" +
               String.format("%.2f", costoTotal);
    }
}