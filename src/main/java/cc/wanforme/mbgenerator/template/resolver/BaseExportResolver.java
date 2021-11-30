package cc.wanforme.mbgenerator.template.resolver;

import cc.wanforme.mbgenerator.config.TemplateConfig;

/** 文件导出的基础实现
 * @since 2021-11-30
 */
public abstract class BaseExportResolver implements ExportResolver {

	protected TemplateConfig config;
	
	public BaseExportResolver(TemplateConfig config) {
		this.config = config;
		this.refresh();
	}
	
	/**
	 * 初始化和刷新
	 */
	public abstract void refresh();
	
}
