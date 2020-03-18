package net.benorourke.stocks.userinterface.scene;

import net.benorourke.stocks.userinterface.scene.splash.SplashController;

public enum SceneType
{
    /**
     * The Scene that shows information & options for a given module, i.e.
     * a list of bots.
     */
    DIRECTORY_SELECTION("/splash.fxml", "/darktheme.css", SplashController.class),
	/** The dashboard Scene */
	/*DASHBOARD("splash.fxml", "darktheme.css", DashboardController.class)*/;
	
	private String fxmlName;
	private String cssName;
	private Class<? extends Controller> controllerClazz;
	
	SceneType(String fxmlName, String cssName, Class<? extends Controller> controllerClazz)
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
	
	public Class<? extends Controller> getControllerClazz()
    {
		return controllerClazz;
	}
	
}
