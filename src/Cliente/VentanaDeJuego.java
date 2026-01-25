package Cliente;
import javax.swing.*; //importar la herramienta para dibujar ventanas y botones.

public class VentanaDeJuego extends JFrame{ //Definicion de la "VentanaDeJuego" con JFrame
    public VentanaDeJuego(){ //constructor para crear la ventana
        setTitle("Tetris"); // titulo en la parte arriba de la ventana
        setSize(300, 600); //Definicion de tamaño, ancho y alto
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Si se cierra la ventana, el programa se detiene por completo
        add(new PanelDeJuego()); // se añade el tablero(panelDeJuego) dentro de la ventana
        setVisible(true); //se hace visible la ventana para el cliente
    }

    
}
