package Cliente;
import java.io.*;   // Herramientas para enviar y recibir datos
import java.net.*;  // Herramientas para la red
import Shared.EstadoJuego; // El paquete de información que se va a enviar


public class ConexionCliente {
    // para que los objetos vayan al servidor
    ObjectOutputStream salida;
    ObjectInputStream entrada;
    PanelDeJuego miPanel;

    // el constructor que va a hacer la llamada inicial
    public ConexionCliente(String ipServidor) throws Exception {
        // se crea el Socket usando la ip
        Socket s = new Socket(ipServidor, 5000);

        // se conecta al socket
        salida = new ObjectOutputStream(s.getOutputStream());
        this.salida.flush();
        entrada = new ObjectInputStream(s.getInputStream());
         // hilo de escucha
         iniciarEscucha();
    }

    public void setPanel(PanelDeJuego panel){
        this.miPanel = panel;
    }
    private void iniciarEscucha() {
        new Thread(() -> {
            try {
                while (true) {
                    // espera a que el servidor mande el puntaje del oponente
                    EstadoJuego ejOponente = (EstadoJuego) entrada.readObject();

                    if(miPanel != null){
                    
                    // pantalla actualizada
                    System.out.println("El oponente ahora tiene: " + ejOponente.puntuacion);
                    miPanel.actualizarPuntajeRival(ejOponente.puntuacion);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al recibir datos del oponente.");
            }
        }).start();
    }

    // funcion para enviar el paquete de datos
    public void enviar(EstadoJuego ej) throws IOException {
        // escribe la puntuacion y el estado
        salida.writeObject(ej);
        
        //asegura la salida de los datos 
        salida.flush();
    }
}
