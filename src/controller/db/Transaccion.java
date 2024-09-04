package controller.db;
 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;

import java.time.LocalDate;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Transaccion {
	
	protected final int INVALID_NRO_TRANSACCION = -1 ;
	protected int Nro_Transaccion;
	protected Cliente cliente;
	protected StringProperty Fecha;
	protected char Evento;
	protected DoubleProperty Monto;
	protected StringProperty concepto_observacion;
	protected DoubleProperty Estado_cuenta_corriente;
	protected Date fechita;	
	
	public Transaccion(Cliente cliente, Date fecha, char evento, double monto, String obs, double Estado_cuenta_corriente){
		this.cliente = cliente;
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		this.Fecha = new SimpleStringProperty(format1.format(fecha));
		this.Evento = evento;
		this.Monto = new SimpleDoubleProperty(monto);
		this.concepto_observacion = new SimpleStringProperty(obs);
		this.Estado_cuenta_corriente = new SimpleDoubleProperty(Estado_cuenta_corriente);
		this.fechita=fecha;
	}
	public char getEvento() {
		return Evento;
	}
	public String getObservacion() {
		return this.concepto_observacion.get();
	}

	public double getMonto() {
		return this.Monto.doubleValue();
	}
	public double getEstadoCuentaCorriente() {
		return this.Estado_cuenta_corriente.doubleValue();
	}
	
	public String getFormattedEvent() {
		String ev = "";
		switch(this.Evento) {
		case('C'):
			ev = "Pago";
			break;
		case('P'):
			ev = "Presupuesto";
			break;
		case('X'):
			ev = "Cancelación";
			break;
		default:
			ev= "Sin información";
			break;
		}
		return ev;
	}
	
	public void actualizarNroTransaccion(int nroT){
		Nro_Transaccion = nroT;
	}
	
	public boolean isValidTransaction(){
		return (Nro_Transaccion != this.INVALID_NRO_TRANSACCION);
	}
	public Cliente getCliente() {
		return this.cliente;
	}
	
	public String getFecha(){
		return Fecha.get();
	}
	public StringProperty fechaProperty(){
		return this.Fecha;
	}
	
	public StringProperty fechaARGProperty(){
		//Format2: lo que se muestra por pantallas (formato argentino)
		//Solo para mostrarlo en la aplicacion cuando sea necesario
		SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
		StringProperty fecha_ARG = new SimpleStringProperty(format2.format(this.fechita));
		
		return fecha_ARG;
	}
	
	//TODO	
	public ObjectProperty fechaObjectProperty(){
		return new SimpleObjectProperty(fechita);
		
	}
	
	public StringProperty eventoProperty(){
		StringProperty toret = new SimpleStringProperty(); 
		toret.set(getFormattedEvent());
		
		return toret;
	}
	
	public DoubleProperty montoProperty(){
		return this.Monto;
	}
	
	public StringProperty montoStringProperty(){
		StringProperty msp = new SimpleStringProperty(String.format("$ %,.2f", this.Monto.get()));
		return msp;
	}
	
	
	public StringProperty observacionProperty(){
		return this.concepto_observacion;
	}
	
	public DoubleProperty estadoCuentaProperty(){
		return this.Estado_cuenta_corriente;
	}
	
	public StringProperty estadoStringProperty(){
		StringProperty esp = new SimpleStringProperty(String.format("$ %,.2f", this.Estado_cuenta_corriente.get()));
		return esp;
	}
	
	public int getNroTransaccion() {
		return Nro_Transaccion;
	}
	
	public String toString(){
		return "("+this.Nro_Transaccion+") ["+this.Fecha+"]-- Cliente: ["+this.cliente.toString()+"] -- Monto: "+this.Monto+" -- Estado cuenta corriente: "+this.Estado_cuenta_corriente+".";
	}

}
