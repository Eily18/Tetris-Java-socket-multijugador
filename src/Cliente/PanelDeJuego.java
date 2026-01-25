package Cliente;
import javax.swing.*; //importacion para ventana, colores, teclado y red
import java.awt.*;
import java.awt.event.*;
import Shared.EstadoJuego;

// Definicion de panel 
// Jpanel es donde se va a dibujar
// Runnable es la capacidad de hacer cosas al mismo tiempo (como un reloj) 
public class PanelDeJuego extends JPanel implements Runnable{

    TableroDeJuego Tablero = new TableroDeJuego(); // los cuadritos
    Pieza actual = Pieza.piezaRandom(); //pieza que cae 
    boolean GameOver = false; //
    int puntuacion = 0; //puntos acumulados
    ConexionCliente red; // Conexion con el servidor

    // el constructor se ejecuta una vez al iniciar

    public PanelDeJuego(){
        try { red = new ConexionCliente(); } //intenta conectar con el servidor
        catch(Exception e){}
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e){ // si se presiona izquierda y hay espacio la pieza se mueve
                if (e.getKeyCode() == KeyEvent.VK_LEFT && Tablero.puedemover(actual,-1,0))
                    actual.x--;
                //si se presiona derecha y hay espacio la pieza se mueve
                if (e.getKeyCode() == KeyEvent.VK_RIGHT  && Tablero.puedemover(actual,1,0))
                    actual.x++;
                // si se presiona abajo y hay espacio la pieza baja rapido
                if (e.getKeyCode() == KeyEvent.VK_DOWN  && Tablero.puedemover(actual,0,1))
                    actual.y++;

                repaint(); // borra y vuelve a dibujar para ver el movimiento
            }
        });

        setFocusable(true); // Dice que este panel debe recibir los clicks del teclado
        new Thread(this).start(); //arranca el tiempo

    }

    public void run(){ // bucle
        while(!GameOver){
            //mientras no haya perdido
            try{Thread.sleep(500); }
            catch(Exception e){} // pausa de medio segundo (gravedad)
            // si la pieza puede seguir bajando
            if (Tablero.puedemover(actual,0,1)){actual.y++;} //baja un nivel
            else {Tablero.fijar(actual); //deja la pieza fija
                puntuacion += Tablero.limpiarlineas() *100; //suma puntos si lleno filas
                actual = Pieza.piezaRandom(); //lanza una pieza nueva desde arriba

                //si la nueva pieza no cabe, se acaba el juego

                if (!Tablero.puedemover(actual,0,0))
                    GameOver = true;
            }
            repaint(); //actualiza la pantalla
        }
        // cuando el bucle termina
        try {
            red.enviar(new EstadoJuego(puntuacion, true));
        } 
        catch(Exception e){}
    }
    protected void paintComponent(Graphics g){

        super.paintComponent(g); // limpia el lienzo

        // recorre todas las filas (r) y columnas (c) del tablero
        for(int r=0; r< TableroDeJuego.filas; r++){
            for(int c=0; c< TableroDeJuego.columnas; c++) {
                // si en esa celda hay una "piedra" (un 1)
                if(Tablero.Matriz[r][c] == 1) { //dibuja un cuadrito 30x30
                    g.fillRect(c *30, r *30, 30, 30 );
                }
            }
        }
    }
    
}
