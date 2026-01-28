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
            // espera a que se llenen los datos de EstadoJuego
            EstadoJuego ej = (EstadoJuego) entrada.readObject();
            
            // imprime el puntaje en la consola del servidor
            System.out.println("Puntaje recibido de un jugador: " + ej.puntuacion);
            salida.writeObject(ej);
        } catch (Exception e) {
            // si el jugador se desconecta bruscamente, el error cae aqui
            System.out.println("Conexión perdida con un jugador.");
        }
    }
}
