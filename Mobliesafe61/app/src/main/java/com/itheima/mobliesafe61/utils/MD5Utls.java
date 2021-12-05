package com.itheima.mobliesafe61.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Utls {
	/**
	 * MD5加密
	 * @param password
	 * @return
	 */
	public static String digestPassWord(String password){
		StringBuilder builder = new StringBuilder();
		try {
			//数据摘要器
			//参数：输入加密方式
			MessageDigest digest = MessageDigest.getInstance("MD5");
			//把一个byte数组进行加密，返回一个加密过的byte数组
			byte[] b = digest.digest(password.getBytes());
			for (int i = 0; i < b.length; i++) {
				//将得到的每个byte值和int类型的255做与运算，得到正数，也是一个整数
				int result =  b[i] & 0xff;
				//转换成16进制的字符串
//				String str = Integer.toHexString(result)+1;//不规则加密，加盐
				String str = Integer.toHexString(result);
				if (str.length() < 2) {
					builder.append("0");
//					System.out.print("0");
				}
//				System.out.println(str);
				builder.append(str);
			}
//			System.out.println(builder.toString());
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			//找不到加密方式
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取文件的md5  不同的文件有不同的md5
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static  String getFileMd5(String path) throws FileNotFoundException, NoSuchAlgorithmException, IOException {
		// 读取流
		File file = new File(path);
		// 流
		InputStream input = new FileInputStream(file);
		// 获取摘要器
		MessageDigest digest = MessageDigest.getInstance("MD5");
		// 1byte=8bit
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = input.read(buffer)) != -1) {
			// 提取特征值 需要的字节数组
			digest.update(buffer, 0, len);
		}
		byte[] data = digest.digest();// 生成特征值 128bit 1byte=8bit 16byte -->32位字符
		StringBuffer sb = new StringBuffer();
		for (byte temp : data) {
			String hex = Integer.toHexString((int) temp & 0xFF); // 1
			// 转成16进制
			if (hex.length() < 2) {
				sb.append("0").append(hex);
			} else {
				sb.append(hex);
			}
		}
		input.close();
		String result=sb.toString();
		return result;
	}

}
