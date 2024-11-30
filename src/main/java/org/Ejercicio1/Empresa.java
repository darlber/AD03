package org.Ejercicio1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Empresa {

    public static boolean existeEmpresa(Connection c, int codEmpre) throws SQLException {
        ResultSet rs;

        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) " +
                "FROM EMPRESAS " +
                "WHERE codEmpre = ?")) {

            ps.setInt(1, codEmpre);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    public static void datosEmpresa(Connection c, int codEmpre) throws SQLException {
        String sqlEmpresa = "SELECT codEmpre, nombre, direccion " +
                "FROM EMPRESAS " +
                "WHERE codEmpre = ?";
        String sqlDepartamentos = "SELECT COUNT(*) " +
                "FROM DEPARTAMENTOS " +
                "WHERE codEmpre = ?";

        try (PreparedStatement psEmpresa = c.prepareStatement(sqlEmpresa);
             PreparedStatement psDeptos = c.prepareStatement(sqlDepartamentos)) {

            // Obtener los datos básicos de la empresa
            psEmpresa.setInt(1, codEmpre);
            ResultSet rsEmpresa = psEmpresa.executeQuery();

            if (rsEmpresa.next()) {
                int cod = rsEmpresa.getInt("codEmpre");
                String nombre = rsEmpresa.getString("nombre");
                String direccion = rsEmpresa.getString("direccion");

                // Contar los departamentos de la empresa
                psDeptos.setInt(1, codEmpre);
                ResultSet rsDeptos = psDeptos.executeQuery();
                int numDeptos = 0;
                if (rsDeptos.next()) {
                    numDeptos = rsDeptos.getInt(1);
                }

                System.out.println("-----------------------------------------------------------------------------------");
                System.out.printf("%-11s: %-3d %-6s: %-20s \n%-9s: %-35s %-23s %-3d\n",
                        "COD-EMPRESA", cod,
                        "NOMBRE", nombre,
                        "DIRECCIÓN", direccion,
                        "Número de departamentos", numDeptos);

                System.out.println("-----------------------------------------------------------------------------------");

                Departamento.datosDepartamento(c, codEmpre);

            } else {
                System.out.println("No se encontró la empresa con código: " + codEmpre);

            }
        }
    }
}