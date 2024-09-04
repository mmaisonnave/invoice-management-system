package controller.db;
 
import javafx.beans.property.*;

public class Concepto {

	protected String concepto;
	protected double monto;
	private StringProperty conceptoProperty;
	private DoubleProperty montoConceptoProperty;
	
	
	public Concepto(String c, double monto){
		this.monto = monto;
		this.concepto = c;
		this.conceptoProperty = new SimpleStringProperty(c);
		this.montoConceptoProperty = new SimpleDoubleProperty(monto);
	}
	
	public String toString(){
		return ""+concepto+" - "+monto+"";
	}
	
	public String getConcepto(){
		return concepto;
	}
	
	public StringProperty getConceptoProperty(){
		return conceptoProperty;
	}
	
	public Double getMonto(){
		return monto;
	}
	
	public StringProperty getMontoConceptoStringProperty(){
		StringProperty aux = new SimpleStringProperty(String.format("$ %,.2f", this.monto));
		return aux;
	}
	
	public void setConcepto(String c ){
		concepto = c;
	}
	
	public void setMonto(double m ){
		monto = m;
	}

}
