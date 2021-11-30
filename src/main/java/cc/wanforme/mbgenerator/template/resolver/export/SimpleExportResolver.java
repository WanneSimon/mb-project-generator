package cc.wanforme.mbgenerator.template.resolver.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import cc.wanforme.mbgenerator.config.TemplateConfig;
import cc.wanforme.mbgenerator.entity.PathRules;
import cc.wanforme.mbgenerator.entity.RegAndStr;
import cc.wanforme.mbgenerator.exception.NoRulesException;
import cc.wanforme.mbgenerator.template.resolver.BaseExportResolver;
import cc.wanforme.mbgenerator.util.PathResource;

/** 文件导出处理
 * @since 2021-11-30
 */
public class SimpleExportResolver extends BaseExportResolver {
	private static final Logger log = LoggerFactory.getLogger(SimpleExportResolver.class);
	
	private PathRules pathRules;
	
	public SimpleExportResolver(TemplateConfig config) {
		super(config);
	}

	@Override
	public void refresh() {
		try {
			this.readFileRules();
		} catch (IOException e) {
			throw new NoRulesException("No path-rules can be fund! is the file existed ? ", e);
		}
	}
	
	public void readFileRules() throws FileNotFoundException, IOException {
		String ruleFile = config.getPathFile();
		
		// 读取路径规则文件
		TypeDescription customDesc = new TypeDescription(PathRules.class);
		customDesc.addPropertyParameters("pathRules", RegAndStr.class);
		Constructor constructor = new Constructor(PathRules.class);
		constructor.addTypeDescription(customDesc);
		Yaml yaml = new Yaml(constructor);
		
		try (InputStream fis = PathResource.loadResource(ruleFile)) {
			PathRules loadAs = yaml.loadAs(fis, PathRules.class);
			pathRules = loadAs;
		}
		
		if(pathRules==null) {
			throw new NoRulesException("No path-rules in file: '" + ruleFile + "', please check file!");
		}
	}

	@Override
	public void export(String relativePath, InputStream is) throws IOException {
		// 计算最终路径
		String outPath = relativePath;
		RegAndStr matched = null;
		for (RegAndStr ras : pathRules.getPathRules()) {
			if(relativePath.matches(ras.getMatcher())) {
				matched = ras;
				outPath = relativePath.replaceFirst(ras.getReplace(), ras.getValue());
				break;
			}
		}
		
		if (matched != null) {
			log.info("\n"+String.format("{\"%s\": %s}\n%s", relativePath, relativePath, matched.toJsonStr()));
		} else {
			log.info("\n"+String.format("{\"%s\": %s}", relativePath, relativePath));
		}
		
		File outFile = new File(config.getOutPath(), outPath);
		if(!outFile.getParentFile().exists()) {
			outFile.getParentFile().mkdirs();
		}
		
		int len = 0;
		byte[] bs = new byte[2048];
		try (FileOutputStream fos = new FileOutputStream(outFile)) {
			while( (len=is.read(bs)) != -1) {
				fos.write(bs, 0, len);
			}
		}
	}

}
