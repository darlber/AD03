package org.Ejercicio2;

import org.Ejercicio1.ConnectionDB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FuncionAlmacenada {

private static int contadorPruebas = 1;

    public static void main(String[] args) throws SQLException {
                /*
    las funciones solo pueden retornar 1 valor, a diferencia de los procedimientos.
    como el ejercicio nos pide que retorne dos valores, lo que podemos hacer es crear
    un objeto con 2 parametros y retornar el objeto per se.
     */
        Scanner sn = new Scanner(System.in);
        try (Connection c = ConnectionDB.openConnection()) {
            System.out.println("=================================================================================");
            System.out.println("EJERCICIO2.");
            System.out.println("Nombre de alumno: Alberto Serradilla Gutiérrez");
            System.out.println("=================================================================================");

            crearFuncion(c);
            crearProcedure(c);

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
                } catch (InputMismatchException ex) {
                    System.out.println("Por favor, introduce un número válido.");
                    System.out.println("---------------------------------------------------------------------------------");
                    sn.nextLine();
                    continue;
                }

                if (opt == 0) {
                    sn.close();
                    break;
                }

                llamadafuncion(c, opt);


            }
            //pruebas
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("PROBANDO LAS LLAMADAS AL PROCEDIMIENTO");
            System.out.println("---------------------------------------------------------------------------------");
            PruebaProcedimiento(c, 301, "Maria", "C/Peñaflor 2", "Berrocalejo", 101, 11, 250);
            PruebaProcedimiento(c, 1, "Maria", "C/Peñaflor 2", "Berrocalejo", 1, 1, 1);
            PruebaProcedimiento(c, 1, "Maria", "C/Peñaflor 2", "Berrocalejo", 1, 1, 250);
            PruebaProcedimiento(c, 1, "Maria", "C/Peñaflor 2", "Berrocalejo", 101, 1, 1);
            PruebaProcedimiento(c, 301, "Maria", "C/Peñaflor 2", "Berrocalejo", 1, 1, 1);
            PruebaProcedimiento(c, 1, "Maria", "C/Peñaflor 2", "Berrocalejo", 101, 11, 1);
            PruebaProcedimiento(c, 1, "Maria", "C/Peñaflor 2", "Berrocalejo", 101, 11, 1);

        }
    }

    private static void crearFuncion(Connection c) throws SQLException {
        // SQL para crear el tipo EMPRESA_DATOS
        Statement s = c.createStatement();
        String objeto = "CREATE OR REPLACE TYPE EMPRESA_DATOS AS OBJECT (" +
                "    numEmpleados INTEGER," +
                "    numDepartamentos INTEGER" +
                ")";

        s.execute(objeto);


        // Crear la función F1_SerradillaGutierrezAlberto
        String funcion = """
                create or replace function f1_serradillagutierrezalberto (
                   codempresa in INTEGER
                ) return empresa_datos is
                   numempleados     INTEGER;
                   numdepartamentos INTEGER;
                   v_exists         INTEGER; -- Variable para verificar si la empresa existe \s
                begin
                   select count(*)
                     into v_exists
                     from empresas
                    where codempre = codempresa;
                   if v_exists = 0 then
                      return empresa_datos(
                         -1,
                         -1
                      ); -- Si no existe, devolver -1 en ambos campos \s
                   end if;
                   select count(distinct e.codemple)
                     into numempleados
                     from empleados e
                     join departamentos d
                   on e.coddepart = d.coddepart
                    where d.codempre = codempresa;
                   select count(distinct d.coddepart)
                     into numdepartamentos
                     from departamentos d
                    where d.codempre = codempresa;
                   return empresa_datos(
                      numempleados,
                      numdepartamentos
                   );
                end;""";


        s.execute(funcion);

    }

    private static void llamadafuncion(Connection c, int opt) {
        String sql = "{? = call F1_SerradillaGutierrezAlberto(?)}";

        //construimos llamada
        try (CallableStatement llamada = c.prepareCall(sql)) {
            //registrar parámetro de resultado
            llamada.registerOutParameter(1, Types.STRUCT, "EMPRESA_DATOS");  // Valor devuelto
            llamada.setInt(2, opt);

            llamada.execute();

            // Obtener el resultado como un STRUCT, especifico para los objetos
            Struct result = llamada.getObject(1, Struct.class);

            // Extraer los valores del resultado del STRUCT
            Object[] attributes = result.getAttributes();

            //el script sql, retorna valores BigDecimal
            //podemos hacer un casting dentro del propio script
            BigDecimal totalEmpleSQL = (BigDecimal) attributes[0];
            BigDecimal totalDepartSQL = (BigDecimal) attributes[1];

            //o albergar los datos en bigDecimal y convertirlos dentro del propio código
            int numEmpleados = totalEmpleSQL.intValue();
            int numDepartamentos = totalDepartSQL.intValue();


            // Mostrar los resultados
            if (numEmpleados == -1 && numDepartamentos == -1) {
                System.out.println("---------------------------------------------------------------------------------");
                System.out.println("Número de departamentos: " + numDepartamentos);
                System.out.println("Número de empleados: " + numEmpleados);
                System.out.println("---------------------------------------------------------------------------------");

            } else {
                System.out.println("---------------------------------------------------------------------------------");
                System.out.println("Número de departamentos: " + numDepartamentos);
                System.out.println("Número de empleados: " + numEmpleados);
                System.out.println("---------------------------------------------------------------------------------");

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void crearProcedure(Connection c) throws SQLException {
        Statement s = c.createStatement();

        String procedure = """
                CREATE OR REPLACE PROCEDURE P1_SerradillaGutierrezAlberto (
                    p_codemple IN EMPLEADOS.codemple%TYPE,
                    p_nombre IN EMPLEADOS.nombre%TYPE,
                    p_direccion IN EMPLEADOS.direccion%TYPE,
                    p_poblacion IN EMPLEADOS.poblacion%TYPE,
                    p_codencargado IN EMPLEADOS.codencargado%TYPE,
                    p_coddepart IN EMPLEADOS.coddepart%TYPE,
                    p_codoficio IN EMPLEADOS.codoficio%TYPE,
                    p_mensaje OUT VARCHAR2
                ) IS
                    v_exists NUMBER;
                    v_mensaje VARCHAR2(4000);
                BEGIN
                    v_mensaje := '';
                
                
                    SELECT COUNT(*) INTO v_exists FROM EMPLEADOS WHERE codemple = p_codemple;
                    IF v_exists > 0 THEN
                        v_mensaje := v_mensaje || 'EMPLEADO ' || p_codemple || ' YA EXISTE. ' || CHR(10);
                    END IF;
                
                
                    SELECT COUNT(*) INTO v_exists FROM EMPLEADOS WHERE codemple = p_codencargado;
                    IF v_exists = 0 THEN
                        v_mensaje := v_mensaje || 'ENCARGADO ' || p_codencargado || ' NO EXISTE. ' || CHR(10);
                    END IF;
                
                
                    SELECT COUNT(*) INTO v_exists FROM DEPARTAMENTOS WHERE coddepart = p_coddepart;
                    IF v_exists = 0 THEN
                        v_mensaje := v_mensaje || 'DEPARTAMENTO ' || p_coddepart || ' NO EXISTE. ' || CHR(10);
                    END IF;
                
                
                    SELECT COUNT(*) INTO v_exists FROM OFICIOS WHERE codoficio = p_codoficio;
                    IF v_exists = 0 THEN
                        v_mensaje := v_mensaje || 'OFICIO ' || p_codoficio || ' NO EXISTE. ' || CHR(10);
                    END IF;
                
                
                    IF v_mensaje IS NOT NULL THEN
                        p_mensaje := v_mensaje;
                        RETURN;
                    END IF;
                
                
                    INSERT INTO EMPLEADOS (codemple, nombre, direccion, poblacion, fechaalta, codencargado, coddepart, codoficio)
                    VALUES (p_codemple, p_nombre, p_direccion, p_poblacion, SYSDATE, p_codencargado, p_coddepart, p_codoficio);
                
                
                    p_mensaje := 'EMPLEADO ' || p_codemple || ' INSERTADO EXITOSAMENTE' || CHR(10);
                EXCEPTION
                    WHEN OTHERS THEN
                        p_mensaje := 'ERROR: ' || SQLERRM;
                END P1_SerradillaGutierrezAlberto;
                """;


        s.execute(procedure);
        s.close();

    }

    public static void PruebaProcedimiento(Connection c,
                                           int codemple, String nombre, String direccion, String poblacion,
                                           int codencargado, int coddepart, int codoficio) {

        String sql = "{call P1_SerradillaGutierrezAlberto(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement s = c.prepareCall(sql)) {
            s.setInt(1, codemple);
            s.setString(2, nombre);
            s.setString(3, direccion);
            s.setString(4, poblacion);
            s.setInt(5, codencargado);
            s.setInt(6, coddepart);
            s.setInt(7, codoficio);

            // Registramos la salida, que sera el mensaje que muestre info
            s.registerOutParameter(8, Types.VARCHAR);

            // Ejecutar el procedimiento
            s.execute();

            // Obtener el mensaje de salida

            String mensaje = s.getString(8);
            System.out.println("PRUEBA " + (contadorPruebas++) + ": (" + codemple + ", \"" + nombre + "\", \"" + direccion + "\", \"" + poblacion + "\", " + codencargado + ", " + coddepart + ", " + codoficio + ")\n");
            System.out.println(mensaje + "---------------------------------------------------------------------------------");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}


