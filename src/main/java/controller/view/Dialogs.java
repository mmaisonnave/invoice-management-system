package controller.view;

import java.time.LocalDate;
import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Dialogs {
	
    public static Optional<LocalDate> promptUserForDate(String title, String message) {
      	 // Create Dialog Asking for confirmation and a date to make invoice effective on that date:
           Dialog<LocalDate> dialog = new Dialog<>();
           dialog.setTitle(title);
           Label label =  new Label(message);
           
           DatePicker datePicker = new DatePicker(LocalDate.now());
           HBox hbox_with_datepicker =  new HBox(new Label("Fecha de Efectivización: "), datePicker);
           hbox_with_datepicker.setAlignment(Pos.CENTER_LEFT);  // Aligns the Label and DatePicker

           dialog.getDialogPane().setContent(new VBox(10, label, hbox_with_datepicker));
           dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

           dialog.setResultConverter(button -> button == ButtonType.OK ? datePicker.getValue() : null);
           dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
           dialog.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);


           return dialog.showAndWait();
       }

}
