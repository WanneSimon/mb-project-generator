package cc.wanforme.mbgenerator.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/** 项目转模板配置
 * @since 2021-11-29
 */
@Component
public class TemplateConfig {

	@Value("${projectPath}")
	private String projectPath;
	@Value("${outPath}")
	private String outPath;
	@Value("${ignore.list}")
	private List<String> ignoreList;
	@Value("${ignore.reg}")
	private List<String> ignoreReg;
	@Value("${ruleFile}")
	private String ruleFile;
	@Value("${pathFile}")
	private String pathFile;
	
	public String getOutPath() {
		return outPath;
	}
	public void setOutPath(String outPath) {
		this.outPath = outPath;
	}
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public List<String> getIgnoreList() {
		return ignoreList;
	}
	public void setIgnoreList(List<String> ignoreList) {
		this.ignoreList = ignoreList;
	}
	public List<String> getIgnoreReg() {
		return ignoreReg;
	}
	public void setIgnoreReg(List<String> ignoreReg) {
		this.ignoreReg = ignoreReg;
	}
	public String getRuleFile() {
		return ruleFile;
	}
	public void setRuleFile(String ruleFile) {
		this.ruleFile = ruleFile;
	}
	public String getPathFile() {
		return pathFile;
	}
	public void setPathFile(String pathFile) {
		this.pathFile = pathFile;
	}
}
