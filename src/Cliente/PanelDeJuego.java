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
    ConexionCliente red; // Conexion con el servidor

    // el constructor se ejecuta una vez al iniciar

    public PanelDeJuego(){
        try { red = new ConexionCliente(); } //intenta conectar con el servidor
        catch(Exception e){}
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
                   // 1. Guardamos la forma actual por si la rotación falla
                   int[][] formaAnterior = actual.forma;
    
                         // 2. Rotamos temporalmente
                         actual.rotar();
    
                        // 3. Verificamos si la nueva posición es válida
                         if (!Tablero.puedemover(actual, 0, 0)) {
                        // Si choca, volvemos a la forma anterior
                        actual.forma = formaAnterior;
                         }
    
                             repaint(); // Actualizamos el "front" para ver el giro
                                                }

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

        // 1. PINTAR EL FONDO DEL PANEL (Esto quitará el blanco)
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());

    // 2. DIBUJAR LAS PIEZAS FIJAS (El tablero)
    for (int r = 0; r < TableroDeJuego.FILAS; r++) {
        for (int c = 0; c < TableroDeJuego.COLUMNAS; c++) {
            if (Tablero.Matriz[r][c] == 1) {
                dibujarCuadrito(g, c * 30, r * 30, Color.WHITE);
            }
        }
    }

    // 3. DIBUJAR LA PIEZA QUE ESTÁ CAYENDO (Para que no sea invisible)
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
           
          // --- DIBUJAR MARCADOR DE PUNTOS ---
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 20)); // Fuente elegante y grande
    
    // Dibujamos el texto: "Puntos: " + el valor de la variable puntuacion
    // Las coordenadas (20, 30) lo ponen en la esquina superior izquierda
    g.drawString("PUNTOS: " + puntuacion, 20, 30);
    
    // Opcional: Si quieres que sea multijugador, puedes poner el tuyo a la izquierda
    // y el del oponente a la derecha.

    //MENSAJE DE QUE PERDISTE------------

       if (GameOver) { // Usando la variable que ya tienes en el código
    // 1. Oscurecer la pantalla (Capucha negra semitransparente)
    g.setColor(new Color(0, 0, 0, 150)); // El 150 es la transparencia
    g.fillRect(0, 0, getWidth(), getHeight());

    // 2. Mensaje de Fin de Juego
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
