package com.hundsun.module;

/**
 * 对象模型，用于临时存取关键信息
 * 
 * @author zhangyx25316
 *
 */
public class StructModule {
	
	private String key;
	private String value;
	
	/**
	 * 获取 key
	 * 
	 * @return key
	 */
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取 value
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "StructModule [key=" + key + ", value=" + value + "]";
	}
	
}
