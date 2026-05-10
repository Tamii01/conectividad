package negocio;

public class ParametroCosto {
    private double costoPorKm;
    private double porcentajeAumentoMas300Km; // ej: 0.15 para 15%
    private double costoFijoInterprovincial;

    public ParametroCosto(double costoPorKm, double porcentajeAumentoMas300Km, double costoFijoInterprovincial) {
        if (costoPorKm < 0 || porcentajeAumentoMas300Km < 0 || costoFijoInterprovincial < 0) {
            throw new IllegalArgumentException("Los costos y porcentajes no pueden ser negativos.");
        }
        this.costoPorKm = costoPorKm;
        this.porcentajeAumentoMas300Km = porcentajeAumentoMas300Km;
        this.costoFijoInterprovincial = costoFijoInterprovincial;
    }

    public double getCostoPorKm() { return costoPorKm; }
    public double getPorcentajeAumentoMas300Km() { return porcentajeAumentoMas300Km; }
    public double getCostoFijoInterprovincial() { return costoFijoInterprovincial; }
}