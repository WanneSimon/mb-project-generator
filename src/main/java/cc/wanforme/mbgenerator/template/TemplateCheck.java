package cc.wanforme.mbgenerator.template;

import java.io.File;
import java.util.List;
import java.util.Objects;

import cc.wanforme.mbgenerator.config.TemplateConfig;

/**
 * 
 * @since 2021-11-29
 */
public class TemplateCheck {

	
	
	/** 检查是否是忽略文件
	 * @param fileAbsPath
	 * @param config
	 * @return
	 */
	public static boolean isIgnoreFile(String fileAbsPath, TemplateConfig config) {
		String relativePath = fileAbsPath.replace(config.getProjectPath(), "");
		if(relativePath.startsWith(File.separator)) {
			relativePath = relativePath.substring(1);
		}
		
		// 直接指定的文件, 比较绝路径
		List<String> igList = config.getIgnoreList();
		for (String igFile : igList) {
			File igAbsFile = new File(config.getProjectPath(), igFile);
			if(Objects.equals(igAbsFile.getAbsolutePath(), fileAbsPath)) {
				return true;
			}
		}
		
		// 正则表达式, 匹配相对路径
		List<String> igRegs = config.getIgnoreReg();
		for (String reg : igRegs) {
			if(relativePath.matches(reg)) {
				return true;
			}
		}
		return false;
	}
	
}
