package negocio;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class PlanificadorFibraTest {

    private ParametrosCosto parametrosBase;
    private List<Localidad> localidadesCuadrado;

    @Before
    public void setUp() {
        parametrosBase = new ParametrosCosto(1.0, 0.0, 0.0);
        localidadesCuadrado = new ArrayList<>();
    }

    @Test
    public void testKruskalEligeCaminosCortos() {
        Localidad a = new Localidad("A", Provincia.BUENOS_AIRES, -34.0, -58.0);
        Localidad b = new Localidad("B", Provincia.BUENOS_AIRES, -34.0, -57.0);
        Localidad c = new Localidad("C", Provincia.BUENOS_AIRES, -35.0, -58.0);
        Localidad d = new Localidad("D", Provincia.BUENOS_AIRES, -35.0, -57.0); 
        
        localidadesCuadrado.add(a);
        localidadesCuadrado.add(b);
        localidadesCuadrado.add(c);
        localidadesCuadrado.add(d);

        PlanificadorFibra planificador = new PlanificadorFibra(localidadesCuadrado, parametrosBase);
        planificador.calcularSolucion();
        

        assertEquals(3, planificador.getCantidadConexionesSolucion());

        Conexion diagonalAD = new Conexion(a, d, parametrosBase);
        Conexion diagonalBC = new Conexion(b, c, parametrosBase);
        
        assertFalse("El MST no debería incluir la diagonal A-D", planificador.contieneEnSolucion(diagonalAD));
        assertFalse("El MST no debería incluir la diagonal B-C", planificador.contieneEnSolucion(diagonalBC));
        
        Conexion ladoAB = new Conexion(a, b, parametrosBase);
        assertTrue("El MST debería incluir el lado corto A-B", planificador.contieneEnSolucion(ladoAB));
    }

    @Test
    public void testKruskalEvitaCostosInterprovincialesSiPuede() {
        ParametrosCosto paramCaros = new ParametrosCosto(1.0, 0.0, 50000.0);
        
        Localidad a = new Localidad("A", Provincia.BUENOS_AIRES, -34.0, -58.0);
        Localidad b = new Localidad("B", Provincia.CORDOBA, -34.0, -57.0); // OTRA PROVINCIA
        Localidad c = new Localidad("C", Provincia.BUENOS_AIRES, -35.0, -58.0);
        Localidad d = new Localidad("D", Provincia.BUENOS_AIRES, -35.0, -57.0); 

        localidadesCuadrado.add(a);
        localidadesCuadrado.add(b);
        localidadesCuadrado.add(c);
        localidadesCuadrado.add(d);

        PlanificadorFibra planificador = new PlanificadorFibra(localidadesCuadrado, paramCaros);
        planificador.calcularSolucion();
        
        Conexion ab = new Conexion(a, b, paramCaros);
        Conexion cb = new Conexion(c, b, paramCaros);
        Conexion db = new Conexion(d, b, paramCaros);
        
        int conexionesHaciaB = 0;
        if (planificador.contieneEnSolucion(ab)) conexionesHaciaB++;
        if (planificador.contieneEnSolucion(cb)) conexionesHaciaB++;
        if (planificador.contieneEnSolucion(db)) conexionesHaciaB++;

        assertEquals("B debería tener solo 1 conexión para no repetir el costo interprovincial.", 1, conexionesHaciaB);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFallaConMenosDeDosLocalidades() {
        Localidad a = new Localidad("A", Provincia.BUENOS_AIRES, -34.0, -58.0);
        localidadesCuadrado.add(a);
        new PlanificadorFibra(localidadesCuadrado, parametrosBase);
    }
}