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
    
    public Main(){
        setTitle("Battleship");
        setSize(1100,700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        crearPantallaJuego();
        crearLogin();
        crearMenu();
        
        
        add(contenedor);
        cl.show(contenedor, "LOGIN");
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    
    void crearLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");
        JButton btnCrear = new JButton("Crear Cuenta");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        panel.add(txtUser, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPass, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);
        gbc.gridx = 1;
        panel.add(btnCrear, gbc);

        btnLogin.addActionListener(e -> {
            if (Battleship.login(txtUser.getText(), new String(txtPass.getPassword()))) {
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

        contenedor.add(panel, "LOGIN");
    }

    
    void crearMenu() {
        JPanel panelCentrado = new JPanel(new GridBagLayout());
        
        JPanel panelBotones = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espacio entre botones
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton jugar = new JButton("Jugar");
        JButton config = new JButton("Configuración");
        JButton salir = new JButton("Cerrar Sesión");

        jugar.addActionListener(e -> {
            Battleship.inicializarPartida();
            p1Colocados = 0; p2Colocados = 0; enJuego = false;
            actualizarTablero();
            cl.show(contenedor, "JUEGO");
            JOptionPane.showMessageDialog(this, "Dificultad: " + Battleship.dificultadActual + " barcos. P1 coloque sus barcos.");
        });
        
        Dimension tamanoBoton = new Dimension(300, 50);
        Font fuenteBotones = new Font("Arial", Font.BOLD, 16);

        
        JButton[] botonesMenu = {jugar, config, salir};
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

        salir.addActionListener(e -> cl.show(contenedor, "LOGIN"));

        panelCentrado.add(panelBotones);
        contenedor.add(panelCentrado, "MENU");
    }
    
    
    void crearPantallaJuego() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel grids = new JPanel(new GridLayout(1, 2, 40, 0));
        grids.add(crearGrid(botones1, true));
        grids.add(crearGrid(botones2, false));
        
        JButton btnRendirse = new JButton("RENDIRSE");
        btnRendirse.setBackground(Color.RED);
        btnRendirse.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Rendido. Fin de partida.");
            cl.show(contenedor, "MENU");
        });

        panel.add(grids, BorderLayout.CENTER);
        panel.add(btnRendirse, BorderLayout.SOUTH);
        contenedor.add(panel, "JUEGO");
    }
    
    JPanel crearGrid(JButton[][] matriz, boolean esP1) {
        JPanel p = new JPanel(new GridLayout(8, 8));
        for (int contador=0;contador<8;contador++) {
            for (int contador2=0;contador2<8;contador2++) {
                matriz[contador][contador2] = new JButton("~");
                final int fila=contador, columna=contador2;
                matriz[contador][contador2].addActionListener(e -> {
                    if (!enJuego) {
                        // Colocación
                        if (esP1 && p1Colocados < Battleship.dificultadActual) {
                            if (Battleship.colocarBarco(fila, columna, p1Colocados, true)) {
                                p1Colocados++;
                            }
                        } else if (!esP1 && p2Colocados < Battleship.dificultadActual) {
                            if (Battleship.colocarBarco(fila, columna, p2Colocados, false)) {
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
                            String res = Battleship.bombardear(fila, columna);
                            if (res.equals("WIN")) {
                                JOptionPane.showMessageDialog(this, "¡GANASTE!");
                                cl.show(contenedor, "MENU");
                            } else {
                                JOptionPane.showMessageDialog(this, res);
                            }
                        }
                    }
                    actualizarTablero();
                });
                p.add(matriz[contador][contador2]);
            }
        }
        return p;
    }

    void actualizarTablero() {
        if (botones1[0][0] == null || Battleship.tabP1 == null) return;
        for (int contador=0;contador<8;contador++) {
            for (int contador2=0;contador2< 8;contador2++) {
                botones1[contador][contador2].setText(String.valueOf(Battleship.tabP1[contador][contador2]));
                char c2 = Battleship.tabP2[contador][contador2];
                // Modo Arcade oculta barcos enemigos
                if ("ARCADE".equals(Battleship.modoJuego) && c2 != 'X' && c2 != 'F') {
                    botones2[contador][contador2].setText("~");
                } else {
                    botones2[contador][contador2].setText(String.valueOf(c2));
                }
            }
        }
    }

    
    public static void main(String[] args){
        new Main();
    }
    
}
