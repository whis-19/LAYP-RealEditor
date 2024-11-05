
package DAL;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class AbstractDAOEditorFactory implements IDAOEditorFactory {

	private static IDAOEditorFactory instance = null;

	public static final IDAOEditorFactory getInstance() {

		if (instance == null) {
			String factoryClassName = null;
			try (FileInputStream input = new FileInputStream("config.properties")) {
				Properties prop = new Properties();
				prop.load(input);
				factoryClassName = prop.getProperty("db.type");
				Class<?> clazz = Class.forName(factoryClassName); // Load class by name
				instance = (IDAOEditorFactory) clazz.getDeclaredConstructor().newInstance(); // Instantiate class
			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

}
