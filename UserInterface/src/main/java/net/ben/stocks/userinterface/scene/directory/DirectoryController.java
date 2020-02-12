package net.ben.stocks.userinterface.scene.directory;

import javafx.fxml.FXML;

public class DirectoryController
{
	private final DirectoryModel model;
	
	public DirectoryController()
    {
		this.model = new DirectoryModel();
	}

	@FXML
	public void initialize()
    {
	}
	
	public DirectoryModel getModel()
    {
		return model;
	}
	
}
