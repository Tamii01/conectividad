package negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlanificadorFibra {

    private List<Localidad> localidades;
    private ParametroCosto parametros;

    private List<Conexion> mstConexiones;
    private double costoTotalMST;

    private List<Conexion> conexionesObligatorias;
    private List<Conexion> conexionesProhibidas;

    public PlanificadorFibra(List<Localidad> localidades, ParametroCosto parametros) {
        if (localidades == null || localidades.size() < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 localidades para planificar.");
        }
        if (parametros == null) {
            throw new IllegalArgumentException("Los parámetros de costo no pueden ser nulos.");
        }
        this.localidades = new ArrayList<>(localidades);
        this.parametros = parametros;
        this.mstConexiones = new ArrayList<>();
        this.costoTotalMST = 0.0;
        this.conexionesObligatorias = new ArrayList<>();
        this.conexionesProhibidas = new ArrayList<>();
    }

    /**
     * Algoritmo de Kruskal sobre el grafo completo.
     * Respeta conexiones obligatorias (se agregan primero) y prohibidas (se saltean).
     */
    public void calcularSolucion() {
        reiniciarSolucion();
        UnionFind uf = inicializarUnionFind();

        agregarConexionesObligatorias(uf);
        agregarConexionesPorKruskal(uf);
    }

    private void reiniciarSolucion() {
        mstConexiones.clear();
        costoTotalMST = 0.0;
    }

    private UnionFind inicializarUnionFind() {
        UnionFind uf = new UnionFind();
        for (Localidad loc : localidades) {
            uf.hacerConjunto(loc);
        }
        return uf;
    }

    private void agregarConexionesObligatorias(UnionFind uf) {
        for (Conexion obligatoria : conexionesObligatorias) {
            if (uf.unir(obligatoria.getOrigen(), obligatoria.getDestino())) {
                agregarAlMST(obligatoria);
            }
        }
    }

    private void agregarConexionesPorKruskal(UnionFind uf) {
        List<Conexion> aristasOrdenadas = generarGrafoCompleto();
        Collections.sort(aristasOrdenadas);

        int aristasEsperadas = localidades.size() - 1;

        for (Conexion conexion : aristasOrdenadas) {
            if (mstConexiones.size() == aristasEsperadas) break;
            if (esConexionProhibida(conexion)) continue;

            if (uf.unir(conexion.getOrigen(), conexion.getDestino())) {
                agregarAlMST(conexion);
            }
        }
    }

    private void agregarAlMST(Conexion conexion) {
        mstConexiones.add(conexion);
        costoTotalMST += conexion.getCostoTotal();
    }

    private List<Conexion> generarGrafoCompleto() {
        List<Conexion> aristas = new ArrayList<>();
        for (int i = 0; i < localidades.size(); i++) {
            for (int j = i + 1; j < localidades.size(); j++) {
                aristas.add(new Conexion(localidades.get(i), localidades.get(j), parametros));
            }
        }
        return aristas;
    }

    public void cambiarConexionManual(Conexion quitar, Conexion agregar) {
        if (quitar != null) conexionesProhibidas.add(quitar);
        if (agregar != null) conexionesObligatorias.add(agregar);
        calcularSolucion();
    }

    public void limpiarModificacionesManuales() {
        conexionesObligatorias.clear();
        conexionesProhibidas.clear();
        calcularSolucion();
    }

    // Visibilidad de paquete (sin "private") para poder testearlo
    boolean esConexionProhibida(Conexion objetivo) {
        for (Conexion prohibida : conexionesProhibidas) {
            if (prohibida.esIgualA(objetivo)) return true;
        }
        return false;
    }

    public double getCostoTotalSolucion() { return costoTotalMST; }

    public boolean contieneEnSolucion(Conexion objetivo) {
        for (Conexion c : mstConexiones) {
            if (c.esIgualA(objetivo)) return true;
        }
        return false;
    }

    public int getCantidadConexionesSolucion() {
        return mstConexiones.size();
    }

    public List<Conexion> getConexionesSolucion() {
        return new ArrayList<>(mstConexiones);
    }

    public List<Localidad> getLocalidades() {
        return new ArrayList<>(localidades);
    }
}