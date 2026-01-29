package Servidor;

public class ServidorDeRespaldo extends Thread{
    // extends Thread" permite que este servidor corra "a un lado" del principal
// sin estorbarle, como un guardaespaldas que vigila en silencio.
public class ServidorRespaldo extends Thread {

    /*El metodo run() es lo que se ejecuta cuando el MainServidor
    hace "new ServidorDeRespaldo().start()"*/
    @Override
    public void run() {
        // Por ahora, solo imprime un mensaje de confirmación
        System.out.println("Servidor de Respaldo activo y listo.");
    }
}
    
}
