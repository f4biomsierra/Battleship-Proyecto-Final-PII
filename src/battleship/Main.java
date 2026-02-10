/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import javax.swing.*;
import java.awt.*;
import java.util.*;


public class Main extends JFrame {
    
    CardLayout cl=new CardLayout();
    JPanel contenedor=new JPanel(cl); 
    int p1Colocados = 0, p2Colocados = 0;
    JTextField usuario=new JTextField(10);
    JPasswordField contra=new JPasswordField(10);
    
    JButton[][] botones1=new JButton[8][8];
    JButton[][] botones2=new JButton[8][8];
    boolean turnoJugador1=true;
    Player jugador2;
    boolean enJuego = false;
    JLabel lblEstadoTurno = new JLabel("Esperando inicio...", SwingConstants.CENTER);
    //Imágenes de barcos
    ImageIcon imgPortaaviones;
    ImageIcon imgAcorazado;
    ImageIcon imgSubmarino;
    ImageIcon imgDestructor;
    
    public Main(){
        setTitle("Battleship");
        setSize(1280,720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        crearPantallaJuego();
        crearLogin();
        crearMenu();
        
        
        add(contenedor);
        cl.show(contenedor, "LOGIN");
        setLocationRelativeTo(null);
        setVisible(true);
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
        
        Font fuenteBasic=new Font("Basic", Font.BOLD, 14);

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
            Player p = Battleship.obtenerPlayer(txtUser.getText(), new String(txtPass.getPassword()));
            if (p != null) {
                Battleship.userActual = p;
                cl.show(contenedor, "MENU");
            } else {
                JOptionPane.showMessageDialog(this, "Error de Login");
            }
        });

        btnCrear.addActionListener(e -> {
            if (Battleship.crearPlayer(txtUser.getText(), new String(txtPass.getPassword()))) {
                JOptionPane.showMessageDialog(this, "Registrado");
            } else {
                JOptionPane.showMessageDialog(this, "Ya existe");
            }
        });

        fondoLogin.add(panel);
        contenedor.add(fondoLogin, "LOGIN");
    }

    
    void crearMenu() {
        JLabel fondoMenu=new JLabel(cargarImagen("wallpaper_menu.png"));
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
        
        
        loginJ2.addActionListener(e -> {
            if (Battleship.jugador2 == null) {
                // Lógica de Login (ya la tienes)
                String u = JOptionPane.showInputDialog("Usuario J2:");
                String p3 = JOptionPane.showInputDialog("Password J2:");
                Player p2 = Battleship.obtenerPlayer(u, p3);

                if (p2 != null) {
                    if (p2 == Battleship.userActual) {
                        JOptionPane.showMessageDialog(this, "El Jugador 2 no puede ser el mismo que el Jugador 1");
                    } else {
                        Battleship.jugador2 = p2;
                        loginJ2.setText("J2: " + p2.getUsername() + " (Editar)");
                        JOptionPane.showMessageDialog(this, "Jugador 2 listo.");
                    }
                }
            } else {
                // NUEVA LÓGICA: Si ya hay un J2, permitir editarlo o quitarlo
                Object[] opciones = {"Cambiar Usuario", "Cambiar Password", "Quitar Jugador 2", "Cancelar"};
                int sel = JOptionPane.showOptionDialog(this, "Perfil de " + Battleship.jugador2.getUsername(),
                        "Opciones J2", 0, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[3]);

                if (sel == 0) { // Cambiar Usuario
                    String nuevo = JOptionPane.showInputDialog("Nuevo nombre para J2:");
                    if (nuevo != null && Battleship.obtenerPlayerPorNombre(nuevo) == null) {
                        Battleship.jugador2.setUsername(nuevo);
                        loginJ2.setText("J2: " + nuevo + " (Editar)");
                    }
                } else if (sel == 1) { // Cambiar Pass
                    String pass = JOptionPane.showInputDialog("Nueva contraseña para J2:");
                    if (pass != null) {
                        Battleship.jugador2.setPassword(pass);
                    }
                } else if (sel == 2) { // Quitar J2
                    Battleship.jugador2 = null;
                    loginJ2.setText("Login Jugador 2 (Pendiente)");
                }
            }
        });
        
        perfil.addActionListener(e -> {
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
        

        jugar.addActionListener(e -> {
            if (Battleship.jugador2 == null) {
                JOptionPane.showMessageDialog(this, "¡Debes ingresar un Jugador 2 primero!");
                return;
            }
            Battleship.inicializarPartida();
            p1Colocados = 0;
            p2Colocados = 0;
            enJuego = false;
            actualizarTablero();
            cl.show(contenedor, "JUEGO");
        });
        
        Dimension tamanoBoton = new Dimension(300, 50);
        Font fuenteBotones = new Font("Arial", Font.BOLD, 16);

        
        JButton[] botonesMenu = {jugar, loginJ2, config, perfil, reportes, salir};
        for (int i = 0; i < botonesMenu.length; i++) {
            botonesMenu[i].setPreferredSize(tamanoBoton);
            botonesMenu[i].setMinimumSize(tamanoBoton);
            botonesMenu[i].setMaximumSize(tamanoBoton);
            botonesMenu[i].setFont(fuenteBotones);
            botonesMenu[i].setFocusPainted(false);
            
            gbc.gridy = i;
            panelBotones.add(botonesMenu[i], gbc);
        }

        config.addActionListener(e -> {
            String[] opciones = {"EASY", "NORMAL", "EXPERT", "GENIUS"};
            String seleccion = (String) JOptionPane.showInputDialog(this, "Seleccione Dificultad", "Config", 
                                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[1]);
            if(seleccion != null) Battleship.setDificultad(seleccion);
            
            String[] modos = {"TUTORIAL", "ARCADE"};
            String modo=(String) JOptionPane.showInputDialog(this, "Seleccione Modo", "Config", 
                                JOptionPane.QUESTION_MESSAGE, null, modos, modos[0]);
            if(modo!= null) Battleship.modoJuego = modo;
        });
        
        reportes.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("--- RANKING ---\n");
            Battleship.players.sort((a, b) -> b.getPuntaje() - a.getPuntaje());
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

        salir.addActionListener(e -> cl.show(contenedor, "LOGIN"));

        panelCentrado.add(panelBotones);
        fondoMenu.add(panelCentrado);
        contenedor.add(fondoMenu, "MENU");
    }
    
    
    void crearPantallaJuego() {
        JPanel panel = new JPanel(new BorderLayout());

        
        lblEstadoTurno.setFont(new Font("Arial", Font.BOLD, 18));
        lblEstadoTurno.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel grids = new JPanel(new GridLayout(1, 2, 40, 0));
        grids.add(crearGrid(botones1, true));
        grids.add(crearGrid(botones2, false));

        JButton btnRendirse = new JButton("RENDIRSE");
        btnRendirse.setBackground(Color.RED);
        btnRendirse.setForeground(Color.WHITE);

        
        btnRendirse.addActionListener(e -> {
            if (enJuego) {
                // Si es turno de P1 (turnoJugador1 = true), el ganador es el J2
                Player ganador = Battleship.turnoJugador1 ? Battleship.jugador2 : Battleship.userActual;
                Player perdedor = Battleship.turnoJugador1 ? Battleship.userActual : Battleship.jugador2;

                ganador.addPuntaje(3);
                ganador.addRegistro(ganador.getUsername() + " triunfó ante "+ perdedor.getUsername());
                perdedor.addRegistro(perdedor.getUsername()+ " se rindió ante "+ ganador.getUsername());

                JOptionPane.showMessageDialog(this, "¡" + perdedor.getUsername() + " se rindió!\nGanador: " + ganador.getUsername());
            }
            enJuego = false;
            cl.show(contenedor, "MENU");
        });

        panel.add(lblEstadoTurno, BorderLayout.NORTH);
        panel.add(grids, BorderLayout.CENTER);
        panel.add(btnRendirse, BorderLayout.SOUTH);
        contenedor.add(panel, "JUEGO");
    }
    
    JPanel crearGrid(JButton[][] matriz, boolean esP1) {
        JPanel p = new JPanel(new GridLayout(8, 8));
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                matriz[i][j] = new JButton("~");
                matriz[i][j].setPreferredSize(new Dimension(60, 60));
                matriz[i][j].setFocusPainted(false);
                final int f = i, c = j;
                matriz[i][j].addActionListener(e -> {
                    if (!enJuego) {
                        // Colocación
                        if (esP1 && p1Colocados < Battleship.dificultadActual) {
                            if (Battleship.colocarBarco(f, c, p1Colocados, true)) {
                                p1Colocados++;
                            }
                        } else if (!esP1 && p2Colocados < Battleship.dificultadActual) {
                            if (Battleship.colocarBarco(f, c, p2Colocados, false)) {
                                p2Colocados++;
                            }
                        }
                        if (p1Colocados == Battleship.dificultadActual && p2Colocados == Battleship.dificultadActual) {
                            enJuego = true;
                            JOptionPane.showMessageDialog(this, "¡Batalla!");
                        }
                    } else {
                        // Ataque
                        if ((Battleship.turnoJugador1 && !esP1) || (!Battleship.turnoJugador1 && esP1)) {
                            String res = Battleship.bombardear(f, c);
                            if (res.equals("WIN")) {
                                Player pGanador = Battleship.turnoJugador1 ? Battleship.userActual : Battleship.jugador2;
                                Player perdedor = Battleship.turnoJugador1 ? Battleship.jugador2 : Battleship.userActual;

                                pGanador.addPuntaje(3); // Ejemplo de puntos
                                pGanador.addRegistro("Victoria de "+ pGanador.getUsername() +" contra " + perdedor.getUsername());
                                perdedor.addRegistro("Victoria de " + pGanador.getUsername() +" contra " + perdedor.getUsername());

                                JOptionPane.showMessageDialog(this, "¡EL GANADOR ES: " + pGanador.getUsername() + "!");
                                enJuego=false;
                                cl.show(contenedor, "MENU");
                            } else{
                                JOptionPane.showMessageDialog(this, res);
                            }
                        }
                    }
                    actualizarTablero();
                });
                p.add(matriz[i][j]);
            }
        }
        return p;
    }

    void actualizarTablero() {
        if (botones1[0][0] == null || Battleship.tabP1 == null) {
            return;
        }

        if (!enJuego) {
            // Fase de colocación
            if (p1Colocados < Battleship.dificultadActual) {
                lblEstadoTurno.setText("COLOCA P1: " + Battleship.userActual.getUsername());
                lblEstadoTurno.setForeground(Color.BLUE);
            } else {
                lblEstadoTurno.setText("COLOCA P2: " + Battleship.jugador2.getUsername());
                lblEstadoTurno.setForeground(Color.MAGENTA);
            }
        } else {
            // Fase de combate
            if (Battleship.turnoJugador1) {
                lblEstadoTurno.setText("TURNO DE: " + Battleship.userActual.getUsername());
                lblEstadoTurno.setForeground(Color.BLUE);
            } else {
                lblEstadoTurno.setText("TURNO DE: " + Battleship.jugador2.getUsername());
                lblEstadoTurno.setForeground(Color.RED);
            }
        }

        for (int contador=0;contador<8;contador++) {
            for (int contador2=0;contador2< 8;contador2++) {
                char c1 = Battleship.tabP1[contador][contador2];
                botones1[contador][contador2].setText("");
                botones1[contador][contador2].setIcon(obtenerIconoBarco(c1)); // ===== CAMBIO
                char c2 = Battleship.tabP2[contador][contador2];
                // Modo Arcade oculta barcos enemigos
                if ("ARCADE".equals(Battleship.modoJuego) && c2 != 'X' && c2 != 'F') {
                    botones2[contador][contador2].setText("~");
                } else {
                    botones2[contador][contador2].setText("");
                    botones2[contador][contador2].setIcon(obtenerIconoBarco(c2)); // ===== CAMBIO
                }
            }
        }

        // ... resto de la lógica para pintar los botones ~ , X, F
    }
    
    private ImageIcon obtenerIconoBarco(char c) {
        int tam = 60; // Tamaño de la celda del botón
        switch (c) {
            case 'P':
                return cargarImagenBarcos("portaaviones.png", tam, tam);
            case 'A':
                return cargarImagenBarcos("acorazado.png", tam, tam);
            case 'S':
                return cargarImagenBarcos("submarino.png", tam, tam);
            case 'D':
                return cargarImagenBarcos("destructor.png", tam, tam);
            case 'X': // Si quieres icono para impacto
                return cargarImagenBarcos("fuego.gif", tam, tam);
            default:
                return null;
        }
    }

    
    public static void main(String[] args){
        new Main();
    }
    
}
