package cc.wanforme.mbgenerator.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.yaml.snakeyaml.Yaml;

public class ConfigReader {

	public static TemplateConfig readTemplateConfig(String configPath)
			throws FileNotFoundException {
		FileInputStream config = new FileInputStream(configPath);
		Yaml yaml = new Yaml();
		TemplateConfig ttc = yaml.loadAs(config, TemplateConfig.class);
		return ttc;
	}
	

}
