package negocio;

import java.util.HashMap;
import java.util.Map;

public class UnionFind {

    private Map<Localidad, Localidad> padre;
    private Map<Localidad, Integer> rango;

    public UnionFind() {
        this.padre = new HashMap<>();
        this.rango = new HashMap<>();
    }

    public void hacerConjunto(Localidad localidad) {
        padre.put(localidad, localidad);
        rango.put(localidad, 0);
    }

    /**
     * Versión iterativa con path compression.
     * Sube hasta la raíz y luego "aplana" el camino.
     */
    public Localidad encontrar(Localidad localidad) {
        if (!padre.containsKey(localidad)) return null;

        // Subir hasta la raíz
        Localidad actual = localidad;
        while (!padre.get(actual).equals(actual)) {
            actual = padre.get(actual);
        }
        Localidad raiz = actual;

        // Compresión de camino: apuntar todos directamente a la raíz
        actual = localidad;
        while (!padre.get(actual).equals(raiz)) {
            Localidad siguiente = padre.get(actual);
            padre.put(actual, raiz);
            actual = siguiente;
        }
        return raiz;
    }

    /**
     * Union by rank: el árbol más bajo se cuelga del más alto.
     * Devuelve true si efectivamente unió dos conjuntos distintos.
     */
    public boolean unir(Localidad loc1, Localidad loc2) {
        Localidad raiz1 = encontrar(loc1);
        Localidad raiz2 = encontrar(loc2);

        if (raiz1 == null || raiz2 == null) return false;
        if (raiz1.equals(raiz2)) return false;

        int rango1 = rango.get(raiz1);
        int rango2 = rango.get(raiz2);

        if (rango1 < rango2) {
            padre.put(raiz1, raiz2);
        } else if (rango1 > rango2) {
            padre.put(raiz2, raiz1);
        } else {
            padre.put(raiz2, raiz1);
            rango.put(raiz1, rango1 + 1);
        }
        return true;
    }
}