package Servidor; // carpeta donde esta el prgrama que controla todo
import java.net.*;

public class MainServidor {
    public static void main(String[] args) throws Exception {
        // recibe conexiones
        try (ServerSocket ServidorSocket = new ServerSocket(5000)) {
        System.out.println("Servidor iniciado y esperando jugadores...");

        // da un servidor de respaldo por si hay una falla
        new ServidorDeRespaldo().start();

        // bucle
        while (true) {
            
            /* espera a que un jugador se conecte
            cuando alguien entra se crea un manejador especifico y lo inicia */

            Socket Cliente = ServidorSocket.accept();
            System.out.println("¡Jugador conectado desde: " + Cliente.getInetAddress());
            // hilo para manejar al jugador
            new Thread(new ManejadorJugador(Cliente)).start();
            }
        }
    }
}
