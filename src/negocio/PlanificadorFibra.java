package negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import interfaz.VisualizadorMapa;

public class PlanificadorFibra {
    
    private List<Localidad> localidades;
    private ParametrosCosto parametros;
    

    private List<Conexion> mstConexiones;
    private double costoTotalMST;


    private List<Conexion> conexionesObligatorias;
    private List<Conexion> conexionesProhibidas;

    public PlanificadorFibra(List<Localidad> localidades, ParametrosCosto parametros) {
        if (localidades == null || localidades.size() < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 localidades para planificar.");
        }
        this.localidades = new ArrayList<>(localidades); 
        this.parametros = parametros;
        this.mstConexiones = new ArrayList<>();
        this.costoTotalMST = 0.0;
        
        this.conexionesObligatorias = new ArrayList<>();
        this.conexionesProhibidas = new ArrayList<>();
    }

    public void calcularSolucion() {
        List<Conexion> todasLasConexiones = generarGrafoCompleto();
        Collections.sort(todasLasConexiones); 

        UnionFind uf = new UnionFind();
        for (Localidad loc : localidades) {
            uf.hacerConjunto(loc);
        }

        mstConexiones.clear();
        costoTotalMST = 0.0;
        int aristasEsperadas = localidades.size() - 1;

        for (Conexion obligatoria : conexionesObligatorias) {
            if (uf.unir(obligatoria.esMismoOrigen(obligatoria), obligatoria.esMismoDestino(obligatoria))) {
                agregarAlMST(obligatoria);
            }
        }

        for (Conexion conexion : todasLasConexiones) {

            if (mstConexiones.size() == aristasEsperadas) break;


            if (esConexionProhibida(conexion)) {
                continue; 
            }


            if (uf.unir(conexion.esMismoOrigen(conexion), conexion.esMismoDestino(conexion))) {
                agregarAlMST(conexion);
            }
        }
    }

    private void agregarAlMST(Conexion conexion) {
        mstConexiones.add(conexion);
        costoTotalMST = conexion.sumarCostoA(costoTotalMST); 
    }


    private List<Conexion> generarGrafoCompleto() {
        List<Conexion> aristas = new ArrayList<>();
        
        for (int i = 0; i < localidades.size(); i++) {
            for (int j = i + 1; j < localidades.size(); j++) {
                Localidad origen = localidades.get(i);
                Localidad destino = localidades.get(j);
                
                Conexion nuevaConexion = new Conexion(origen, destino, parametros);
                aristas.add(nuevaConexion);
            }
        }
        return aristas;
    }


    public void cambiarConexionManual(Conexion quitar, Conexion agregar) {
        if (quitar != null) {
            conexionesProhibidas.add(quitar);
        }
        if (agregar != null) {
            conexionesObligatorias.add(agregar);
        }
        calcularSolucion(); 
    }

    public void limpiarModificacionesManuales() {
        conexionesObligatorias.clear();
        conexionesProhibidas.clear();
        calcularSolucion();
    }

    private boolean esConexionProhibida(Conexion objetivo) {
        for (Conexion prohibida : conexionesProhibidas) {
            if (prohibida.esIgualA(objetivo)) {
                return true;
            }
        }
        return false;
    }

    public String obtenerResumenSolucion() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- PLAN DE FIBRA ÓPTICA ---\n");
        for (Conexion c : mstConexiones) {

            sb.append(c.describirConexion()).append("\n");
        }
        sb.append("Costo Total del Proyecto: $").append(String.format("%.2f", costoTotalMST));
        return sb.toString();
    }


    public void dibujarSolucionEnMapa(VisualizadorMapa visualizador) {
        visualizador.limpiarMapa();
        

        for (Localidad loc : localidades) {
             loc.dibujarEnMapa(visualizador);
        }
        

        for (Conexion conn : mstConexiones) {
             conn.dibujarEnMapa(visualizador);
        }
    }
    

    public double getCostoTotalSolucion() {
        return costoTotalMST;
    }
    
    public boolean contieneEnSolucion(Conexion objetivo) {
        for (Conexion c : mstConexiones) {
            if (c.esIgualA(objetivo)) {
                return true;
            }
        }
        return false;
    }
    
    public int getCantidadConexionesSolucion() {
        return mstConexiones.size();
    }
    
 // Agrega esto en tu clase PlanificadorFibra.java
    public List<Conexion> getConexionesSolucionSegura() {
        return new ArrayList<>(mstConexiones);
    }
}