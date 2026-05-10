package negocio;

import static org.junit.Assert.*;
import org.junit.Test;

public class LocalidadTest {

    // === EXCEPCIONES PRIMERO ===

    @Test(expected = IllegalArgumentException.class)
    public void testNombreVacioLanzaExcepcion() {
        new Localidad("   ", Provincia.BUENOS_AIRES, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNombreNullLanzaExcepcion() {
        new Localidad(null, Provincia.BUENOS_AIRES, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProvinciaNullLanzaExcepcion() {
        new Localidad("X", null, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatitudFueraRangoLanzaExcepcion() {
        new Localidad("X", Provincia.BUENOS_AIRES, 95, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLongitudFueraRangoLanzaExcepcion() {
        new Localidad("X", Provincia.BUENOS_AIRES, 0, 200);
    }

    // === COMPORTAMIENTO ===

    @Test
    public void testCreacionValida() {
        Localidad l = new Localidad("Tigre", Provincia.BUENOS_AIRES, -34.42, -58.58);
        assertEquals("Tigre", l.getNombre());
        assertEquals(Provincia.BUENOS_AIRES, l.getProvincia());
    }

    @Test
    public void testEqualsEsCaseInsensitive() {
        Localidad l1 = new Localidad("Tigre", Provincia.BUENOS_AIRES, -34.4, -58.5);
        Localidad l2 = new Localidad("TIGRE", Provincia.BUENOS_AIRES, -34.4, -58.5);
        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
    }

    @Test
    public void testNoEsIgualSiCambiaProvincia() {
        Localidad l1 = new Localidad("Tigre", Provincia.BUENOS_AIRES, -34.4, -58.5);
        Localidad l2 = new Localidad("Tigre", Provincia.CORDOBA, -34.4, -58.5);
        assertNotEquals(l1, l2);
    }
}