package cc.wanforme.mbgenerator.entity;

import java.util.List;

public class PathRules {
	// 文件路径替换规则
	private List<RegAndStr> pathRules;
	
	public List<RegAndStr> getPathRules() {
		return pathRules;
	}
	public void setPathRules(List<RegAndStr> pathRules) {
		this.pathRules = pathRules;
	}
}
