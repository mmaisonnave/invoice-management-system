package controller.view;

 

import javafx.fxml.FXML;
import controller.Main;

/**
 * Controlador para el Panel Raiz. El Panel Raiz proporciona
 * el layout basico de la aplicacion, conteniendo una barra de menues
 * y espacio para los Panes de JavaFX.
 * 
 * @author Maria Virginia Sabando
 */
public class PanelRaizController {

    // Referencia al controlador principal
    private Main mainApp;

    /**
     * Es llamado por la aplicación principal para dar una referencia a sí mismo.
     * 
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
    
    /*---------------------------MENU CLIENTES---------------------------*/

    /**
     * Funcionalidad menú Administrar Clientes
     */
    @FXML
    private void handleAdministrarClientes() {
    	mainApp.showClienteVista();
    	
    }
    
    /**
     * Funcionalidad menú Eliminar cliente
     */
    @FXML
    private void handleEliminarCliente() {
    	    	
    }
    
   

    /*---------------------------MENU PRESUPUESTOS---------------------------*/
    
    /**
     * Funcionalidad menú Presupuestos Efectivos
     * TODO
     */
    @FXML
    private void handleVerPresupuestos() {  
    	mainApp.handleVerPresupuestoVista();
    	
    }
    
    /**
     * Funcionalidad menú Nuevo Presupuesto
     *
     */
    @FXML
    private void handleNuevoPresupuesto() {  
    	
    	mainApp.handleNuevoPresupuestoVista();
    	
    }
    
    /**
     * Funcionalidad menú Anular presupuestos
     * TODO
     */
    @FXML
    private void handleAnularPresupuesto() {   
    	
    }
    
    /*---------------------------MENU AREA DE TRABAJO---------------------------*/
    
    /**
     * Funcionalidad menú Area de trabajo
     */
    @FXML
    private void handleAreaDeTrabajo() {
    	
    	mainApp.showAreaDeTrabajoVista();
    	
    }
    
    
    /*-----------------------MENU CUENTAS CORRIENTES-----------------------*/
    
    @FXML
    private void handleDetalleCuentaCorriente(){
    	
    	mainApp.showDetalleCuentaCorrienteOverview();
    }
    
    @FXML
    private void handleRegistrarPago(){
    	
    	mainApp.showRegistrarPagoOverview();
    }
    
    

    /*-----------------------------MENU AYUDA-----------------------------*/
    
    
   
    
    
    
}