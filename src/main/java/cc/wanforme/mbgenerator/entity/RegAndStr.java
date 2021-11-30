package cc.wanforme.mbgenerator.entity;

public class RegAndStr {

	// 匹配用的正则表达式
	private String matcher;
	// 替换用的正则表达式
	private String replace;
	// 新的值
	private String value;
	
	public String toJsonStr() {
		return "{ \"matcher\": \""+matcher+"\" }" + 
				"{ \"replace\": \""+replace+"\" }" +
				"{ \"value\": \""+value+"\" }";
	}
	
	public String getMatcher() {
		return matcher;
	}
	public void setMatcher(String matcher) {
		this.matcher = matcher;
	}
	public String getReplace() {
		return replace;
	}
	public void setReplace(String replace) {
		this.replace = replace;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
