package negocio;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class PlanificadorFibraTest {

    private ParametroCosto parametrosBase;
    private List<Localidad> localidadesCuadrado;
    private Localidad a, b, c, d;

    @Before
    public void setUp() {
        parametrosBase = new ParametroCosto(1.0, 0.0, 0.0);
        localidadesCuadrado = new ArrayList<>();
        a = new Localidad("A", Provincia.BUENOS_AIRES, -34.0, -58.0);
        b = new Localidad("B", Provincia.BUENOS_AIRES, -34.0, -57.0);
        c = new Localidad("C", Provincia.BUENOS_AIRES, -35.0, -58.0);
        d = new Localidad("D", Provincia.BUENOS_AIRES, -35.0, -57.0);
        localidadesCuadrado.add(a);
        localidadesCuadrado.add(b);
        localidadesCuadrado.add(c);
        localidadesCuadrado.add(d);
    }

    // === EXCEPCIONES PRIMERO ===

    @Test(expected = IllegalArgumentException.class)
    public void testFallaConMenosDeDosLocalidades() {
        List<Localidad> una = new ArrayList<>();
        una.add(a);
        new PlanificadorFibra(una, parametrosBase);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFallaConListaNula() {
        new PlanificadorFibra(null, parametrosBase);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFallaConParametrosNulos() {
        new PlanificadorFibra(localidadesCuadrado, null);
    }

    // === COMPORTAMIENTO ===

    @Test
    public void testKruskalEligeCaminosCortos() {
        PlanificadorFibra planificador = new PlanificadorFibra(localidadesCuadrado, parametrosBase);
        planificador.calcularSolucion();

        assertEquals(3, planificador.getCantidadConexionesSolucion());

        Conexion diagonalAD = new Conexion(a, d, parametrosBase);
        Conexion diagonalBC = new Conexion(b, c, parametrosBase);
        assertFalse(planificador.contieneEnSolucion(diagonalAD));
        assertFalse(planificador.contieneEnSolucion(diagonalBC));

        Conexion ladoAB = new Conexion(a, b, parametrosBase);
        assertTrue(planificador.contieneEnSolucion(ladoAB));
    }

    @Test
    public void testKruskalEvitaCostosInterprovincialesSiPuede() {
        ParametroCosto paramCaros = new ParametroCosto(1.0, 0.0, 50000.0);
        Localidad bCordoba = new Localidad("B", Provincia.CORDOBA, -34.0, -57.0);
        List<Localidad> mixtas = new ArrayList<>();
        mixtas.add(a); mixtas.add(bCordoba); mixtas.add(c); mixtas.add(d);

        PlanificadorFibra planificador = new PlanificadorFibra(mixtas, paramCaros);
        planificador.calcularSolucion();

        Conexion ab = new Conexion(a, bCordoba, paramCaros);
        Conexion cb = new Conexion(c, bCordoba, paramCaros);
        Conexion db = new Conexion(d, bCordoba, paramCaros);

        int conexionesHaciaB = 0;
        if (planificador.contieneEnSolucion(ab)) conexionesHaciaB++;
        if (planificador.contieneEnSolucion(cb)) conexionesHaciaB++;
        if (planificador.contieneEnSolucion(db)) conexionesHaciaB++;

        assertEquals("B debería tener solo 1 conexión interprovincial", 1, conexionesHaciaB);
    }

    @Test
    public void testForzarConexionAumentaCostoSiNoEraOptima() {
        PlanificadorFibra planificador = new PlanificadorFibra(localidadesCuadrado, parametrosBase);
        planificador.calcularSolucion();
        double costoOptimo = planificador.getCostoTotalSolucion();

        // Forzamos la diagonal A-D (que el MST óptimo no había elegido)
        Conexion diagonalAD = new Conexion(a, d, parametrosBase);
        Conexion ladoAB = new Conexion(a, b, parametrosBase);
        planificador.cambiarConexionManual(ladoAB, diagonalAD);

        assertTrue("Forzar la diagonal debería aumentar el costo",
                   planificador.getCostoTotalSolucion() >= costoOptimo);
        assertTrue(planificador.contieneEnSolucion(diagonalAD));
    }

    @Test
    public void testLimpiarModificacionesRestauraOptimo() {
        PlanificadorFibra planificador = new PlanificadorFibra(localidadesCuadrado, parametrosBase);
        planificador.calcularSolucion();
        double costoOptimo = planificador.getCostoTotalSolucion();

        Conexion diagonalAD = new Conexion(a, d, parametrosBase);
        Conexion ladoAB = new Conexion(a, b, parametrosBase);
        planificador.cambiarConexionManual(ladoAB, diagonalAD);
        planificador.limpiarModificacionesManuales();

        assertEquals("Limpiar modificaciones debería restaurar el costo óptimo",
                     costoOptimo, planificador.getCostoTotalSolucion(), 0.01);
    }

    @Test
    public void testEsConexionProhibida() {
        PlanificadorFibra planificador = new PlanificadorFibra(localidadesCuadrado, parametrosBase);
        Conexion ab = new Conexion(a, b, parametrosBase);
        Conexion cd = new Conexion(c, d, parametrosBase);

        assertFalse(planificador.esConexionProhibida(ab));

        planificador.cambiarConexionManual(ab, null);
        assertTrue("AB debería estar prohibida después de pedir quitarla",
                   planificador.esConexionProhibida(ab));
        assertFalse("CD no fue prohibida", planificador.esConexionProhibida(cd));
    }
}