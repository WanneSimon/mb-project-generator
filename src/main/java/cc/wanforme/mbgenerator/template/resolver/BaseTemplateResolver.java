package cc.wanforme.mbgenerator.template.resolver;

import java.io.InputStream;

import cc.wanforme.mbgenerator.config.TemplateConfig;

/** 文件处理的基础实现
 * @since 2021-11-29
 */
public abstract class BaseTemplateResolver implements TemplateResolver {
	protected TemplateConfig config;
	
	public BaseTemplateResolver(TemplateConfig config) {
		this.config = config;
		this.refresh();
	}
	
	/**
	 * 初始化和刷新
	 */
	public abstract void refresh();
	
	@Override
	public InputStream resolve(String relativePath, InputStream is) {
		if(this.isMatch(relativePath)) {
			return this.handle(is);
		}
		return is;
	}
	
	/** 传入文件 和 文件的相对路径, 返回是否处理这个文件 
	 * @param relativePath 文件相对路径
	 * @return
	 */
	protected abstract boolean isMatch(String relativePath);
	
	/** 处理当前的字符流, 返回新生成的字节流。
	 * @param is 
	 * @return 
	 */
	protected abstract InputStream handle(InputStream is);
	
}
