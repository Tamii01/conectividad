package negocio;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ConexionTest {
	private ParametrosCosto parametros;
    private Localidad bsas;
    private Localidad laPlata;
    private Localidad cordobaCapital;
    private Localidad mendozaCapital;


    @Before
    public void setUp() {

        parametros = new ParametrosCosto(10.0, 0.20, 500.0);


        bsas = new Localidad("Buenos Aires", Provincia.BUENOS_AIRES, -34.60, -58.38);
        laPlata = new Localidad("La Plata", Provincia.BUENOS_AIRES, -34.92, -57.95);
        cordobaCapital = new Localidad("Córdoba", Provincia.CORDOBA, -31.42, -64.18);
        mendozaCapital = new Localidad("Mendoza", Provincia.MENDOZA, -32.88, -68.83);
    }


    @Test
    public void testCostoConexionCortaMismaProvincia() {

        Conexion conexion = new Conexion(bsas, laPlata, parametros);

        double costoEsperado = conexion.getDistanciaKm() * 10.0; 
        

        assertEquals(costoEsperado, conexion.getCostoTotal(), 0.01);
    }


    @Test
    public void testCostoConexionLargaMismaProvincia() {

        Localidad bahiaBlanca = new Localidad("Bahía Blanca", Provincia.BUENOS_AIRES, -38.71, -62.26);
        

        Conexion conexion = new Conexion(bsas, bahiaBlanca, parametros);

        double costoBase = conexion.getDistanciaKm() * 10.0;
        double costoEsperado = costoBase + (costoBase * 0.20); 


        assertEquals(costoEsperado, conexion.getCostoTotal(), 0.01);
        

        assertTrue(conexion.getDistanciaKm() > 300);
    }


    @Test
    public void testCostoConexionCortaDistintaProvincia() {

        Localidad puebloBA = new Localidad("PuebloBA", Provincia.BUENOS_AIRES, -33.5, -60.0);
        Localidad puebloER = new Localidad("PuebloSF", Provincia.SANTA_FE, -33.4, -60.0);

        Conexion conexion = new Conexion(puebloBA, puebloER, parametros);

        double costoBase = conexion.getDistanciaKm() * 10.0;
        double costoEsperado = costoBase + 500.0; 

        assertEquals(costoEsperado, conexion.getCostoTotal(), 0.01);
        assertTrue(conexion.getDistanciaKm() < 300); 
    }

    @Test
    public void testCostoConexionLargaDistintaProvincia() {

        Conexion conexion = new Conexion(bsas, cordobaCapital, parametros);

        double costoBase = conexion.getDistanciaKm() * 10.0;
        double costoRecargoDistancia = costoBase * 0.20;
        double costoEsperado = costoBase + costoRecargoDistancia + 500.0;

        assertEquals(costoEsperado, conexion.getCostoTotal(), 0.01);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConexionMismaLocalidadLanzaExcepcion() {

        new Conexion(bsas, bsas, parametros);
    }

}
