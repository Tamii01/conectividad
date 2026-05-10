package negocio;

import java.util.HashMap;
import java.util.Map;


class UnionFind {
	
    private Map<Localidad, Localidad> padre;

    public UnionFind() {
        this.padre = new HashMap<>();
    }

    public void hacerConjunto(Localidad localidad) {
        padre.put(localidad, localidad);
    }

    public Localidad encontrar(Localidad localidad) {
        Localidad representante = padre.get(localidad);
        if (representante == null) return null; 

        if (representante.equals(localidad)) {
            return localidad;
        }

        Localidad representanteRaiz = encontrar(representante);
        padre.put(localidad, representanteRaiz); 
        
        return representanteRaiz;
    }

    public boolean unir(Localidad loc1, Localidad loc2) {
        Localidad raiz1 = encontrar(loc1);
        Localidad raiz2 = encontrar(loc2);

        if (raiz1.equals(raiz2)) {
            return false;
        }

        padre.put(raiz1, raiz2);
        return true;
    }
}
