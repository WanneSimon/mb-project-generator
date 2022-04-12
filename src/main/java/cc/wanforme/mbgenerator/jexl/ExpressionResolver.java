package cc.wanforme.mbgenerator.jexl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.ObjectContext;

/** 模板引擎导出
 *
 */
public class ExpressionResolver {
	// 表达式匹配规则 （检测表达式）  // 匹配 '${...}'， 同时中间不能出现 $或{
	// 表达式中不能出现 '$' 和 '{'
	private static final String EXPRESSION_MATCHER = "(?<!\\\\)(\\$\\{[^${]+\\})";
//	private static final String EXPRESSION_MATCHER = "(?<!\\\\)(\\$\\{([^$][^{])+\\})";
	// 表达式合法性检查 // 匹配 '${}'
	private static final String EXPRESSION_LEGAL_MATCHER = "\\$\\{.+\\}";
	// 正则表达式的元字符（保留字符）
	private static final String[] REG_META_ARRAY = { "\\", "^", "$", "*", "+", "?", ",", ".",
			"!", ":", "-", "<", "|", "=",
			"{", "}", "{", "}", "(", ")", "[", "]",  };
	
	private JexlEngine engine;
	
	private Pattern expressionMatchPattern;
	private Pattern expressionLegalPattern;
	/* 出现复杂表达式时，需要转义的字符 
	 * 例如: ${nums.size()+':'+ nums.get(0)+ nums.get(1)+ nums.get(2)}
	 * 则需要转义 .()+:
  	 */
	private List<String> regexMetaList;
	
	public ExpressionResolver() {
		engine = new JexlEngine();
		expressionMatchPattern = Pattern.compile(EXPRESSION_MATCHER);
		expressionLegalPattern = Pattern.compile(EXPRESSION_LEGAL_MATCHER);
		regexMetaList = new ArrayList<>(Arrays.asList(REG_META_ARRAY));
	}
	
	/** 简单的实现
	 */
	public <T> String export(T data, String rawText) {
		// 两边加空格是因为表达式出现在 开始和结尾时，会有 bug
		rawText = " "+rawText+" ";
		List<String> expressions = this.scanExpressions(rawText);
		String result = this.injectData(expressions, rawText, data, false);
		return result.substring(1, result.length()-1);
	}
	
	// 获取文本中出现的所有表达式， 表达式用 '${}' 包裹
	public List<String> scanExpressions(String rawTxt) {
		Set<String> re = new HashSet<>();
		Matcher matcher = expressionMatchPattern.matcher(rawTxt);
		while (matcher.find()) {
			re.add(matcher.group(0));
		}
		return new ArrayList<String>(re);
	}
	
	public <T> String injectData (List<String> expressions, String rawText, T t) {
		return injectData(expressions, rawText, t, true);
	}
	
	/** 
	 * 1. rawText 首尾不能出现表达式（如果首位出现表达式，可以在首尾添加换行来解决）。<br>
	 * 2. 当某个字符串不希望被解析成表达式时, 在前面添加 \ 转义。 例如： '${w}' => '\${w}' <br>
	 * 3. '\${w}' 最后会变成 '${w}'，多添加一个 '\' 即可: '\${w}' => '\\${w}'。
	 * 这个问题跟 {@link String#split(String)} 方法有关， 此方法不会返回空结果。（但JS会返回空结果）
	 * @param <T>
	 * @param expressions rawText中出现的表达式
	 * @param rawText 原生字符串
	 * @param t 数据对象。 public修饰的公共类，需要 getters。（相关问题：'#0.nums;' inaccessible or unknown property #0）
	 * @param check 是否需要表达式语法检查
	 * @return
	 */
	public <T> String injectData (List<String> expressions, String rawText, T t, boolean check) {
		// 1.检查表达式的合法性
		if(expressions==null || expressions.isEmpty()) {
			return rawText;
		}
		
		Stream<String> stream = expressions.stream();
		if(check) {
			stream = stream.filter(e -> expressionLegalPattern.matcher(e).matches());
		}
		List<String> esList = stream
					.map(e -> e.substring(2,e.length()-1)) // ${domain} => domain
					.collect(Collectors.toList());
		if(esList==null || esList.isEmpty()) {
			return rawText;
		}
		
		String reText = rawText;
		
		JexlContext context = new ObjectContext<T>(engine, t);
		
//		System.out.println("datas:" + context.get("w")+","+context.get("domain"));
		
		for (String es : esList) {
			// 2.计算表达式的值
			Expression exp = engine.createExpression(es);
			Object value = exp.evaluate(context);
			String valueStr = value.toString();
			
			// 处理特殊情况: 转义，防止表达式的值中出现 ${.*} 这样的内容
			valueStr = valueStr.replace("${", "\\${");
			
			// 3. 使用表达式进行分割
			// Hello ${w} ${w}, This is ${domain}!   =>  ['Hello ${w} ${w}, This is', '', '!'] 
			// 正则字符转义，防止被识别成正则表达式的保留字符
			//String escapedExpression = es.replace(".", "\\.");
			String escapedExpression = this.escapeRegexMeta(es);
			// 根据表达式 ${w} 分割， 以 \${ 开始视为普通字符 
			String splitReg = "(?<!\\\\)\\$\\{" + escapedExpression +"\\}";
			String[] splitArr = reText.split(splitReg);
			
			// 4. 使用表达式的值连接分割后的数组
			reText = String.join(valueStr, splitArr);
		}
		
		// 处理特殊情况： 恢复
		reText = reText.replace("\\${", "${");
		return reText;
	}
	
	protected String escapeRegexMeta(String str) {
//		String re = str;
//		for (String meta : regexMetaList) {
//			re = re.replace(meta, "\\"+meta);
//		}
//		return re;
		
		StringBuilder re = new StringBuilder();
		for (int i=0; i<str.length(); i++) {
			String t = str.charAt(i)+"";
			if(regexMetaList.contains(t)) {
				re.append("\\");
			}
			re.append(t);
		}
		return re.toString();
	}
	
	public List<String> getRegexMetaList() {
		return regexMetaList;
	}
	
	public static void main(String[] args) {
		// 不希望被解析成表达式时, 在前面添加 \ 转义，
		// 这个例子包含了 3 种特殊情况。（第一个 ${w} 和 最后一个 ${domain} 不见了）
		String teststr = "${w} Hello ${w} \\${w} \\\\${w}, This is ${domain}! ${domain}";
		ExpressionResolver resolver = new ExpressionResolver();
		
		List<String> expressions = resolver.scanExpressions(teststr);
		System.out.println("expressions: " + expressions);
		// expressions: [${domain}, ${w}]
//		TestObject obj = new TestObject("world", "baike.cc");
		TestObject obj = new TestObject("world", "${value}");
		String result = resolver.injectData(expressions, teststr, obj);
		System.out.println("result:\n" + result);
		// world Hello world ${w} \${w}, This is ${value}! 
	
//		String st = "Hello ${w} ${w}, This is \\${w}!${domain}";
//		String reg = "(?<!\\\\)\\$\\{domain\\}" ;
//		String[] arr = st.split(reg);
//		System.out.println(Arrays.toString(arr));
		
		// 正则字符转义测试
		String escape = resolver.escapeRegexMeta("nums.size()+':'+ nums.get(0)+ nums.get(1)+ nums.get(2)");
		System.out.println("escaped:\n " + escape);
		
		String str2 = "Hi ${w}! It's ${domain}!";
		TestObject obj2 = new TestObject("siri", "sunday");
		System.out.println(resolver.export(obj2, str2));
	}
	
	/** 测试类，注意必须是 public */
	public static class TestObject {
		private String w;
		private String domain;
		
		public TestObject(String w, String domain) {
			super();
			this.w = w;
			this.domain = domain;
		}
		
		public String getW() {
			return w;
		}
		public String getDomain() {
			return domain;
		}
	}
}
