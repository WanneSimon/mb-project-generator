package cc.wanforme.mbgenerator;

import java.util.regex.Pattern;

public class TemplateTest {

	public static void main(String[] args) {
		testReg();
	}
	
	
	public static void testReg() {
		String[] line = {
				"<mapper namespace=\"cc.wanforme.st.server.base.mapper.AuthTokenMapper\">"
		};
		String regStr = ".*cc\\.wanforme\\.st.*";
		
		for (String str : line) {
			System.out.println(Pattern.matches(regStr, str) + " " + str);
			String replaceAll = str.replaceAll("cc\\.wanforme\\.st", "cc.wanforme.munkblog");
			System.out.println(replaceAll);
			String replaceAll2 = str.replace("cc.wanforme.st", "cc.wanforme.munkblog");
			System.out.println(replaceAll2);
		}
		
	}
	
}
