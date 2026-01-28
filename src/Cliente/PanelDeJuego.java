package Cliente;
import Shared.EstadoJuego; //importacion para ventana, colores, teclado y red
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// definicion de panel 
// Jpanel es donde se va a dibujar
// Runnable es la capacidad de hacer cosas al mismo tiempo (como un reloj) 
public class PanelDeJuego extends JPanel implements Runnable{

    TableroDeJuego Tablero = new TableroDeJuego(); // los cuadritos
    Pieza actual= Pieza.piezaRandom() ;//= Pieza.piezaRandom(); //pieza que cae 
    Pieza siguiente = Pieza.piezaRandom() ;
    boolean GameOver = false; //
    boolean EnMenu = true;
    int puntuacion = 0; //puntos acumulados
    int puntuacionRival = 0; // guardar los puntos que recibimos del oponente
    ConexionCliente red; // Conexion con el servidor
    

    // el constructor se ejecuta una vez al iniciar

    public PanelDeJuego(ConexionCliente red){
       this.red = red; // Guardamos la conexión que recibimos

       /* se ponen las piezas en null al inicio para que el paintcomponent
        muestre el esperando contrincante*/
        this.actual = null;
        this.siguiente = null;
        // IMPORTANTE: Le decimos a la conexión que este es su panel actual
        // para que cuando lleguen puntos del rival, sepa a quién avisar.
        if (this.red != null) {
            this.red.setPanel(this);
        }
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e){
                //presiona s para jugar solo
                if (e.getKeyCode() == KeyEvent.VK_S && actual == null){
                    EmpezarJuego();
                }
                if(actual != null){
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
    
                             
                    }
                 }

                repaint(); // borra y vuelve a dibujar para ver el movimiento
            }
        });

        setFocusable(true); // Dice que este panel debe recibir los clicks del teclado
        this.requestFocusInWindow();

    }
    
    //Método para que ConexionCliente nos dé los puntos del rival
    public void actualizarPuntajeRival(EstadoJuego ej) {
        this.puntuacionRival = ej.puntuacion;
        if(ej.isGameOver){ //si el oponente perdio, el juego se detiene
            this.GameOver = true;
        }
        this.repaint(); // actualiza la pantalla para ver el cambio
    }

    //metodo para empezar el juego
    public void EmpezarJuego(){
        if(actual == null){
            actual = Pieza.piezaRandom();
            siguiente = Pieza.piezaRandom();
            new Thread(this).start();
        }
    }

    public void run(){ // bucle
        while(!GameOver){
            //mientras no haya perdido
            try{Thread.sleep(500); }
            catch(Exception e){} // pausa de medio segundo (gravedad)
            // si la pieza puede seguir bajando
            if (Tablero.puedemover(actual,0,1)){actual.y++;} //baja un nivel
            else {
                Tablero.fijar(actual);
                puntuacion += Tablero.limpiarlineas() * 100;

                if (puntuacion >= 1000) {
                    GameOver = true;
                }

                actual = siguiente;
                siguiente = Pieza.piezaRandom();

                // notificar al servidor el estado actual
                if (red != null) {
                    try {
                        red.enviar(new EstadoJuego(puntuacion, GameOver));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // si al salir la nueva pieza no cabe, es GameOver real
                if (!Tablero.puedemover(actual, 0, 0)) {
                    GameOver = true;
                    if (red != null) {
                        try {
                            red.enviar(new EstadoJuego(puntuacion, true));
                        } catch (Exception e) {}
                    }
                }
            } //Esta llave es la que faltaba para cerrar el 'else'

            repaint(); // actualiza la pantalla en cada paso del bucle
        } 
    } 

    protected void paintComponent(Graphics g){

        super.paintComponent(g); // limpia el lienzo

   /*  g.setColor(Color.BLACK); // Fondo del área de juego (izq)
    g.fillRect(0, 0, 300, getHeight()); */

           //  FONDO PRINCIPAL
Graphics2D g2d = (Graphics2D) g;

// Un azul cobalto/espacial 
Color azulEspacial = new Color(30, 60, 150); 
Color negroProfundo = new Color(5, 5, 15); // Casi negro, pero con un toque azul

// El degradado 
GradientPaint gp = new GradientPaint(0, 0, azulEspacial, 0, getHeight(), negroProfundo);

g2d.setPaint(gp);
g2d.fillRect(0, 0, 300, getHeight());

// --- estrellitas
// Dibujamos unos puntitos blancos fijos para que parezcan estrellas
g.setColor(new Color(255, 255, 255, 150)); // Blanco con transparencia
g.fillOval(50, 80, 2, 2);
g.fillOval(200, 150, 2, 2);
g.fillOval(120, 300, 2, 2);
g.fillOval(250, 450, 2, 2);
g.fillOval(30, 550, 2, 2);




            // seudomenu
    if(!GameOver && actual == null){
            g.setColor(Color.WHITE);

            g.drawString("ESPERANDO CONTRINCANTE...", 50, getHeight()/2-20);

            g.setColor(Color.YELLOW);
            g.drawString("O presiona 'S' para jugar solo", 50, (getHeight()/2+20));
            return;
        }

    // color de la cadricula
g.setColor(new Color(40, 40, 40)); // Un gris 

// Dibujar líneas verticales
for (int c = 0; c <= TableroDeJuego.COLUMNAS; c++) {
    int x = c * 30;
    g.drawLine(x, 0, x, TableroDeJuego.FILAS * 30);
}

// Dibujar líneas horizontales
for (int r = 0; r <= TableroDeJuego.FILAS; r++) {
    int y = r * 30;
    g.drawLine(0, y, 300, y);
}

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

            // panel lateral
    g.setColor(new Color(75, 0, 130)); // El color gris 
    g.fillRect(300, 0, 200, getHeight()); // Empieza en 300 y mide 200 de ancho
    
    // Borde separador 
    g.setColor(Color.WHITE);
    g.drawLine(300, 0, 300, getHeight());
     
             // ventana de la proxi mapieza
g.setColor(Color.WHITE);
g.setFont(new Font("Arial", Font.BOLD, 14));
g.drawString("SIGUIENTE:", 315, 260); // Un poco más abajo de los puntos

           // 1. Dibujamos el fondo negro de la cajita
        // (x=315, y=275, ancho=90, alto=90)
g.setColor(Color.BLACK);
g.fillRoundRect(315, 275, 110, 90, 15, 15); // RoundRect para que se vea más moderno

// 2. Dibujamos un borde blanco sutil para la cajita
g.setColor(new Color(255, 255, 255, 100)); // Blanco con transparencia
g.drawRoundRect(315, 275, 110, 90, 15, 15);

if (siguiente != null) {
    // Recorremos la matriz de la pieza que está en espera
    for (int r = 0; r < siguiente.forma.length; r++) {
        for (int c = 0; c < siguiente.forma[r].length; c++) {
            if (siguiente.forma[r][c] == 1) {
                // Dibujamos en miniatura (20px) para que quepa bien
                // 330 y 310 son las coordenadas iniciales dentro del panel gris
                int xMini = 330 + (c * 20); 
                int yMini = 310 + (r * 20);
                
                // Usamos un color fijo por ahora o el que definamos en Pieza.java
                g.setColor(Color.ORANGE); 
                g.fillRect(xMini, yMini, 18, 18); // 18px para dejar 2px de borde
                
                g.setColor(Color.WHITE);
                g.drawRect(xMini, yMini, 18, 18);
            }
        }
    }
}
           
        // puntuaje----
// Configuración para las "cajas" de los puntos
int cajaAncho = 160;
int cajaAlto = 70;
int margenX = 320; // Dejamos 20px de margen desde el inicio del panel (300+20)
Color colorCajaFondo = new Color(100, 20, 160); // Un morado un poco más claro

// --- Marcador: TÚ ---
// 1. Fondo de la caja con esquinas redondeadas
g.setColor(colorCajaFondo);
g.fillRoundRect(margenX, 50, cajaAncho, cajaAlto, 20, 20);
// 2. Borde brillante para que resalte
g.setColor(Color.CYAN);
g.drawRoundRect(margenX, 50, cajaAncho, cajaAlto, 20, 20);

// 3. Texto de la etiqueta (pequeño)
g.setColor(Color.LIGHT_GRAY);
g.setFont(new Font("Monospaced", Font.BOLD, 14));
g.drawString("TÚ", margenX + 15, 75);

// 4. Texto del PUNTAJE (grande y llamativo)
g.setColor(Color.CYAN);
g.setFont(new Font("Monospaced", Font.BOLD, 30));
// Usamos tu variable 'puntuacion'
g.drawString("" + puntuacion, margenX + 15, 110);


// --- Marcador: RIVAL ---
int yRival = 140; // Posición Y para el segundo bloque

// 1. Fondo y borde (usamos Amarillo para diferenciar al rival)
g.setColor(colorCajaFondo);
g.fillRoundRect(margenX, yRival, cajaAncho, cajaAlto, 20, 20);
g.setColor(Color.YELLOW); 
g.drawRoundRect(margenX, yRival, cajaAncho, cajaAlto, 20, 20);

// 2. Etiqueta
g.setColor(Color.LIGHT_GRAY);
g.setFont(new Font("Monospaced", Font.BOLD, 14));
g.drawString("RIVAL", margenX + 15, yRival + 25);

// 3. Puntaje del Rival
g.setColor(Color.YELLOW);
g.setFont(new Font("Monospaced", Font.BOLD, 30));
// Usamos tu variable 'puntuacionRival'
g.drawString("" + puntuacionRival, margenX + 15, yRival + 60);
    
    //GameOver

       if (GameOver) {
    // oscurecer la pantalla 
    g.setColor(new Color(0, 0, 0, 150)); // El 150 es la transparencia
    g.fillRect(0, 0, getWidth(), getHeight());

    if(puntuacion > puntuacionRival){
        g.setColor(Color.GREEN);
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        g.drawString("YOU WIN", 50, getHeight() / 2);
    }
    else if (puntuacion < puntuacionRival){
        
    g.setColor(Color.RED);
    g.setFont(new Font("Monospaced", Font.BOLD, 40));
    g.drawString("YOU LOSE", 50, getHeight() / 2);
    } 
    else {
        g.setColor(Color.YELLOW);
        g.drawString("EMPATE", 85, getHeight() / 2);
    }
    
    g.setFont(new Font("Arial", Font.PLAIN, 15));
    g.setColor(Color.WHITE);
    g.drawString("Puntaje final: " + puntuacion, 80, (getHeight() / 2) + 40);
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

            
