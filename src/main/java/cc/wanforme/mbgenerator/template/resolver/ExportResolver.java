package cc.wanforme.mbgenerator.template.resolver;

import java.io.IOException;
import java.io.InputStream;

/**
 * 导出处理器
 * @since 2021-11-30
 */
public interface ExportResolver {

	/**  
	 * 导出文件
	 * @param relativePath 文件的旧相对路径
	 * @parm is 文件字节流
	 * @return 文件是否已保存
	 */
	void export(String relativePath, InputStream is) throws IOException;
	
}
