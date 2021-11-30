package cc.wanforme.mbgenerator.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cc.wanforme.mbgenerator.template.resolver.ExportResolver;
import cc.wanforme.mbgenerator.template.resolver.TemplateResolver;
import cc.wanforme.mbgenerator.util.PathResource;

/** 处理中心
 * @since 2021-11-29
 */
@Service
public class TemplateManager {
	private static final Logger log = LoggerFactory.getLogger(TemplateManager.class);
	
	protected List<TemplateResolver> resolvers = new ArrayList<>();
	protected ExportResolver exportResolver;
	
	
	public void addResolver(TemplateResolver resolver) {
		resolvers.add(resolver);
	}
	
	public void setExportResolver(ExportResolver exportResolver) {
		this.exportResolver = exportResolver;
	}
	
	/** 处理单个文件
	 * @param absolutFile 文件的绝对路径
	 * @param relativeFile 文件在项目中的相对卢路径
	 */
	public void resolve(String absolutFile, String relativeFile) {
		InputStream is = null;
		for (TemplateResolver resolver : resolvers) {
			try{
				is = PathResource.loadResource(absolutFile);
				InputStream newIs = resolver.resolve(relativeFile, is);
				is.close();
				is = newIs;
			} catch (IOException e) {
				log.error("convert error, ignored!", e);
			}
		}
		
		if(is!=null) {
			try {
				exportResolver.export(relativeFile, is);
			} catch (IOException e) {
				log.error("export error, ignored! " + relativeFile, e);
			}
		}
	}
	
}
