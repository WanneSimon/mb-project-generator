package cc.wanforme.mbgenerator.template.resolver;

import java.io.InputStream;

/**
 * @since 2021-11-29
 */
public interface TemplateResolver {
	
	/**  
	 * 处理当前的字符流, 返回新生成的字符流
	 * @param relativePath 文件的相对路径
	 * @parm is 文件字节流
	 * @return
	 */
	InputStream resolve(String relativePath, InputStream is);
	
}
