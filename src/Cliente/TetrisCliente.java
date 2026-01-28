package Cliente; //ubicacion del archivo

import javax.swing.JOptionPane;

// este es el archivo que inicia todo
public class TetrisCliente {
    public static void main(String[] args){

        //Solicitar la IP al usuario antes de iniciar nada
        String ip = JOptionPane.showInputDialog(null, 
            "Introduce la IP del Servidor (usa 'localhost' para pruebas locales):", 
            "Conexión al Tetris Multijugador", 
            JOptionPane.QUESTION_MESSAGE);

        // Si el usuario cancela o deja vacío, cerramos el programa
        if (ip == null || ip.isEmpty()) {
            System.exit(0);
        }

        try {
            // intentar establecer la conexión de red ANTES de abrir el juego
            // Esto valida si la IP es correcta y si el servidor está encendido
            ConexionCliente red = new ConexionCliente(ip); 

            // ahora la ventana y el panel podrán usar 'red' para enviar/recibir puntos
            new VentanaDeJuego(red); 

        } catch (Exception e) {
            // si la IP es falsa o no hay conexión, se muestra este error
            // el juego NO se abrirá, lo cual es lo correcto
            JOptionPane.showMessageDialog(null, 
                "No se pudo conectar al servidor: " + e.getMessage(), 
                "Error de Red", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}