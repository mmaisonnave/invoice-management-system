package controller.view;
 
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.util.Optional;

import controller.db.Presupuesto;
import exception.InvalidBudgetException;
import controller.Main;
import controller.db.Cliente;
import controller.db.Concepto;
import controller.db.DBEngine;
import controller.db.DBSingleton;


/**
 * Diálogo para editar los detalles de un presupuesto especificado.
 * 
 * @author Maria Virginia Sabando
 */
public class ModificarPresupuestoOverviewController {

    @FXML
    private Label nroPresupuestoField;
    @FXML
    private Label cuitField;
    @FXML
    private Label denominacionField;
    @FXML
    private Label condicionIvaField;
    @FXML
    private ChoiceBox<String> alicuotaChoiceBox = new ChoiceBox<String>();
    @FXML
    private ChoiceBox<String> mesChoiceBox = new ChoiceBox<String>();
    @FXML
    private Label subtotalField;
    @FXML
    private Label montoTotalField;
    @FXML
    private TableView<Concepto> conceptosTable;
    @FXML
    private TableColumn<Concepto,String> conceptoColumn;
    @FXML
    private TableColumn<Concepto,String> montoColumn;
    @FXML
    private Button nuevoButton;
    @FXML
    private Button editarButton;
    @FXML
    private Button borrarButton;
    
 
    private Stage dialogStage;
    private Presupuesto presupuesto;
    private boolean okClicked = false;
    
    private Object propietario;
    
    private Main mainApp;
    private DBEngine DBMotor = DBSingleton.getInstance();

    /**
     * Inicializa la clase controlador. Este método es llamado automáticamente
     * luego de que el archivo .fxml ha sido cargado.
     */
    @FXML
    private void initialize() {
    	
    	editarButton.disableProperty().bind(Bindings.isEmpty(conceptosTable.getSelectionModel().getSelectedItems()));
        borrarButton.disableProperty().bind(Bindings.isEmpty(conceptosTable.getSelectionModel().getSelectedItems()));
            	
    	conceptosTable.setPlaceholder(new Label("No hay conceptos para mostrar."));
    	conceptosTable.getColumns().forEach(this::addTooltipToColumnCells_Concepto);
        
    	alicuotaChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                	
                	newValue = newValue.replace(',','.');
                	newValue = newValue.replace('%', ' ');
                	this.montoTotalField.setText(
                	String.valueOf(recalcularMonto(Double.parseDouble(newValue))));

                });
    	
    	montoColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");
    	
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
     * Setea el stage para este diálogo
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.dialogStage.setOnCloseRequest((WindowEvent event1) -> {
            handleCancelar();
        });
    }
    
    public void setmainApp(Main m){
    	this.mainApp=m;
    }

    /**
     * Ubica en la vista los datos del presupuesto que esta siendo modificado.
     * 
     * @param presupuesto
     */
    public void setPresupuesto(Presupuesto presupuesto) {
        this.presupuesto = presupuesto;
        Cliente cliente = presupuesto.getCliente();

        nroPresupuestoField.setText(String.valueOf(presupuesto.getNroPresupuesto()));
        cuitField.setText(cliente.getCuit());
        denominacionField.setText(cliente.getDenominacion());
        String ci = cliente.getCondicionIva();
        if(ci!= null){
        	if(ci.startsWith("RI")){
            	this.condicionIvaField.setText("Responsable Inscripto");
            }
            else if(ci.startsWith("EX")){
            	this.condicionIvaField.setText("Exento");
            }
            else if(ci.startsWith("MO")){
            	this.condicionIvaField.setText("Monotributista");
            }
            else if(ci.startsWith("NR")){
            	this.condicionIvaField.setText("No Responsable");
            }
            else if(ci.startsWith("CF")){
            	this.condicionIvaField.setText("Consumidor Final");
            }
        }
        
        alicuotaChoiceBox.setItems(FXCollections.observableArrayList("0%","10,5%","21%"));
        
        Float ali = presupuesto.getAlicuota();
        if(ali!= null){
        	if(ali == 0.0){
            	alicuotaChoiceBox.setValue("0%");
            }
            else if(ali == 10.5){
            	alicuotaChoiceBox.setValue("10,5%");
            }
            else if(ali == 21.0){
            	alicuotaChoiceBox.setValue("21%");
            }
            else{
            	alicuotaChoiceBox.setValue("");
            }
        }
        
        mesChoiceBox.setItems(FXCollections.observableArrayList(Presupuesto.getMeses()));
		
		//TODO: elegir el mes actual de los presupuestos
		int mesesito = presupuesto.getMes() -1 ;
		mesChoiceBox.getSelectionModel().select(mesesito);
        
        subtotalField.setText(String.valueOf(presupuesto.getSubtotal()));
        montoTotalField.setText(String.valueOf(recalcularMonto(presupuesto.getAlicuota())));
        
        conceptosTable.setItems(presupuesto.getConceptosObservables());
        conceptoColumn.setCellValueFactory(
			cellData -> cellData.getValue().getConceptoProperty());
        
		montoColumn.setCellValueFactory(
			cellData -> cellData.getValue().getMontoConceptoStringProperty());
 
    }

    /**
     * Retorna true si el cliente presionó el botón OK, falso otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Llamado cuando el usuario presiona el botón OK: Guarda los cambios realizados
     * sobre el presupuesto en la base de datos
     * TODO: editar conceptos en el objeto presupuesto
     */
    @FXML
    private void handleOk() {
    	       	
    	//ALICUOTA
    	String ali = alicuotaChoiceBox.getValue();
        if(ali!= null){
            if(ali.startsWith("0%")){
            	presupuesto.setAlicuota(0);
            }
            else if(ali.startsWith("10,5%")){
                presupuesto.setAlicuota((float)10.5);
            }
            else if(ali.startsWith("21%")){
               	presupuesto.setAlicuota((float)21.0);
            }
        }
        
        //MES
        String mes = mesChoiceBox.getValue();
        if(mes!=null){
        	presupuesto.setMes(mesChoiceBox.getSelectionModel().getSelectedIndex() + 1);
        }
        
            
        //CONCEPTOS: se editan solos en el presupuesto
        
        //subtotal: hay que recalcular y setearlo en el presupuesto
        presupuesto.setSubtotal(recalcularSubtotal());
        
        
        //INICIO
        //DESEA GUARDAR LOS CAMBIOS?
        Alert alert = new Alert(AlertType.CONFIRMATION,
        		"",
        		ButtonType.YES, 
                ButtonType.NO);
        alert.initOwner(dialogStage);
        alert.setTitle("Editar presupuesto");
        alert.setHeaderText(null);
        alert.setContentText("¿Desea guardar los cambios realizados?");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);


        Optional<ButtonType> result = alert.showAndWait();

        //GUARDAR LOS CAMBIOS = OK
        if (result.get() == ButtonType.YES) {
        	okClicked = true;
           
            //Actualizo el presupuesto en la base de datos
            try {
				DBMotor.editarPresupuesto(presupuesto);
			} catch (InvalidBudgetException e) {
				e.printStackTrace();
				System.out.println("El presupuesto es inválido, y no lo puedo editar.");
			}
            //si fue llamado desde Main: preguntar si quiere efectivizar
			if(this.propietario.getClass().getName().equals("controller.Main")){
				
				 //DESEA ADEMÁS EFECTIVIZAR?
				 Alert alerta = new Alert(AlertType.CONFIRMATION, 
	 		  			 "",
	                    ButtonType.YES, 
	                    ButtonType.NO);
				 
				 
				//Deactivate Defaultbehavior for yes-Button:
				Button yesButton = (Button) alerta.getDialogPane().lookupButton( ButtonType.YES );
				yesButton.setDefaultButton( false );

				//Activate Defaultbehavior for no-Button:
				Button noButton = (Button) alerta.getDialogPane().lookupButton( ButtonType.NO );
				noButton.setDefaultButton( true );

				 
				 
	              alerta.initOwner(dialogStage);
	              alerta.setTitle("Efectivizar presupuesto");
	              alerta.setHeaderText("Presupuesto seleccionado: "+ presupuesto.getNroPresupuesto()+ " - "
	            		  			  + presupuesto.getCliente().getDenominacion());
	              alerta.setContentText("¿Desea efectivizar el presupuesto creado? \n\nSi pulsa \"NO\" el presupuesto estará abierto en el menú Área de trabajo.");
	              alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	              Optional<ButtonType> resultado = alerta.showAndWait();

	              //DESEA ADEMAS EFECTIVIZAR = 0K
	              if (resultado.get() == ButtonType.YES) {
	             	 //Efectivizo el presupuesto en la base de datos
	             	 try{
	             		 DBMotor.efectivizarPresupuesto(presupuesto);
	                     
	             	 }
	             	 catch(InvalidBudgetException e){
	             		e.printStackTrace();
	             		System.out.println("La efectivización del presupuesto "+ presupuesto.getNroPresupuesto() + " tiro error.");
	             	 }
	             	 //Se informa al usuario que termino el proceso
	                  alert = new Alert(AlertType.INFORMATION, 
	     		  			 "",
	                        ButtonType.OK);
	                  alert.initOwner(dialogStage);
	                  alert.setTitle("Efectivizar presupuesto");
	                  alert.setHeaderText(null);
	                  alert.setContentText("Se ha efectivizado el presupuesto nº "+ presupuesto.NroPresupuestoStringProperty().get());
	                  alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	                  alert.showAndWait();
	              }
	              
	              //DESEA ADEMÁS EFECTIVIZAR = NO
	              else{
	            	//actualizo la lista observable desde la DB (en teoria)
  					mainApp.setPresupuestosNoEfectivosData(FXCollections.observableArrayList(DBMotor.obtenerPresupuestosNoEfectivos()));
  				
	              }
			}//FIN FUE LLAMADO DESDE MAIN?
			
        }//FIN GUARDAR LOS CAMBIOS OK
			
        dialogStage.close();
    }//FIN HANDLE OK
    

    /**
     * LLamado cuando el usuario presiona el botón Cancelar
     */
    @FXML
    private void handleCancelar() {
    	
    	  Alert alert = new Alert(AlertType.CONFIRMATION, 
    			  			 "",
    	                     ButtonType.YES, 
    	                     ButtonType.NO);
          alert.initOwner(dialogStage);
          alert.setTitle("Cancelar: Editar presupuesto");
          alert.setHeaderText(null);
          alert.setContentText("¿Desea descartar los cambios realizados?");
         
          Optional<ButtonType> result = alert.showAndWait();

          if (result.get() == ButtonType.YES) {
        	  dialogStage.close();
          }   
    }

    
    public void handleNuevoConcepto(){
    	
    	Concepto nuevo;
    	
    	Dialog<ButtonType> dialog = new Dialog<>();
    	dialog.setTitle("Nuevo concepto");
    	dialog.setHeaderText(null);
    	dialog.setResizable(true);

    	Label label1 = new Label("Descripción: ");
    	Label label2 = new Label("Importe: $");
    	TextArea text1 = new TextArea();
    	text1.setPrefHeight(100);  
    	text1.setPrefWidth(250);
    	text1.setWrapText(true);
    	TextField text2 = new TextField();
    	text1.setPromptText("Nuevo concepto");
    	text2.setPromptText("0.0");
    			
    	//Validacion de campos
    	 text1.textProperty().addListener((obs, oldText, newText) -> {
             if (oldText.length() < 300 && newText.length() >= 300) {
                      text2.requestFocus();
             }
          });
    	 
     	 
     	 text2.textProperty().addListener((obs, oldText, newText) -> {
     		 if (newText.matches("(\\-)?\\d+\\.|(\\-)?\\d*(\\.\\d+)?")) {
     			 text2.setText(newText);
     		 }
     		 else
     			 text2.setText(oldText);
     		 
     	 });
    	
    	GridPane grid = new GridPane();
    	grid.add(label1, 1, 1);
    	grid.add(text1, 2, 1);
    	grid.add(label2, 1, 2);
    	grid.add(text2, 2, 2);
    	dialog.getDialogPane().setContent(grid);
    	
    	Platform.runLater(() -> text1.requestFocus());
    			
    	ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
    	
    	ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
    	dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
    	
    	Optional<ButtonType> result = dialog.showAndWait();
    	
    	if (result.get() == buttonTypeOk) {
    		String cs;
    		double m;
    		//Chequeo si las cajas de texto quedaron vacías:
    		//en ese caso creo un concepto por default
    		if (text1.getText().length()==0){
    			cs="Concepto nuevo";
    		}
    		else{
    			cs = text1.getText();
    		}
    		
    		if (text2.getText().length()==0){
    			m=0.0;
    		}
    		else{
    			m = Double.parseDouble(text2.getText());
    		}
    		
    		//Creo nuevo concepto con los datos ingresados
            nuevo = new Concepto(cs, m);
            //agrego el concepto nuevo a la lista de conceptos del presupuesto
            presupuesto.getConceptos().add(nuevo);
            
            //Recalculo el subtotal con el nuevo concepto
            presupuesto.setSubtotal(recalcularSubtotal());
            
            //seteo nuevo valor en los label en base a los valores de la vista (alicuota)
            this.subtotalField.setText(String.valueOf(presupuesto.getSubtotal()));
            String ali = this.alicuotaChoiceBox.getValue();
            ali = ali.replace("%", "");
            ali = ali.replace(",", ".");
            double alix = Double.parseDouble(ali);
            this.montoTotalField.setText(String.valueOf(recalcularMonto(alix)));
          
            //Actualizo contenido tabla en la vista
       	 	conceptosTable.setItems(presupuesto.getConceptosObservables());
        	
         }
         else{
        	 //No hago nada y cierro
         }
    }
    
    public void handleEditarConcepto(){
    	
    	Concepto concepto = conceptosTable.getSelectionModel().getSelectedItem();
    	int indice_concepto = conceptosTable.getSelectionModel().getSelectedIndex();
    	if (concepto != null) {
    	Dialog<ButtonType> dialog = new Dialog<>();
    	dialog.setTitle("Editar concepto");
    	dialog.setHeaderText(null);
    	dialog.setResizable(true);

    	Label label1 = new Label("Descripción: ");
    	Label label2 = new Label("Importe: $");
    	TextArea text1 = new TextArea(concepto.getConcepto());
    	//You can use these methods
    	text1.setPrefHeight(100);  //sets height of the TextArea to 400 pixels 
    	text1.setPrefWidth(250);    //sets width of the TextArea to 300 pixels
    	text1.setWrapText(true);
    	text1.positionCaret(text1.getLength());
    	
    	TextField text2 = new TextField(String.valueOf(concepto.getMonto()));
    	text1.setPromptText("Editar concepto");
    	text2.setPromptText("0.0");
    			
    	//Validacion de campos
    	 text1.textProperty().addListener((obs, oldText, newText) -> {
             if (oldText.length() < 300 && newText.length() >= 300) {
                      text2.requestFocus();
             }
          });
    	 
     	 
     	 text2.textProperty().addListener((obs, oldText, newText) -> {
     		 if (newText.matches("(\\-)?\\d+\\.|(\\-)?\\d*(\\.\\d+)?")) {
     			 text2.setText(newText);
     		 }
     		 else
     			 text2.setText(oldText);
     		 
     	 });
    	    			
    	GridPane grid = new GridPane();
    	grid.add(label1, 1, 1);
    	grid.add(text1, 2, 1);
    	grid.add(label2, 1, 2);
    	grid.add(text2, 2, 2);
    	dialog.getDialogPane().setContent(grid);
    	
    	Platform.runLater(() -> text1.requestFocus());
    			
    	ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
    	
    	ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
    	dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

    	Optional<ButtonType> result = dialog.showAndWait();
    	
    	
    	if (result.get() == buttonTypeOk) {
    		String cs;
    		double m;
    		//Chequeo si las cajas de texto quedaron vacías:
    		//en ese caso creo un concepto por default
    		if (text1.getText().length()==0){
    			cs="Concepto nuevo";
    		}
    		else{
    			cs = text1.getText();
    		}
    		
    		if (text2.getText().length()==0){
    			m=0.0;
    		}
    		else{
    			m = Double.parseDouble(text2.getText());
    		}
    		
    		
    		//Creo nuevo concepto con los datos ingresados
            Concepto nuevo = new Concepto(cs, m);
           
    		//borro el concepto viejo de la lista
    		presupuesto.getConceptos().remove(concepto);
    		
        	//agrego el concepto nuevo a la lista de nuevo
        	//presupuesto.getConceptos().add(nuevo);
    		presupuesto.getConceptos().add(indice_concepto, nuevo);
        	
        	//recalculo el monto con el concepto nuevo
       	 	presupuesto.setSubtotal(recalcularSubtotal());
       	 	
       	 	//seteo nuevo valor en su label de acuerdo a los valores de la vista (alicuota)
       	 	this.subtotalField.setText(String.valueOf(presupuesto.getSubtotal()));
       	 	String ali = this.alicuotaChoiceBox.getValue();
       	 	ali = ali.replace("%", "");
       	 	ali = ali.replace(",", ".");
       	 	double alix = Double.parseDouble(ali);
       	 	this.montoTotalField.setText(String.valueOf(recalcularMonto(alix)));
       	 	
            //Actualizo contenido tabla en la vista
       	 	conceptosTable.setItems(presupuesto.getConceptosObservables());
        	
         }
         else{
        	 //No hago nada y cierro
         }
    	}
    	else{
    		 // No se seleccionó ningún concepto
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(dialogStage);
            alert.setTitle("Seleccionar concepto");
            alert.setHeaderText(null);
            alert.setContentText("No se ha seleccionado un concepto de la lista.");

            alert.showAndWait();
    	}
    }
    
    public void handleBorrarConcepto(){
    	
    	Concepto selectedConcepto = conceptosTable.getSelectionModel().getSelectedItem();
        if (selectedConcepto != null) {
        	 // Se procede a alertar al usuario
             Alert alert = new Alert(AlertType.CONFIRMATION, 
		  			 "",
                   ButtonType.YES, 
                   ButtonType.NO);
             alert.initOwner(dialogStage);
             alert.setTitle("Borrar concepto");
             alert.setHeaderText(null);
             alert.setContentText("¿Borrar concepto seleccionado?");


             Optional<ButtonType> result = alert.showAndWait();

             if (result.get() == ButtonType.YES) {
            	 //Elimino el concepto de la lista de conceptos del presupuesto
            	 presupuesto.getConceptos().remove(selectedConcepto);
            	 
            	 //Recalculo el monto sin el concepto eliminado
            	 presupuesto.setSubtotal(recalcularSubtotal());
            	 
            	//seteo nuevo valor en su label de acuerdo a los valores de la vista (alicuota)
            	 this.subtotalField.setText(String.valueOf(presupuesto.getSubtotal()));
            	 String ali = this.alicuotaChoiceBox.getValue();
                 ali = ali.replace("%", "");
                 ali = ali.replace(",", ".");
                 double alix = Double.parseDouble(ali);
                 this.montoTotalField.setText(String.valueOf(recalcularMonto(alix)));
             	                 
            	 //Actualizo contenido tabla en la vista
            	 conceptosTable.setItems(presupuesto.getConceptosObservables());
                
             }
             else{
            	 //No hago nada y cierro
             }

        } 
        else {
            // No se seleccionó ningún concepto
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(dialogStage);
            alert.setTitle("Seleccionar concepto");
            alert.setHeaderText(null);
            alert.setContentText("No se ha seleccionado un concepto de la lista.");

            alert.showAndWait();
        }
    }
    
    private double  recalcularMonto(double ali){

        double monto_calculado = recalcularSubtotal();
        monto_calculado = monto_calculado * (1.0 + ali/100);
        monto_calculado = Math.round(monto_calculado * 20.0) / 20.0;
        return monto_calculado;
    }
    
    private double recalcularSubtotal(){
    	double monto_calculado = 0;
        for(Concepto c : presupuesto.getConceptos()){
        	monto_calculado= monto_calculado + c.getMonto();
        }
        
        monto_calculado = Math.round(monto_calculado * 20.0) / 20.0;
        return monto_calculado;
    }

    public void setPropietario(Object p){
    	this.propietario = p;
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
    
    
}