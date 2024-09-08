package controller.reports;

public class CuentaCorrienteBean{
	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	public String getDenominacion() {
		return denominacion;
	}
	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}
	public double getSaldo() {
		return saldo;
	}
	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
	private String cuit;
	private String denominacion;
	private double saldo;
	
	public CuentaCorrienteBean(String CUIT, String denominacion, double saldo){
		this.cuit = CUIT;
		this.denominacion = denominacion;
		this.saldo = saldo;
	}
}