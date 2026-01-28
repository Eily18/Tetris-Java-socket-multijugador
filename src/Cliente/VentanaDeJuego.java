package Cliente;
import java.awt.Color; //importar la herramienta para dibujar ventanas y botones.
import javax.swing.*;

public class VentanaDeJuego extends JFrame{ //Definicion de la "VentanaDeJuego" con JFrame
    public VentanaDeJuego(ConexionCliente red){ //constructor para crear la ventana
        setTitle("TETRIS"); // titulo en la parte arriba de la ventana
        setSize(500, 650); //Definicion de tamaño, ancho y alto!!!!!
        setLocationRelativeTo(null); // ESTO CENTRA LA VENTANA EN LA PANTALLA AL ABRIR!!!
        setResizable(false); // Evita que el usuario estire la ventana y rompa el diseño!!!!!
        getContentPane().setBackground(Color.DARK_GRAY); // Color de fondo del marco!!!!!
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Si se cierra la ventana, el programa se detiene por completo
        add(new PanelDeJuego(red)); // se añade el tablero(panelDeJuego) dentro de la ventana
        setVisible(true); //se hace visible la ventana para el cliente
    }

    
}
