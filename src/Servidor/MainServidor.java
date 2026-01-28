package Servidor; // carpeta donde esta el prgrama que controla todo
import java.net.*;
import java.util.ArrayList;

public class MainServidor {

    public static ArrayList <ManejadorJugador>
    jugadores = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // recibe conexiones
        ServerSocket ServidorSocket = new ServerSocket(5000);
        System.out.println("Servidor iniciado y esperando jugadores...");

        // da un servidor de respaldo por si hay una falla
        new ServidorDeRespaldo().start();

        // bucle
        while (true) {
            Socket socketCliente = ServidorSocket.accept();
            ManejadorJugador mj= new ManejadorJugador(socketCliente);
            jugadores.add(mj);
            mj.start();
            System.out.println("Nuevo jugador conectado. Total: " + jugadores.size());
            
            /* espera a que un jugador se conecte
            cuando alguien entra se crea un manejador especifico y lo inicia */
        }
    }
}
