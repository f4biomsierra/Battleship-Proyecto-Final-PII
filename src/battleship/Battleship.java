/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Battleship {

    public static ArrayList<Player> players = new ArrayList<>();
    public static Player userActual, jugador2;

    public static char[][] tabP1, tabP2;
    public static int[][] vidasP1, vidasP2;
    public static boolean[][] explosionesP1, explosionesP2;
    public static boolean[][] impactosP1, impactosP2; 

    public static int dificultadActual = 4; //Set en modo normal de forma predeterminada
    public static String modoJuego = "TUTORIAL";
    public static boolean turnoJugador1 = true;

    public static final String[] codigosBarcos = {"PA", "AZ", "SM", "DT"};
    public static final int[] resistencia = {5, 4, 3, 2};
    public static final String[] nombresBarcos = {"Portaaviones", "Acorazado", "Submarino", "Destructor"};

    
    private static ImageIcon imgPortaaviones = null;
    private static ImageIcon imgAcorazado = null;
    private static ImageIcon imgSubmarino = null;
    private static ImageIcon imgDestructor = null;
    private static ImageIcon imgFuego = null;
    private static ImageIcon imgFallo = null;
    private static ImageIcon imgAgua = null;
    private static ImageIcon imgExplosion = null;
    private static ImageIcon imgImpacto = null;

    //--- Apartado de Configuración de Dificultad ---
    public static void setDificultad(String dif) {
        switch (dif) {
            case "EASY":
                dificultadActual = 5;
                break;
            case "NORMAL":
                dificultadActual = 4;
                break;
            case "EXPERT":
                dificultadActual = 2;
                break;
            case "GENIUS":
                dificultadActual = 1;
                break;
        }
    }

    //--- Apartado de Partida ---
    public static void inicializarPartida() {
        tabP1 = new char[8][8];
        tabP2 = new char[8][8];
        vidasP1 = new int[8][8];
        vidasP2 = new int[8][8];
        explosionesP1 = new boolean[8][8];
        explosionesP2 = new boolean[8][8];
        impactosP1 = new boolean[8][8];
        impactosP2 = new boolean[8][8];
        for (int fila = 0; fila < 8; fila++) {
            Arrays.fill(tabP1[fila], '~');
            Arrays.fill(tabP2[fila], '~');
        }
        turnoJugador1 = true;
    }

    public static boolean colocarBarco(int fila, int columna, int correlativo, boolean esP1) {
        char[][] tablero = esP1 ? tabP1 : tabP2;
        int[][] vidas = esP1 ? vidasP1 : vidasP2;

        
        if (tablero[fila][columna] == '~') {
            int tipo = correlativo % 4;
            tablero[fila][columna] = codigosBarcos[tipo].charAt(0);
            vidas[fila][columna] = resistencia[tipo];
            return true;
        }
        return false;
    }

    public static String bombardear(int fila, int columna) {
        char[][] objetivo = turnoJugador1 ? tabP2 : tabP1;
        int[][] vObj = turnoJugador1 ? vidasP2 : vidasP1;
        boolean[][] explosiones = turnoJugador1 ? explosionesP2 : explosionesP1;
        boolean[][] impactos = turnoJugador1 ? impactosP2 : impactosP1;

        
        limpiarFallos(objetivo);
        limpiarImpactos(impactos);

        if (objetivo[fila][columna] == '~') {
            objetivo[fila][columna] = 'F';
            turnoJugador1 = !turnoJugador1;
            return "¡Agua!";
        } else if (objetivo[fila][columna] != 'F') {
            
            char tipoChar = objetivo[fila][columna];
            String nombreBarco = "";

            
            if (tipoChar == 'P') {
                nombreBarco = nombresBarcos[0];
            } else if (tipoChar == 'A') {
                nombreBarco = nombresBarcos[1];
            } else if (tipoChar == 'S') {
                nombreBarco = nombresBarcos[2];
            } else if (tipoChar == 'D') {
                nombreBarco = nombresBarcos[3];
            }

            vObj[fila][columna]--;

            if (vObj[fila][columna] <= 0) {
                objetivo[fila][columna] = '~'; 
                explosiones[fila][columna] = true; 
                regenerar(objetivo, vObj);
                if (esVictoria(objetivo)) {
                    return "WIN";
                }
                turnoJugador1 = !turnoJugador1;
                // RETORNO MODIFICADO:
                return "¡HUNDIDO! Has destruido un " + nombreBarco;
            }

            
            impactos[fila][columna] = true;
            regenerar(objetivo, vObj);
            turnoJugador1 = !turnoJugador1;
            return "¡Impacto! El barco se movió.";
        }
        return "Ya disparaste aquí.";
    }

    
    private static void limpiarFallos(char[][] tablero) {
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                if (tablero[fila][columna] == 'F') {
                    tablero[fila][columna] = '~'; // Volver a agua
                }
            }
        }
    }

    
    private static void limpiarImpactos(boolean[][] impactos) {
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                impactos[fila][columna] = false;
            }
        }
    }

    private static void regenerar(char[][] tab, int[][] vid) {
        
        boolean[][] impactos = (tab == tabP1) ? impactosP1 : impactosP2;

        java.util.List<Object[]> barcos = new ArrayList<>();
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                if (tab[fila][columna] != '~' && tab[fila][columna] != 'X' && tab[fila][columna] != 'F') {
                    barcos.add(new Object[]{tab[fila][columna], vid[fila][columna]});
                    tab[fila][columna] = '~';
                    vid[fila][columna] = 0;
                    impactos[fila][columna] = false; // Limpiar marca de impacto de la celda antigua
                }
            }
        }
        Random random = new Random();
        for (Object[] barco : barcos) {
            int nuevaFila, nuevaColumna;
            do {
                nuevaFila = random.nextInt(8);
                nuevaColumna = random.nextInt(8);
            } while (tab[nuevaFila][nuevaColumna] != '~');
            tab[nuevaFila][nuevaColumna] = (char) barco[0];
            vid[nuevaFila][nuevaColumna] = (int) barco[1];
        }
    }

    public static boolean esVictoria(char[][] tablero) {
        for (char[] fila : tablero) {
            for (char celda : fila) {
                if (celda != '~' && celda != 'X' && celda != 'F') {
                    return false;
                }
            }
        }
        return true;
    }

    //--- Apartado de Jugador y Usuarios ---
    public static Player obtenerPlayer(String usuario, String contra) {
        for (Player jugador : players) {
            if (jugador.getUsername().equals(usuario) && jugador.getPassword().equals(contra)) {
                return jugador;
            }
        }
        return null;
    }

    public static Player obtenerPlayerPorNombre(String usuario) {
        for (Player jugador : players) {
            if (jugador.getUsername().equals(usuario)) {
                return jugador;
            }
        }
        return null;
    }

    //Crear Jugador
    public static boolean crearPlayer(String usuario, String contra) {
        for (int contador = 0; contador < players.size(); contador++) {
            Player playerActual = players.get(contador);
            if (playerActual.getUsername().equals(usuario)) {
                return false;
            }
        }
        players.add(new Player(usuario, contra));
        return true;
    }

    public static void eliminarCuenta(Player jugador) {
        players.remove(jugador);
    }

    //--- Apartado de Interfaz Gráfica ---
    public static JPanel crearPantallaJuego(Main ventana, JButton[][] botones1, JButton[][] botones2,
            JLabel lblEstadoTurno, CardLayout cl, JPanel contenedor) {
        JPanel panel = new JPanel(new BorderLayout());

        lblEstadoTurno.setFont(new Font("Arial", Font.BOLD, 18));
        lblEstadoTurno.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel grids = new JPanel(new GridLayout(1, 2, 40, 0));
        grids.add(crearGrid(ventana, botones1, true, lblEstadoTurno, cl, contenedor));
        grids.add(crearGrid(ventana, botones2, false, lblEstadoTurno, cl, contenedor));

        JButton btnRendirse = new JButton("RENDIRSE");
        btnRendirse.setBackground(Color.RED);
        btnRendirse.setForeground(Color.WHITE);

        btnRendirse.addActionListener(evento -> {
            if (ventana.enJuego) {
                Player ganador = turnoJugador1 ? jugador2 : userActual;
                Player perdedor = turnoJugador1 ? userActual : jugador2;

                ganador.addPuntaje(3);
                ganador.addRegistro(ganador.getUsername() + " triunfó ante " + perdedor.getUsername());
                perdedor.addRegistro(perdedor.getUsername() + " se rindió ante " + ganador.getUsername());

                JOptionPane.showMessageDialog(ventana, "¡" + perdedor.getUsername() + " se rindió!\nGanador: " + ganador.getUsername());
            }
            ventana.enJuego = false;
            cl.show(contenedor, "MENU");
        });

        panel.add(lblEstadoTurno, BorderLayout.NORTH);
        panel.add(grids, BorderLayout.CENTER);
        panel.add(btnRendirse, BorderLayout.SOUTH);

        return panel;
    }

    public static JPanel crearGrid(Main ventana, JButton[][] matriz, boolean esP1,
            JLabel lblEstadoTurno, CardLayout cl, JPanel contenedor) {
        JPanel panel = new JPanel(new GridLayout(8, 8, 0, 0)); 

        
        ImageIcon fondoBoton = Main.cargarImagenBarcos("agua.png", 110, 40);

        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                matriz[fila][columna] = new JButton();
                matriz[fila][columna].setPreferredSize(new Dimension(60, 60));
                matriz[fila][columna].setMinimumSize(new Dimension(60, 60));
                matriz[fila][columna].setMaximumSize(new Dimension(60, 60));
                matriz[fila][columna].setFocusPainted(false);
                matriz[fila][columna].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1)); 
                matriz[fila][columna].setContentAreaFilled(false);
                matriz[fila][columna].setMargin(new Insets(0, 0, 0, 0));

                
                matriz[fila][columna].setVerticalAlignment(SwingConstants.BOTTOM);
                matriz[fila][columna].setHorizontalAlignment(SwingConstants.CENTER);

                
                if (fondoBoton != null) {
                    matriz[fila][columna].setIcon(fondoBoton);
                } else {
                    matriz[fila][columna].setText("~");
                }

                final int filaFinal = fila, columnaFinal = columna;

                matriz[fila][columna].addActionListener(evento -> {
                    if (!ventana.enJuego) {
                        // Fase de colocación
                        if (esP1) {
                            if (ventana.p1Colocados < dificultadActual) {
                                if (colocarBarco(filaFinal, columnaFinal, ventana.p1Colocados, true)) {
                                    ventana.p1Colocados++;
                                }
                            } else {
                                JOptionPane.showMessageDialog(ventana, "Ya colocaste tus barcos. ¡Toca el turno del Jugador 2!");
                            }
                        } else {
                            if (ventana.p1Colocados < dificultadActual) {
                                JOptionPane.showMessageDialog(ventana, "Primero debe colocar sus barcos el Jugador 1");
                            } else if (ventana.p2Colocados < dificultadActual) {
                                if (colocarBarco(filaFinal, columnaFinal, ventana.p2Colocados, false)) {
                                    ventana.p2Colocados++;
                                }
                            }
                        }

                        if (ventana.p1Colocados == dificultadActual && ventana.p2Colocados == dificultadActual) {
                            ventana.enJuego = true;
                            
                            actualizarTablero(ventana, ventana.botones1, lblEstadoTurno);
                            actualizarTablero(ventana, ventana.botones2, lblEstadoTurno);
                            JOptionPane.showMessageDialog(ventana, "¡Batalla!");
                        }
                    } else {
                        // Fase de ataque
                        if ((turnoJugador1 && !esP1) || (!turnoJugador1 && esP1)) {
                            String resultado = bombardear(filaFinal, columnaFinal);
                            if (resultado.equals("WIN")) {
                                Player pGanador = turnoJugador1 ? userActual : jugador2;
                                Player perdedor = turnoJugador1 ? jugador2 : userActual;

                                pGanador.addPuntaje(3);
                                pGanador.addRegistro("Victoria de " + pGanador.getUsername() + " contra " + perdedor.getUsername());
                                perdedor.addRegistro("Victoria de " + pGanador.getUsername() + " contra " + perdedor.getUsername());

                                JOptionPane.showMessageDialog(ventana, "¡EL GANADOR ES: " + pGanador.getUsername() + "!");
                                ventana.enJuego = false;
                                cl.show(contenedor, "MENU");
                            } else {
                                
                                actualizarTablero(ventana, ventana.botones1, lblEstadoTurno);
                                actualizarTablero(ventana, ventana.botones2, lblEstadoTurno);
                                JOptionPane.showMessageDialog(ventana, resultado);
                            }
                        }
                    }
                    
                    actualizarTablero(ventana, ventana.botones1, lblEstadoTurno);
                    actualizarTablero(ventana, ventana.botones2, lblEstadoTurno);
                });
                panel.add(matriz[fila][columna]);
            }
        }
        return panel;
    }

    public static void actualizarTablero(Main ventana, JButton[][] botones, JLabel lblEstadoTurno) {
        if (botones[0][0] == null || tabP1 == null) {
            return;
        }

        
        if (!ventana.enJuego) {
            if (ventana.p1Colocados < dificultadActual) {
                String barcoActual = nombresBarcos[ventana.p1Colocados % 4];
                lblEstadoTurno.setText(userActual.getUsername() + " - Coloca tu: " + barcoActual);
                lblEstadoTurno.setForeground(Color.BLUE);
            } else {
                String barcoActual = nombresBarcos[ventana.p2Colocados % 4];
                lblEstadoTurno.setText(jugador2.getUsername() + " - Coloca tu: " + barcoActual);
                lblEstadoTurno.setForeground(Color.MAGENTA);
            }
        } else {
            if (turnoJugador1) {
                lblEstadoTurno.setText("TURNO DE: " + userActual.getUsername());
                lblEstadoTurno.setForeground(Color.BLUE);
            } else {
                lblEstadoTurno.setText("TURNO DE: " + jugador2.getUsername());
                lblEstadoTurno.setForeground(Color.RED);
            }
        }

        // Actualizar botones del tablero
        boolean esTableroP1 = (botones == ventana.botones1);
        boolean[][] explosiones = esTableroP1 ? explosionesP1 : explosionesP2;
        boolean[][] impactos = esTableroP1 ? impactosP1 : impactosP2;

        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                if (esTableroP1) {
                    // Actualizar Tablero Jugador 1
                    char celda = tabP1[fila][columna];
                    botones[fila][columna].setText("");

                    
                    if (impactos[fila][columna]) {
                        botones[fila][columna].setIcon(obtenerIconoBarco('I'));
                        botones[fila][columna].setEnabled(true);
                    } 
                    else if ("ARCADE".equals(modoJuego) && ventana.enJuego && celda != 'X' && celda != 'F' && !explosiones[fila][columna]) {
                        botones[fila][columna].setIcon(obtenerIconoBarco('~'));
                        botones[fila][columna].setEnabled(true);
                    } 
                    else if (explosiones[fila][columna]) {
                        botones[fila][columna].setIcon(obtenerIconoBarco('~')); 
                        botones[fila][columna].setEnabled(true); 
                    } else {
                        botones[fila][columna].setIcon(obtenerIconoBarco(celda));
                        
                        botones[fila][columna].setEnabled(true);
                    }
                } else {
                    // Actualizar Tablero Jugador 2
                    char celda = tabP2[fila][columna];

                    
                    if (impactos[fila][columna]) {
                        botones[fila][columna].setIcon(obtenerIconoBarco('I'));
                        botones[fila][columna].setText("");
                        botones[fila][columna].setEnabled(true);
                    } 
                    else if ("ARCADE".equals(modoJuego) && ventana.enJuego && celda != 'X' && celda != 'F' && !explosiones[fila][columna]) {
                        
                        botones[fila][columna].setIcon(obtenerIconoBarco('~'));
                        botones[fila][columna].setText("");
                        botones[fila][columna].setEnabled(true);
                    } else {
                        
                        botones[fila][columna].setText("");

                        
                        if (explosiones[fila][columna]) {
                            botones[fila][columna].setIcon(obtenerIconoBarco('~')); 
                            botones[fila][columna].setEnabled(true); 
                        } else {
                            botones[fila][columna].setIcon(obtenerIconoBarco(celda));
                            
                            botones[fila][columna].setEnabled(true);
                        }
                    }
                }
            }
        }
    }

    private static ImageIcon obtenerIconoBarco(char codigo) {
        int tamanoCelda = 60;

        
        switch (codigo) {
            case 'P':
                if (imgPortaaviones == null) {
                    imgPortaaviones = Main.cargarImagenBarcos("portaaviones.png", 110, 45);
                }
                return imgPortaaviones;
            case 'A':
                if (imgAcorazado == null) {
                    imgAcorazado = Main.cargarImagenBarcos("acorazado.png", 110, 45);
                }
                return imgAcorazado;
            case 'S':
                if (imgSubmarino == null) {
                    imgSubmarino = Main.cargarImagenBarcos("submarino.png", 110, 45);
                }
                return imgSubmarino;
            case 'D':
                if (imgDestructor == null) {
                    imgDestructor = Main.cargarImagenBarcos("destructor.png", 110, 45);
                }
                return imgDestructor;
            case 'X': // Barco hundido - mostrar explosión
                if (imgExplosion == null) {
                    imgExplosion = Main.cargarImagenBarcos("explosion.gif", 110, 45);
                }
                return imgExplosion;
            case 'I': // Impacto (barco golpeado pero no hundido)
                if (imgImpacto == null) {
                    imgImpacto = Main.cargarImagenBarcos("impacto.png", 110, 45);
                    if (imgImpacto == null) {
                        
                        imgImpacto = Main.cargarImagenBarcos("fuego.gif", 110, 45);
                    }
                }
                return imgImpacto;
            case 'F':
                if (imgFallo == null) {
                    
                    imgFallo = Main.cargarImagenBarcos("fallo.png", 110, 45);
                    if (imgFallo == null) {
                        
                        imgFallo = Main.cargarImagenBarcos("agua.png", 110, 40);
                    }
                }
                return imgFallo;
            case '~':
            default:
                if (imgAgua == null) {
                    imgAgua = Main.cargarImagenBarcos("agua.png", 110, 40);
                }
                return imgAgua;
        }
    }

}