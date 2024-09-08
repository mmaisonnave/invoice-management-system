package controller.view;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.application.Platform;

import java.util.Optional;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.Thread;

import controller.Main;
import controller.db.Cliente;
import controller.db.Concepto;
import controller.db.DBEngine;
import controller.db.Presupuesto;
import controller.reports.ReportsEngine;
import exception.InvalidBudgetException;

public class AreaDeTrabajoOverviewController  {

	//Lista de presupuestos
    @FXML
    private TableView<Presupuesto> presupuestosTable;
    @FXML
    private TableColumn<Presupuesto, Number> numeroColumn;
    @FXML
    private TableColumn<Presupuesto, String> cuitColumn;
    @FXML
    private TableColumn<Presupuesto, String> denominacionColumn;

    //Lista de Conceptos
    @FXML
    private TableView<Concepto> conceptosTable;
    @FXML
    private TableColumn<Concepto, String> conceptoColumn;
    @FXML
    private TableColumn<Concepto, String> montoConceptoColumn;

    @FXML
    private Label numeroLabel;
    @FXML
    private Label cuitLabel;
    @FXML
    private Label denominacionLabel;
    @FXML
    private Label ivaLabel;
    @FXML
    private Label alicuotaLabel;
    @FXML
    private Label montoLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label mesLabel;
    @FXML
    private RadioButton cuitRadioButton;
    @FXML
    private RadioButton denominacionRadioButton;
    @FXML
    private Button editar;
    @FXML
    private Button efectivizarUno;
    @FXML
    private Button efectivizarTodos;
    @FXML
    private Button vistaPrevia;
    @FXML
    private Button descartarTodos;
    @FXML
    private Button descartarUno;
    @FXML
    private Button generarPresupuestosMensuales;
    @FXML
    private Button aumento;
    
    @FXML
    private Label conteoLabel;
    
    
    //Usados solamente en el metodo: efectivizar todos
    @FXML
    private ProgressBar barraProgreso;
    
    
    //Variables booleanas de control
    private boolean cuitPresionado = false;
    private boolean denomPresionado = true;
    
    @FXML
    private TextField busquedaTextField;

    //Referencia a la aplicación principal.
    private Main mainApp;
    
    //Motor de la base de datos.
    private DBEngine DBMotor;

    /**
     * Constructor llamado antes del metodo initialize().
     */
    public AreaDeTrabajoOverviewController() {
    }
    
    /**
     * Completa los labels informativos) para mostrar detalles del presupuesto seleccionado.
     * Si no se ha seleccionado ningun presupuesto (presupuesto null), todos los labels son clareados.
     * 
     * @param presupuesto o null
     * 
     */
    private void showPresupuestoDetails(Presupuesto presupuesto) {
        if (presupuesto != null) {
        	
        	Cliente cliente= presupuesto.getCliente();
        	
            // Completa los labels con info de la instancia presupuesto.

            numeroLabel.setText(String.valueOf(presupuesto.getNroPresupuesto()));
            cuitLabel.setText(cliente.getCuit());
            denominacionLabel.setText(cliente.getDenominacion());
                       
            String ci = cliente.getCondicionIva();
            if(ci.startsWith("RI")){
            	ivaLabel.setText("Responsable Inscripto");
            }
            else if(ci.startsWith("EX")){
            	ivaLabel.setText("Exento");
            }
            else if(ci.startsWith("MO")){
            	ivaLabel.setText("Monotributista");
            }
            else if(ci.startsWith("NR")){
            	ivaLabel.setText("No Responsable");
            }
            else if(ci.startsWith("CF")){
            	ivaLabel.setText("Consumidor Final");
            }
            else{
            	ivaLabel.setText("Sin información");
            }
            
            
            Float ali = presupuesto.getAlicuota();
            if(ali!= null){
            	if(ali == 0.0){
            		alicuotaLabel.setText("0%");
                }
                else if(ali == 10.5){
                	alicuotaLabel.setText("10,5%");
                }
                else if(ali == 21.0){
                	alicuotaLabel.setText("21%");
                }
                else{
                	alicuotaLabel.setText("");
                }
            }
            
            subtotalLabel.setText(String.valueOf(presupuesto.getSubtotal()));
            montoLabel.setText(String.valueOf(presupuesto.calcularMontoTotal()));
          
            conceptosTable.setItems(presupuesto.getConceptosObservables());
            conceptoColumn.setCellValueFactory(
    			cellData -> cellData.getValue().getConceptoProperty());
            
    		montoConceptoColumn.setCellValueFactory(
    			cellData -> cellData.getValue().getMontoConceptoStringProperty());
            
    		mesLabel.setText(presupuesto.getMesFormateado());

        } else {
        	
            // Presupuesto nulo, clarear todas las etiquetas.
        	numeroLabel.setText("");
            cuitLabel.setText("");
            denominacionLabel.setText("");
            ivaLabel.setText("");
            alicuotaLabel.setText("");
            subtotalLabel.setText("");
            montoLabel.setText("");
            conceptosTable.setItems(null);
            mesLabel.setText("");
            
        }
        
    }
  
    /**
     * Inicializa la clase controller. Este metodo es automaticamente llamado
     * luego de que fue cargado el archivo fxml. Usa expresiones lambda.
     */
    @FXML
    private void initialize() {
        // Inicializa la tabla de presupuestos con los valores de las 3 columnas.
    	numeroColumn.setCellValueFactory(
    			cellData -> cellData.getValue().NroPresupuestoProperty());
        cuitColumn.setCellValueFactory(
                cellData -> cellData.getValue().getCliente().cuitProperty());
        denominacionColumn.setCellValueFactory(
                cellData -> cellData.getValue().getCliente().denominacionProperty());

        //Borrar detalles de Presupuesto
        showPresupuestoDetails(null);
        
        // Se queda escuchando por cambios en seleccion de presupuesto en la tabla
        // y muestra los detalles del presupuesto seleccionado.
        presupuestosTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPresupuestoDetails(newValue));
        presupuestosTable.setPlaceholder(new Label("No hay presupuestos para mostrar."));
        presupuestosTable.getColumns().forEach(this::addTooltipToColumnCells_Presupuesto);
        
        conceptosTable.getColumns().forEach(this::addTooltipToColumnCells_Concepto);
        conceptosTable.setPlaceholder(new Label("No hay conceptos."));
    	
        //generarPresupuestosMensuales.disableProperty().bind(Bindings.size(presupuestosTable.getItems()).isEqualTo(0));
        
        montoConceptoColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");

        ContextMenu cm_conceptos = new ContextMenu();
        MenuItem mi_copiar = new MenuItem("Copiar descripción");
        cm_conceptos.getItems().add(mi_copiar);

        conceptosTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    cm_conceptos.show(conceptosTable, t.getScreenX(), t.getScreenY());
                }
                if(t.getButton() == MouseButton.PRIMARY) {
                    cm_conceptos.hide();
                }
            }
        });
        
        
        mi_copiar.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	 final Clipboard clipboard = Clipboard.getSystemClipboard();
                 final ClipboardContent content = new ClipboardContent();
                 content.putString(conceptosTable.getSelectionModel().getSelectedItem().getConcepto());
                 clipboard.setContent(content);
            }
        });
        
    }
    
    /**
     * Llamado por la aplicacion principal para dar una autorreferencia
     * 
     * @param mainApp
     */
    public void setMainApp(Main mainApp, DBEngine motor) {
        this.mainApp = mainApp;
        this.DBMotor = motor;

        //Agrega el contenido de la lista observable a la tabla
        presupuestosTable.setItems(mainApp.getPresupuestosNoEfectivosData());
        conteoLabel.setText(String.valueOf(presupuestosTable.getItems().size() ) + " presupuestos.");
    }
    
    //-------------------------------BOTONES----------------------------------
    
    /**
      * LLamado cuando el usuario hace click en el boton Nuevo. Abre un 
      * diálogo para crear un nuevo cliente.
      * 
      */
     @FXML
     private void handleEfectivizarUno() {
    	 Presupuesto selectedPresupuesto = presupuestosTable.getSelectionModel().getSelectedItem();
         if (selectedPresupuesto != null) {
         	  // Se procede a alertar al usuario
              Alert alert = new Alert(AlertType.CONFIRMATION, 
 		  			 "",
                    ButtonType.YES, 
                    ButtonType.NO);
              alert.initOwner(mainApp.getPrimaryStage());
              alert.setTitle("Efectivizar presupuesto");
              alert.setHeaderText("Presupuesto seleccionado: "+ selectedPresupuesto.getNroPresupuesto()+ " - "
            		  			  + selectedPresupuesto.getCliente().getDenominacion());
              alert.setContentText("¿Desea efectivizar el presupuesto seleccionado?");
              alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
              Optional<ButtonType> result = alert.showAndWait();

              if (result.get() == ButtonType.YES) {
             	 //Efectivizo el presupuesto en la base de datos
             	 try{
             		 DBMotor.efectivizarPresupuesto(selectedPresupuesto);
                     
             	 }
             	 catch(InvalidBudgetException e){
             		e.printStackTrace();
             		System.out.println("La efectivización del presupuesto "+ selectedPresupuesto.getNroPresupuesto() + " tiro error.");
             	 }
             	 //Actualizo contenido tabla en la vista
             	 handleSearch();
             	 //Se informa al usuario que termino el proceso
                  alert = new Alert(AlertType.INFORMATION, 
     		  			 "",
                        ButtonType.OK);
                  alert.initOwner(mainApp.getPrimaryStage());
                  alert.setTitle("Efectivizar presupuesto");
                  alert.setHeaderText(null);
                  alert.setContentText("Se ha efectivizado el presupuesto nº "+ selectedPresupuesto.NroPresupuestoStringProperty().get());
                  alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                  alert.showAndWait();
              }
              else{
             	 //No hago nada y cierro
              }
    	 } 
         else {
             // No se seleccionó ningún cliente
             Alert alert = new Alert(AlertType.WARNING);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("Seleccionar presupuesto");
             alert.setHeaderText(null);
             alert.setContentText("No se ha seleccionado un presupuesto de la lista.");
             alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
             alert.showAndWait();
         }
     }

     /**
      * LLamado cuando el usuario hace click en el boton Efectivizar Todos.
      * Abre un alerta donde dice cuantos presupuestos se harán efectivos, y pide confirmación.
      * 
      */
     @FXML
     private void handleEfectivizarTodos() {

        
    	 //Primero recupero la lista de presupuestos no efectivos desde la DB
    	 mainApp.setPresupuestosNoEfectivosData_DB();
    	 ObservableList<Presupuesto> lista = mainApp.getPresupuestosNoEfectivosData();
    	 
    	 //Luego chequeo que haya algo en la lista
    	 if (!lista.isEmpty()){
    		 //Hay algo en la lista, luego hay que efectivizarlo
         	 // Se procede a alertar al usuario
             Alert alert = new Alert(AlertType.CONFIRMATION, 
 		  			 "",
                    ButtonType.YES, 
                    ButtonType.NO);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("Efectivizar todo");
             alert.setHeaderText("Cantidad total de presupuestos: " + lista.size());
             alert.setContentText("¿Desea efectivizar TODOS los presupuestos no efectivos?");
             alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
             Optional<ButtonType> result = alert.showAndWait();

             
             if (result.get() == ButtonType.YES) {
            	
            	 //Efectivizo TODOS los presupuestos en la base de datos.
            	 
              	Task<Void> task_efectivizar = new Task<Void>() {
         		    @Override
         		    protected Void call() throws Exception {
         		    	
         		    	//Efectivizo cada item de la lista
                        for (Presupuesto p : lista){
        		    		try{
        		    			
                   			 	DBMotor.efectivizarPresupuesto(p);
                   			 	
        		    		}
                        	catch(InvalidBudgetException e){
                        		e.printStackTrace();
                        		System.out.println("La efectivización del presupuesto "+ p.getNroPresupuesto() + " tiro error.");
                        	}
        		    	}
                        
                        //Vuelvo a habilitar componentes de la ventana.
                        deshabilitarComponentes(false);
                        try{           
                        	
                        	//Actualizo tabla y muestro alerta de finalizacion
                        	Platform.runLater(() -> {
         		    		
                        		//Actualizo contenido tabla en la vista: deberia no retornar nada
                            	clarearTablaYLista();
                            	
              		           barraProgreso.progressProperty().unbind();
              		           barraProgreso.progressProperty().set(0);
                         
         		    		//Se informa al usuario que termino el proceso
         		           Alert alert = new Alert(AlertType.INFORMATION, 
         		  	  			 "",
         		                 ButtonType.OK);
         		           alert.initOwner(mainApp.getPrimaryStage());
         		           alert.setTitle("Efectivizar todo");
         		           alert.setHeaderText(null);
         		           alert.setContentText("Se han efectivizado "+ lista.size() + " presupuestos.\n\nPuede consultar los presupuestos efectivos en el menú Buscar Presupuestos.");
         		           alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
         		           alert.showAndWait();
         		           
                         });
         		    	 }
                         catch(IllegalStateException e){
                        	System.out.println("El problema esta en el Efectivizar todos: illegal state exception");
                         }
         		    	return null;
         		    };
         	 };
            		
            	//Bindea la barra de progreso con la property de presupuestos efectivizados so far
                barraProgreso.progressProperty().unbind();
                barraProgreso.progressProperty().bind(task_efectivizar.workDoneProperty());
              
                //Primero pongo como disabled los elementos graficos de la 
 		    	//ventana: botones, lista, etc.
 		    	deshabilitarComponentes(true);
                
 		    	//Arranco la efectivizacion de todos los presupuestos
                new Thread(task_efectivizar).start();
               
             }
             else{
            	 //No hago nada: el usuario eligió el botón NO.
              }
    	 } 
         else {
        	 //No hay presupuesto no efectivos: la lista del area de trabajo debería estar vacía,
        	 //Se debe informar al usuario que no hay nada para efectivizar.
             Alert alert = new Alert(AlertType.INFORMATION);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("Efectivizar todo");
             alert.setHeaderText(null);
             alert.setContentText("No existen presupuestos no efectivos.");
             alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
             alert.showAndWait();
         }
     }
     
     
     /**
      * LLamado cuando el usuario hace click en el boton Editar. Abre un 
      * diálogo para editar el presupuesto seleccionado.
      * 
      * (tal vez debo solo mostrar los resultados, al igual que en la vista cliente. en ese caso descomentar)
      */
     @FXML
     private void handleEditarPresupuesto() {
         Presupuesto selectedPresupuesto = presupuestosTable.getSelectionModel().getSelectedItem();
         if (selectedPresupuesto != null) {
             boolean okClicked = mainApp.showModificarPresupuestoOverview(selectedPresupuesto, this);
             if (okClicked) {
                 showPresupuestoDetails(selectedPresupuesto);
             }
         } 
         else{
             // Nothing selected.
             Alert alert = new Alert(AlertType.WARNING);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("Seleccionar presupuesto");
             alert.setHeaderText(null);
             alert.setContentText("No se ha seleccionado un presupuesto de la lista.");
             alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
             alert.showAndWait();
         }
     }
     
     /**
      * LLamado cuando el usuario hace click en el boton Vista Previa. Abre un 
      * diálogo para mostrar la vista previa imprimible del presupuesto.
      * TODO
      */
    @FXML
      private void handleVistaPrevia() {

    	Presupuesto p = presupuestosTable.getSelectionModel().getSelectedItem();
    	
    	if(p!= null){
    	String filename = ReportsEngine.generarReporteBorrador(p);
    	
    	File f = new File(filename);
    	
    	//Thread para abrir el Okular o el Adobe
    	Task<Void> task = new Task<Void>() {
		    @Override
		    protected Void call() throws Exception {
		    	
		    	//Llamo a la aplicacion por defecto
		    	Desktop escritorio = Desktop.getDesktop();
		    	
		    	//Abro el pdf en la aplicacion por defecto
		    	try {
		    		if(f.exists()) 
		    			escritorio.open(f);
				} catch (IOException e) {
					System.out.println("error al abrir el pdf para vista previa");
					e.printStackTrace();
				}
		    	
		    	//TODO: Ver como y cuando borrar el archivo!
		    	
		    	return null;
		    };
    	};
	 
    	//Inicio trabajo del thread
    	new Thread(task).start();
    	}
    	else{
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.initOwner(mainApp.getPrimaryStage());
    		alert.setTitle("Seleccionar presupuesto");
    		alert.setHeaderText(null);
    		alert.setContentText("No ha seleccionado ningún presupuesto de la lista.");
    		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    		alert.showAndWait();
    	}
    	
     }
    
    //------------------------BUSQUEDA-----------------------------------

     /**
      * LLamado cuando el usuario tipea en la caja de texto de busqueda.
      * Actualiza la lista de presupuestos segun se haya seleccionado cuit o denominacion
      * en los radio buttons.
      * 
      */
     @FXML
     private void handleSearch() {

 		String busqueda = busquedaTextField.getText();
 		
     	if(cuitPresionado){
     		ObservableList<Presupuesto> listaPresupuestos = FXCollections.observableArrayList(DBMotor.verPresupuestosNoEfectivosPorCuit(busqueda));
     		mainApp.setPresupuestosNoEfectivosData(listaPresupuestos);
     	}
     	if(denomPresionado){
     		ObservableList<Presupuesto> listaPresupuestos = FXCollections.observableArrayList(DBMotor.verPresupuestosNoEfectivosPorDenominacion(busqueda));
     		mainApp.setPresupuestosNoEfectivosData(listaPresupuestos);
     	}
     	presupuestosTable.setItems(mainApp.getPresupuestosNoEfectivosData());
    
        conteoLabel.setText(String.valueOf(presupuestosTable.getItems().size() ) + " presupuestos.");

     }
     
     private void clarearTablaYLista(){
    	 ObservableList<Presupuesto> listaPresupuestos = FXCollections.observableArrayList(DBMotor.obtenerPresupuestosNoEfectivos());
    	 mainApp.setPresupuestosNoEfectivosData(listaPresupuestos);
    	 presupuestosTable.setItems(mainApp.getPresupuestosNoEfectivosData());
         conteoLabel.setText(String.valueOf(presupuestosTable.getItems().size() ) + " presupuestos.");

     }
     
     /**
     * LLamado cuando el usuario selecciona el radiobutton cuit
     */
     @FXML
     private void handleCuitRadioButton() {
 	   cuitPresionado = true;
 	   denomPresionado = false;
    }
    
    /**
     * LLamado cuando el usuario selecciona el radiobutton cuit
     */
     @FXML
     private void handleDenomRadioButton() {
 	   cuitPresionado = false;
 	   denomPresionado = true;
    }
     
     //-----------------------------OTROS----------------------------------
   
     private void deshabilitarComponentes(boolean value){
    	 
    	//Tablas
    	this.conceptosTable.setDisable(value);
    	this.presupuestosTable.setDisable(value);
    	
    	//Botones
    	this.editar.setDisable(value);
    	this.vistaPrevia.setDisable(value);    
    	this.efectivizarTodos.setDisable(value);
    	this.efectivizarUno.setDisable(value);
    	this.descartarTodos.setDisable(value);
    	this.descartarUno.setDisable(value);
    	this.generarPresupuestosMensuales.setDisable(value); //TODO: este se configura aparte
    	this.aumento.setDisable(value);
    	
    	//Radiobutton
    	this.cuitRadioButton.setDisable(value);
    	this.denominacionRadioButton.setDisable(value);
    	
    	//Textfield
    	this.busquedaTextField.setDisable(value);
    	    	
    	//Label
    	this.alicuotaLabel.setDisable(value);
    	this.cuitLabel.setDisable(value);
    	this.denominacionLabel.setDisable(value);
    	this.ivaLabel.setDisable(value);
    	this.subtotalLabel.setDisable(value);
    	this.montoLabel.setDisable(value);
    	this.numeroLabel.setDisable(value);
    	
    	
    }
     
     private <T> void addTooltipToColumnCells_Concepto(TableColumn<Concepto,T> column) {

    	    Callback<TableColumn<Concepto, T>, TableCell<Concepto,T>> existingCellFactory 
    	        = column.getCellFactory();

    	    column.setCellFactory(c -> {
    	        TableCell<Concepto, T> cell = existingCellFactory.call(c);

    	        Tooltip tooltip = new Tooltip();
    	        // can use arbitrary binding here to make text depend on cell
    	        // in any way you need:
    	        tooltip.textProperty().bind(cell.itemProperty().asString());

    	        cell.setTooltip(tooltip);
    	        return cell;
    	    });
    	}
     

     private <T> void addTooltipToColumnCells_Presupuesto(TableColumn<Presupuesto,T> column) {

 	    Callback<TableColumn<Presupuesto, T>, TableCell<Presupuesto,T>> existingCellFactory 
 	        = column.getCellFactory();

 	    column.setCellFactory(c -> {
 	        TableCell<Presupuesto, T> cell = existingCellFactory.call(c);

 	        Tooltip tooltip = new Tooltip();
 	        // can use arbitrary binding here to make text depend on cell
 	        // in any way you need:
 	        tooltip.textProperty().bind(cell.itemProperty().asString());
 	       
 	        cell.setTooltip(tooltip);
 	        return cell;
 	    });
 	}
     
     /**
      * Permite al usuario realizar la facturación en borrador para todo los clientes
      * habilitados, si y solo si no existe actualmente ningún presupuesto no efectivo 
      * en la DB.
      * 
      */
     @FXML
     private void handleGenerarPresupuestosMensuales(){
     	
     	//Primero, pregunto si ya hay no efectivos en la DB
     	boolean noHay = (DBMotor.obtenerPresupuestosNoEfectivos().size() == 0);
     	//Si no hay no efectivos, procedo con el método.
     	if (noHay){
     		 //Primero averiguo cuantos presupuestos debo generar
    		int cantidad = DBMotor.cantidadClientesHabilitados();
    		
     		//Creo una alerta para pedir confirmacion
     		Alert alert = new Alert(AlertType.CONFIRMATION, 
 		  			 "",
                    ButtonType.OK,
                    ButtonType.CANCEL);
              alert.initOwner(mainApp.getPrimaryStage());
              alert.setTitle("Generar presupuestos mensuales");
              alert.setHeaderText(null);
              alert.setContentText("¿Desea generar "+cantidad+" nuevos presupuestos para los clientes habilitados?");
              alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
              Optional<ButtonType> result = alert.showAndWait();
              
           
              	//Si el usuario elige Generar Presupuestos mensuales = OK
          	 	if (result.get() == ButtonType.OK) {
          		 
          	 	//Deshabilito componentes
            	deshabilitarComponentes(true);
            		
          		//Generar alert con la barra de progreso 
          		//Creo la barra de progreso
           		
           		barraProgreso.progressProperty().unbind();
           		barraProgreso.progressProperty().set(0);
           		
           		//Creo el dialogo que la va a mostrar
           		Alert dialog = new Alert(AlertType.INFORMATION);
              	dialog.setTitle("Generando "+cantidad+" presupuestos mensuales...");
              	dialog.setHeaderText(null);
              	dialog.setContentText("Operación en proceso. Por favor, aguarde unos instantes.");
              	dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
              	dialog.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
              	dialog.show();
           		
              
              	//Creo el task para generar los borradores
              	Task<Void> task_borradores = new Task<Void>() {
         		    @Override
         		    protected Void call() throws Exception {
         		    	
         		    	
         		    	//Primero hago lo que debo
         		    	DBMotor.facturarTodos();
         		    	
           		    	Platform.runLater(() -> {
           		    		//Despues actualizo la vista
               		    	handleSearch();
                    		deshabilitarComponentes(false);
                    		dialog.close();
                    		barraProgreso.progressProperty().unbind();
                    		barraProgreso.progressProperty().set(0);
       		           
                    	});
          		    	
           		    	
         		    	return null;
         		    };
              	};//Fin task generar borradores
              	
              	 barraProgreso.progressProperty().unbind();
                 barraProgreso.progressProperty().bind(task_borradores.workDoneProperty());
               
                 //Arranco la efectivizacion de todos los presupuestos
                 new Thread(task_borradores).start();
                 
                 
          	 }
          	 //Si no, si el usuario elige Generar Presupuestos mensuales = CANCEL
          	 else{
          		 //No hacer nada.
          	 }
          	 
     	}
     	//sino, es decir, si SI hay no efectivos, aviso al usuario que no se puede proceder.
     	else{
     		Alert alert = new Alert(AlertType.WARNING, 
 		  			 "",
                   ButtonType.OK);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("Generar presupuestos mensuales");
             alert.setHeaderText("No es posible generar nuevos presupuestos mensuales.");
             alert.setContentText("Aún existen presupuestos no efectivos en la base de datos. \n\nPuede ver y editar dichos presupuestos en el menú Área de Trabajo.");
             alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
             alert.showAndWait();
     	}
     }
     
     @FXML
     private void handleDescartarUno(){
    	 Presupuesto selectedPresupuesto = presupuestosTable.getSelectionModel().getSelectedItem();
         
    	 if (selectedPresupuesto != null) {
    		 Alert alert = new Alert(AlertType.CONFIRMATION,
    				 "",
    				 ButtonType.OK,
    				 ButtonType.CANCEL);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("Descartar presupuesto");
             alert.setHeaderText(null);
             alert.setContentText("¿Desea descartar el presupuesto seleccionado?");
             alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
             Optional<ButtonType> result = alert.showAndWait();
             
             if(result.get().equals(ButtonType.OK)){
            	 //Eliminar el presupuesto de todos lados:
            	 //1: de la DB
            	 try {
					DBMotor.eliminarNoEfectivo(selectedPresupuesto);
				} catch (InvalidBudgetException e) {
					System.out.println("Error al descartar uno");
					e.printStackTrace();
				}
            	 //2: de la vista
            	 handleSearch();
            	 //3: de la lista en mainapp
            	 mainApp.setPresupuestosNoEfectivosData_DB();
             }
         } 
         else{
        	 // Nothing selected.
             Alert alert = new Alert(AlertType.WARNING);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("Seleccionar presupuesto");
             alert.setHeaderText(null);
             alert.setContentText("No se ha seleccionado un presupuesto de la lista.");
             alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
             alert.showAndWait();
         }
     }
     
     @FXML
     private void handleDescartarTodos(){
    	 if (presupuestosTable.getItems().size()>0) {
    		 Alert alert = new Alert(AlertType.CONFIRMATION,
    				 "",
    				 ButtonType.OK,
    				 ButtonType.CANCEL);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("Descartar todos");
             alert.setHeaderText(null);
             alert.setContentText("¿Desea descartar los "+presupuestosTable.getItems().size() +" presupuestos de la lista?");
             alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
             Optional<ButtonType> result = alert.showAndWait();
             
             if(result.get().equals(ButtonType.OK)){
            	 //Eliminar el presupuesto de todos lados:
            	 
            	 //1: de la DB
            	 DBMotor.eliminarPresupuestosNoEfectivos();
				
            	 //2: de la vista
            	 handleSearch();
            	 
            	 //3: de la lista en mainapp
            	 mainApp.setPresupuestosNoEfectivosData_DB();
             }
         } 
         else{
        	 // Nothing selected.
             Alert alert = new Alert(AlertType.INFORMATION);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("Descartar todos");
             alert.setHeaderText(null);
             alert.setContentText("No existen presupuestos para descartar.");
             alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
             alert.showAndWait();
         }
     }
     
     @FXML
     private void handleAplicarAumento(){
    	 
    	Dialog<ButtonType> dialog = new Dialog<>();
     	dialog.setTitle("Aplicar aumento");
     	dialog.setHeaderText(null);
     	dialog.setResizable(false); 
     	
     	Label label1 = new Label("Coeficiente de aumento:");
     //	Label ayuda = new Label("");
     	TextField porcentaje = new TextField();
     	Label labelpor = new Label("  %");
     	CheckBox descuentoSiNo = new CheckBox();
     	descuentoSiNo.setText("Deshacer aumento previo");
     	
     	
     	//Validacion de campos textfield
     	porcentaje.textProperty().addListener((obs, oldText, newText) -> {
     		if (newText.matches("\\d*")) {
     			porcentaje.setText(newText);
     		}
     		else
     			porcentaje.setText(oldText);
    	 });
     	
     	final Tooltip tooltip = new Tooltip();
	     	tooltip.setText(
	     	    "Ingrese un valor\n" +
	     	    "entre 1 y 100.\n");
	     	porcentaje.setTooltip(tooltip);
     	
	    //Creo la grilla de componentes para el dialogo    	
    	GridPane grid = new GridPane();
    	grid.add(label1, 1, 1);
    	//grid.add(dummy, 1, 2);
    	grid.add(porcentaje, 1, 3);
    	//grid.add(dummy2, 1, 4);
    	grid.add(labelpor, 2, 3);
    	//grid.add(ayuda, 1, 5);
    	grid.add(descuentoSiNo, 1, 6);
    	dialog.getDialogPane().setContent(grid);
    	
    	//dialog.getDialogPane().setMinWidth(300);
    	
    	
    	//genero los dos botones : aplicar (OK) y cancelar.
    	ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
    	dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
    	
    	ButtonType buttonTypeOk = new ButtonType("Aplicar", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
   
    	Optional<ButtonType> result = dialog.showAndWait();
    
    	//Si el usuario elige Generar Presupuesto
    	if (result.get() == buttonTypeOk) {
    		
    		//NO VACIO
    		if (porcentaje.getText().length()<=0 || porcentaje.getText() == null || porcentaje.getText() ==""){
    			Alert alert = new Alert(AlertType.ERROR);
	            alert.initOwner(mainApp.getPrimaryStage());
	            alert.setHeaderText(null);
	            alert.setContentText("El campo de coeficiente es obligatorio.");
	            alert.showAndWait();
	            handleAplicarAumento();
    		}
    		
    		//NO mal valor
    		else if (Integer.parseInt(porcentaje.getText())>100  || Integer.parseInt(porcentaje.getText())< 1 ){
    			Alert alert = new Alert(AlertType.ERROR);
	            alert.initOwner(mainApp.getPrimaryStage());
	            alert.setHeaderText(null);
	            alert.setContentText("Ingrese un valor entre 1 y 100.");
	            alert.showAndWait();
	            handleAplicarAumento();
    		}
    		
    		//Todo ok
    		else{

				int porcentajeFinal = Integer.parseInt(porcentaje.getText());
				 
				//Pido confirmacion
				Alert alert = new Alert(AlertType.CONFIRMATION,"",ButtonType.YES, ButtonType.NO);
	            alert.initOwner(mainApp.getPrimaryStage());
	            if (descuentoSiNo.isSelected()){
	             		alert.setTitle("Aplicar descuento");
	             		alert.setContentText("¿Desea aplicar un descuento del "+porcentajeFinal+" % sobre los conceptos?\n\nEsta acción afectará a todos los presupuestos no efectivos. ");
	            }
	            else{
	             		alert.setTitle("Aplicar aumento");
	             		alert.setContentText("¿Desea aplicar un aumento del "+porcentajeFinal+" % sobre los conceptos?\n\nEsta acción afectará a todos los presupuestos no efectivos. ");
	            }
	            alert.setHeaderText(null);
	            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	            Optional<ButtonType> confirma = alert.showAndWait();
	             
	            if(confirma.get().equals(ButtonType.YES)){
	            	Task<Void> task = new Task<Void>() {
	         		    @Override
	         		    protected Void call() throws Exception {
	         		    	deshabilitarComponentes(true);
	         		    	return null;}};
	         		    	
	         		   Task<Void> task2 = new Task<Void>() {
	    	         		    @Override
	    	         		    protected Void call() throws Exception {
	    	         		    	
	    	         		    	

	    	   	            	 if (descuentoSiNo.isSelected()){
	    	   	            		DBMotor.deshacerAumentoNoEfectivos(porcentajeFinal);
	    	   	            	 }
	    	   	            	 else{
	    	   	            		 DBMotor.aplicarAumentoNoEfectivos(porcentajeFinal);
	    	   	            	 }
	    	   	            	 
	    	   	            	
	    	   	            	 
	    	   	            	Platform.runLater(() -> {
	             		    		
	    	   	            	 deshabilitarComponentes(false);
	    	   	            	 
	                         
	    	   	            	Alert alerta = new Alert(AlertType.INFORMATION);
	    		 	            alerta.initOwner(mainApp.getPrimaryStage());
	    		 	            alerta.setHeaderText(null);
	    		 	            alerta.setContentText("El procedimiento se ha realizado exitosamente.\nPuede continuar editando los presupuestos en el Área de Trabajo.");
	    		 	            alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

	    		 	            alerta.showAndWait();
	    		 	            
	    		 	            int index = presupuestosTable.getSelectionModel().getSelectedIndex();
	    		 	            
	    		 	            //Actualizo vista: todo como antes
	    		 	            handleSearch();
	    		 	            presupuestosTable.getSelectionModel().select(index);
	    		 	            showPresupuestoDetails( presupuestosTable.getSelectionModel().getSelectedItem());
	    		 	          
	         		           
	                         });
	    	         		    	
	    	         		    	
	    	         		    	return null;}};
	         		    	
	         		 new Thread(task).start(); 
	         		new Thread(task2).start();
	            	
	            	
	            }
	            else{
	             	handleAplicarAumento();
	            }
    		}
    	}
    	
    	//No hago nada y me voy
    	else{
    		
    	}
			
    }
    
     /*
     @FXML
     private void handleElegirMes(){
    	 
    	if(!mainApp.getPresupuestosNoEfectivosData().isEmpty()){
    	 
    		Dialog<ButtonType> dialog = new Dialog<>();
    		dialog.setTitle("Elegir mes:");
    		dialog.setHeaderText("Seleccione el mes al que corresponden los\npresupuestos en la lista");
    		dialog.setResizable(true);
    		
    		ComboBox<String> mesComboBox = new ComboBox<String>();
    		mesComboBox.setItems(FXCollections.observableArrayList(Presupuesto.getMeses()));
    		
    		//TODO: elegir el mes actual de los presupuestos
    		int mesesito = presupuestosTable.getItems().get(0).getMes();
    		mesComboBox.getSelectionModel().select(mesesito - 1);
    		
    		dialog.getDialogPane().setContent(mesComboBox);
     	
    		Platform.runLater(() -> mesComboBox.requestFocus());
     				
    		ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.OK_DONE);
    		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
     	
    		ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
    		dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
     	
    		Optional<ButtonType> result = dialog.showAndWait();
     	
    		if (result.get() == buttonTypeOk) {
    			Alert alert = new Alert(AlertType.CONFIRMATION, 
   		  			 "",
                     ButtonType.OK,
                     ButtonType.CANCEL);
               alert.initOwner(mainApp.getPrimaryStage());
               alert.setTitle("Elegir mes");
               alert.setHeaderText(null);
               alert.setContentText("¿Desea modificar el mes de todos\nlos presupuestos de la lista?");
               alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
               alert.showAndWait();
               
               Optional<ButtonType> result_alert = dialog.showAndWait();
               
               if(result_alert.get().equals(ButtonType.OK)){
            	   
            	   
            	   
               }
               
    		}
    		else{
    			//No hago nada
    		}
    	}
    	else{
    		Alert alert = new Alert(AlertType.WARNING, 
		  			 "",
                  ButtonType.OK);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Elegir mes");
            alert.setHeaderText(null);
            alert.setContentText("No hay presupuestos no efectivos en la lista.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
    	}
    	 
    	 
     }*/
    	
}
     