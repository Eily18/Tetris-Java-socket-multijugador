package Servidor;
import java.io.*;
import java.net.*;
import Shared.EstadoJuego; // datos serializable
import java.util.ArrayList; // herramienta para la lista de oponentes

public class ManejadorJugador extends Thread{ //la clase es un hilo y hace que el servidor sea multitareas
    ObjectInputStream entrada;
    ObjectOutputStream salida; // para hablarle al cliente
    Socket socket;

    //lista para que todos los hilos compartan a los jugadores conectados
     public static ArrayList<ManejadorJugador> jugadores = new ArrayList<>();

    //el constructor que recibe el Socket del jugador que acaba de llegar
    public ManejadorJugador(Socket s) throws IOException {
        this.socket = s;
        // se prepara la comunicacion
        this.salida = new ObjectOutputStream(s.getOutputStream());
        entrada = new ObjectInputStream(s.getInputStream());
        jugadores.add(this); // se agregan los jugadores a la lista
    }

    // el metodo run() es lo que el hilo hace cuando se le da a "start()"
    @Override
    public void run() {
        try {
            while(true){
            // el servidor espera a que llegue un nuevo paquete
                EstadoJuego ej = (EstadoJuego) entrada.readObject();
                // reenvio de puntaje a los jugadores
                for (ManejadorJugador oponente : jugadores) {
                    if (oponente != this) { // No nos lo mandamos a nosotros mismos
                        oponente.enviarAOponente(ej);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Conexión perdida.");
        } finally {
            jugadores.remove(this); // se quitan los jugadores de la lista si se retiran
        }
    }

    public void enviarAOponente(EstadoJuego ej) throws IOException {
        salida.writeObject(ej);
        salida.flush(); // asegura que el paquete salga
    }
}
