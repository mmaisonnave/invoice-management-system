package controller;

import controller.db.Cliente;
import controller.db.CuentaCorriente;
import controller.view.*;
import exception.InvalidClientException;
import controller.db.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    
    /**
     * lista observable de clientes.
     */
    private ObservableList<Cliente> clienteData;
    
    /**
     * lista observable de presupuestos no efectivos para el area de trabajo.
     */
    private ObservableList<Presupuesto> presupuestosNoEfectivosData;
    
 
    /**
     * Motor de la base de datos.
     */
    private DBEngine DBMotor;
    
    /**
     * lista observable de cuentas corrientes
     */
    private ObservableList<CuentaCorriente> cuentasCorrientesData;
   
    /**
     * Constructor de la clase principal. Inicializa las listas de clientes y 
     * presupuestos que se cargan en las tablas en cada vista.
     */
    public Main() {
    	//Primero, voy a las carpetas de reportes y borro lo que haya en tmp
    	File A = new File("reportes/tmp/");
    	File B = new File("borradores/tmp/");
    	
    	for(File file: A.listFiles()) 
    	    if (!file.isDirectory()) 
    	        file.delete();
    	
    	for(File file: B.listFiles()) 
    	    if (!file.isDirectory()) 
    	        file.delete();
    	
    	DBMotor = DBSingleton.getInstance();
    	clienteData = FXCollections.observableArrayList(DBMotor.buscarCliente(""));
    	presupuestosNoEfectivosData = FXCollections.observableArrayList(DBMotor.obtenerPresupuestosNoEfectivos());
    	cuentasCorrientesData = FXCollections.observableArrayList(DBMotor.getCuentasCorrientesHabilitados());
    }
    
    /**
     * Retorna el stage principal
     * @return primaryStage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public List<Cliente> getClientesHabilitados(){
    	return DBMotor.getClientesHabilitados();
    }
    
    /**
     * Retorna la informacion de clientes como una lista observable de Clientes. 
     * @return
     */
    public ObservableList<Cliente> getClienteData() {
        return clienteData;
    }
    
    /**
     * Setea lista observable de clientes 
     * @return
     */
    public void setClienteData(ObservableList<Cliente> listaClientes) {
       clienteData = listaClientes;
    }
    
    
    /**
     * Setea lista observable de clientes 
     * @return
     */
    public void setClienteDataDB() {
    	clienteData = FXCollections.observableArrayList(DBMotor.buscarCliente(""));
    }
    
    /**
     * Retorna la informacion de presupuestos no efectivos como una lista observable de Clientes. 
     * @return
     */
    public ObservableList<Presupuesto> getPresupuestosNoEfectivosData() {
        return presupuestosNoEfectivosData;
    }
    
    /**
     * Setea lista observable de presupuestos no efectivos 
     * @return
     */
    public void setPresupuestosNoEfectivosData(ObservableList<Presupuesto> listaPresupuestos) {
       presupuestosNoEfectivosData = listaPresupuestos;
    }
    
    /**
     * Setea lista observable de presupuestos no efectivos desde la DB
     * @return
     */
    public void setPresupuestosNoEfectivosData_DB() {
    	presupuestosNoEfectivosData = FXCollections.observableArrayList(DBMotor.obtenerPresupuestosNoEfectivos());
    	
    }
    
    /**
     * Retorna la informacion de cuentas corrientes como una lista observable.
     * @return
     */
    public ObservableList<CuentaCorriente> getCuentasCorrientesData() {
        return cuentasCorrientesData;
    }
    
    /**
     * Setea lista observable de cuentas corrientes
     * @return
     */
    public void setCuentasCorrientesData(ObservableList<CuentaCorriente> listaCuentas) {
       cuentasCorrientesData = listaCuentas;
    }
    
    /**
     * Setea lista observable de presupuestos no efectivos desde la DB
     * @return
     */
    public void setCuentasCorrientesData_DB() {
    	cuentasCorrientesData = FXCollections.observableArrayList(DBMotor.getCuentasCorrientesHabilitados());
    }
    
     /**
     * Disparador de la aplicación para el ejecutable, propio de JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Inicia la aplicación
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Maisonnave Hermosilla - Estudio Contable ");
        this.primaryStage.setMaximized(true);
        this.primaryStage.getIcons().add(new Image("file:Resources/Images/iconito.png"));
      
        initRootLayout();
        
    }

    /**
     * Inicializa el Panel Raiz
     */
    public void initRootLayout() {
        try {
            // Carga el Panel Raiz desde el archivo .fxml correspondiente
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/PanelRaiz.fxml"));
            rootLayout = (BorderPane) loader.load();
        
            // Brinda acceso a la apilcaciòn principal al controlador particular de la vista
            PanelRaizController controller = loader.getController();
            controller.setMainApp(this);
            
            // Muestra la scene conteniendo el Panel Raiz
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //---------------------CLIENTES-------------------------------------------

    /**
     * Muestra la vista ClienteVista dentro del Panel Raiz
     */
    public void showClienteVista() {
        try {
            // Carga la vista ClienteVista desde el archivo .fxml correspondiente
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/ClienteVista.fxml"));
            AnchorPane clienteVista = (AnchorPane) loader.load();

            // Setea la vista en el centro del Panel Raiz
            rootLayout.setCenter(clienteVista);

            // Brinda acceso a la apilcaciòn principal al controlador particular de la vista
            ClienteOverviewController controller = loader.getController();
            controller.setMainApp(this, DBMotor);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra la vista ModificarClienteOverview dentro del Panel Raiz
     */
    public boolean showModificarClienteOverview(Cliente cliente) {
        try {
            // Carga el archivo .fxml y crea un nuevo stage para el diálogo pop-up
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/ModificarClienteOverview.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Crea el Stage para el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar cliente");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Setear los parametros del controlador       
            ModificarClienteOverviewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCliente(cliente);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Muestra la vista NuevoClienteOverview dentro del Panel Raiz
     */
    public boolean showNuevoClienteOverview(Cliente cliente) {
        try {
            // Carga el archivo .fxml y crea un nuevo stage para el diálogo pop-up
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/NuevoClienteOverview.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Crea el Stage para el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nuevo cliente");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Setear los parametros del controlador       
            NuevoClienteOverviewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCliente(cliente);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //---------------------PRESUPUESTOS-------------------------------------------------
    
    /**
     * Muestra la vista AreaDeTrabajoVista dentro del Panel Raiz
     */
    
    public void showAreaDeTrabajoVista(){
    	try {
            // Carga la vista AreaDeTrabajoVista desde el archivo .fxml correspondiente
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/AreaDeTrabajoVista.fxml"));
            AnchorPane areaTrabajoVista = (AnchorPane) loader.load();
            this.setPresupuestosNoEfectivosData_DB();

            // Setea la vista en el centro del Panel Raiz
            rootLayout.setCenter(areaTrabajoVista);

            // Brinda acceso a la apilcaciòn principal al controlador particular de la vista
            AreaDeTrabajoOverviewController controller = loader.getController();
            controller.setMainApp(this, DBMotor);

        } catch (IOException e) {
            e.printStackTrace();
        }    	
    	
    }
    
    
    /** 
     * Muestra al usuario para modificar un presupuesto determinado. 
     * Si el método es invocado desde la clase Main (en el curso de confección
     * de un nuevo presupuesto individual), le da opción al usuario de efectivizar
     * el presupuesto indicado.
     * 
     */
    public boolean showModificarPresupuestoOverview(Presupuesto presupuesto, Object propietario){
    	try {
            // Carga el archivo .fxml y crea un nuevo stage para el diálogo pop-up
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/ModificarPresupuestoOverview.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Crea el Stage para el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar presupuesto");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Setear los parametros del controlador       
            ModificarPresupuestoOverviewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setmainApp(this);
            controller.setPresupuesto(presupuesto);
            controller.setPropietario(propietario);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Muestra un dialogo para elegir a que cliente realizarle el presupuesto
     * y luego la vista para editar dicho presupuesto. Por último, consulta
     * al usuario por si quiere efectivizar el presupuesto.
     * 
     */
    public void handleNuevoPresupuestoVista(){
    	
    	//Primero instancio al cliente cuyo presupuesto se fabricará
    	Cliente cliente;
    	
    	AnchorPane pantallaGris= new AnchorPane();

        // Setea la vista en el centro del Panel Raiz
        rootLayout.setCenter(pantallaGris);
    	
    	//Creo un diálogo con una lista de clientes ordenados por denominación
    	//para seleccionar el cliente
    	Dialog<ButtonType> dialog = new Dialog<>();
    	dialog.setTitle("Nuevo presupuesto");
    	dialog.setHeaderText(null);
    	dialog.setResizable(false); 

    	Label label1 = new Label("Seleccione un cliente de la lista:");
    	
    	//Creo la lista de clientes
    	ChoiceBox<String> clientesChoiceBox = new ChoiceBox<String>();
    	ObservableList<String> listaDenominaciones = FXCollections.observableArrayList();
    	ObservableList<String> listaMostrable =  FXCollections.observableArrayList();
    	List<Cliente> unaListita = this.getClientesHabilitados();
    	for(Cliente c : unaListita ){
    		listaDenominaciones.add(c.getDenominacion());
    		listaMostrable.add(c.getDenominacion());
    	}
    	
    	java.util.Collections.sort(listaMostrable);
    	clientesChoiceBox.setItems(listaMostrable);
    	
    	//Seteo valor por defecto del choice box
    	clientesChoiceBox.getSelectionModel().selectFirst();
    	
    	//Creo la grilla de componentes para el dialogo    	
    	GridPane grid = new GridPane();
    	grid.add(label1, 1, 1);
    	grid.add(clientesChoiceBox, 1, 2);
    	dialog.getDialogPane().setContent(grid);
    	
    			
    	//genero los dos botones : generar presupuesto (OK) y cancelar.
    	ButtonType buttonTypeOk = new ButtonType("Generar presupuesto", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
    	
    	ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
    	dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

    	
    	dialog.getDialogPane().setMinWidth(390);
    	
    	Optional<ButtonType> result = dialog.showAndWait();
    	
    	//Si el usuario elige Generar Presupuesto
    	if (result.get() == buttonTypeOk) {
    		
    		//Seteo al cliente
    		int indice = clientesChoiceBox.getItems().indexOf(clientesChoiceBox.getValue());
    		String nombre = listaMostrable.get(indice);
    		int indiceverdadero = listaDenominaciones.indexOf(nombre);
    		
    		cliente = unaListita.get(indiceverdadero);
                		
    		//Chequeo que el cliente no sea nulo y este habilitado
    		if (cliente != null || cliente.getHabilitado().equals("N")){
    			
    			//antes que nada veo que no haya ya un presupuesto no efectivo para ese cliente
    			if(DBMotor.verPresupuestosNoEfectivosPorDenominacion(cliente.getDenominacion()).size()==0){
    			
    				//genero un presupuesto nuevo para ese cliente por medio del motor de DB
    				Presupuesto presupuesto = null;
    				try {
    					presupuesto = DBMotor.facturarBorrador(cliente);
    				} catch (InvalidClientException e) {
    					System.out.println("falló el facturar borrador: estoy en main");
    					e.printStackTrace();
    				}
    				//con ese presupuesto voy a la vista de modificar presupuesto
    				this.showModificarPresupuestoOverview(presupuesto, this);
    			}
    			//Si habia ya un presupuesto no efectivo para el cliente informo al usuario
    			else{
    				 Alert alert = new Alert(AlertType.WARNING, 
    	 		  			 "",
    	                    ButtonType.OK);
    	              alert.initOwner(this.getPrimaryStage());
    	              alert.setTitle("Nuevo presupuesto");
    	              alert.setHeaderText(null);
    	              alert.setContentText("Ya existe un presupuesto no efectivo para el cliente "+cliente.getDenominacion()+".\n\nPuede editar dicho presupuesto en el menú Área de Trabajo.");
    	              alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    	              alert.showAndWait();
    			}
    		}
    		//Si el cliente es nulo:
    		else{
    			System.out.println("cliente nulo en nuevo presupuesto");
    			//por defecto ya tiene un cliente cargado al arranque, 
    			//entonces no deberia jamas poder ser nulo y nunca deberia entrar aca
    		}
    	}
    	else{
    		//apretó cancelar, no hago nada y cierro
    	}
    }
  
    
    /**
     * 
     * 
     */
    public void handleVerPresupuestoVista(){
    	try {
            // Carga la vista BuscarPresupuestosVista desde el archivo .fxml correspondiente
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/VerPresupuestosVista.fxml"));
            AnchorPane verPrespuestosVista = (AnchorPane) loader.load();

            // Setea la vista en el centro del Panel Raiz
            rootLayout.setCenter(verPrespuestosVista);

            // Brinda acceso a la apilcaciòn principal al controlador particular de la vista
             VerPresupuestosOverviewController controller = loader.getController();
            controller.setMainApp(this, DBMotor);

        } catch (IOException e) {
            e.printStackTrace();
        }  
    }
    
    public void showDetallePresupuestoVista(Presupuesto presupuesto){
    	try {
            // Carga el archivo .fxml y crea un nuevo stage para el diálogo pop-up
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/DetallePresupuestoOverview.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Crea el Stage para el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Detalle de Presupuesto");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page); 
            dialogStage.setScene(scene);

            // Setear los parametros del controlador       
            DetallePresupuestoOverviewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPresupuesto(presupuesto);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            //return controller.isOkClicked();
        	} 
    		catch (IOException e) {
    			e.printStackTrace();
    			//return false;
    		}
    }
   
    /**
     * 
     * 
     */
    
    public void showDetalleCuentaCorrienteOverview(){
    	try {
            // Carga el archivo .fxml y crea un nuevo stage para el diálogo pop-up
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/DetalleCuentaCorrienteOverview.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            /*
            AnchorPane pantallaGris= new AnchorPane();

            // Setea la vista en el centro del Panel Raiz
            rootLayout.setCenter(pantallaGris);
             */
            // Crea el Stage para el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Detalle de Cuentas Corrientes");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setMaximized(false);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Setear los parametros del controlador       
            DetalleCuentaCorrienteOverviewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);
            controller.arranque();

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showRegistrarPagoOverview(){
    	/*
    	AnchorPane pantallaGris= new AnchorPane();

        // Setea la vista en el centro del Panel Raiz
        rootLayout.setCenter(pantallaGris);*/
    	
    	try {
            // Carga el archivo .fxml y crea un nuevo stage para el diálogo pop-up
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RegistrarPagoOverview.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Crea el Stage para el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Registrar Pago");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setMaximized(false);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Setear los parametros del controlador       
            RegistrarPagoOverviewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);
            controller.arranque();

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}