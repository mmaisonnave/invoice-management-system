package controller.reports;

public class ConceptosBean{
	private String descripcion;
	private double monto;
	public ConceptosBean(){
	}
	
	
	public String getDescripcion(){ return descripcion; }
	public double getMonto()      { return monto;       }
	
	public void setMonto(double monto)            { this.monto = monto;             }
	public void setDescripcion(String descripcion){ this.descripcion = descripcion; }
}
