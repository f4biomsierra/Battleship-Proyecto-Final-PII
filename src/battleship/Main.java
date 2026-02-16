/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Main extends JFrame {

    CardLayout cl = new CardLayout();
    JPanel contenedor = new JPanel(cl);
    public int p1Colocados = 0, p2Colocados = 0;
    JTextField usuario = new JTextField(10);
    JPasswordField contra = new JPasswordField(10);

    public JButton[][] botones1 = new JButton[8][8];
    public JButton[][] botones2 = new JButton[8][8];
    boolean turnoJugador1 = true;
    Player jugador2;
    public boolean enJuego = false;
    JLabel lblEstadoTurno = new JLabel("Esperando inicio...", SwingConstants.CENTER);
    //Imágenes de barcos
    ImageIcon imgPortaaviones;
    ImageIcon imgAcorazado;
    ImageIcon imgSubmarino;
    ImageIcon imgDestructor;

    public Main() {
        setTitle("Battleship");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setAlwaysOnTop(true); // Mantener la ventana siempre al frente

        // Llamada al método movido a Battleship
        JPanel pantallaJuego = Battleship.crearPantallaJuego(this, botones1, botones2, lblEstadoTurno, cl, contenedor);
        contenedor.add(pantallaJuego, "JUEGO");

        crearLogin();
        crearMenu();

        add(contenedor);
        cl.show(contenedor, "LOGIN");
        setLocationRelativeTo(null);
        setVisible(true);
        toFront(); // Traer la ventana al frente
        requestFocus(); // Dar foco a la ventana
    }

    public static ImageIcon cargarImagenBarcos(String nombre, int ancho, int alto) {
        java.net.URL imgURL = Main.class.getResource("/recursos/" + nombre);
        if (imgURL != null) {
            ImageIcon iconoOriginal = new ImageIcon(imgURL);
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(imgEscalada);
        }
        return null;
    }

    public static ImageIcon cargarImagen(String nombre) {
        java.net.URL imgURL = Main.class.getResource("/recursos/" + nombre);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        }
        return null;
    }

    void crearLogin() {
        JLabel fondoLogin = new JLabel(cargarImagen("wallpaper2.gif"));
        fondoLogin.setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        Font fuenteBasic = new Font("Basic", Font.BOLD, 14);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(fuenteBasic);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setFont(fuenteBasic);

        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");
        JButton btnCrear = new JButton("Crear Cuenta");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblUsuario, gbc);

        gbc.gridx = 1;
        panel.add(txtUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblPassword, gbc);

        gbc.gridx = 1;
        panel.add(txtPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);
        gbc.gridx = 1;
        panel.add(btnCrear, gbc);

        btnLogin.addActionListener(e -> {
            Player jugador = Battleship.obtenerPlayer(txtUser.getText(), new String(txtPass.getPassword()));
            if (jugador != null) {
                Battleship.userActual = jugador;
                cl.show(contenedor, "MENU");
            } else {
                JOptionPane.showMessageDialog(this, "Error de Login");
            }
        });

        btnCrear.addActionListener(e -> {
            String usuario = txtUser.getText();
            String password = new String(txtPass.getPassword());

            // Validar que no estén vacíos
            if (usuario.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes ingresar un nombre de usuario");
                return;
            }

            if (password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes ingresar una contraseña");
                return;
            }

            if (Battleship.crearPlayer(usuario, password)) {
                JOptionPane.showMessageDialog(this, "Registrado");
            } else {
                JOptionPane.showMessageDialog(this, "Ya existe");
            }
        });

        fondoLogin.add(panel);
        contenedor.add(fondoLogin, "LOGIN");
    }

    void crearMenu() {
        JLabel fondoMenu = new JLabel(cargarImagen("wallpaper_menu.png"));
        fondoMenu.setLayout(new GridBagLayout());

        JPanel panelCentrado = new JPanel(new GridBagLayout());
        panelCentrado.setOpaque(false);

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espacio entre botones
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton jugar = new JButton("Jugar");
        JButton config = new JButton("Configuración");
        JButton salir = new JButton("Cerrar Sesión");
        JButton perfil = new JButton("Mi Perfil");
        JButton reportes = new JButton("Reportes");
        JButton loginJ2 = new JButton("Login Jugador 2 (Pendiente)");

        loginJ2.addActionListener(evento -> {
            if (Battleship.jugador2 == null) {
                // Lógica de Login
                String usuario = JOptionPane.showInputDialog(this, "Usuario J2:");

                // Validar que no canceló o dejó vacío
                if (usuario == null || usuario.trim().isEmpty()) {
                    return; // Salir sin hacer nada
                }

                String password = JOptionPane.showInputDialog(this, "Password J2:");

                // Validar que no canceló o dejó vacío
                if (password == null || password.trim().isEmpty()) {
                    return; // Salir sin hacer nada
                }

                Player p2 = Battleship.obtenerPlayer(usuario, password);

                if (p2 != null) {
                    if (p2 == Battleship.userActual) {
                        JOptionPane.showMessageDialog(this, "El Jugador 2 no puede ser el mismo que el Jugador 1");
                    } else {
                        Battleship.jugador2 = p2;
                        loginJ2.setText("J2: " + p2.getUsername() + " (Editar)");
                        JOptionPane.showMessageDialog(this, "Jugador 2 listo.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
                }
            } else {
                // NUEVA LÓGICA: Si ya hay un J2, permitir editarlo o quitarlo
                Object[] opciones = {"Cambiar Usuario", "Cambiar Password", "Quitar Jugador 2", "Cancelar"};
                int sel = JOptionPane.showOptionDialog(this, "Perfil de " + Battleship.jugador2.getUsername(),
                        "Opciones J2", 0, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[3]);

                if (sel == 0) { // Cambiar Usuario
                    String nuevo = JOptionPane.showInputDialog(this, "Nuevo nombre para J2:");
                    if (nuevo != null && !nuevo.trim().isEmpty() && Battleship.obtenerPlayerPorNombre(nuevo) == null) {
                        Battleship.jugador2.setUsername(nuevo);
                        loginJ2.setText("J2: " + nuevo + " (Editar)");
                        JOptionPane.showMessageDialog(this, "Usuario actualizado");
                    } else if (nuevo != null && !nuevo.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Ese nombre ya existe");
                    }
                } else if (sel == 1) { // Cambiar Pass
                    String pass = JOptionPane.showInputDialog(this, "Nueva contraseña para J2:");
                    if (pass != null && !pass.trim().isEmpty()) {
                        Battleship.jugador2.setPassword(pass);
                        JOptionPane.showMessageDialog(this, "Contraseña actualizada");
                    }
                } else if (sel == 2) { // Quitar J2
                    Battleship.jugador2 = null;
                    loginJ2.setText("Login Jugador 2 (Pendiente)");
                    JOptionPane.showMessageDialog(this, "Jugador 2 removido");
                }
            }
        });

        perfil.addActionListener(evento -> {
            Object[] opciones = {"Cambiar Usuario", "Cambiar Password", "Eliminar Cuenta", "Cerrar"};
            int seleccion = JOptionPane.showOptionDialog(this,
                    "Usuario: " + Battleship.userActual.getUsername() + "\nPuntos: " + Battleship.userActual.getPuntaje(),
                    "Mi Perfil",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, opciones, opciones[3]);

            switch (seleccion) {
                case 0: // Cambiar Usuario
                    String nuevoUser = JOptionPane.showInputDialog(this, "Nuevo nombre de usuario:");
                    if (nuevoUser != null && !nuevoUser.trim().isEmpty()) {
                        // Verificar si el nombre ya existe
                        if (Battleship.obtenerPlayerPorNombre(nuevoUser) == null) {
                            Battleship.userActual.setUsername(nuevoUser);
                            JOptionPane.showMessageDialog(this, "Usuario actualizado con éxito.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Ese nombre de usuario ya está en uso.");
                        }
                    }
                    break;

                case 1: // Cambiar Password
                    String nuevaPass = JOptionPane.showInputDialog(this, "Nueva contraseña:");
                    if (nuevaPass != null && !nuevaPass.trim().isEmpty()) {
                        Battleship.userActual.setPassword(nuevaPass);
                        JOptionPane.showMessageDialog(this, "Contraseña actualizada.");
                    }
                    break;

                case 2: // Eliminar Cuenta
                    int confirmar = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar tu cuenta?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (confirmar == JOptionPane.YES_OPTION) {
                        Battleship.eliminarCuenta(Battleship.userActual);
                        cl.show(contenedor, "LOGIN");
                    }
                    break;
            }
        });

        jugar.addActionListener(evento -> {
            if (Battleship.jugador2 == null) {
                JOptionPane.showMessageDialog(this, "¡Debes ingresar un Jugador 2 primero!");
                return;
            }
            Battleship.inicializarPartida();
            p1Colocados = 0;
            p2Colocados = 0;
            enJuego = false;
            Battleship.actualizarTablero(this, botones1, lblEstadoTurno);
            Battleship.actualizarTablero(this, botones2, lblEstadoTurno);
            cl.show(contenedor, "JUEGO");
        });

        Dimension tamanoBoton = new Dimension(300, 50);
        Font fuenteBotones = new Font("Arial", Font.BOLD, 16);

        JButton[] botonesMenu = {jugar, loginJ2, config, perfil, reportes, salir};
        for (int contador = 0; contador < botonesMenu.length; contador++) {
            botonesMenu[contador].setPreferredSize(tamanoBoton);
            botonesMenu[contador].setMinimumSize(tamanoBoton);
            botonesMenu[contador].setMaximumSize(tamanoBoton);
            botonesMenu[contador].setFont(fuenteBotones);
            botonesMenu[contador].setFocusPainted(false);

            gbc.gridy = contador;
            panelBotones.add(botonesMenu[contador], gbc);
        }

        config.addActionListener(evento -> {
            String[] opciones = {"EASY", "NORMAL", "EXPERT", "GENIUS"};
            String seleccion = (String) JOptionPane.showInputDialog(this, "Seleccione Dificultad", "Config",
                    JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[1]);
            if (seleccion != null) {
                Battleship.setDificultad(seleccion);
            }

            String[] modos = {"TUTORIAL", "ARCADE"};
            String modo = (String) JOptionPane.showInputDialog(this, "Seleccione Modo", "Config",
                    JOptionPane.QUESTION_MESSAGE, null, modos, modos[0]);
            if (modo != null) {
                Battleship.modoJuego = modo;
            }
        });

        reportes.addActionListener(evento -> {
            StringBuilder sb = new StringBuilder("--- RANKING ---\n");
            Battleship.players.sort((jugadorA, jugadorB) -> jugadorB.getPuntaje() - jugadorA.getPuntaje());
            for (Player p2 : Battleship.players) {
                sb.append(p2.getUsername()).append(": ").append(p2.getPuntaje()).append(" pts\n");
            }
            sb.append("\n--- ÚLTIMOS JUEGOS ---\n");
            for (String reg : Battleship.userActual.getRegistros()) {
                if (reg != null) {
                    sb.append(reg).append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        });

        salir.addActionListener(evento -> cl.show(contenedor, "LOGIN"));

        panelCentrado.add(panelBotones);
        fondoMenu.add(panelCentrado);
        contenedor.add(fondoMenu, "MENU");
    }

    public static void main(String[] args) {
        new Main();
    }

}