package com.hundsun.match;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

/**
 * 获取结构体名字以及结构体详细内容
 * 
 * @author zhangyx25316
 *
 */
public class ThostFtdcUserApiStruct {
	
	private static Map<String, Map<String, String>> struct = null;
	private static ThostFtdcUserApiDataType thostFtdcUserApiDataType = null;
	
	public ThostFtdcUserApiStruct() {
		//String filePath = System.getProperty("user.dir") + "\\ThostFtdcUserApiStruct.h";
		String filePath = "F:\\文件内容替换工具\\ThostFtdcUserApiStruct.h";
		thostFtdcUserApiDataType = new ThostFtdcUserApiDataType();
		readFile(filePath);
	}
	
	/**
	 * 读取 ThostFtdcUserApiStruct.h
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
			struct = new LinkedHashMap<>();
			
			String line = null;
			String subLine = null;
			while((line = bufferedReader.readLine()) != null) {
				//匹配结构体
				Pattern pattern = Pattern.compile("^struct.*");
				String mString = "";
				Matcher matcher = pattern.matcher(line);
				while(matcher.find()){
					mString += matcher.group(0);
					Map<String, String> structMember = new HashMap<>();
					Label:while((subLine = bufferedReader.readLine()) != null) {
						//匹配结构体内容
						Pattern fPattern = Pattern.compile(".*TThost.*");
						String fString = "";
						Matcher fMatcher = fPattern.matcher(subLine);
						//匹配结构体内容结束标志
						Pattern ePattern = Pattern.compile("^};.*");
						Matcher eMatcher = ePattern.matcher(subLine);
						while(fMatcher.find()) {
							fString += fMatcher.group(0);
							String key = fString.split("\\s+")[1];
							String value = fString.split("\\s+")[2].split(";")[0];
							structMember.put(value, key);
						}
						while(eMatcher.find()) {
							break Label;
						}
					}
					struct.put(mString.split("\\s+")[1], structMember);
				}
			}
			//遍历打印map信息
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
         * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar 
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
	 * @param key 第一个关键字
	 * @param value 第二个关键字
	 * @return 对应的容量大小
	 */
	public static int specifyQuery(String key, String value) {
		for(Map.Entry<String, Map<String, String>> entry : getStruct().entrySet()) {
			//判断找到第一个关键字
			if(entry.getKey().equals(key)) {
				return thostFtdcUserApiDataType.queryCapacity(entry.getValue().get(value));
			}
		}
		return 0;
	}
	
	/**
	 * 获取从 ThostFtdcUserApiStruct.h 文件中提取出的关键信息
	 * 
	 * @return 提取出的关键信息
	 */
	public static Map<String, Map<String, String>> getStruct() {
		return struct;
	}
	
	/**
	 * 遍历打印 map
	 */
	public static void printMap() {
        for(Map.Entry<String, Map<String, String>> entry : struct.entrySet()) {
        	System.out.println(entry.getKey());
        	for(Map.Entry<String, String> entry2: entry.getValue().entrySet()) {
        		System.out.println(entry2.getKey() + ": " + entry2.getValue());
        	}
        	System.out.println();
        }
	}
}
