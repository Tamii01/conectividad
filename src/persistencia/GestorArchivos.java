package persistencia;

import negocio.Localidad;
import negocio.Provincia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivos {

    private static final String SEPARADOR = ";";
    private String rutaArchivo;

    public GestorArchivos(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public void guardarLocalidades(List<Localidad> localidades) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (Localidad loc : localidades) {
                String linea = loc.getNombre() + SEPARADOR +
                               loc.getProvincia().name() + SEPARADOR +
                               loc.getLatitud() + SEPARADOR +
                               loc.getLongitud();
                writer.write(linea);
                writer.newLine();
            }
        }
    }

    public List<Localidad> cargarLocalidades() throws IOException {
        List<Localidad> localidadesCargadas = new ArrayList<>();
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) return localidadesCargadas;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(SEPARADOR);
                if (partes.length == 4) {
                    try {
                        String nombre = partes[0];
                        Provincia provincia = Provincia.valueOf(partes[1]);
                        double lat = Double.parseDouble(partes[2]);
                        double lon = Double.parseDouble(partes[3]);
                        localidadesCargadas.add(new Localidad(nombre, provincia, lat, lon));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error al parsear la línea: " + linea + " - " + e.getMessage());
                    }
                }
            }
        }
        return localidadesCargadas;
    }
}