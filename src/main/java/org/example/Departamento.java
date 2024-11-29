package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Departamento {

    // Metodo para obtener los departamentos de una empresa
    public static void datosDepartamento(Connection c, int codEmpre) throws SQLException {
        // Consulta para obtener los datos de los departamentos asociados a la empresa
        String sqlDepartamentos = "SELECT coddepart, nombre, localidad " +
                "FROM DEPARTAMENTOS " +
                "WHERE codEmpre = ? " +
                "ORDER BY coddepart";

        try (PreparedStatement psDeptos = c.prepareStatement(sqlDepartamentos)) {
            // Establecer el código de empresa
            psDeptos.setInt(1, codEmpre);
            ResultSet rsDeptos = psDeptos.executeQuery();

            // Verificar si hay departamentos y mostrarlos
            boolean hayDepartamentos = false;

            while (rsDeptos.next()) {
                hayDepartamentos = true;
                int codDepto = rsDeptos.getInt("coddepart");
                String nombreDepto = rsDeptos.getString("nombre");
                String localidadDepto = rsDeptos.getString("localidad");


                System.out.printf("COD-DEPARTAMENTO: %-5d NOMBRE: %-15s LOCALIDAD: %-20s%n", codDepto, nombreDepto, localidadDepto);
                System.out.println("-----------------------------------------------------------------------------------");

                // Obtener los empleados del departamento

                Empleado.datosEmpleados(c, codDepto);

                System.out.println("--------------------------------------------------");
            }


            if (!hayDepartamentos) {
                System.out.println("LA EMPRESA NO TIENE DEPARTAMENTOS");
                System.out.println("-----------------------------------------------------------------------------------");

            }
        }
    }
}