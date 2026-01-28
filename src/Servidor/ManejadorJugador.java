package Servidor;
import java.io.*;
import java.net.*;
import Shared.EstadoJuego; // datos serializable

public class ManejadorJugador extends Thread{ //la clase es un hilo y hace que el servidor sea multitareas
    ObjectInputStream entrada;
    ObjectOutputStream salida;

    //el constructor que recibe el Socket del jugador que acaba de llegar
    public ManejadorJugador(Socket s) throws IOException {
        salida = new ObjectOutputStream(s.getOutputStream());
        this.salida.flush();
        // Prepara el tubo de entrada para escuchar al jugador
        entrada = new ObjectInputStream(s.getInputStream()); 
    }

    // el metodo run() es lo que el hilo hace cuando se le da a "start()"
    @Override
    public void run() {
        try {
             while (true) {
                // Escucha el puntaje que envía el Jugador A
                EstadoJuego ej = (EstadoJuego) entrada.readObject();
                
                System.out.println("Puntaje recibido: " + ej.puntuacion);

                // REENVÍO: Le avisamos a TODOS los demás jugadores
                // Para esto usamos la lista que debe estar en tu MainServidor
                for (ManejadorJugador otro : MainServidor.jugadores) {
                    if (otro != this) { // No te lo mandes a ti mismo
                        otro.salida.writeObject(ej);
                        otro.salida.flush(); 
                    }
                }
            }
        } catch (Exception e) {
            // si el jugador se desconecta bruscamente, el error cae aqui
            System.out.println("Conexión perdida con un jugador.");
        }
    }
}
