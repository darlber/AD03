package org.Ejercicio1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {


    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE?useUnicode=true&characterEncoding=UTF-8";
    private static final String USUARIO = "EMPRESAS";
    private static final String CONTRASEÑA = "EMPRESAS";


    private static Connection c;

    // Constructor privado para evitar instanciación. Clase Singleton por motivos de seguridad
    private ConnectionDB() {
    }

    // Metodo para obtener la conexión
    public static Connection openConnection() throws SQLException {
        if (c == null || c.isClosed()) {
            try {
                c = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
                System.out.println("Conexión exitosa a la base de datos.");
            } catch (SQLException e) {
                System.err.println("Error al conectar con la base de datos.");
                e.printStackTrace();
                throw e;
            }
        }
        return c;
    }

    // Metodo para cerrar la conexión
    public static void closeConnection() {
        if (c != null) {
            try {
                c.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión.");
                e.printStackTrace();
            }
        }
    }
}