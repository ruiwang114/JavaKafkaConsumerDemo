package io.netty.example.http.websocketx.base;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertyUtil {
	private static PropertyUtil reader = new PropertyUtil();

	private Properties props = new Properties();

	public PropertyUtil() {
//		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config/parameter.properties");
//		System.out.println(this.getClass().getClassLoader().getResource("parameter.properties").getPath());
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("parameter.properties");
		try {
			props.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static PropertyUtil getInstance() {
		return reader;
	}

	public String read(String key) {
		String value = props.getProperty(key);
//		System.out.println(value);

		log.info(key+" : "+value);

		return value;
	}

	public static Properties load(String path) {
		Properties properties = new Properties();
		String basePath = getInstance().getClass().getClassLoader().getResource("/").getPath();
		String fullPath = basePath + "/config/config.properties";

		InputStream inputStream = getInstance().getClass().getClassLoader().getResourceAsStream(path);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			System.out.println("加载配置文件失败:" + fullPath);
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return properties;
	}

	/**
	 * 根据传输的路径获取配置属性
	 * 
	 * @param filePath
	 * @return
	 */
	public Properties getProperties(String filePath) {
		Properties m_properties = new Properties();
		try {
			System.out.println(this.getClass().getClassLoader().getResource("").getPath());

			String basePath = this.getClass().getClassLoader().getResource("/").getPath();
			basePath = basePath.replaceAll("%20", " ");
			String path = basePath + filePath;
			m_properties.load(new FileInputStream(path));
		} catch (Exception ex) {
			ex.printStackTrace();
			m_properties = null;
		}
		return m_properties;
	}
}
