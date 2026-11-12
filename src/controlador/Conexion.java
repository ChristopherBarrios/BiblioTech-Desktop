package controlador;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

public class Conexion {
    
    private Connection enlace;
            
    public Conexion() {
        String ruta= "jdbc:postgresql://localhost:5432/db_libreria";
        try{
        Class.forName("org.postgresql.Drive");
        enlace=DriverManager.getConnection(ruta, "postgres", "root");

    }catch(Exception error){
            System.out.println("Error:" + error.toString());
    
    }
    }
    
}
