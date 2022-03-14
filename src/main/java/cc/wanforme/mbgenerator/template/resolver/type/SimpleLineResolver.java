package cc.wanforme.mbgenerator.template.resolver.type;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import cc.wanforme.mbgenerator.config.TemplateConfig;
import cc.wanforme.mbgenerator.entity.RegAndStr;
import cc.wanforme.mbgenerator.entity.SimpleLineRules;
import cc.wanforme.mbgenerator.exception.NoRulesException;
import cc.wanforme.mbgenerator.template.resolver.BaseTemplateResolver;
import cc.wanforme.mbgenerator.util.PathResource;

/** 简单对每行字符串进行正则匹配和替换
 * @since 2021-11-29
 */
public class SimpleLineResolver extends BaseTemplateResolver {
	protected SimpleLineRules lineRules;
	
	public SimpleLineResolver(TemplateConfig config) {
		super(config);
	}

	public void refresh() {
		try {
			this.readRegexAndReplaceString();
		} catch (IOException e) {
			throw new NoRulesException("No rules can be fund! is the file existed ? ", e);
		}
	}
	
	/** 初始化, 找出替换时需要用到的正则表达式和被替换字符串 
	 * @throws IOException 
	 * @throws FileNotFoundException */
	private void readRegexAndReplaceString() throws FileNotFoundException, IOException {
		String ruleFile = config.getRuleFile();
		
		// 读取规则文件
		TypeDescription customDesc = new TypeDescription(SimpleLineRules.class);
		customDesc.addPropertyParameters("rules", RegAndStr.class);
		Constructor constructor = new Constructor(SimpleLineRules.class);
		constructor.addTypeDescription(customDesc);
		Yaml yaml = new Yaml(constructor);
		
		try (InputStream fis = PathResource.loadResource(ruleFile)) {
			SimpleLineRules loadAs = yaml.loadAs(fis, SimpleLineRules.class);
			lineRules = loadAs;
		}
		
		// 转换成正则表达式
		if(lineRules==null) {
			throw new NoRulesException("No rules in file: '" + ruleFile + "', please check file!");
		}
		
	}
	
	
	@Override
	protected boolean isMatch(String relativePath) {
		return true;
	}

	/**
	 * 返回新生成的字节流
	 */
	@Override
	protected ByteArrayInputStream handle(InputStream is) {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(is);
				
			StringBuffer sb = new StringBuffer();

			while (in.hasNext()) {
				String line = in.nextLine();
				boolean isHandled = false;
				for (RegAndStr ras : this.lineRules.getRules()) {
					// 只能由其中某一个处理
					if (Pattern.matches(ras.getMatcher(), line)) {
						sb.append(line.replaceAll(ras.getReplace(), ras.getValue()));
						sb.append(System.lineSeparator());
						isHandled = true;
					} 
				}
				if(!isHandled) {
					sb.append(line + System.lineSeparator());
				}
			}
			return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
	}

}
