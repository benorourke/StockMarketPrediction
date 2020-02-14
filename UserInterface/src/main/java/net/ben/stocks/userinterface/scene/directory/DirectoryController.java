package net.ben.stocks.userinterface.scene.directory;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import net.ben.stocks.userinterface.scene.Controller;

public class DirectoryController extends Controller
{
	private final DirectoryModel model;

	// |- root
	//    |- vbox
	//	     |-
	
	public DirectoryController()
    {
		this.model = new DirectoryModel();
	}

	@FXML
	public void initialize()
    {
		VBox vBox = new VBox();
		vBox.setId("vbox");
		vBox.prefWidthProperty().bind(getRoot().widthProperty());
		vBox.prefHeightProperty().bind(getRoot().prefHeightProperty());
		getRoot().getChildren().add(vBox);

		TextField field = new TextField("Test");
		field.prefWidthProperty().bind(vBox.widthProperty().multiply(0.5));
		vBox.getChildren().add(field);
    }
	
	public DirectoryModel getModel()
    {
		return model;
	}
	
}
