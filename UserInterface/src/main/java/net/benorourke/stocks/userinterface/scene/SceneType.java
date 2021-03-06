package net.benorourke.stocks.userinterface.scene;

import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.createseries.CreateSeriesController;
import net.benorourke.stocks.userinterface.scene.tasks.TasksController;

/**
 * An enum containing data about all the types of scenes that can be created.
 */
public enum SceneType
{
	/** The dashboard Scene */
	DASHBOARD("/dashboard.fxml", new String[] {"/dashboard.css"}, DashboardController.class),
	TASKS("/tasks.fxml", new String[] {"/tasks.css"}, TasksController.class),
	CREATE_SERIES("/createseries.fxml", new String[] {"/createseries.css"}, CreateSeriesController.class);
	
	private String fxmlName;
	private String[] cssNames;
	private Class<? extends Controller> controllerClazz;
	
	SceneType(String fxmlName, String[] cssNames, Class<? extends Controller> controllerClazz)
    {
		this.fxmlName = fxmlName;
		this.cssNames = cssNames;
		this.controllerClazz = controllerClazz;
	}
	
	public String getFXMLName()
    {
		return fxmlName;
	}
	
	public String[] getCssNames()
    {
		return cssNames;
	}
	
	public Class<? extends Controller> getControllerClazz()
    {
		return controllerClazz;
	}
	
}
