package com.itheima.mobliesafe61.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class StreamUtils {
	/**
	 * 将流信息转化成字符串
	 * @param in
	 * @return
	 * @throws IOException 
	 */
	public static String parserStream(InputStream in) throws IOException{
		//字符流，读取流
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		//写入流
		StringWriter sw = new StringWriter();
		//数据缓冲区
		String str = null;
		while((str = br.readLine()) != null){
			sw.write(str);
		}
		sw.close();
		br.close();
		return sw.toString();
	}
}
