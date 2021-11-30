package cc.wanforme.mbgenerator.entity;

import java.util.List;

public class SimpleLineRules {
	// 每行字符串替换规则
	private List<RegAndStr> rules;
	
	public List<RegAndStr> getRules() {
		return rules;
	}
	public void setRules(List<RegAndStr> rules) {
		this.rules = rules;
	}
}
