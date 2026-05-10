package interfaz;

import negocio.*;
import persistencia.GestorArchivos;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame implements VisualizadorMapa {

    private JMapViewer mapa;
    private List<Localidad> listaLocalidades;
    private PlanificadorFibra planificador;
    private GestorArchivos gestorArchivos;
    private ParametroCosto parametrosActuales;

    private JTextField txtNombre, txtLat, txtLon;
    private JComboBox<Provincia> comboProvincia;
    private JTextField txtCostoPorKm, txtPorcentaje, txtCostoInterprov;
    private JTextArea txtResumen;
    private JComboBox<Conexion> comboQuitar;
    private JComboBox<Conexion> comboAgregar;
    private JButton btnModificar;
    private JButton btnRestaurarMST;
    private double costoOptimoGuardado;

    public VentanaPrincipal() {
        configurarVentana();
        inicializarMapa();
        inicializarDatos();
        add(crearPanelIzquierdo(), BorderLayout.WEST);
    }

    private void configurarVentana() {
        setTitle("Planificador de Fibra Óptica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarMapa() {
        mapa = new JMapViewer();
        mapa.setDisplayPosition(new Coordinate(-38.41, -63.61), 4);
        add(mapa, BorderLayout.CENTER);
    }

    private void inicializarDatos() {
        listaLocalidades = new ArrayList<>();
        gestorArchivos = new GestorArchivos("localidades_guardadas.txt");
        cargarDatosPrevios();
    }

    // === ARMADO DE LA UI (cada panel en su método) ===

    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(380, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(crearPanelParametros());
        panel.add(separadorVertical());
        panel.add(crearPanelCargaLocalidad());
        panel.add(separadorVertical());
        panel.add(crearPanelPlanificacion());
        panel.add(separadorVertical());
        panel.add(crearPanelModificacion());

        return panel;
    }

    private JPanel crearPanelParametros() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("--- Parámetros de Costo ---");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(titulo);

        JLabel lbl1 = new JLabel("Costo por km ($):");
        lbl1.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lbl1);
        txtCostoPorKm = new JTextField("100.0");
        txtCostoPorKm.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtCostoPorKm.setMaximumSize(new Dimension(250, 25));
        p.add(txtCostoPorKm);

        JLabel lbl2 = new JLabel("Aumento si > 300 km (ej: 0.20):");
        lbl2.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lbl2);
        txtPorcentaje = new JTextField("0.20");
        txtPorcentaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPorcentaje.setMaximumSize(new Dimension(250, 25));
        p.add(txtPorcentaje);

        JLabel lbl3 = new JLabel("Costo fijo interprovincial ($):");
        lbl3.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lbl3);
        txtCostoInterprov = new JTextField("5000.0");
        txtCostoInterprov.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtCostoInterprov.setMaximumSize(new Dimension(250, 25));
        p.add(txtCostoInterprov);

        return p;
    }
    
    private JPanel crearPanelCargaLocalidad() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("--- Agregar Localidad ---");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(titulo);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lblNombre);
        txtNombre = new JTextField();
        txtNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtNombre.setMaximumSize(new Dimension(250, 25));
        p.add(txtNombre);

        JLabel lblProv = new JLabel("Provincia:");
        lblProv.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lblProv);
        comboProvincia = new JComboBox<>(Provincia.values());
        comboProvincia.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboProvincia.setMaximumSize(new Dimension(250, 25));
        p.add(comboProvincia);

        JLabel lblLat = new JLabel("Latitud (-90 a 90):");
        lblLat.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lblLat);
        txtLat = new JTextField();
        txtLat.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtLat.setMaximumSize(new Dimension(250, 25));
        p.add(txtLat);

        JLabel lblLon = new JLabel("Longitud (-180 a 180):");
        lblLon.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lblLon);
        txtLon = new JTextField();
        txtLon.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtLon.setMaximumSize(new Dimension(250, 25));
        p.add(txtLon);

        JButton btnAgregar = new JButton("Agregar Localidad");
        btnAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAgregar.addActionListener(e -> agregarLocalidadDesdeUI());
        p.add(Box.createVerticalStrut(8));
        p.add(btnAgregar);
        return p;
    }

    private JPanel crearPanelPlanificacion() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnPlanificar = new JButton("Calcular y Dibujar MST");
        btnPlanificar.setBackground(new Color(135, 206, 250));
        btnPlanificar.setFont(new Font("Arial", Font.BOLD, 14));
        btnPlanificar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPlanificar.addActionListener(e -> ejecutarPlanificacion());
        p.add(btnPlanificar);

        // Panel con los dos botones de borrar lado a lado, centrados
        JPanel panelBorrar = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panelBorrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBorrar.setMaximumSize(new Dimension(300, 40));

        JButton btnBorrarTodo = new JButton("Borrar Todo");
        btnBorrarTodo.setBackground(new Color(255, 153, 153));
        btnBorrarTodo.addActionListener(e -> borrarTodo());

        JButton btnBorrarUna = new JButton("Borrar Una");
        btnBorrarUna.setBackground(new Color(255, 200, 153));
        btnBorrarUna.addActionListener(e -> borrarUnaLocalidad());

        panelBorrar.add(btnBorrarTodo);
        panelBorrar.add(btnBorrarUna);
        p.add(Box.createVerticalStrut(5));
        p.add(panelBorrar);

        p.add(Box.createVerticalStrut(8));
        txtResumen = new JTextArea(8, 20);
        txtResumen.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtResumen);
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(scroll);
        return p;
    }

    private JPanel crearPanelModificacion() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("--- Modificar Solución ---");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(titulo);

        JLabel lblQuitar = new JLabel("Conexión a QUITAR:");
        lblQuitar.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lblQuitar);
        comboQuitar = new JComboBox<>();
        comboQuitar.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboQuitar.setMaximumSize(new Dimension(300, 25));
        p.add(comboQuitar);

        JLabel lblAgregar = new JLabel("Conexión a FORZAR:");
        lblAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lblAgregar);
        comboAgregar = new JComboBox<>();
        comboAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboAgregar.setMaximumSize(new Dimension(300, 25));
        p.add(comboAgregar);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        botones.setAlignmentX(Component.CENTER_ALIGNMENT);
        botones.setMaximumSize(new Dimension(300, 40));
        btnModificar = new JButton("Aplicar Cambio");
        btnModificar.setEnabled(false);
        btnModificar.addActionListener(e -> aplicarModificacionManual());
        btnRestaurarMST = new JButton("Restaurar Óptimo");
        btnRestaurarMST.addActionListener(e -> restaurarMSTOptimo());
        botones.add(btnModificar);
        botones.add(btnRestaurarMST);

        p.add(Box.createVerticalStrut(5));
        p.add(botones);
        return p;
    }

    private Component separadorVertical() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(Box.createVerticalStrut(10));
        p.add(new JSeparator());
        p.add(Box.createVerticalStrut(10));
        return p;
    }

    // === ACCIONES DEL USUARIO ===

    private ParametroCosto leerParametrosDesdeUI() {
        double costoKm = Double.parseDouble(txtCostoPorKm.getText().replace(',', '.'));
        double porcentaje = Double.parseDouble(txtPorcentaje.getText().replace(',', '.'));
        double costoInter = Double.parseDouble(txtCostoInterprov.getText().replace(',', '.'));
        return new ParametroCosto(costoKm, porcentaje, costoInter);
    }

    private void agregarLocalidadDesdeUI() {
        try {
            Localidad nueva = construirLocalidadDesdeUI();
            if (listaLocalidades.contains(nueva)) {
                mostrarAdvertencia("Ya existe una localidad con ese nombre en esa provincia.");
                return;
            }
            listaLocalidades.add(nueva);
            gestorArchivos.guardarLocalidades(listaLocalidades);
            limpiarCamposLocalidad();
            dibujarLocalidad(nueva);
            JOptionPane.showMessageDialog(this, "Localidad agregada.");

        } catch (NumberFormatException ex) {
            mostrarError("Las coordenadas deben ser números válidos.");
        } catch (IllegalArgumentException ex) {
            mostrarAdvertencia(ex.getMessage());
        } catch (IOException ex) {
            mostrarError("Error al guardar en el archivo.");
        }
    }

    private Localidad construirLocalidadDesdeUI() {
        String nombre = txtNombre.getText();
        Provincia prov = (Provincia) comboProvincia.getSelectedItem();
        double lat = Double.parseDouble(txtLat.getText().replace(',', '.'));
        double lon = Double.parseDouble(txtLon.getText().replace(',', '.'));
        return new Localidad(nombre, prov, lat, lon);
    }

    private void limpiarCamposLocalidad() {
        txtNombre.setText("");
        txtLat.setText("");
        txtLon.setText("");
    }

    private void ejecutarPlanificacion() {
        try {
            parametrosActuales = leerParametrosDesdeUI();
            // No validamos size aquí: dejamos que negocio decida y nos avise
            planificador = new PlanificadorFibra(listaLocalidades, parametrosActuales);
            planificador.calcularSolucion();
            costoOptimoGuardado = planificador.getCostoTotalSolucion();

            txtResumen.setText(armarResumenSolucion(planificador));
            dibujarSolucion(planificador);
            actualizarCombosModificacion();

        } catch (NumberFormatException ex) {
            mostrarError("Revisá los parámetros de costo: deben ser números válidos.");
        } catch (IllegalArgumentException ex) {
            mostrarAdvertencia(ex.getMessage());
        }
    }

    private void aplicarModificacionManual() {
        Conexion aQuitar = (Conexion) comboQuitar.getSelectedItem();
        Conexion aAgregar = (Conexion) comboAgregar.getSelectedItem();

        if (aQuitar == null || aAgregar == null) {
            mostrarAdvertencia("Seleccioná ambas conexiones para hacer el cambio.");
            return;
        }
        planificador.cambiarConexionManual(aQuitar, aAgregar);
        dibujarSolucion(planificador);
        txtResumen.setText(armarResumenSolucion(planificador));
        mostrarImpactoCambio();
    }

    private void mostrarImpactoCambio() {
        double nuevoCosto = planificador.getCostoTotalSolucion();
        double diferencia = nuevoCosto - costoOptimoGuardado;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Cambio aplicado.%n%n"));
        sb.append(String.format("Costo Original: $%.2f%n", costoOptimoGuardado));
        sb.append(String.format("Nuevo Costo: $%.2f%n", nuevoCosto));
        sb.append(String.format("Diferencia: $%.2f%n", diferencia));

        if (diferencia == 0) sb.append("El costo no se vio afectado.");
        else if (diferencia > 0) sb.append("El proyecto cuesta más que el óptimo.");
        else sb.append("El nuevo costo es menor (revisá las restricciones aplicadas).");

        JOptionPane.showMessageDialog(this, sb.toString(),
            "Impacto de la Modificación", JOptionPane.INFORMATION_MESSAGE);
    }

    private void restaurarMSTOptimo() {
        if (planificador == null) return;
        planificador.limpiarModificacionesManuales();
        txtResumen.setText(armarResumenSolucion(planificador));
        dibujarSolucion(planificador);
        actualizarCombosModificacion();
        JOptionPane.showMessageDialog(this, "Se restauró el MST óptimo.");
    }

    // === FORMATEO DE TEXTO (responsabilidad de la UI) ===

    private String armarResumenSolucion(PlanificadorFibra plan) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- PLAN DE FIBRA ÓPTICA ---\n");
        for (Conexion c : plan.getConexionesSolucion()) {
            sb.append(c.toString()).append("\n");
        }
        sb.append(String.format("Costo Total del Proyecto: $%.2f", plan.getCostoTotalSolucion()));
        return sb.toString();
    }

    // === DIBUJO ===

    private void dibujarLocalidad(Localidad loc) {
        dibujarPunto(loc.getLatitud(), loc.getLongitud(), loc.getNombre());
    }

    private void dibujarSolucion(PlanificadorFibra plan) {
        limpiarMapa();
        for (Localidad loc : plan.getLocalidades()) dibujarLocalidad(loc);
        for (Conexion c : plan.getConexionesSolucion()) {
            dibujarLinea(
                c.getOrigen().getLatitud(), c.getOrigen().getLongitud(),
                c.getDestino().getLatitud(), c.getDestino().getLongitud()
            );
        }
    }

    private void cargarDatosPrevios() {
        try {
            listaLocalidades = gestorArchivos.cargarLocalidades();
            for (Localidad loc : listaLocalidades) dibujarLocalidad(loc);
        } catch (IOException e) {
            System.err.println("No se pudo cargar el archivo anterior. Empezando de cero.");
        }
    }

    private void actualizarCombosModificacion() {
        if (comboQuitar == null || comboAgregar == null) return;
        comboQuitar.removeAllItems();
        comboAgregar.removeAllItems();

        List<Conexion> aristasActuales = planificador.getConexionesSolucion();
        for (Conexion c : aristasActuales) comboQuitar.addItem(c);

        for (int i = 0; i < listaLocalidades.size(); i++) {
            for (int j = i + 1; j < listaLocalidades.size(); j++) {
                try {
                    Conexion posible = new Conexion(
                        listaLocalidades.get(i),
                        listaLocalidades.get(j),
                        parametrosActuales);
                    if (!aristasActuales.contains(posible)) comboAgregar.addItem(posible);
                } catch (IllegalArgumentException ex) {
                    // Ignoramos pares inválidos
                }
            }
        }
        btnModificar.setEnabled(true);
    }

    // === MENSAJES ===

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAdvertencia(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    // === VisualizadorMapa ===

    @Override
    public void limpiarMapa() {
        mapa.removeAllMapMarkers();
        mapa.removeAllMapPolygons();
    }

    @Override
    public void dibujarPunto(double latitud, double longitud, String etiqueta) {
        mapa.addMapMarker(new MapMarkerDot(etiqueta, new Coordinate(latitud, longitud)));
    }

    @Override
    public void dibujarLinea(double latOrigen, double lonOrigen,
                             double latDestino, double lonDestino) {
        // JMapViewer dibuja polígonos cerrados, repetimos el origen para que parezca línea
        List<Coordinate> ruta = new ArrayList<>();
        ruta.add(new Coordinate(latOrigen, lonOrigen));
        ruta.add(new Coordinate(latDestino, lonDestino));
        ruta.add(new Coordinate(latOrigen, lonOrigen));

        MapPolygonImpl linea = new MapPolygonImpl(ruta);
        linea.setColor(Color.BLUE);
        mapa.addMapPolygon(linea);
    }

    private void borrarTodo() {
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Seguro que querés borrar todas las localidades y empezar de cero?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) return;

        // Limpiar datos de memoria
        listaLocalidades.clear();
        planificador = null;
        costoOptimoGuardado = 0;

        // Limpiar la UI
        limpiarMapa();
        txtResumen.setText("");
        comboQuitar.removeAllItems();
        comboAgregar.removeAllItems();
        btnModificar.setEnabled(false);

        // Borrar también del archivo (para que no vuelva a aparecer al reiniciar)
        try {
            gestorArchivos.guardarLocalidades(listaLocalidades);
        } catch (IOException ex) {
            mostrarError("No se pudo limpiar el archivo guardado.");
        }

        JOptionPane.showMessageDialog(this, "Localidades borradas. Empezá agregando nuevas localidades.");
    }
    
    private void borrarUnaLocalidad() {
        if (listaLocalidades.isEmpty()) {
            mostrarAdvertencia("No hay localidades cargadas para borrar.");
            return;
        }

        // Combo con las localidades disponibles
        Localidad[] opciones = listaLocalidades.toArray(new Localidad[0]);
        Localidad seleccionada = (Localidad) JOptionPane.showInputDialog(
            this,
            "¿Qué localidad querés borrar?",
            "Borrar Localidad",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (seleccionada == null) return; // canceló

        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Borrar \"" + seleccionada.getNombre() + "\"?",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (respuesta != JOptionPane.YES_OPTION) return;

        listaLocalidades.remove(seleccionada);

        try {
            gestorArchivos.guardarLocalidades(listaLocalidades);
        } catch (IOException ex) {
            mostrarError("No se pudo actualizar el archivo.");
            return;
        }

        // Redibujar el mapa
        limpiarMapa();
        for (Localidad loc : listaLocalidades) dibujarLocalidad(loc);

        // La planificación anterior ya no sirve
        planificador = null;
        txtResumen.setText("");
        comboQuitar.removeAllItems();
        comboAgregar.removeAllItems();
        btnModificar.setEnabled(false);

        JOptionPane.showMessageDialog(this,
            "Listo, se borró \"" + seleccionada.getNombre() + "\".");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}