package net.ben.stocks.userinterface.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import net.ben.stocks.userinterface.exception.SceneCreationDataException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SceneFactory
{
	/**
	 * The singleton instance of the {@link SceneFactory}.
	 */
	private static SceneFactory instance;
	
	private SceneFactory() {}
	
	public Scene create(SceneType type, Object... initParams)
					throws SceneCreationDataException
	{
		
		Constructor<?> constructor;
		Object controller;
		try
		{
			Class<?> controllerClazz = type.getControllerClazz();
			Class<?>[] paramTypes = getConstructorParamTypes(initParams);
			
			constructor = controllerClazz.getDeclaredConstructor(paramTypes);
			controller = constructor.newInstance(initParams);
		}
		catch (NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new SceneCreationDataException("Unable to instantiate controller");
		}

		// Attempt to load the FXML
		FXMLLoader loader = new FXMLLoader(type.getControllerClazz()
													.getResource(type.getFXMLName()));
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
        String sheet = type.getControllerClazz().getResource(type.getCssName()).toExternalForm();
        scene.getStylesheets().add(sheet);
        
		return scene;
	}
	
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
	
	public static SceneFactory getInstance()
	{
		if(instance == null)
			instance = new SceneFactory();
		
		return instance;
	}

}
