package negocio;

public enum Provincia {
	BUENOS_AIRES("Buenos Aires"),
    CATAMARCA("Catamarca"),
    CHACO("Chaco"),
    CHUBUT("Chubut"),
    CORDOBA("Córdoba"),
    CORRIENTES("Corrientes"),
    ENTRE_RÍOS("Entre Ríos"),
    FORMOSA ("Formosa"),
    JUJUY ("Jujuy"),
    LA_PAMPA("La Pampa"),
    LA_RIOJA("La Rioja"),
    MENDOZA("Mendoza"),
    MISIONES("Misiones"),
    NEUQUÉN("Neuquén"),
    RÍO_NEGRO("Río Negro"),
    SALTA("Salta"),
    SAN_JUAN("San Juan"),
    SAN_LUIS("San Luis"),
    SANTA_CRUZ("Santa Cruz"),
    SANTA_FE("Santa Fe"),
    SANTIAGO_DEL_ESTERO("Santiago del Estero"),
    TIERRA_DEL_FUEGO("Tierra de Fuego"),
    TUCUMAN("Tucumán");

    private final String nombreParaMostrar;

    Provincia(String nombreParaMostrar) {
        this.nombreParaMostrar = nombreParaMostrar;
    }

    public String getNombreParaMostrar() {
        return nombreParaMostrar;
    }
    
    @Override
    public String toString() {
        return nombreParaMostrar;
    }
}
