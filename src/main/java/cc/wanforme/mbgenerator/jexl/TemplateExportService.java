package cc.wanforme.mbgenerator.jexl;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.ObjectContext;

/** 模板引擎导出
 *
 */
public class TemplateExportService {
	// 表达式匹配规则 （检测表达式）  // 匹配 '${...}'， 同时中间不能出现 $或{
	private static final String EXPRESSION_MATCHER = "(?<!\\\\)(\\$\\{[^${]+\\})";
	// 表达式合法性检查 // 匹配 '${}'
	private static final String EXPRESSION_LEGAL_MATCHER = "\\$\\{.+\\}";
	// 表达式转义
//	private static final String ESCAPE_VALUE_PART = "\\${";
	
	
	/**
	 * 导出基础类型组成的对象，非基础数据忽略。 (复杂对象不确定)
	 */
	public <T> void exportSimpleObject(T t, String tempaltePath, String fileName) {
		// 表达式引擎
		JexlEngine engine = new JexlEngine();
		JexlContext context = new ObjectContext<T>(engine, t);

		// 识别模板中的表达式
		
	}
	
	// 获取文本中出现的所有表达式， 表达式用 '${}' 包裹
	public static List<String> scanExpressions(String rawTxt) {
		String regstr = EXPRESSION_MATCHER;
		Pattern pattern = Pattern.compile(regstr);
		
		Set<String> re = new HashSet<>();
		Matcher matcher = pattern.matcher(rawTxt);
		while (matcher.find()) {
			re.add(matcher.group(0));
		}
		return new ArrayList<String>(re);
	}
	
	
	/** 
	 * 1. rawText 首尾不能出现表达式（如果首位出现表达式，可以在首尾添加换行来解决）。<br>
	 * 2. 当某个字符串不希望被解析成表达式时, 在前面添加 \ 转义。 例如： '${w}' => '\${w}' <br>
	 * 3. '\${w}' 最后会变成：'${w}'，多添加一个 '\' 即可: '\${w}' => '\\${w}'
	 * 这个问题跟 {@link String#split(String)} 方法有关， 此方法不会返回空结果。（但JS会返回空结果）
	 * @param <T>
	 * @param expressions rawText中出现的表达式
	 * @param rawText 原生字符串
	 * @param t public修饰的公共类
	 * @return
	 */
	public static <T> String injectData (List<String> expressions, String rawText, T t) {
		// 1.检查表达式的合法性
		if(expressions==null || expressions.isEmpty()) {
			return rawText;
		}
		String legealReg = EXPRESSION_LEGAL_MATCHER;
		Pattern pattern = Pattern.compile(legealReg);
		List<String> esList = expressions.stream()
				.filter(e -> pattern.matcher(e).matches())
				.map(e -> e.substring(2,e.length()-1)) // ${domain} => domain
				.collect(Collectors.toList());
		if(esList==null || esList.isEmpty()) {
			return rawText;
		}
		
		String reText = rawText;
		
		JexlEngine engine = new JexlEngine();
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
			// dot 转义，防止被识别成正则表达式的保留字符
			String escapedExpression = es.replace(".", "\\.");
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
	
	
	public static void main(String[] args) {
		// 不希望被解析成表达式时, 在前面添加 \ 转义，
		// 这个例子包含了 3 种特殊情况
		String teststr = "Hello ${w} \\${w} \\\\${w}, This is ${domain}! ${domain}";
		
		List<String> expressions = scanExpressions(teststr);
		System.out.println("expressions: " + expressions);
//		TestObject obj = new TestObject("world", "baike.cc");
		TestObject obj = new TestObject("world", "${value}");
		String result = injectData(expressions, teststr, obj);
		System.out.println("result:\n" + result);
	
//		String st = "Hello ${w} ${w}, This is \\${w}!${domain}";
//		String reg = "(?<!\\\\)\\$\\{domain\\}" ;
//		String[] arr = st.split(reg);
//		System.out.println(Arrays.toString(arr));
		
	}
	
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
