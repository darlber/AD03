package org.example;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
/*
para este primer ejercicio necesitamos
1. establecer conexion con DB
2. bucle repetitivo que corta cuando se introduce 0
3. comprobar si la empresa existe o no. metodo existeEmpresa()
4. mostrar datos metodo datosEmpresa()
 */

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or

// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

public class Main {
    public static void main(String[] args) throws SQLException {

        Scanner sn = new Scanner(System.in);

        //try with resources para que gestione el cierre automático
        try (Connection c = ConnectionDB.openConnection()) {
            System.out.println("=================================================================================");
            System.out.println("EJERCICIO1.");
            System.out.println("Nombre de alumno: Alberto Serradilla Gutiérrez");
            System.out.println("=================================================================================");
            int opt = -1;

            //bucle infinito hasta que se introduzca 0
            while (opt != 0) {
                System.out.print("Introduce un código de empresa (0 para salir): ");
                // Validación de entrada
                try {
                    opt = sn.nextInt();

                    if (opt < 0) {
                        System.out.println("El código de empresa no puede ser negativo.");
                        System.out.println("---------------------------------------------------------------------------------");
                        continue;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Por favor, introduce un número válido.");
                    System.out.println("---------------------------------------------------------------------------------");
                    sn.nextLine();
                    continue;
                }

                if (opt == 0) {
                    break;
                }
                if (!Empresa.existeEmpresa(c, opt)) {
                    System.out.println("---------------------------------------------------------------------------------");
                    System.out.println("EL CÓDIGO DE EMPRESA NO EXISTE");
                    System.out.println("---------------------------------------------------------------------------------");
                    continue;
                }
                Empresa.datosEmpresa(c, opt);

            }
        }
    }
}


