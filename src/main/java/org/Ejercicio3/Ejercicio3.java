package org.Ejercicio3;

import org.Ejercicio1.ConnectionDB;

import java.sql.*;


public class Ejercicio3 {
    private static int contadorPruebas = 1;


    public static void main(String[] args) throws SQLException {
        try (Connection c = ConnectionDB.openConnection()) {
            System.out.println("=================================================================================");
            System.out.println("EJERCICIO3.");
            System.out.println("Nombre de alumno: Alberto Serradilla Gutiérrez");
            System.out.println("=================================================================================");

            //en este metodo creamos el valor numerico y el procedimiento que nos ayudará con la inserción de datos
            crearProc(c);

            ejercicio3(c, 11, "GESTIÓN", "C/Mayor 17", "Madrid", 101, 1);
            ejercicio3(c, 111, "GESTIÓN", "C/Mayor 17", "Madrid", 10, 123);
            ejercicio3(c, 11, "GESTIÓN", "C/Mayor 17", "Madrid", 10, 123);
            ejercicio3(c, 112, "GESTIÓN", "C/Mayor 17", "Madrid", 101, 1);
            ejercicio3(c, 114, "GESTIÓN", "C/Mayor 17", "Madrid", 101, 1);
            ejercicio3(c, 115, "GESTIÓN", "C/Mayor 17", "Madrid", 101, 2);


        }
    }


    private static void ejercicio3(Connection c, int codDepart, String nombre, String direccion,
                                   String localidad, int codjefedepartamento, int codempre) throws SQLException {

        String sql = "{CALL InsertarDepartamento(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement s = c.prepareCall(sql)) {
            s.setInt(1, codDepart);
            s.setString(2, nombre);
            s.setString(3, direccion);
            s.setString(4, localidad);
            s.setInt(5, codjefedepartamento);
            s.setInt(6, codempre);

            s.registerOutParameter(7, Types.VARCHAR);

            s.executeUpdate();

            String mensaje = s.getString(7);

            System.out.println(contadorPruebas + ")\n" + mensaje + "\n");
            contadorPruebas++;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private static void crearProc(Connection c) throws SQLException {

        //creamos la columna numerica dentro de la tabla empresas a la que sumaremos 1 en cada insercion existosa
        try (Statement s = c.createStatement()) {
            s.executeUpdate("ALTER TABLE EMPRESAS ADD (num_departamentos NUMBER DEFAULT 0)");
        } catch (SQLException e) {
            //error de que ya existe la columna
            if (!e.getMessage().contains("ORA-01430")) {
                throw e;
            }
        }

        try (Statement s = c.createStatement()) {
            String procedure = """
                    CREATE OR REPLACE PROCEDURE InsertarDepartamento (
                        p_coddepart IN DEPARTAMENTOS.coddepart%TYPE,
                        p_nombre IN DEPARTAMENTOS.nombre%TYPE,
                        p_direccion IN DEPARTAMENTOS.direccion%TYPE,
                        p_localidad IN DEPARTAMENTOS.localidad%TYPE,
                        p_codjefedepartamento IN DEPARTAMENTOS.codjefedepartamento%TYPE,
                        p_codempre IN DEPARTAMENTOS.codempre%TYPE,
                        p_mensaje OUT VARCHAR2
                    ) IS
                        v_exists NUMBER;
                        v_num_departamentos NUMBER;
                        v_mensaje VARCHAR2(4000);
                    BEGIN
                        v_mensaje := '';
                    
                        -- coddepart
                        SELECT COUNT(*) INTO v_exists FROM DEPARTAMENTOS WHERE coddepart = p_coddepart;
                        IF v_exists > 0 THEN
                            v_mensaje := v_mensaje || 'EL DEPARTAMENTO ' || p_coddepart || ', ya existe.' || CHR(10);
                        END IF;
                    
                        -- codjefedepartamento 
                        SELECT COUNT(*) INTO v_exists FROM EMPLEADOS WHERE codemple = p_codjefedepartamento;
                        IF v_exists = 0 THEN
                            v_mensaje := v_mensaje || 'EL JEFE DE DEPARTAMENTO ' || p_codjefedepartamento || ', NO existe.' || CHR(10);
                        END IF;
                    
                        -- codempre 
                        SELECT COUNT(*) INTO v_exists FROM EMPRESAS WHERE codempre = p_codempre;
                        IF v_exists = 0 THEN
                            v_mensaje := v_mensaje || 'EL CODIGO DE EMPRESA ' || p_codempre || ', NO existe.' || CHR(10);
                        END IF;
                    
                        -- Errores
                        IF v_mensaje IS NOT NULL THEN
                            p_mensaje := v_mensaje || 'HAY ERRORES, NO SE INSERTARÁ EL REGISTRO';
                            RETURN;
                        END IF;
                    
                        -- Insertar
                        INSERT INTO DEPARTAMENTOS (coddepart, nombre, direccion, localidad, codjefedepartamento, codempre)
                        VALUES (p_coddepart, p_nombre, p_direccion, p_localidad, p_codjefedepartamento, p_codempre);
                    
                        -- Actualizar
                        UPDATE EMPRESAS SET num_departamentos = num_departamentos + 1 WHERE codempre = p_codempre 
                        RETURNING num_departamentos INTO v_num_departamentos;                    
                        p_mensaje := 'Registro INSERTADO... ' || CHR(10) || 'Se ha sumado 1 a la empresa: '|| v_num_departamentos;
                    EXCEPTION
                        WHEN OTHERS THEN
                            p_mensaje := 'HAY ERRORES, NO SE INSERTARÁ EL REGISTRO ' || SQLERRM;
                    END InsertarDepartamento;
                    """;
            s.execute(procedure);
        }

    }

}
