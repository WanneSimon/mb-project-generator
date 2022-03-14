## 基于JEXL和正则表达式的简约模板引擎
<br/>
### 效果
`Hi ${name}! It's ${day}!`  \>\>\>  `Hi siri! It's sunday!`
<br/>

### 规则

  1. 表达式使用 `${}` 括起来；例如： `${domain}`
  2. 表达式中不能出现 `$` 和 `{` ，例如：`${domain.$a}`；
  3. 当出现类似成表达式的字符串时, 在前面添加 `\` 转义。 例如： `${w}` => `\${w}`；
  4. 字符串 `\${w}` 最后会变成 `${w}`，多添加一个 `\` 即可。例如： `\${w}` => `\\${w}`。
  5. 输入字符串首尾不能出现表达式，但是可以通过首位添加无意义字符规避。例如在首尾添加空格或换行。参考 `<T> String export(T data, String rawText)` 方法。
  5. 数据类必须是公共类，有所有的属性的 `get` 方法。

<br/>

### 自动处理

```java
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
	
	public String getW() { return w; }
	public String getDomain() { return domain; }
	public List<String> getNums() { return nums; }
	
	public static void main(String[] args) {
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
}
```

<br/>

### 手动调用

```java
		// 不希望被解析成表达式时, 在前面添加 \ 转义，
		// 这个例子包含了 3 种特殊情况。（第一个 ${w} 和 最后一个 ${domain} 不见了）
		String teststr = "${w} Hello ${w} \\${w} \\\\${w}, This is ${domain}! ${domain}";
		ExpressionResolver resolver = new ExpressionResolver();
		
		List<String> expressions = resolver.scanExpressions(teststr);
		System.out.println("expressions: " + expressions);
		// expressions: [${domain}, ${w}]
		
        //TestObject obj = new TestObject("world", "baike.cc");
		TemplateExample obj = new TemplateExample("world", "${value}");
		String result = resolver.injectData(expressions, teststr, obj);
		System.out.println("result:\n" + result);
		// world Hello world ${w} \${w}, This is ${value}! 
```

<br/>

### 常见问题

1. `'#0.day;' inaccessible or unknown property #0`

   (1) 检查是否存在 `day` 这个变量；(2) 检查类是否是 `public`，`day`变量是否有 `get` 方法。
