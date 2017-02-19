package eu.erbs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils
{

	public static Properties loadProperties(String path) throws IOException
	{
		Properties properties;
		InputStream in = null;
		try
		{
			if (PropertyUtils.class.getClassLoader().getResource("resources/" + path) != null)
			{
				in = PropertyUtils.class.getClassLoader().getResourceAsStream("resources/" + path);
			}
			else if (PropertyUtils.class.getClassLoader().getResourceAsStream("resources/" + path) != null)
			{
				in = PropertyUtils.class.getClassLoader().getResourceAsStream("resources/" + path);
			}
			else if (PropertyUtils.class.getClassLoader().getResource(path) != null)
			{
				in = PropertyUtils.class.getClassLoader().getResourceAsStream(path);
			}
			else
			{
				in = PropertyUtils.class.getClassLoader().getResourceAsStream(path);

			}
			properties = new Properties();
			properties.load(in);
		}
		finally
		{
			in.close();
		}
		return properties;
	}
}