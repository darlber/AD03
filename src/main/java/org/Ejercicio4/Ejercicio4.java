package org.Ejercicio4;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.Ejercicio1.ConnectionDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Ejercicio4 {
    public static void main(String[] args) {
        System.out.println("=================================================================================");
        System.out.println("EJERCICIO4.");
        System.out.println("Nombre de alumno: Alberto Serradilla Gutiérrez");
        System.out.println("=================================================================================");

        String reportSource = "./plantillas/plantilla.jrxml";
        String reportPDF = "./informes/";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("titulo", "LISTADO DE DEPARTAMENTOS DE LA EMPRESA.");
        params.put("autor", "Alberto Serradilla Gutierrez");
        params.put("fecha", (new java.util.Date()).toString());

        try {
            JasperReport jr = JasperCompileManager.compileReport(reportSource);
            Connection c = ConnectionDB.openConnection();
            JasperPrint MiInforme = JasperFillManager.fillReport(jr, params, c);

            JasperViewer.viewReport(MiInforme);
            JasperExportManager.exportReportToPdfFile(MiInforme, reportPDF);

            System.out.println("ARCHIVOS CREADOS");
        } catch (JRException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
