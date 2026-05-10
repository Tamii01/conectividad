package negocio;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class UnionFindTest {

    private UnionFind uf;
    private Localidad a, b, c, d;

    @Before
    public void setUp() {
        uf = new UnionFind();
        a = new Localidad("A", Provincia.BUENOS_AIRES, -34.0, -58.0);
        b = new Localidad("B", Provincia.BUENOS_AIRES, -34.0, -57.0);
        c = new Localidad("C", Provincia.BUENOS_AIRES, -35.0, -58.0);
        d = new Localidad("D", Provincia.BUENOS_AIRES, -35.0, -57.0);
        uf.hacerConjunto(a);
        uf.hacerConjunto(b);
        uf.hacerConjunto(c);
        uf.hacerConjunto(d);
    }

    // === CASOS LÍMITE PRIMERO ===

    @Test
    public void testEncontrarEnElementoInexistenteDevuelveNull() {
        Localidad fantasma = new Localidad("Fantasma", Provincia.CORDOBA, 0, 0);
        assertNull(uf.encontrar(fantasma));
    }

    // === COMPORTAMIENTO ===

    @Test
    public void testCadaElementoEsSuPropioRepresentanteAlPrincipio() {
        assertEquals(a, uf.encontrar(a));
        assertEquals(b, uf.encontrar(b));
    }

    @Test
    public void testUnirDosConjuntosDistintos() {
        assertTrue(uf.unir(a, b));
        assertEquals(uf.encontrar(a), uf.encontrar(b));
    }

    @Test
    public void testUnirElementosYaUnidos() {
        uf.unir(a, b);
        assertFalse("No debería poder unir elementos del mismo conjunto",
                    uf.unir(a, b));
    }

    @Test
    public void testUnionTransitiva() {
        uf.unir(a, b);
        uf.unir(c, d);
        uf.unir(b, c);
        assertEquals(uf.encontrar(a), uf.encontrar(d));
    }
}