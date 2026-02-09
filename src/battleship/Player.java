/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

/**
 *
 * @author Fabio Sierra
 */
public class Player {
    private String username;
    private String password;
    private int puntaje;
    private String[] registros=new String[10];
    
    public Player(String usuario, String contra){
        this.username=usuario;
        this.password=contra;
        this.puntaje=0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String usuario) {
        this.username = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String contra) {
        this.password = contra;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void addPuntaje(int puntos) {
        puntaje=puntaje+puntos;
    }
    
    //Guardar historial de partidas
    public void addRegistro(String reporte) {
        for (int i = 9; i > 0; i--) {
            registros[i] = registros[i - 1];
        }
        registros[0] = reporte;
    }

    public String[] getRegistros() {
        return registros;
    }
    
    public int getPartidasJugadas() {
        int count = 0;
        for (String r : registros) {
            if (r != null) {
                count++;
            }
        }
        return count;
    }
    
    
}
