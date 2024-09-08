package controller.reports;

import java.util.ArrayList;
import java.util.List;

import controller.db.Cliente;
import controller.db.Transaccion;

public class ResumenBean {
	protected String desde;
	protected String hasta;
	protected Cliente cliente;
	protected List<TransaccionBean> transacciones;
	
	public double getSaldoInicial() {
		double saldo_inicial = 0;
		if(this.transacciones== null || this.transacciones.size()==0 ) {
			System.out.println("[WARNING] se pidio saldo inicial de lista de transacciones vacias.");
			return 0;
		}
		else {
			TransaccionBean t_aux = this.transacciones.get(0);
			if (t_aux.getEvento() == 'C'||t_aux.getEvento() == 'X') //pago
				saldo_inicial = t_aux.getSaldo()-t_aux.getHaber() ;
			else
				if  (t_aux.getEvento()=='P') //presupuesto
					saldo_inicial = t_aux.getSaldo() + t_aux.getDebe();
				else {
					System.out.println("[WARNING] se pidio saldo inicial de lista de transacciones con evento distinto de P y C.");
					return 0;
				}
			
			
			
		}
		return saldo_inicial;
		
	}
 	public ResumenBean(Cliente cliente, String desde, String hasta, List<TransaccionBean> transacciones) {
 		this.cliente       = cliente;
 		this.desde         = desde;
 		this.hasta         = hasta;
 		this.transacciones = transacciones;
 	}
 	
 	public static List<TransaccionBean> TransaccionToTransaccionBean(List<Transaccion> lista){
 		
 		List<TransaccionBean> l = new ArrayList<TransaccionBean>();
 		
 		
 		for (Transaccion t : lista) {
 			double debe        = 0;
 			double haber       = 0;
 			double saldo       = t.getEstadoCuentaCorriente();
 			if (t.getEvento()=='C'||t.getEvento() == 'X') { //credito (pago)
 				haber = t.getMonto(); 
 			}
 			else if (t.getEvento()=='P') { //Presupuesto
 				debe = t.getMonto();
 			}
 			String descripcion = t.getObservacion();
 			String fecha       = t.getFecha();
 			l.add(new TransaccionBean(fecha,descripcion, debe,haber,saldo,t.getEvento()));

 			System.out.println("procesando transaccion..."+l.size());
 		}

 		return l;
 		
 	}
 	
 	public String getDesde() {
 		return this.desde;
 	}
	public String getHasta() {
		return this.hasta;
	}
	public String getDenominacion() {
		return this.cliente.getDenominacion();
	}
	public String getCUIT() {
		return this.cliente.getCuit();
	}
	
	public List<TransaccionBean> getTransacciones(){
		return this.transacciones;
	}

}
