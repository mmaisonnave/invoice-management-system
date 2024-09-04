package controller.view;
 


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import controller.Main;
import controller.db.Cliente;
import controller.db.CuentaCorriente;
import controller.db.DBEngine;
import controller.db.DBSingleton;
import controller.db.Presupuesto;
import controller.db.Transaccion;
import controller.reports.ReportsEngine;


/**
 * Diálogo para editar los detalles de un cliente especificado.
 * 
 * @author Maria Virginia Sabando
 */
public class UltimosMovimientosOverviewController {
   
    @FXML
    private TableView<Transaccion> transaccionesTable;
    @FXML
    private TableColumn<Transaccion, String> fechaColumn;
    @FXML
    private TableColumn<Transaccion, String> eventoColumn;
    @FXML
    private TableColumn<Transaccion, String> importeColumn;
    @FXML
    private TableColumn<Transaccion, String> observacionColumn;
    @FXML
    private TableColumn<Transaccion, String> estadoColumn;
    
    @FXML
    private Label denominacionLabel;
    @FXML
    private Label cuitLabel;
    @FXML 
    private Label montoLabel;
    @FXML
    private Label fechaUltimoLabel;
   
 
    private Stage dialogStage;
    private DBEngine DBMotor=  DBSingleton.getInstance();
    private CuentaCorriente cuenta;
        

    /**
     * Inicializa la clase controlador. Este método es llamado automáticamente
     * luego de que el archivo .fxml ha sido cargado.
     */
    @FXML
    private void initialize() {
    	transaccionesTable.setPlaceholder(new Label("No hay transacciones para mostrar."));
    	  	  	
 		transaccionesTable.getColumns().forEach(this::addTooltipToColumnCells_Transaccion);
 		 		
 		importeColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");
 		
 		estadoColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");
 		
    }

    /**
     * Setea el stage para este diálogo
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    
    public void setCuentaCorriente(CuentaCorriente c){
    	this.cuenta=c;
    	if(cuenta!= null){
    		this.cuitLabel.setText(cuenta.getCliente().getCuit());
    		this.denominacionLabel.setText(cuenta.getCliente().getDenominacion());
    		
    		//Viene asi: yyyy-MM-dd
    		String s = DBMotor.fechaUltimoPago(cuenta.getCliente());
    		String ss = " ";
    		if(s!=null && s.length()==0){
    			//Lo quiero asi: dd/MM/yyyy
    			ss = s.substring(8,10)+"/"+s.substring(5,7)+"/"+s.substring(0,4);
    		}
    		this.fechaUltimoLabel.setText(ss); 
    		
    		this.montoLabel.setText(String.valueOf(cuenta.getEstadoCuentaCorriente()));
    		
    		ObservableList<Transaccion> listaTransacciones = FXCollections.observableArrayList(DBMotor.ultimosMovimientos(this.cuenta.getCliente()));
    	 	transaccionesTable.setItems(listaTransacciones);
    	 	
    	 	fechaColumn.setCellValueFactory(
    	 				cellData -> cellData.getValue().fechaARGProperty());
    	 	
    	 	    	 	
    	 	eventoColumn.setCellValueFactory(
    	 				cellData -> cellData.getValue().eventoProperty());
    			
    	 	importeColumn.setCellValueFactory(
     				cellData -> cellData.getValue().montoStringProperty());
    	 	observacionColumn.setCellValueFactory(
     				cellData -> cellData.getValue().observacionProperty());
    		
    	 	estadoColumn.setCellValueFactory(
    				cellData -> cellData.getValue().estadoStringProperty());
    	}
    	else{
    		this.cuitLabel.setText("");
    		this.denominacionLabel.setText("");
    		this.fechaUltimoLabel.setText("");
    		this.montoLabel.setText("");
    		transaccionesTable.setItems(null);
    	}
    }
    

    private <T> void addTooltipToColumnCells_Transaccion(TableColumn<Transaccion,T> column) {

	    Callback<TableColumn<Transaccion, T>, TableCell<Transaccion,T>> existingCellFactory 
	        = column.getCellFactory();

	    column.setCellFactory(c -> {
	        TableCell<Transaccion, T> cell = existingCellFactory.call(c);

	        Tooltip tooltip = new Tooltip();
	        // can use arbitrary binding here to make text depend on cell
	        // in any way you need:
	        tooltip.textProperty().bind(cell.itemProperty().asString());

	        cell.setTooltip(tooltip);
	        return cell;
	    });
	}
       
    /**
     * Llamado cuando el usuario presiona el botón OK: Guarda al cliente en la base de datos
     */
    @FXML
    private void handleAceptar() {
    	dialogStage.close();
    }
    
    @FXML
    private void handleVerPresupuestoAsociado(){
    	
    	if(transaccionesTable.getSelectionModel().getSelectedItem()!=null){
    		Transaccion T = transaccionesTable.getSelectionModel().getSelectedItem();
    		if(T.eventoProperty().get().equals("Presupuesto")){
    			Presupuesto p = DBMotor.getPresupuestoAsociado(transaccionesTable.getSelectionModel().getSelectedItem());
    	    	try {
    	            // Carga el archivo .fxml y crea un nuevo stage para el diálogo pop-up
    	            FXMLLoader loader = new FXMLLoader();
    	            loader.setLocation(Main.class.getResource("view/DetallePresupuestoOverview.fxml"));
    	            AnchorPane page = (AnchorPane) loader.load();

    	            // Crea el Stage para el diálogo
    	            Stage dialogStage = new Stage();
    	            dialogStage.setTitle("Detalle de Presupuesto");
    	            dialogStage.initModality(Modality.WINDOW_MODAL);
    	            dialogStage.initOwner(this.dialogStage);
    	            Scene scene = new Scene(page);
    	            dialogStage.setScene(scene);

    	            // Setear los parametros del controlador       
    	            DetallePresupuestoOverviewController controller = loader.getController();
    	            controller.setDialogStage(dialogStage);
    	            controller.setPresupuesto(p);

    	            // Show the dialog and wait until the user closes it
    	            dialogStage.showAndWait();

    	            //return controller.isOkClicked();
    	        	} 
    	    		catch (IOException e) {
    	    			e.printStackTrace();
    	    			//return false;
    	    		}
    		}
    		else{
    			Alert alert = new Alert(AlertType.INFORMATION, 
    		  			 "",
                       ButtonType.OK);
                 alert.initOwner(dialogStage);
                 alert.setTitle("Registro de pago");
                 alert.setHeaderText(null);
                 //TODO: cancelaciones
                 alert.setContentText("La transacción seleccionada corresponde a un pago o a una cancelación, y por ende no tiene asociada un presupuesto efectivo.");
                 alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                 alert.showAndWait();
    		}
    	
    	}
    	else{
    		 Alert alert = new Alert(AlertType.WARNING, 
 		  			 "",
                    ButtonType.OK);
              alert.initOwner(dialogStage);
              alert.setTitle("Seleccionar transacción");
              alert.setHeaderText(null);
              alert.setContentText("No se ha seleccionado una transaccion de la lista.");
              alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
              alert.showAndWait();
    	}
    }

    @FXML 
    private void handleImprimirDetalle(){
    	LocalDate EPOCH = LocalDate.of(2003, 1, 1);
    	LocalDate NOW = LocalDate.now();
    	DateTimeFormatter formatter_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	DateTimeFormatter formatter_2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  	    	
    	Dialog<ButtonType> dialog = new Dialog<>();
     	dialog.setTitle("Imprimir Detalle");
     	dialog.setHeaderText(null);
     	dialog.setResizable(false); 
     	
     	Label label1 = new Label("Desde:");
     	Label label2 = new Label("Hasta:");
     	DatePicker desdeDatePicker = new DatePicker();
    	DatePicker hastaDatePicker = new DatePicker();
    	desdeDatePicker.setPromptText(EPOCH.format(formatter_2));
        hastaDatePicker.setPromptText(NOW.format(formatter_2));
     	
        Platform.runLater(() -> 
    	desdeDatePicker.requestFocus());
        
        // TODO: GUARDAR DETALLE COMO
        
        final Callback<DatePicker, DateCell> HCellFactory = 
    	        new Callback<DatePicker, DateCell>() {
    	            @Override
    	            public DateCell call(final DatePicker datePicker) {
    	                return new DateCell() {
    	                    @Override
    	                    public void updateItem(LocalDate item, boolean empty) {
    	                        super.updateItem(item, empty);
    	                        LocalDate piso;
    	                        if(desdeDatePicker.getValue()==null){
    	                        	piso = LocalDate.of(2003, 1, 1);
    	                        }
    	                        else{
    	                        	piso = desdeDatePicker.getValue().plusDays(1);
    	                        }
    	                        if (item.isBefore(piso)){
    	                                setDisable(true);
    	                                setStyle("-fx-background-color: #d3d3d3;");
    	                        }
    	                        if (item.isAfter(
    	                                LocalDate.now()) 
    	                        		){
    	                                setDisable(true);
    	                                setStyle("-fx-background-color: #d3d3d3;");
    	                        }
    	                        if (item.isBefore(
    	                        		LocalDate.of(2003, 1, 1)) 
    	                        		){
    	                                setDisable(true);
    	                                setStyle("-fx-background-color: #d3d3d3;");
    	                        }
    	                    }
    	                };
    	            }
        		};
    	    
    	final Callback<DatePicker, DateCell> DECellFactory = 
        	        new Callback<DatePicker, DateCell>() {
        	            @Override
        	            public DateCell call(final DatePicker datePicker) {
        	                return new DateCell() {
        	                    @Override
        	                    public void updateItem(LocalDate item, boolean empty) {
        	                        super.updateItem(item, empty);
        	                        if (item.isAfter(
        	                                LocalDate.now()) 
        	                        		){
        	                                setDisable(true);
        	                                setStyle("-fx-background-color: #d3d3d3;");
        	                        }
        	                        if (item.isBefore(
        	                        		LocalDate.of(2003, 1, 1)) 
        	                        		){
        	                                setDisable(true);
        	                                setStyle("-fx-background-color: #d3d3d3;");
        	                        }
        	                       
        	                }
        	            };
        	        }
        	    };
        
    	hastaDatePicker.setDayCellFactory(HCellFactory);
    	desdeDatePicker.setDayCellFactory(DECellFactory);
    	
	    //Creo la grilla de componentes para el dialogo    	
    	GridPane grid = new GridPane();
    	grid.add(label1, 1, 1);
    	grid.add(desdeDatePicker, 2, 1);
    	grid.add(label2, 1, 3);
    	grid.add(hastaDatePicker, 2, 3);
    	dialog.getDialogPane().setContent(grid);
    	   	
    	for (int i = 0; i <= 3; i++) {
            RowConstraints con = new RowConstraints();
            con.setPrefHeight(20);
            grid.getRowConstraints().add(con);
        }
    	
    	//genero los dos botones : aplicar (OK) y cancelar.
    	ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
    	
    	ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
    	dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
   
    	Optional<ButtonType> result = dialog.showAndWait();
    	
    	//Si el usuario elige Generar Presupuesto
    	if (result.get() == buttonTypeOk) {
    		
    		Cliente c = this.cuenta.getCliente();
    		
    		String fecha_desde = EPOCH.format(formatter_1);
    		String fecha_hasta = NOW.format(formatter_1);
    		
    		if(desdeDatePicker.getValue()!=null){
    			fecha_desde = desdeDatePicker.getValue().format(formatter_1);
    		}
    		else{
    			
    			desdeDatePicker.setValue(EPOCH);
    		}
    			
    		if(hastaDatePicker.getValue()!=null)
    			fecha_hasta = hastaDatePicker.getValue().format(formatter_1);
    		else{
    			hastaDatePicker.setValue(NOW);
    		}
    		System.out.println(fecha_desde);
    		//Chequeo errores en las fechas
    		boolean error = (desdeDatePicker.getValue().isAfter(NOW) || hastaDatePicker.getValue().isAfter(NOW)) || (hastaDatePicker.getValue().isBefore(desdeDatePicker.getValue()));
    			
    		if(error){
    				desdeDatePicker.setValue(null);
    				hastaDatePicker.setValue(null);
    				Alert alert = new Alert(AlertType.ERROR);
    				alert.initOwner(dialogStage);
    				alert.setTitle("Error");
    				alert.setHeaderText(null);
    				alert.setContentText("La fecha ingresada es inválida.");
    				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    				alert.showAndWait();
    		}
    		else{//Si no hay error procedo a la busqueda en la DB
    			String filename = ReportsEngine.generarResumen(c, fecha_desde, fecha_hasta);
            	
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
    	}
    	else{
    		//No hago nada y cierro
    	}
    	
    }

}
