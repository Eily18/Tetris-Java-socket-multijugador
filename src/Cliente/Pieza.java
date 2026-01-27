package Cliente;

public class Pieza {
    // pieza representada por una matriz pequeña de numeros
    public int[][] forma;
    
    // posicion en el tablero, empieza en la columna 3 y la fila 0 (arriba)
    public int x = 3, y = 0;

    // el constructor recibe una forma y se la asigna a la pieza
    public Pieza(int[][] f) { 
        this.forma = f; 
    }
    // elige una pieza al azar
    public static Pieza piezaRandom() {
        // almacen que guarda todas las formas posibles
        int[][][] formasPosibles = {
            {{1,1,1,1}},           // El palo largo (I)
            {{1,1},{1,1}},         // El cuadrado (O)
            {{0,1,0},{1,1,1}},     // La pieza en forma de T
            {{1,0,0},{1,1,1}},     // La L
            {{0,0,1},{1,1,1}}      // La L invertida (J)
        };
        
        // elige una forma de la lista usando un numero al azar y crea una pieza nueva
        return new Pieza(formasPosibles[(int)(Math.random() * formasPosibles.length)]);
    }
          
           public void rotar() {
            int[][] nuevaForma = new int[forma[0].length][forma.length];
              for (int r = 0; r < forma.length; r++) {

                for (int c = 0; c < forma[0].length; c++) {

                 nuevaForma[c][forma.length - 1 - r] = forma[r][c];
        }
    }
    forma = nuevaForma;
}

}
