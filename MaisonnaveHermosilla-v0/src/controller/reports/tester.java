package controller.reports;

import java.util.List;

import controller.db.Cliente;
import controller.db.DBEngine;
import controller.db.Transaccion;

public class tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBEngine motor = new DBEngine("localhost");
		String desde = "2017-1-01";
		String hasta = "2018-01-01";
	

		ReportsEngine.generarResumen(motor.getCliente(4), desde, hasta); 
		/**
		System.out.println("arranca reportes");
		ReportsEngine.generarReporte(motor.verPresupuesto(2175),"/home/maiso/tmp.pdf");
		System.out.println("reporte 1");
		ReportsEngine.generarReporte(motor.verPresupuesto(2775));
		System.out.println("reporte 2");
		ReportsEngine.generarReporteBorrador(motor.verPresupuesto(2575));
		System.out.println("reporte 3");
*/
			System.out.println("Terminó.");
	}

}
