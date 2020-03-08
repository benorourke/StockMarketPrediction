package net.benorourke.stocks.userinterface.scene.directory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.scene.Controller;

import java.util.Random;

public class DirectoryController extends Controller
{
	public static final double VBOX_PADDING = 50;

	private final DirectoryModel model;

	@FXML private AnchorPane root;

	// |- root
	//    -| GridPane (pane)
	//       |- HBox (centreHBox) (for horizontal center alignment)
	//          |- VBox (vBox) (for vertical center alignment)
	//	           |- field

	public DirectoryController()
    {
		this.model = new DirectoryModel();
	}

	@FXML
	public void initialize()
	{
		StockApplication.debug("Initialised DirectoryController");

		GridPane pane = new GridPane();
		bindX(pane, root);
		bindY(pane, root);
		pane.setAlignment(Pos.CENTER);
		root.getChildren().add(pane);

		HBox centreHBox = new HBox();
		bindX(centreHBox, pane, 0.8);
		bindY(centreHBox, pane, 0.5);
		pane.getChildren().add(centreHBox);

		VBox vBox = new VBox();
		vBox.setPadding(new Insets(VBOX_PADDING, VBOX_PADDING, VBOX_PADDING, VBOX_PADDING));
		bind(vBox, centreHBox);
		centreHBox.getChildren().add(vBox);
		//		VBox vBox = new VBox();
//		bind(vBox, root);
//		root.getChildren().add(vBox);

		TextField field = new TextField("Test2");
//		bind(field, root, 0.8);
//		bind(field, vBox, 0.5);
		bind(field, vBox);
		vBox.getChildren().add(field);
	}

//	@FXML
//	public void initialize()
//    {
//    	HBox centreHBox = new HBox();
//    	bind(centreHBox, root);
//    	centreHBox.setAlignment(Pos.CENTER);
//		root.getChildren().add(centreHBox);
//
//    	createVBox(centreHBox);
//    }
//
//    private void createVBox(Pane parent)
//	{
//		VBox vBox = new VBox();
//		bind(vBox, parent, 0.8);
////		vBox.setPadding(new Insets(VBOX_PADDING, VBOX_PADDING, VBOX_PADDING, VBOX_PADDING));
//		vBox.setAlignment(Pos.CENTER);
//
//		TextField field = new TextField("Test2");
//		bindX(field, vBox, 0.5);
//		vBox.getChildren().add(field);
//
//		// TODO - Remove Button
//		final Button button = new Button("Refresh");
//		button.setOnAction( e -> button.setText(String.valueOf(new Random().nextInt(500))) );
//		bindX(button, vBox);
//		vBox.getChildren().add(button);
//
//		parent.getChildren().add(vBox);
//	}

	public DirectoryModel getModel()
    {
		return model;
	}

}
