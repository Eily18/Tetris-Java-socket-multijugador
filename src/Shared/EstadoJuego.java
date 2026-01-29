package Shared;
import java.io.Serializable; //herramienta para convertir a datos de envio

public class EstadoJuego implements Serializable {
    //los datos que se envian
    public int puntuacion; 
    public boolean isGameOver;

    // constructor para llenar
    // los datos actuales
    public EstadoJuego(int p, boolean jt) {
        this.puntuacion = p;
        this.isGameOver = jt;
    }
}