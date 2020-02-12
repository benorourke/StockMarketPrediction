package net.ben.stocks.userinterface.scene;

import net.ben.stocks.userinterface.scene.directory.DirectoryController;

public enum SceneType
{
    /**
     * The Scene that shows information & options for a given module, i.e.
     * a list of bots.
     */
    DIRECTORY_SELECTION("directory.fxml", "directory.css", DirectoryController.class),
	/** The dashboard Scene */
	/*DASHBOARD("directory.fxml", "directory.css", DashboardController.class)*/;
	
	private String fxmlName;
	private String cssName;
	private Class<?> controllerClazz;
	
	SceneType(String fxmlName, String cssName, Class<?> controllerClazz)
    {
		this.fxmlName = fxmlName;
		this.cssName = cssName;
		this.controllerClazz = controllerClazz;
	}
	
	public String getFXMLName()
    {
		return fxmlName;
	}
	
	public String getCssName()
    {
		return cssName;
	}
	
	public Class<?> getControllerClazz()
    {
		return controllerClazz;
	}
	
}
