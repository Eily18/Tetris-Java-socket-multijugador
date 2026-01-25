package Cliente;
import java.io.*;   // Herramientas para enviar y recibir datos
import java.net.*;  // Herramientas para la red
import Shared.EstadoJuego; // El paquete de información que se va a enviar

public class ConexionCliente {
    // para que los objetos vayan al servidor
    ObjectOutputStream salida;

    // el constructor que va a hacer la llamada inicial
    public ConexionCliente() throws Exception {
        // se crea el Socket
        Socket s = new Socket("localhost", 5000);

        // se conecta al socket
        salida = new ObjectOutputStream(s.getOutputStream());
    }

    // funcion para enviar el paquete de datos
    public void enviar(EstadoJuego ej) throws IOException {
        // escribe la puntuacion y el estado
        salida.writeObject(ej);
        
        //asegura la salida de los datos 
        salida.flush();
    }
}
