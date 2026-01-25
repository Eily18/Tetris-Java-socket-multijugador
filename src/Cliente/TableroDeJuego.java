package Cliente;

public class TableroDeJuego { // se define el tamaño de filas y columnas
    public static final int filas = 20, columnas = 10;
    // se crea el tablero como una cuadricula de numeros (0 para vacio, 1 para bloque)
    public int[][] Matriz = new int[filas][columnas];
    // funcion para mover la pieza (p=pieza, dx = movimiento horizontal, dy = movimiento vertical)
    public boolean puedemover(Pieza p, int movX, int movY){
        // revisa cada fila (r) y columna (c) de la forma de la pieza
        for(int r=0; r < p.forma.length; r++)
            for(int c=0; c < p.forma[r].length; c++)
        // si en ese cuadro de la pieza hay un bloque(1)
        if (p.forma[r][c] == 1){
        // calcula la posicion futura en el tablero
        int futuroX = p.x + c + movX;
        int futuroY = p.y + r + movY;
        // verifica si se sale de los bordes izquierdo, derecho o inferior
        if(futuroX < 0 || futuroX >= columnas || futuroY >= filas) return false;
        // se verifica si en esa posicion ya hay otra piedra (1)
        if(futuroY >= 0 && Matriz[futuroY][futuroX] == 1) return false;
    }
    //si paso todas las pruebas, la pieza se puede mover 
    return true;
    }
    // funcion para fijar la pieza al tablero
    public void fijar(Pieza p){
        for (int r=0; r < p.forma.length; r++)
            for (int c=0; c < p.forma[r].length; c++)
                if (p.forma[r][c] == 1)
                    // lo vacio se convierte en 0 y las piedras en 1
                    Matriz[p.y + r][p.x + c] = 1;

    }
    // funcion para limpiar las lineas que esten completas
    public int limpiarlineas() {
        int lineasContadas = 0;
        for (int r=0; r < filas; r++) {
            boolean llena = true;
            // Si encuentra un solo 0, la fila no está llena
            for (int c=0; c < columnas; c++)
                if (Matriz[r][c] == 0) llena = false;
            
            if (llena) {
                lineasContadas++;
                // Borra la fila y hace que todas las de arriba bajen un nivel
                for (int i = r; i > 0; i--)
                    Matriz[i] = Matriz[i-1].clone();
            }
        }
        return lineasContadas; // Avisa cuántas filas se borraron para los puntos
    }
}
