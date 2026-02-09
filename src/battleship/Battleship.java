/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.*;

public class Battleship {
    public static ArrayList<Player> players=new ArrayList<>();
    public static Player userActual, jugador2;
    
    public static char[][] tabP1, tabP2;
    public static int[][] vidasP1, vidasP2;
    
    public static int dificultadActual=4; //Set en modo normal de forma predeterminada
    public static String modoJuego="TUTORIAL";
    public static boolean turnoJugador1=true;
    
    public static final String[] codigosBarcos={"PA", "AZ", "SM", "DT"};
    public static final int[] resistencia={5, 4, 3, 2};
    
    
    //--- Apartado de Configuración de Dificultad ---
    
    public static void setDificultad(String dif){
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
        for (int i = 0; i < 8; i++) {
            Arrays.fill(tabP1[i], '~');
            Arrays.fill(tabP2[i], '~');
        }
        turnoJugador1 = true;
    }
    
    
    public static boolean colocarBarco(int f, int c, int correlativo, boolean esP1) {
        char[][] tablero = esP1 ? tabP1 : tabP2;
        int[][] vidas = esP1 ? vidasP1 : vidasP2;
        
        // El correlativo nos ayuda a saber qué tipo de barco toca poner
        if (tablero[f][c] == '~') {
            int tipo = correlativo % 4;
            tablero[f][c] = codigosBarcos[tipo].charAt(0);
            vidas[f][c] = resistencia[tipo];
            return true;
        }
        return false;
    }
    
    
    public static String bombardear(int f, int c) {
        char[][] objetivo = turnoJugador1 ? tabP2 : tabP1;
        int[][] vObj = turnoJugador1 ? vidasP2 : vidasP1;

        if (objetivo[f][c] == '~') {
            objetivo[f][c] = 'F';
            turnoJugador1 = !turnoJugador1;
            return "¡Agua!";
        } else if (objetivo[f][c] != 'X' && objetivo[f][c] != 'F') {
            vObj[f][c]--;
            if (vObj[f][c] <= 0) {
                objetivo[f][c] = 'X';
                regenerar(objetivo, vObj);
                if (esVictoria(objetivo)) {
                    return "WIN"; // El Main manejará quién ganó
                }
                turnoJugador1 = !turnoJugador1;
                return "¡HUNDIDO!";
            }
            regenerar(objetivo, vObj);
            turnoJugador1 = !turnoJugador1;
            return "¡Impacto! El barco se movió.";
        }
        return "Ya disparaste aquí.";
    }
    
    
    private static void regenerar(char[][] tab, int[][] vid) {
        List<Object[]> barcos = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tab[i][j] != '~' && tab[i][j] != 'X' && tab[i][j] != 'F') {
                    barcos.add(new Object[]{tab[i][j], vid[i][j]});
                    tab[i][j] = '~';
                    vid[i][j] = 0;
                }
            }
        }
        Random r = new Random();
        for (Object[] b : barcos) {
            int nf, nc;
            do {
                nf = r.nextInt(8);
                nc = r.nextInt(8);
            } while (tab[nf][nc] != '~');
            tab[nf][nc] = (char) b[0];
            vid[nf][nc] = (int) b[1];
        }
    }
    
    
    public static boolean esVictoria(char[][] t) {
        for (char[] fila : t) {
            for (char c : fila) {
                if (c != '~' && c != 'X' && c != 'F') {
                    return false;
                }
            }
        }
        return true;
    }
    
    
    //--- Apartado de Jugador y Usuarios ---
    
    
    public static Player obtenerPlayer(String usuario, String contra) {
        for (Player p : players) {
            if (p.getUsername().equals(usuario) && p.getPassword().equals(contra)) {
                return p;
            }
        }
        return null;
    }
    
    public static Player obtenerPlayerPorNombre(String usuario) {
        for (Player p : players) {
            if (p.getUsername().equals(usuario)) {
                return p;
            }
        }
        return null;
    }
    
    //Crear Jugador
    public static boolean crearPlayer(String usuario, String contra){
        for(int contador=0;contador<players.size();contador++){
            Player playerActual=players.get(contador);
            if(playerActual.getUsername().equals(usuario)){
                return false;
            }
        }
        players.add(new Player(usuario, contra));
        return true;
    }
    
    public static void eliminarCuenta(Player p) {
        players.remove(p);
    }
      
}
