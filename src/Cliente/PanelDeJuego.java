package Cliente;
import Shared.EstadoJuego; //importacion para ventana, colores, teclado y red
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Definicion de panel 
// Jpanel es donde se va a dibujar
// Runnable es la capacidad de hacer cosas al mismo tiempo (como un reloj) 
public class PanelDeJuego extends JPanel implements Runnable{

    TableroDeJuego Tablero = new TableroDeJuego(); // los cuadritos
    Pieza actual = Pieza.piezaRandom(); //pieza que cae 
    boolean GameOver = false; //
    int puntuacion = 0; //puntos acumulados
    int puntuacionRival = 0; // guardar los puntos que recibimos del oponente
    ConexionCliente red; // Conexion con el servidor
    

    // el constructor se ejecuta una vez al iniciar

    public PanelDeJuego(ConexionCliente red){
       this.red = red; // Guardamos la conexión que recibimos
        
        // IMPORTANTE: Le decimos a la conexión que este es su panel actual
        // para que cuando lleguen puntos del rival, sepa a quién avisar.
        if (this.red != null) {
            this.red.setPanel(this);
        }
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e){
                 // si se presiona izquierda y hay espacio la pieza se mueve
                if (e.getKeyCode() == KeyEvent.VK_LEFT && Tablero.puedemover(actual,-1,0))
                    actual.x--;
                //si se presiona derecha y hay espacio la pieza se mueve
                if (e.getKeyCode() == KeyEvent.VK_RIGHT  && Tablero.puedemover(actual,1,0))
                    actual.x++;
                // si se presiona abajo y hay espacio la pieza baja rapido
                if (e.getKeyCode() == KeyEvent.VK_DOWN  && Tablero.puedemover(actual,0,1))
                    actual.y++;

               if (e.getKeyCode() == KeyEvent.VK_UP) {
                   // guardar por si la rotacion falla
                   int[][] formaAnterior = actual.forma;
    
                         // rotar pieza
                         actual.rotar();
    
                        // ver si la posicion es valida
                         if (!Tablero.puedemover(actual, 0, 0)) {
                        // si choca volver a la anterior
                        actual.forma = formaAnterior;
                         }
    
                             repaint(); // actualizar para ver el giro
                                                }

                repaint(); // borra y vuelve a dibujar para ver el movimiento
            }
        });

        setFocusable(true); // Dice que este panel debe recibir los clicks del teclado
        new Thread(this).start(); //arranca el tiempo

    }
    
    //Método para que ConexionCliente nos dé los puntos del rival
    public void actualizarPuntajeRival(int puntos) {
        this.puntuacionRival = puntos;
        this.repaint(); // actualiza la pantalla para ver el cambio
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
                if(red !=null){
                    try{
                        red.enviar(new EstadoJuego(puntuacion, false));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                actual = Pieza.piezaRandom(); //lanza una pieza nueva desde arriba

                try {
                    red.enviar(new EstadoJuego(puntuacion, false));
                } catch(Exception e) {}

                //si la nueva pieza no cabe, se acaba el juego

                if (!Tablero.puedemover(actual,0,0))
                    GameOver = true;
            }
            repaint(); //actualiza la pantalla
        }
        try {
                    red.enviar(new EstadoJuego(puntuacion, true));
                } catch(Exception e) {}
       
    }
    protected void paintComponent(Graphics g){

        super.paintComponent(g); // limpia el lienzo

        // pintar fondo de panel
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());

    // tablero, dibujar piezas filas
    for (int r = 0; r < TableroDeJuego.FILAS; r++) {
        for (int c = 0; c < TableroDeJuego.COLUMNAS; c++) {
            if (Tablero.Matriz[r][c] == 1) {
                dibujarCuadrito(g, c * 30, r * 30, Color.WHITE);
            }
        }
    }

    // dibujar pieza cayendo
    if (actual != null) {
        for (int r = 0; r < actual.forma.length; r++) {
            for (int c = 0; c < actual.forma[r].length; c++) {
                if (actual.forma[r][c] == 1) {
                    // Calculamos la posición real en píxeles
                    int x = (actual.x + c) * 30;
                    int y = (actual.y + r) * 30;
                    dibujarCuadrito(g, x, y, Color.CYAN);
                }
            }
        }
    }
           
          // marcador de puntos
        g.setFont(new Font("Arial", Font.BOLD, 18));
        
        g.setColor(Color.WHITE);
        g.drawString("TÚ: " + puntuacion, 10, 25);
        
        g.setColor(Color.YELLOW);
        g.drawString("RIVAL: " + puntuacionRival, 180, 25);

    //GameOver

       if (GameOver) {
    // oscurecer la pantalla 
    g.setColor(new Color(0, 0, 0, 150)); // El 150 es la transparencia
    g.fillRect(0, 0, getWidth(), getHeight());

    // mensaje de Fin de Juego
    g.setColor(Color.RED);
    g.setFont(new Font("Monospaced", Font.BOLD, 40));
    g.drawString("GAME OVER", 50, getHeight() / 2);
    
    g.setFont(new Font("Arial", Font.PLAIN, 15));
    g.setColor(Color.WHITE);
    g.drawString("Presiona R para reiniciar", 80, (getHeight() / 2) + 40);
}

}

// Método auxiliar para que los cuadritos se vean con borde
private void dibujarCuadrito(Graphics g, int x, int y, Color color) {
    g.setColor(color);
    g.fillRect(x, y, 30, 30);
    g.setColor(color.darker()); // Borde más oscuro para que se note el bloque
    g.drawRect(x, y, 30, 30);
                }
            }
