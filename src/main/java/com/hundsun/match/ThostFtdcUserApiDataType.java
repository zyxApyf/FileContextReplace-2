package com.hundsun.match;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

/**
 * 获取结构体指定变量对应 char 型数组大小
 * 
 * @author zhangyx25316
 *
 */
public class ThostFtdcUserApiDataType {
	
	private static Map<String, String> member = null;
	
	public ThostFtdcUserApiDataType() {
		//String filePath = System.getProperty("user.dir") + "\\ThostFtdcUserApiDataType.h";
		String filePath = "F:\\文件内容替换工具\\ThostFtdcUserApiDataType.h";
		readFile(filePath);
	}
	
	/**
	 * 读取 ThostFtdcUserApiDataType.h
	 * 
	 * @param filePath 文件所在路径
	 */
	public static void readFile(String filePath) {
		File file = new File(filePath);
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
            String enCode = getFileEncode(file.getAbsolutePath());  
            if("void".equals(enCode)){  
            	enCode="UTF-8";  
            }
            if("windows-1252".equals(enCode)){  
                enCode="GBK";  
            }
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, enCode);
			bufferedReader = new BufferedReader(inputStreamReader);
			member = new LinkedHashMap<>();
			
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				//匹配 char 型
				Pattern pattern = Pattern.compile("^typedef char.*");
				String str = "";
				Matcher matcher = pattern.matcher(line);
				Pattern p = Pattern.compile("[\\[]{1}[0-9]");
				while(matcher.find()){
					str += matcher.group(0);
					if(p.matcher(str).find()) {
						String key = str.split("\\[")[0]
						        .split("\\s+")[2];
						String value = str.split("\\[")[1]
						          .split("\\]")[0];
						member.put(key, value);
					} else {
						String key = str.split("\\s+")[2]
								        .split(";")[0];
						String value = 1 + "";
						member.put(key, value);
					}
				}
			}
			//遍历打印 map
			//printMap();
			
		}catch (FileNotFoundException e) {
        	e.printStackTrace();
        	System.out.println("文件不存在！");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("读取文件失败！");
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 检查文件类型
	 * 
	 * @param filePath 文件所在路径
	 * @return 文件类型
	 */
	public static String getFileEncode(String filePath) {  
        /* 
         * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。 
         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、 
         * JChardetFacade、ASCIIDetector、UnicodeDetector。 
         * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的 
                *  字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar 
         * cpDetector是基于统计学原理的，不保证完全正确。 
         */  
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();  
        /* 
         * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于 
                 * 指示是否显示探测过程的详细信息，为false不显示。 
         */  
        detector.add(new ParsingDetector(false));  
        /* 
         * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码 
                * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以 
                * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。 
         */  
        // ASCIIDetector用于ASCII编码测定  
        detector.add(ASCIIDetector.getInstance());  
        // UnicodeDetector用于Unicode家族编码的测定  
        detector.add(UnicodeDetector.getInstance());  
        java.nio.charset.Charset charset = null;  
        File f = new File(filePath);  
        try {  
            charset = detector.detectCodepage(f.toURI().toURL());  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
        if (charset != null)  
            return charset.name();  
        else  
            return null;  
    }
	
	/**
	 * 关键字查找
	 * 
	 * @param key 关键字
	 * @return 对应的容量大小
	 */
	public int queryCapacity(String key) {
		Set<Map.Entry<String, String>> entries = getMap().entrySet();
		for(Map.Entry<String, String> entry : entries) {
			//判断找到关键字
			if(entry.getKey().equals(key)) {
				return Integer.parseInt(entry.getValue());
			}
		}
		return 0;
	}
	
	/**
	 * 获取从 ThostFtdcUserApiDataType.h文件中提取出来的关键信息
	 * 
	 * @return 提取出来的关键信息
	 */
	public static Map<String, String> getMap() {
		return member;
	}
	
	/**
	 * 遍历打印 map
	 */
	public static void printMap() {
		for(Map.Entry<String, String> entry : member.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}
}
