package org.Ejercicio1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Empleado { // Constructor

    public static void datosEmpleados(Connection c, int codDepart) throws SQLException {
        int numTotal = 0;

        String sql = "SELECT e.codemple, e.nombre AS empleado_nombre, o.nombre AS nombre_oficio, " +
                "enc.nombre AS encargado_nombre " +
                "FROM EMPLEADOS e " +
                "JOIN OFICIOS o ON e.codoficio = o.codoficio " +
                "LEFT JOIN EMPLEADOS enc ON e.codencargado = enc.codemple " +
                "WHERE e.coddepart = ? " +
                "ORDER BY e.codemple";


        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, codDepart);
            ResultSet rs = ps.executeQuery();
            System.out.println("COD-EMP        NOMBRE                OFICIO            NOMBRE ENCARGADO");
            System.out.println("------- -------------------- ----------------------- ----------------");
            // Mostrar los datos de cada empleado
            while (rs.next()) {
                int codEmpleado = rs.getInt("codemple");
                String nombre = rs.getString("empleado_nombre");
                String oficio = rs.getString("nombre_oficio");
                String encargado = rs.getString("encargado_nombre");
                if (encargado == null) {
                    encargado = "***";
                }

                System.out.printf("%-7s %-20s %-25s %-20s%n", codEmpleado, nombre, oficio, encargado);
                numTotal++;
            }
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("Número de empleados del departamento: " + numTotal);
            getJefe(c, codDepart);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getJefe(Connection c, int codDepart) throws SQLException {

        String sql = "SELECT e.nombre " +
                "FROM DEPARTAMENTOS d " +
                "JOIN EMPLEADOS e ON d.codjefedepartamento = e.codemple " +
                "WHERE d.coddepart = ?";

        PreparedStatement ps = c.prepareStatement(sql);
        ps.setInt(1, codDepart);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String jefe = rs.getString("nombre");
            System.out.println("Nombre del jefe del departamento: " + jefe);
        }

    }
}