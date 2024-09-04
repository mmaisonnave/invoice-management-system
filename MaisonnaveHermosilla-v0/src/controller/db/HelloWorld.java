package controller.db;
 
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import exception.InvalidClientException;

public class HelloWorld {
	static DBEngine motor;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello world");
		motor = new DBEngine();

		Cliente maiso = new Cliente("20-36698447-0", "MAISONNAVE, Mariano","EXE");

		Cliente alberdi = motor.getCliente(1);
		
		
		try {
			motor.efectuarPago(alberdi,  459.8 , "Cancelación de deuda generada por error. ");
		} catch (InvalidClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// ALBERDI TIENE UNA DEUDA DE 459.8 que esta MAALL
		// CHEQUEAR!! 				(CHEQUEADO CON ARROQUY, SO TRI MA y  DON MODESTO, SOLO ESTA MAL CON ALBERDI ) 
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//		Presupuesto p = motor.verUltimoPresupuesto(alberdi);
//		System.out.println(p.toString());
//		System.out.println("modificamos presupuesto..");
//		p.getConceptos().get(0).setConcepto("concepto modificado.");
//		p.setMontoTotal(2800.00);
//		motor.editarPresupuesto(p);
//		p = motor.verUltimoPresupuesto(alberdi);
//		System.out.println(p.toString());
		
		
//		for(Presupuesto pp : motor.verPresupuestos(alberdi)){
//			System.out.println(pp.toString());
//		}
//
//		Concepto c1 = new Concepto("Concepto prueba 1", 1200);
//		Concepto c2 = new Concepto("Concepto prueba 2", 1500);
//		List<Concepto> con = new ArrayList<Concepto>();
//		con.add(c1);
//		con.add(c2);
//		Presupuesto nuevo = new Presupuesto( con, alberdi, false,0.0f ,2700, (Calendar.getInstance().getTime()) );
//		
//		System.out.println("Nro presu antes de insertar: "+nuevo.getNroPresupuesto());
//		motor.agregarPresupuesto(nuevo);
//		System.out.println("Nro presu despues de insertar: "+nuevo.getNroPresupuesto());
//			
//		for(Presupuesto pp : motor.verPresupuestos(alberdi)){
//			System.out.println(pp.toString());
//		}

	}
	
	public static void buscarYMostrarClientes(String busqueda){
		System.out.println("Consultando en la base de datos por un cliente que conincida con la busqueda: '"+busqueda+"' ");
		for(Cliente cliente: motor.buscarCliente(busqueda))
			System.out.println(cliente.toString());
	}

}
