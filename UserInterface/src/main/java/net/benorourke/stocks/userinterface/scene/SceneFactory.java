package net.benorourke.stocks.userinterface.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import net.benorourke.stocks.userinterface.exception.SceneCreationDataException;
import net.benorourke.stocks.userinterface.util.ResourceUtil;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A factory singleton class for generating scenes.
 */
public class SceneFactory
{
	/**
	 * The singleton instance of the {@link SceneFactory}.
	 */
	private static SceneFactory instance;
	
	private SceneFactory() {}

	/**
	 * Generate a new Scene.
	 *
	 * @param type the type of the scene
	 * @param initParams the parameters to construct the scene with
	 * @return the new scene
	 * @throws SceneCreationDataException if any errors were faced
	 */
	public Scene create(SceneType type, Object... initParams) throws SceneCreationDataException
	{

		Constructor<?> constructor;
		Object controller;
		try
		{
			// Reflect the constructor for the controller
			Class<?> controllerClazz = type.getControllerClazz();
			Class<?>[] paramTypes = getConstructorParamTypes(initParams);

			// Instantiate a new instance of the controller
			constructor = controllerClazz.getDeclaredConstructor(paramTypes);
			controller = constructor.newInstance(initParams);
		}
		catch (NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new SceneCreationDataException("Unable to instantiate controller");
		}

		// Attempt to load the FXML
		FXMLLoader loader = new FXMLLoader(ResourceUtil.getResource(type.getFXMLName()));
		loader.setController(controller);
		
		Parent root;
		try
		{
			root = loader.load();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			throw new SceneCreationDataException("Invalid fxml file / component mapping");
		}

		Scene scene = new Scene(root);
		// Add relevant style sheets for this scene
		for (String cssPath : type.getCssNames())
		{
			String sheet = ResourceUtil.getResource(cssPath).toExternalForm();
			scene.getStylesheets().add(sheet);
		}
		return scene;
	}

	/**
	 * Convert an array of constructor parameters into an equivalent array of constructor parameters types.
	 *
	 * @param initParams the parameters
	 * @return the parameters mapped to types
	 */
	private Class<?>[] getConstructorParamTypes(Object... initParams)
	{
		if(initParams.length == 0)
			return new Class<?>[]{};
		
		Class<?>[] array = new Class<?>[initParams.length];
		for(int i = 0; i < initParams.length; i ++)
		{
			array[i] = initParams[i].getClass();
		}
		return array;
	}

	/**
	 * Get the singleton instance.
	 *
	 * @return the factory instance
	 */
	public static SceneFactory getInstance()
	{
		if(instance == null)
			instance = new SceneFactory();
		
		return instance;
	}

}
