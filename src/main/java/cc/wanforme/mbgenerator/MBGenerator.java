package cc.wanforme.mbgenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cc.wanforme.mbgenerator.config.TemplateConfig;
import cc.wanforme.mbgenerator.template.TemplateCheck;
import cc.wanforme.mbgenerator.template.TemplateManager;
import cc.wanforme.mbgenerator.template.resolver.export.SimpleExportResolver;
import cc.wanforme.mbgenerator.template.resolver.type.SimpleLineResolver;

/**
 * @since 2021-11-29
 */
@Component
public class MBGenerator implements CommandLineRunner{
	private static final Logger log = LoggerFactory.getLogger(MBGenerator.class);
	
	@Autowired
	private TemplateConfig ttc;
	@Autowired
	private TemplateManager manager;
	
	public void init() {
		manager.addResolver(new SimpleLineResolver(ttc));
		manager.setExportResolver(new SimpleExportResolver(ttc));
	}
	
	@Override
	public void run(String... args) throws Exception {
		this.init();
		
		this.convert(new File(ttc.getProjectPath()), ttc);
		
		System.out.println("=== end ====");
		
	}
	
	public void convert(File dir, TemplateConfig ttc) {
		if (dir==null || dir.listFiles()==null || dir.listFiles().length==0) {
			return;
		}
		
		File[] fs = dir.listFiles();
		List<File> dirs = new ArrayList<>();
		for (File f : fs) {
			boolean isIgnore = TemplateCheck.isIgnoreFile(f.getAbsolutePath(), ttc);
			if(isIgnore) {
				log.info("ignore: " + f.getAbsolutePath());
			} else if(f.isDirectory()) { // 被忽略的文件夹就不要了
				dirs.add(f);
		 	} else {
		 		String relativePath = f.getAbsolutePath().substring(ttc.getProjectPath().length()+1);
		 		manager.resolve( f.getAbsolutePath(), relativePath);
		 	}
		}
		
		dirs.forEach(e -> convert(e, ttc));
	}


}
