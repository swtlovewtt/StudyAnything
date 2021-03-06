package Main;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.filechooser.FileSystemView;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.Dom4JXmlWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import XMLParser.*;



/*
 XMLChecker 是一个占位符检查器,用来检查安卓开发的各国语言文件中的占位符的问题.


 本类有两个功能
 1.检查占位符是否有遗漏的,不一致的.
 2.检查某一种语言中的占位符是否和英文中的是一样的.(类型和数量一一进行比对)

 check 方法用于功能1
 checkPlaceHolders 方法用于功能2
 */
public class XMLChecker {
	public static long checkTime;
	public static int errorCount;
	public static Date startDate;
	public static Date endDate;

	public XMLChecker() {
		super();
		checkTime = 0;

	}

	public static boolean isMac(){
		String os = System.getProperty("os.name");
		// 是mac电脑
		boolean isMac = false;
		if (os.indexOf("Mac") != -1) {
			isMac = true;
		} else {

		}
		return isMac;
	}

	// 根路径 相当于home下的桌面下的xml 就是home/desktop/xml
	public static String homeDirectory() {
		String os = System.getProperty("os.name");
		// 是mac电脑
		boolean isMac = false;
		if (os.indexOf("Mac") != -1) {
			isMac = true;
		} else {

		}

		FileSystemView fsv = FileSystemView.getFileSystemView();
		// 将桌面的那个文件目录赋值给file
		// File desktop=fsv.getHomeDirectory();
		String path = fsv.getHomeDirectory().getAbsolutePath();
		if (isMac) {
			path = path + "/Desktop/xml";
		} else {
			path = path + "\\xml";
		}

		return path;
	}

	public void printDate(){
		Date date1 = new Date();
		System.out.println("print Date:" + date1.toString());
	}


	/*
	检查桌面下名字为xml的文件夹下所有的xml文件,遍历筛选字符串


	 */
	public void check() {
				Date date1 = new Date();
				startDate = date1;
				System.out.print("start checking place holder grammer...  \n");

				String home = XMLChecker.homeDirectory();
				//得到home路径下所有的xml文件
				ArrayList<String> findAllXMLFilePathes = findAllXMLFilePathesAtPath(home);

				System.out.print("number of xml files: " + findAllXMLFilePathes.size() + "\n\n");
				//遍历该数组,挨个检查
				for (int i = 0; i < findAllXMLFilePathes.size(); i++) {

					//根据文件路径来检查
					checkFileWithPath(findAllXMLFilePathes.get(i));
				}

				date1 = new Date();
				endDate = date1;
				long useTime = (endDate.getTime() - startDate.getTime()) / 1000;
				System.out.println(
						"---------------------------------------------------------------------------------------------");
				System.out.println("grammar check finished, number of lines:" + checkTime + " error count: " + errorCount + "\n"
						+ date1.toString() + " use time(s): " + useTime);
				System.out.println(
						"---------------------------------------------------------------------------------------------"+ "\n");


	}

	// 检查占位符是否和英文的一样
	public void checkPlaceHolders() {
				int errorCount = 0;
				int checkPlaceHoldersCount = 0;

				// 开始检查占位符是否相等
				String homeDirectory = XMLChecker.homeDirectory();
				System.out.println("start checking place holders equal... \n"+"home directory:" + homeDirectory);
				File homeFile = new File(homeDirectory);
				String[] homeFilelist = homeFile.list();

				//得到英文的路径下所有的文件
				ArrayList<String> englishPathes = findAllXMLFilePathesAtPath(homeDirectory + "/en");
				System.out.println("get the number of xml files at english path:" + englishPathes.size()+"\n");

				//遍历英文文件
				for (int a = 0; a < englishPathes.size(); a++) {

					String englishPath = englishPathes.get(a);
					//遍历home路径下所有的文件
					for (int i = 0; i < homeFilelist.length; i++) {

						// current language
						String currentLanguage = homeFilelist[i];
						// if get .SD_Store or get English path,do nothing,find others
						if (!currentLanguage.equals(".DS_Store") || currentLanguage.equals("en")) {

							String currentPath = englishPath.replace("/en", "/" + currentLanguage);

							//如果是mac
							if (XMLChecker.isMac()) {
								//把英文换成当前路径
								currentPath = englishPath.replace("/en", "/" + currentLanguage);
							}else{
								//把英文换成当前路径
								currentPath = englishPath.replace("\\en", "\\" + currentLanguage);
							}

							//英文路径解析的数据
							Map<String, Object> englishFile = DomXMLParser.Dom2MapFromPath(englishPath);
							//当前路径解析的数据
							Map<String, Object> currentFile = DomXMLParser.Dom2MapFromPath(currentPath);

							//遍历英文的数据
							for (Map.Entry<String, Object> entry : englishFile.entrySet()) {
								//得到key
								String key = entry.getKey();
								//得到英文数据
								Object object1 = entry.getValue();
								//得到当前的数据
								Object object2 = currentFile.get(entry.getKey());
								//如果两条数据都是字符串类型
								if (object1 instanceof String && object2 instanceof String) {
									//强制转换这两条字符串的类型
									String v1 = (String) entry.getValue();
									String v2 = (String) currentFile.get(entry.getKey());
									//得到解析出的数据
									HashMap<String, Integer> map1 = DomXMLParser.findPlaceholdersInString(v1.toLowerCase());
									HashMap<String, Integer> map2 = DomXMLParser.findPlaceholdersInString(v2.toLowerCase());
									//得到key
									Set<String>ssssss =  map1.keySet();
									//是否相当
									Boolean equal = true;
									//遍历该set
									for (String str : ssssss) {
										Integer vv1 = map1.get(str);
										Integer vv2 = map2.get(str);
										if (vv1 != vv2) {
											equal = false;
										}

									}
									if (!equal) {

										System.out.println("strings have different place holders found: \nenglish path is:" + englishPath + "\ncurrent path is:"
												+ currentPath + "\nkey: " + key + "\nenglish value: " + v1
												+ "\ncurrent value: " + v2 + "\n");
										errorCount ++;
									}
									checkPlaceHoldersCount++;



								} else {
									//System.out.print("找到不是字符串类型的数据:" + entry.getKey());
								}

							}
							// System.out.println(englishFile+currentFile);

						}
					}
				}
				System.out.println(
						"---------------------------------------------------------------------------------------------");
				System.out.println("find place holder equal has finished" +" check count:"+checkPlaceHoldersCount +"  error count:" + errorCount);
				System.out.println(
						"---------------------------------------------------------------------------------------------");
	}

	public ArrayList<String> findAllXMLFilePathesAtPath(String path) {

		// 找到桌面的文件夹 /Users/songwentong/Desktop/xml
		File file = new File(path);
		// 获取文件列表
		String[] list = file.list();

		// 所有的XML文件路径
		ArrayList<String> allXMLFilePaths = new ArrayList<>();

		// 如果文件不存在
		if (list == null) {
			// XML路径找不到
			return allXMLFilePaths;
		} else {
			// 从1开始是为了去掉.DS_Store
			for (int i = 0; i < list.length; i++) {

				if (list[i].equals(".DS_Store")) {
					// 如果是DS_Store文件就什么都不做
				} else {
					// 获取某个语言的路径
					String languageDir = file.getAbsolutePath() + "/" + list[i];
					// 语言文件的文件夹
					File languageFile = new File(languageDir);
					if (languageFile.isDirectory()) {
						allXMLFilePaths.addAll(findAllXMLFilePathesAtPath(languageDir));

					} else {
						if (languageDir.indexOf(".xml") != -1) {
							allXMLFilePaths.add(languageDir);
						}

					}

				}
			}
		}

		return allXMLFilePaths;
	}

	// 递归读取文件,这样做的目的是防止for循环导致卡死
	public void checkFilePathes(ArrayList<String> paths, int fromIndex) {
		checkFileWithPath(paths.get(fromIndex));
		if (fromIndex != paths.size() - 1) {
			// 如果不是最后一个,就读取下一个
			checkFilePathes(paths, fromIndex + 1);
		}
	}

	// 检查单个文件
	public void checkFileWithPath(String path) {
		// 读取出字符串

		// String xmlString = XMLChecker.stringFromFilePath(path);
		Map<String, Object> map = null;
		try {
			// 解析得到一个map,在解析的过程中调用一下检查吧
			map = DomXMLParser.XMLObjectFromPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// checkMap(map);
	}

	public boolean checkString(String string) {
		boolean result = true;
		if (string != null) {

			// 判断是否包含%
			if (string.indexOf("%%") >= 0) {
				// 包含
				System.out.println(string);
			} else {
				// 不包含
			}
		}
		return result;

	}

}
