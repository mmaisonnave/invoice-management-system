package controller.view;
 

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.util.Callback;


import controller.db.Cliente;
import controller.db.Concepto;
import controller.db.Presupuesto;


/**
 * Diálogo para editar los detalles de un cliente especificado.
 * 
 * @author Maria Virginia Sabando
 */
public class DetallePresupuestoOverviewController {

    @FXML
    private Label cuitLabel;
    @FXML
    private Label denominacionLabel;
    @FXML
    private Label numeroLabel;
    @FXML
    private Label fechaLabel;
    @FXML
    private Label ivaLabel;
    @FXML
    private Label alicuotaLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label montoTotalLabel;
    @FXML
    private Label mesLabel;
    @FXML
    private TableView<Concepto> conceptosTable;
    @FXML
    private TableColumn<Concepto, String> descripcionColumn;
    @FXML
    private TableColumn<Concepto,String> importeColumn;
   
 
    private Stage dialogStage;
    private boolean okClicked = false;
    private Presupuesto presupuesto;

    /**
     * Inicializa la clase controlador. Este método es llamado automáticamente
     * luego de que el archivo .fxml ha sido cargado.
     */
    @FXML
    private void initialize() {
    	conceptosTable.setPlaceholder(new Label("No hay conceptos para mostrar."));
    	  	  	
 		conceptosTable.getColumns().forEach(this::addTooltipToColumnCells_Concepto);
    
 		importeColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");
 		
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
    }

    /**
     * Ubica en la vista los datos del cliente que esta siendo modificado.
     * 
     * @param cliente
     */
    public void setPresupuesto(Presupuesto presupuesto) {
    	
    	if(presupuesto != null){
    		this.presupuesto = presupuesto;
    		Cliente cliente = this.presupuesto.getCliente();
    		
    		cuitLabel.setText(cliente.getCuit());
    		denominacionLabel.setText(cliente.getDenominacion());
    		numeroLabel.setText(String.valueOf(presupuesto.getNroPresupuesto()));
    		String ci = cliente.getCondicionIva();
            if(ci!= null){
            	if(ci.startsWith("RI")){
                	this.ivaLabel.setText("Responsable Inscripto");
                }
                else if(ci.startsWith("EX")){
                	this.ivaLabel.setText("Exento");
                }
                else if(ci.startsWith("MO")){
                	this.ivaLabel.setText("Monotributista");
                }
                else if(ci.startsWith("NR")){
                	this.ivaLabel.setText("No Responsable");
                }
                else if(ci.startsWith("CF")){
                	this.ivaLabel.setText("Consumidor Final");
                }
            }
            
    		alicuotaLabel.setText(String.valueOf(presupuesto.getAlicuota()) + " %");
    		subtotalLabel.setText(String.valueOf(presupuesto.getSubtotal()));
    		montoTotalLabel.setText(String.valueOf(calcularMonto(presupuesto.getAlicuota())));
    		//Actualizo contenido tabla en la vista
   	 		conceptosTable.setItems(presupuesto.getConceptosObservables());
   	 		conceptosTable.setItems(presupuesto.getConceptosObservables());
   	 		descripcionColumn.setCellValueFactory(
   	 				cellData -> cellData.getValue().getConceptoProperty());
   	 		
   	 		importeColumn.setCellValueFactory(
   	 				cellData -> cellData.getValue().getMontoConceptoStringProperty());
    	  	
   	 		String fechaMostrar = presupuesto.getFecha_ARG().replaceAll("-", "/");
   	 		fechaLabel.setText(fechaMostrar);
   	 		
   	 		mesLabel.setText(presupuesto.getMesFormateado());
    	}
    	else {
    		cuitLabel.setText("");
    		denominacionLabel.setText("");
    		numeroLabel.setText("");
    		ivaLabel.setText("");
    		alicuotaLabel.setText("");
    		subtotalLabel.setText("");
    		montoTotalLabel.setText("");
    		//Actualizo contenido tabla en la vista
   	 		conceptosTable.setItems(null);
   	 		fechaLabel.setText("");
   	 		mesLabel.setText("");
    	}
    }
    
    private double calcularMonto(float ali){
    	double monto_calculado = presupuesto.getSubtotal();
    	monto_calculado = monto_calculado * (1.0 + ali/100);
    	monto_calculado = Math.round(monto_calculado * 20.0) / 20.0;
    	return monto_calculado;
    }


    /**
     * Llamado cuando el usuario presiona el botón OK: Guarda al cliente en la base de datos
     */
    @FXML
    private void handleAceptar() {
    	dialogStage.close();
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