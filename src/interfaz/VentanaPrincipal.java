package interfaz;

import negocio.*;
import persistencia.GestorArchivos;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame implements VisualizadorMapa {

    private JMapViewer mapa;
    private List<Localidad> listaLocalidades;
    private PlanificadorFibra planificador;
    private GestorArchivos gestorArchivos;

    // Componentes de la UI
    private JTextField txtNombre, txtLat, txtLon;
    private JComboBox<Provincia> comboProvincia;
    private JTextArea txtResumen;

    private JComboBox<Conexion> comboQuitar;
    private JComboBox<Conexion> comboAgregar;
    private JButton btnModificar;
    private JButton btnRestaurarMST;
    private double costoOptimoGuardado;
    
    public VentanaPrincipal() {

        setTitle("Planificador de Fibra Óptica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700); 
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());

        mapa = new JMapViewer();
        mapa.setDisplayPosition(new Coordinate(-38.41, -63.61), 4);
        add(mapa, BorderLayout.CENTER);


        listaLocalidades = new ArrayList<>();
        gestorArchivos = new GestorArchivos("localidades_guardadas.txt");
        cargarDatosPrevios(); 


        JPanel panelIzquierdo = crearPanelIzquierdo();
        add(panelIzquierdo, BorderLayout.WEST);
    }
    
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        panel.add(new JLabel("Nombre Localidad:"));
        txtNombre = new JTextField();
        panel.add(txtNombre);

        panel.add(new JLabel("Provincia:"));
        comboProvincia = new JComboBox<>(Provincia.values());
        panel.add(comboProvincia);

        panel.add(new JLabel("Latitud (-90 a 90):"));
        txtLat = new JTextField();
        panel.add(txtLat);

        panel.add(new JLabel("Longitud (-180 a 180):"));
        txtLon = new JTextField();
        panel.add(txtLon);

        JButton btnAgregar = new JButton("Agregar Localidad");
        panel.add(Box.createVerticalStrut(10)); // Espaciador
        panel.add(btnAgregar);


        panel.add(Box.createVerticalStrut(20));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(20));


        JButton btnPlanificar = new JButton("Calcular y Dibujar MST");
        btnPlanificar.setBackground(new Color(135, 206, 250));
        btnPlanificar.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(btnPlanificar);


        panel.add(Box.createVerticalStrut(10));
        txtResumen = new JTextArea();
        txtResumen.setEditable(false);
        JScrollPane scrollResumen = new JScrollPane(txtResumen);
        panel.add(scrollResumen);



        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarLocalidadDesdeUI();
            }
        });

        btnPlanificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarPlanificacion();
            }
        });


        panel.add(Box.createVerticalStrut(10));
        panel.add(new JSeparator());
        panel.add(new JLabel("--- Modificar Solución ---"));

        panel.add(new JLabel("Conexión a QUITAR del árbol:"));
        comboQuitar = new JComboBox<>();
        panel.add(comboQuitar);

        panel.add(new JLabel("Conexión a FORZAR en el árbol:"));
        comboAgregar = new JComboBox<>();
        panel.add(comboAgregar);

        JPanel panelBotonesMod = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnModificar = new JButton("Aplicar Cambio");
        btnModificar.setEnabled(false); 
        btnRestaurarMST = new JButton("Restaurar Óptimo");
        
        panelBotonesMod.add(btnModificar);
        panelBotonesMod.add(Box.createHorizontalStrut(10));
        panelBotonesMod.add(btnRestaurarMST);
        
        panel.add(Box.createVerticalStrut(5));
        panel.add(panelBotonesMod);


        btnModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aplicarModificacionManual();
            }
        });

        btnRestaurarMST.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (planificador != null) {
                    planificador.limpiarModificacionesManuales();
                    

                    txtResumen.setText(planificador.obtenerResumenSolucion());
                    planificador.dibujarSolucionEnMapa(VentanaPrincipal.this);
                    actualizarCombosModificacion();
                    
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, "Se restauró el MST óptimo.");
                }
            }
        });
        return panel;
    }
    
    private void aplicarModificacionManual() {
        Conexion aQuitar = (Conexion) comboQuitar.getSelectedItem();
        Conexion aAgregar = (Conexion) comboAgregar.getSelectedItem();

        if (aQuitar == null || aAgregar == null) {
            JOptionPane.showMessageDialog(this, "Seleccione ambas conexiones para hacer el cambio.");
            return;
        }
        planificador.cambiarConexionManual(aQuitar, aAgregar);


        planificador.dibujarSolucionEnMapa(this);
        

        txtResumen.setText(planificador.obtenerResumenSolucion());


        double nuevoCosto = planificador.getCostoTotalSolucion();
        double diferencia = nuevoCosto - costoOptimoGuardado;


        String mensaje = String.format("Se forzó la conexión.\n\nCosto Original: $%.2f\nNuevo Costo: $%.2f\n\nDiferencia a pagar: +$%.2f", 
                                       costoOptimoGuardado, nuevoCosto, Math.abs(diferencia));
        

        if (diferencia == 0) {
            mensaje += "\nEl costo no se vio afectado.";
        } else if (diferencia < 0) {

            mensaje = "Error: El nuevo costo es menor. El MST base estaba mal calculado."; 
        }

        JOptionPane.showMessageDialog(this, mensaje, "Impacto de la Modificación", JOptionPane.INFORMATION_MESSAGE);
    }



    private void agregarLocalidadDesdeUI() {
        try {

            String nombre = txtNombre.getText();
            Provincia prov = (Provincia) comboProvincia.getSelectedItem();
            double lat = Double.parseDouble(txtLat.getText().replace(',', '.'));
            double lon = Double.parseDouble(txtLon.getText().replace(',', '.'));


            Localidad nueva = new Localidad(nombre, prov, lat, lon);
            listaLocalidades.add(nueva);


            gestorArchivos.guardarLocalidades(listaLocalidades);
            

            txtNombre.setText(""); txtLat.setText(""); txtLon.setText("");
            nueva.dibujarEnMapa(this);
            
            JOptionPane.showMessageDialog(this, "Localidad agregada.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Las coordenadas deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Regla de Negocio", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar en el archivo.", "Error de Disco", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarPlanificacion() {
        if (listaLocalidades.size() < 2) {
            JOptionPane.showMessageDialog(this, "Agregue al menos 2 localidades.");
            return;
        }

        try {
            ParametrosCosto parametros = new ParametrosCosto(100.0, 0.20, 5000.0);
            planificador = new PlanificadorFibra(listaLocalidades, parametros);
            

            planificador.calcularSolucion();
            costoOptimoGuardado = planificador.getCostoTotalSolucion();

            txtResumen.setText(planificador.obtenerResumenSolucion());
            planificador.dibujarSolucionEnMapa(this);


            actualizarCombosModificacion();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al calcular: " + ex.getMessage());
        }
    }

    private void cargarDatosPrevios() {
        try {
            listaLocalidades = gestorArchivos.cargarLocalidades();

            for (Localidad loc : listaLocalidades) {
                loc.dibujarEnMapa(this);
            }
        } catch (IOException e) {
            System.err.println("No se pudo cargar el archivo anterior. Empezando de cero.");
        }
    }



    @Override
    public void limpiarMapa() {
        mapa.removeAllMapMarkers();
        mapa.removeAllMapPolygons();
    }

    @Override
    public void dibujarPunto(double latitud, double longitud, String etiqueta) {

        MapMarkerDot marcador = new MapMarkerDot(etiqueta, new Coordinate(latitud, longitud));
        mapa.addMapMarker(marcador);
    }

    @Override
    public void dibujarLinea(double latOrigen, double lonOrigen, double latDestino, double lonDestino) {

        List<Coordinate> ruta = new ArrayList<>();
        ruta.add(new Coordinate(latOrigen, lonOrigen));
        ruta.add(new Coordinate(latDestino, lonDestino));
        ruta.add(new Coordinate(latOrigen, lonOrigen)); 

        MapPolygonImpl linea = new MapPolygonImpl(ruta);
        linea.setColor(Color.BLUE); 

        
        mapa.addMapPolygon(linea);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }

    private void actualizarCombosModificacion() {
        if (comboQuitar == null || comboAgregar == null) return; // Si aún no se creó la UI, salir

        comboQuitar.removeAllItems();
        comboAgregar.removeAllItems();

        List<Conexion> aristasActuales = planificador.getConexionesSolucionSegura();
        for (Conexion c : aristasActuales) {
            comboQuitar.addItem(c);
        }

        ParametrosCosto parametrosTemp = new ParametrosCosto(100.0, 0.20, 5000.0); // Misma config
        for (int i = 0; i < listaLocalidades.size(); i++) {
            for (int j = i + 1; j < listaLocalidades.size(); j++) {
                Conexion posible = new Conexion(listaLocalidades.get(i), listaLocalidades.get(j), parametrosTemp);
                if (!aristasActuales.contains(posible)) {
                    comboAgregar.addItem(posible);
                }
            }
        }
        
        btnModificar.setEnabled(true);
    }}