package negocio;

import static org.junit.Assert.*;
import org.junit.Test;

public class ParametroCostoTest {

    // === EXCEPCIONES PRIMERO ===

    @Test(expected = IllegalArgumentException.class)
    public void testCostoNegativoLanzaExcepcion() {
        new ParametroCosto(-1, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPorcentajeNegativoLanzaExcepcion() {
        new ParametroCosto(1, -0.1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterprovincialNegativoLanzaExcepcion() {
        new ParametroCosto(1, 0, -100);
    }

    // === COMPORTAMIENTO ===

    @Test
    public void testCreacionValida() {
        ParametroCosto p = new ParametroCosto(10.0, 0.20, 500.0);
        assertEquals(10.0, p.getCostoPorKm(), 0.001);
        assertEquals(0.20, p.getPorcentajeAumentoMas300Km(), 0.001);
        assertEquals(500.0, p.getCostoFijoInterprovincial(), 0.001);
    }

    @Test
    public void testValoresEnCeroSonValidos() {
        ParametroCosto p = new ParametroCosto(0, 0, 0);
        assertEquals(0.0, p.getCostoPorKm(), 0.001);
    }
}