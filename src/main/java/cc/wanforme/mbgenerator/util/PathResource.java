package cc.wanforme.mbgenerator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.ClassPathResource;

/** 
 * @author wanne
 * 2020年7月10日
 */
public class PathResource {
	private static final Logger log = LoggerFactory.getLogger(PathResource.class);
	
	private static ApplicationHome home = null;
	
	/** 先尝试 直接在文件系统中加载文件，<br>
	 * 然后再根据当前项目所处路径拼接成绝对路径去查找，<br>
	 * 最后尝试加载 jar 内部的资源
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public static InputStream loadResource(String path) throws IOException {
		// 直接寻找文件系统中的文件 （绝对路径的情况下）
		try {
			return new FileInputStream(path);
		} catch (FileNotFoundException e) {
			// 使用运行时所在路径拼接绝对路径（相对路径）
			File file = new File(getProjectLocation().getAbsolutePath() + "/" + path);
			log.info("[CheckResource] existed ? (" + file.exists()+"): " + file.getAbsolutePath());
			if( file.exists() && file.isFile()) {
				return new FileInputStream(file);
			} else {
				// 读取jar包内部的资源
				ClassPathResource resource = new ClassPathResource(path);
				return resource.getInputStream();
			}
		}
	}
	
	/** 保存 classPath 资源到外部
	 * @param classPath
	 * @param outPath
	 * @throws IOException
	 */
	public static void saveClassPathResource(String classPath, String outPath)
			throws IOException {
//		InputStream loadResource = loadResource(classPath);
//		FileUtil.saveFile(loadResource, outPath);
		try (InputStream is = loadResource(classPath);
			 FileOutputStream os = new FileOutputStream(new File(outPath))){
			int len = 0;
			byte[] bs = new byte[2048];
			while( (len=is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
		}
	}
	
	/** 检查文件是否存在
	 * @param outFile
	 * @return
	 */
	public static boolean isFileExisted(String outFile) {
		File file = new File(outFile);
		if(file.exists() && file.isFile()) {
			return true;
		}
		
		file = new File(getProjectLocation(), outFile);
		if(file.exists() && file.isFile()) {
			return true;
		}
		return false;
	}
	
	/** 获取项目 ApplicationHome 对象*/
	public static ApplicationHome getApplicationHome() {
		synchronized (PathResource.class) {
			if(home == null) {
				home = new ApplicationHome();
			}
			return home;
		}
	}
	
	/** 获取项目所在路径*/
	public static File getProjectLocation() {
		return getApplicationHome().getDir();
	}
	
}
