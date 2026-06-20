package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.CitaController;
import com.dentalclinicsystem.controller.PacienteController;
import com.dentalclinicsystem.controller.ServicioController;
import com.dentalclinicsystem.controller.UsuariosController;
import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Servicio;
import com.dentalclinicsystem.model.Usuario;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DialogCita extends JDialog {
    
    private CitaController citaController;
    private PacienteController pacienteController;
    private UsuariosController usuariosController;
    private ServicioController servicioController;
    
    private Cita cita;
    private boolean guardado = false;
    private boolean esEdicion = false;
    
    private JComboBox<String> cbPaciente;
    private JComboBox<String> cbOdontologo;
    private JComboBox<String> cbServicio;
    private JComboBox<String> cbEstado;
    private DatePicker dpFecha;
    private JComboBox<String> cbHora;
    private JTextField txtDuracion;
    private JTextArea txtNota;
    private JButton btnGuardar, btnCancelar;
    private JLabel lblPacienteInfo, lblFechaInfo;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentBlue = new Color(70, 130, 200);
    private Color fieldBg = new Color(50, 50, 55);
    private Color fieldBorder = new Color(60, 60, 65);
    
    public DialogCita(JFrame parent, boolean edicion) {
        super(parent, edicion ? "Editar Cita" : "Nueva Cita", true);
        this.citaController = new CitaController();
        this.pacienteController = new PacienteController();
        this.usuariosController = new UsuariosController();
        this.servicioController = new ServicioController();
        this.esEdicion = edicion;
        initComponents();
        setSize(580, 680);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
    }
    
    private void initComponents() {
        setBackground(darkBg);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(darkBg);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(darkBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // --------------------------------------Título-----------------------------------//
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(esEdicion ? "Editar Cita" : "Nueva Cita");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textLight);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);
        row++;
        
        gbc.gridwidth = 1;
        
        //-----------------------------------PACIENTE ---------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Paciente *:"), gbc);
        gbc.gridx = 1;
        cbPaciente = new JComboBox<>();
        cbPaciente.setBackground(fieldBg);
        cbPaciente.setForeground(textLight);
        cbPaciente.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cargarPacientes();
        cbPaciente.addActionListener(e -> mostrarInfoPaciente());
        formPanel.add(cbPaciente, gbc);
        row++;
        
        // ----------------------INFORMACIÓN DEL PACIENTE ----------------------//
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblPacienteInfo = new JLabel(" ");
        lblPacienteInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPacienteInfo.setForeground(textGray);
        lblPacienteInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fieldBorder),
            new EmptyBorder(5, 10, 5, 10)
        ));
        lblPacienteInfo.setBackground(fieldBg);
        lblPacienteInfo.setOpaque(true);
        formPanel.add(lblPacienteInfo, gbc);
        row++;
        gbc.gridwidth = 1;
        
        //----------------------------ODONTÓLOGO---------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Odontólogo *:"), gbc);
        gbc.gridx = 1;
        cbOdontologo = new JComboBox<>();
        cbOdontologo.setBackground(fieldBg);
        cbOdontologo.setForeground(textLight);
        cbOdontologo.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbOdontologo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cargarOdontologos();
        cbOdontologo.addActionListener(e -> actualizarHorasDisponibles());
        formPanel.add(cbOdontologo, gbc);
        row++;
        
        // --------------------------------SERVICIO ---------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Servicio *:"), gbc);
        gbc.gridx = 1;
        cbServicio = new JComboBox<>();
        cbServicio.setBackground(fieldBg);
        cbServicio.setForeground(textLight);
        cbServicio.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbServicio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cargarServicios();
        cbServicio.addActionListener(e -> actualizarDuracion());
        formPanel.add(cbServicio, gbc);
        row++;
        
        // ---------------------------------FECHA ---------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Fecha *:"), gbc);
        gbc.gridx = 1;
        
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        dpFecha = new DatePicker(settings);
        dpFecha.setDate(LocalDate.now());
        dpFecha.setPreferredSize(new Dimension(180, 32));
        dpFecha.addDateChangeListener(e -> {
            actualizarHorasDisponibles();
            validarFechaSeleccionada();
        });
        
        dpFecha.getComponentDateTextField().setBackground(fieldBg);
        dpFecha.getComponentDateTextField().setForeground(textLight);
        dpFecha.getComponentDateTextField().setCaretColor(textLight);
        dpFecha.getComponentDateTextField().setBorder(BorderFactory.createLineBorder(fieldBorder));
        dpFecha.getComponentDateTextField().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        dpFecha.getComponentToggleCalendarButton().setBackground(fieldBg);
        dpFecha.getComponentToggleCalendarButton().setForeground(textLight);
        dpFecha.getComponentToggleCalendarButton().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        formPanel.add(dpFecha, gbc);
        row++;
        
        //------------------------------------- Información de fecha--------------------------------//
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblFechaInfo = new JLabel("Seleccione una fecha (puede ser hoy o futuro)");
        lblFechaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFechaInfo.setForeground(textGray);
        formPanel.add(lblFechaInfo, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // -------------------------------------------HORA --------------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Hora *:"), gbc);
        gbc.gridx = 1;
        cbHora = new JComboBox<>();
        cbHora.setBackground(fieldBg);
        cbHora.setForeground(textLight);
        cbHora.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbHora.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cargarHorasPorDefecto();
        formPanel.add(cbHora, gbc);
        row++;
        
        // ------------------------------------DURACIÓN ---------------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Duración (min):"), gbc);
        gbc.gridx = 1;
        txtDuracion = new JTextField(3);
        txtDuracion.setText("30");
        txtDuracion.setBackground(fieldBg);
        txtDuracion.setForeground(textLight);
        txtDuracion.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtDuracion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDuracion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                validarDuracion();
            }
        });
        formPanel.add(txtDuracion, gbc);
        row++;
        
        // -------------------------------------ESTADO ------------------------------------//
        if (esEdicion) {
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("Estado:"), gbc);
            gbc.gridx = 1;
            cbEstado = new JComboBox<>(Cita.getEstados());
            cbEstado.setBackground(fieldBg);
            cbEstado.setForeground(textLight);
            cbEstado.setBorder(BorderFactory.createLineBorder(fieldBorder));
            cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            formPanel.add(cbEstado, gbc);
            row++;
        }
        
        // ---------------------------------------NOTA -------------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Nota:"), gbc);
        gbc.gridx = 1;
        txtNota = new JTextArea(3, 20);
        txtNota.setLineWrap(true);
        txtNota.setWrapStyleWord(true);
        txtNota.setBackground(fieldBg);
        txtNota.setForeground(textLight);
        txtNota.setCaretColor(textLight);
        txtNota.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtNota.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollNota = new JScrollPane(txtNota);
        scrollNota.setBackground(fieldBg);
        scrollNota.setBorder(BorderFactory.createLineBorder(fieldBorder));
        formPanel.add(scrollNota, gbc);
        row++;
        
        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBackground(darkBg);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.getViewport().setBackground(darkBg);
        scrollForm.setPreferredSize(new Dimension(520, 550));
        
        // -------------------------------------BOTONES --------------------------------------//
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(darkBg);
        
        btnGuardar = createDialogButton("Guardar", accentGreen);
        btnGuardar.addActionListener(e -> guardarCita());
        btnPanel.add(btnGuardar);
        
        btnCancelar = createDialogButton("Cancelar", accentRed);
        btnCancelar.addActionListener(e -> dispose());
        btnPanel.add(btnCancelar);
        
        mainPanel.add(scrollForm, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // ------------------------------MÉTODOS AUXILIARES --------------------------------//
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(textGray);
        return label;
    }
    
    private JButton createDialogButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }
    
    private void cargarHorasPorDefecto() {
        cbHora.removeAllItems();
        cbHora.addItem("");  // Opción vacía para nueva cita
        String[] horasDefault = {"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", 
                                 "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
                                 "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};
        for (String h : horasDefault) {
            cbHora.addItem(h);
        }
    }
    
    private void cargarPacientes() {
        cbPaciente.removeAllItems();
        cbPaciente.addItem("");  // Opción vacía para nueva cita
        List<Paciente> pacientes = pacienteController.listarTodos();
        if (pacientes != null) {
            for (Paciente p : pacientes) {
                cbPaciente.addItem(p.getNombreCompleto());
            }
        }
    }
    
    private void cargarOdontologos() {
        cbOdontologo.removeAllItems();
        cbOdontologo.addItem("");  // Opción vacía para nueva cita
        List<Usuario> odontologos = usuariosController.listarOdontologos();
        if (odontologos != null) {
            for (Usuario u : odontologos) {
                cbOdontologo.addItem(u.getNombre());
            }
        }
    }
    
    private void cargarServicios() {
        cbServicio.removeAllItems();
        cbServicio.addItem("");  // Opción vacía para nueva cita
        List<Servicio> servicios = servicioController.listarActivos();
        
        if (servicios != null && !servicios.isEmpty()) {
            for (Servicio s : servicios) {
                cbServicio.addItem(s.getNombre());
            }
        } else {
            cbServicio.addItem("No hay servicios disponibles");
        }
    }
    
    private int getSelectedServicioId() {
        String nombreServicio = (String) cbServicio.getSelectedItem();
        if (nombreServicio != null && !nombreServicio.isEmpty() && !nombreServicio.equals("No hay servicios disponibles")) {
            List<Servicio> servicios = servicioController.listarActivos();
            if (servicios != null) {
                for (Servicio s : servicios) {
                    if (s.getNombre().equals(nombreServicio)) {
                        return s.getId();
                    }
                }
            }
        }
        return -1;
    }
    //-----------------------------------------Mostrar info paciente ----------------------------------//
    private void mostrarInfoPaciente() {
        String seleccion = (String) cbPaciente.getSelectedItem();
        if (seleccion != null && !seleccion.isEmpty()) {
            List<Paciente> pacientes = pacienteController.listarTodos();
            if (pacientes != null) {
                for (Paciente p : pacientes) {
                    if (p.getNombreCompleto().equals(seleccion)) {
                        String telefono = p.getTelefono() != null ? p.getTelefono() : "Sin teléfono";
                        String email = p.getEmail() != null ? p.getEmail() : "Sin email";
                        lblPacienteInfo.setText(" " + telefono + "  |   " + email);
                        return;
                    }
                }
            }
        }
        lblPacienteInfo.setText(" ");
    }
    
    //----------------------------------------Actualizar horarios disponibles------------------------//
    private void actualizarHorasDisponibles() {
        cbHora.removeAllItems();
        LocalDate fecha = dpFecha.getDate();
        int odontologoId = getSelectedOdontologoId();
        
        if (fecha == null || odontologoId <= 0) {
            cargarHorasPorDefecto();
            cbHora.setSelectedIndex(-1);
            return;
        }
        
        try {
            String fechaStr = fecha.toString();
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            List<String> horas = citaController.getHorasDisponibles(odontologoId, fechaStr, duracion);
            
            if (horas != null && !horas.isEmpty()) {
                cbHora.addItem("");  // Opción vacía
                for (String hora : horas) {
                    cbHora.addItem(hora);
                }
                cbHora.setSelectedIndex(-1);
            } else {
                cargarHorasPorDefecto();
                cbHora.setSelectedIndex(-1);
            }
        } catch (Exception e) {
            cargarHorasPorDefecto();
            cbHora.setSelectedIndex(-1);
        }
    }
    
    //--------------------------------------Actualizar Duracion------------------------------------//
    private void actualizarDuracion() {
        String nombreServicio = (String) cbServicio.getSelectedItem();
        if (nombreServicio != null && !nombreServicio.isEmpty() && !nombreServicio.equals("No hay servicios disponibles")) {
            List<Servicio> servicios = servicioController.listarActivos();
            if (servicios != null) {
                for (Servicio s : servicios) {
                    if (s.getNombre().equals(nombreServicio)) {
                        txtDuracion.setText(String.valueOf(s.getDuracionMinutos()));
                        actualizarHorasDisponibles();
                        break;
                    }
                }
            }
        }
    }
    
    //--------------------------------------Seleccionar paciente-----------------------------------//
    private int getSelectedPacienteId() {
        String seleccion = (String) cbPaciente.getSelectedItem();
        if (seleccion != null && !seleccion.isEmpty()) {
            List<Paciente> pacientes = pacienteController.listarTodos();
            if (pacientes != null) {
                for (Paciente p : pacientes) {
                    if (p.getNombreCompleto().equals(seleccion)) {
                        return p.getId();
                    }
                }
            }
        }
        return -1;
    }
    
    //-------------------------------------Seleccionar odontologo--------------------------------//
    private int getSelectedOdontologoId() {
        String seleccion = (String) cbOdontologo.getSelectedItem();
        if (seleccion != null && !seleccion.isEmpty()) {
            List<Usuario> odontologos = usuariosController.listarOdontologos();
            if (odontologos != null) {
                for (Usuario u : odontologos) {
                    if (u.getNombre().equals(seleccion)) {
                        return u.getId();
                    }
                }
            }
        }
        return -1;
    }
    
    //---------------------------------------VALIDACIONES --------------------------------------//    
    private void validarFechaSeleccionada() {
        LocalDate fecha = dpFecha.getDate();
        if (fecha != null) {
            LocalDate hoy = LocalDate.now();
            if (fecha.isBefore(hoy)) {
                lblFechaInfo.setForeground(accentRed);
                lblFechaInfo.setText("No se pueden agendar citas en fechas pasadas");
            } else {
                lblFechaInfo.setForeground(new Color(60, 180, 110));
                lblFechaInfo.setText("Fecha válida (puede ser hoy o futuro)");
            }
        } else {
            lblFechaInfo.setForeground(textGray);
            lblFechaInfo.setText("Seleccione una fecha válida");
        }
    }
    
    //------------------validar duracion----------------------//
    private void validarDuracion() {
        try {
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            if (duracion < 15 || duracion > 240) {
                txtDuracion.setBorder(BorderFactory.createLineBorder(accentRed, 2));
            } else {
                txtDuracion.setBorder(BorderFactory.createLineBorder(fieldBorder));
            }
        } catch (NumberFormatException e) {
            txtDuracion.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        }
    }
    //------------------validar campos-----------------------//
    private String validarCampos() {
        if (getSelectedPacienteId() <= 0) {
            return "Seleccione un paciente válido";
        }
        
        if (getSelectedOdontologoId() <= 0) {
            return "Seleccione un odontólogo válido";
        }
        
        if (getSelectedServicioId() <= 0) {
            return "Seleccione un servicio válido";
        }
        
        LocalDate fecha = dpFecha.getDate();
        if (fecha == null) {
            return "Seleccione una fecha";
        }
        if (fecha.isBefore(LocalDate.now())) {
            return "No se pueden agendar citas en fechas pasadas";
        }
        
        String hora = (String) cbHora.getSelectedItem();
        if (hora == null || hora.isEmpty()) {
            return "Seleccione una hora disponible";
        }
        
        try {
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            if (duracion < 15 || duracion > 240) {
                return "La duración debe estar entre 15 y 240 minutos";
            }
        } catch (NumberFormatException e) {
            return "Ingrese una duración válida (número)";
        }
        
        return null;
    }
    
    //--------------------------------------GUARDAR CITA -------------------------------------//    
    public void setCita(Cita cita) {
        this.cita = cita;
        if (cita != null && cita.getId() > 0) {
            // ===== EDITAR CITA EXISTENTE =====
            System.out.println("Editando cita ID: " + cita.getId());
            
            // Seleccionar paciente por nombre
            String nombrePaciente = cita.getPacienteNombre();
            if (nombrePaciente != null) {
                for (int i = 0; i < cbPaciente.getItemCount(); i++) {
                    if (cbPaciente.getItemAt(i).equals(nombrePaciente)) {
                        cbPaciente.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Seleccionar odontólogo por nombre
            String nombreOdontologo = cita.getOdontologoNombre();
            if (nombreOdontologo != null) {
                for (int i = 0; i < cbOdontologo.getItemCount(); i++) {
                    if (cbOdontologo.getItemAt(i).equals(nombreOdontologo)) {
                        cbOdontologo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Seleccionar servicio por nombre
            if (cita.getServicioId() > 0) {
                Servicio servicio = servicioController.buscarPorId(cita.getServicioId());
                if (servicio != null) {
                    String nombreServicio = servicio.getNombre();
                    for (int i = 0; i < cbServicio.getItemCount(); i++) {
                        if (cbServicio.getItemAt(i).equals(nombreServicio)) {
                            cbServicio.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
            
            // Seleccionar fecha
            try {
                dpFecha.setDate(LocalDate.parse(cita.getFecha()));
            } catch (Exception e) {
                dpFecha.setDate(LocalDate.now());
            }
            
            // Seleccionar hora
            actualizarHorasDisponibles();
            for (int i = 0; i < cbHora.getItemCount(); i++) {
                if (cbHora.getItemAt(i).equals(cita.getHora())) {
                    cbHora.setSelectedIndex(i);
                    break;
                }
            }
            
            txtDuracion.setText(String.valueOf(cita.getDuracion()));
            txtNota.setText(cita.getNota());
            
            if (esEdicion && cbEstado != null) {
                cbEstado.setSelectedItem(cita.getEstado());
            }
            
            mostrarInfoPaciente();
            validarFechaSeleccionada();
            
        } else {
            // ===== NUEVA CITA - TODOS LOS CAMPOS VACÍOS =====
            System.out.println("Creando nueva cita");
            
            // Limpiar todos los campos
            cbPaciente.setSelectedIndex(-1);
            cbOdontologo.setSelectedIndex(-1);
            cbServicio.setSelectedIndex(-1);
            
            // Fecha por defecto: hoy
            dpFecha.setDate(LocalDate.now());
            
            // Limpiar hora
            cargarHorasPorDefecto();
            cbHora.setSelectedIndex(-1);
            
            // Duración por defecto: 30
            txtDuracion.setText("30");
            
            // Limpiar nota
            txtNota.setText("");
            
            // Limpiar información del paciente
            lblPacienteInfo.setText(" ");
            
            // Validar fecha
            validarFechaSeleccionada();
            
            // Estado por defecto
            if (cbEstado != null) {
                cbEstado.setSelectedItem(Cita.ESTADO_PROGRAMADA);
            }
        }
    }
    
    //------------------------------------Metodo guardar cita----------------------------------//
    private void guardarCita() {
        String error = validarCampos();
        if (error != null) {
            JOptionPane.showMessageDialog(this, " " + error, "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int pacienteId = getSelectedPacienteId();
            int odontologoId = getSelectedOdontologoId();
            int servicioId = getSelectedServicioId();
            String fecha = dpFecha.getDate().toString();
            String hora = (String) cbHora.getSelectedItem();
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            String nota = txtNota.getText().trim();
            
            Cita nuevaCita = new Cita();
            
            if (esEdicion && cita != null && cita.getId() > 0) {
                nuevaCita.setId(cita.getId());
            } else {
                nuevaCita.setId(0);
            }
            
            nuevaCita.setPacienteId(pacienteId);
            nuevaCita.setOdontologoId(odontologoId);
            nuevaCita.setServicioId(servicioId);
            nuevaCita.setFecha(fecha);
            nuevaCita.setHora(hora);
            nuevaCita.setDuracion(duracion);
            nuevaCita.setNota(nota);
            nuevaCita.setModificadaPor("admin");
            
            if (esEdicion && cbEstado != null) {
                nuevaCita.setEstado((String) cbEstado.getSelectedItem());
            } else {
                nuevaCita.setEstado(Cita.ESTADO_PROGRAMADA);
            }
            
            System.out.println(" Guardando cita - ID: " + nuevaCita.getId() + 
                             " | Servicio ID: " + servicioId + 
                             " | Estado: " + nuevaCita.getEstado());
            
            if (citaController.guardarCita(nuevaCita)) {
                guardado = true;
                dispose();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al guardar: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isGuardado() {
        return guardado;
    }
}