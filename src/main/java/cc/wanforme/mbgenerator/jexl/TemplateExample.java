package cc.wanforme.mbgenerator.jexl;

import java.util.ArrayList;
import java.util.List;

public class TemplateExample {

	private String w;
	private String domain;

	private List<String> nums;
	
	public TemplateExample(String w, String domain) {
		super();
		this.w = w;
		this.domain = domain;
		
		this.nums = new ArrayList<>();
		nums.add("a");
		nums.add("b");
		nums.add("c");
	}
	
	public String getW() {
		return w;
	}
	public String getDomain() {
		return domain;
	}
	public List<String> getNums() {
		return nums;
	}
	
	public static void main(String[] args) {
		simpleExample(args);
	}
	
	public static void simpleExample(String[] args) {
		ExpressionResolver service = new ExpressionResolver();
		
		String rawText = "Hello ${w} \\${w} \\\\${w}, This is ${domain}! ${domain}\n${nums.size()+':'+ nums.get(0)+ nums.get(1)+ nums.get(2)}";
		TemplateExample data = new TemplateExample("world", "${domain value}");
		String result = service.export(data, rawText);
		
		System.out.println(rawText);
		// Hello ${w} \${w} \\${w}, This is ${domain}! ${domain}
		// ${nums.size()+':'+ nums.get(0)+ nums.get(1)+ nums.get(2)}
		System.out.println(">>>>>>>>>>>>");
		// >>>>>>>>>>>>
		System.out.println(result);
		// Hello world ${w} \${w}, This is ${domain value}! ${domain value}
		// 3:abc
		
		// JEXL: https://commons.apache.org/proper/commons-jexl/reference/syntax.html
	}
	
	public static void manualExample(String[] args) {
		// 不希望被解析成表达式时, 在前面添加 \ 转义，
		// 这个例子包含了 3 种特殊情况。（第一个 ${w} 和 最后一个 ${domain} 不见了）
		String teststr = "${w} Hello ${w} \\${w} \\\\${w}, This is ${domain}! ${domain}";
		ExpressionResolver resolver = new ExpressionResolver();
		
		List<String> expressions = resolver.scanExpressions(teststr);
		System.out.println("expressions: " + expressions);
		// expressions: [${domain}, ${w}]
//		TestObject obj = new TestObject("world", "baike.cc");
		TemplateExample obj = new TemplateExample("world", "${value}");
		String result = resolver.injectData(expressions, teststr, obj);
		System.out.println("result:\n" + result);
		// world Hello world ${w} \${w}, This is ${value}! 
	}
}
