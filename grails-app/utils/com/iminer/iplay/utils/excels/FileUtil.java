package com.iminer.iplay.utils.excels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;

import org.apache.log4j.Logger;

/**
 * @author Toby 复制文件夹或文件夹
 */
public class FileUtil {
	
	
	/**
	 * 读取字节
	 * 
	 * @param fileName
	 */
	public static void readFileByBytes(String fileName) {
		File file = new File(fileName);
		InputStream in = null;
		try {
			System.out.println("以字节为单位读取文件内容，一次读一个字节");
			in = new FileInputStream(file);
			int tempbyte;
			while ((tempbyte = in.read()) != -1) {
				System.out.write(tempbyte);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			System.out.println("以字节为单位读取文件内容，一次读多个字节");
			byte[] tempbytes = new byte[100];
			int byteread = 0;
			in = new FileInputStream(fileName);
			FileUtil.showAvailableBytes(in);
			while ((byteread = in.read(tempbytes)) != -1) {
				System.out.write(tempbytes, 0, byteread);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取字符
	 * 
	 * @param fileName
	 */
	public static void readFileByChars(String fileName) {
		File file = new File(fileName);
		Reader reader = null;
		try {
			System.out.println("以字符为单位读取文件内容，一次读一个字符");
			reader = new InputStreamReader(new FileInputStream(file));
			// 一次读一个字符
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				if (((char) tempchar) != '\r') {
					System.out.print((char) tempchar);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println("以字符为单位读取文件内容，一次读多个字符");
			reader = new InputStreamReader(new FileInputStream(file));
			char[] tempchars = new char[30];
			// 一次读一个字符
			int charread = 0;
			while ((charread = reader.read(tempchars)) != -1) {
				// 同样屏蔽掉\r不显示
				if ((charread == tempchars.length)
						&& (tempchars[tempchars.length - 1] != '\r')) {
					System.out.print(tempchars);
				} else {
					for (int i = 0; i < charread; i++) {
						if (tempchars[i] == '\r') {
							continue;
						} else {
							System.out.print(tempchars[i]);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取行
	 * 
	 * @param fileName
	 */
	public static void readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			System.out.println("以行为单位读取文件内容");
			reader = new BufferedReader(new FileReader(file));
			// 一次读一个字符
			String tempString = null;
			int line = 1;
			while ((tempString = reader.readLine()) != null) {
				System.out.println("line " + line + ": " + tempString);
				line++;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public static String readFileByLinesAndBack(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			System.out.println("以行为单位读取文件内容");
			reader = new BufferedReader(new FileReader(file));
			// 一次读一个字符
			String tempString = null;
			int line = 1;
			while ((tempString = reader.readLine()) != null) {
				System.out.println("line " + line + ": " + tempString);
				if(!"</graph>".equalsIgnoreCase(tempString)){
					sb.append(tempString);
				}
				line++;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 随机读取
	 * 
	 * @param fileName
	 */
	public static void readFileByRandomAccess(String fileName) {
		RandomAccessFile randomFile = null;
		try {
			System.out.println("随机读取一段文件内容");
			// 打开一个随机访问文件流，按只读方式
			randomFile = new RandomAccessFile(fileName, "r");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 读文件的起始位置
			int beginIndex = (fileLength > 4) ? 4 : 0;
			// 将文件的开始位置移到beginIndex位置
			randomFile.seek(beginIndex);
			byte[] bytes = new byte[10];
			int byteread = 0;
			// 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
			// 将一次读取的字节数赋给byteread
			while ((byteread = randomFile.read(bytes)) != -1) {
				System.out.write(bytes, 0, byteread);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 通过随机访问追加内容
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void appendToFileByRandomAccess(String fileName,
			String content) {
		try {
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			long fileLength = randomFile.length();
			randomFile.seek(fileLength);
			randomFile.writeBytes(content);
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过FileWriter追加内容
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void appendToFileByFileWriter(String fileName, String content) {
		try {
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void showAvailableBytes(InputStream in) {
		try {
			System.out.println("当前字节输入流中的字节数为" + in.available());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeFile(String path, String content) {
	        
		byte[] b = content.getBytes();	
		try {
			FileOutputStream out = new FileOutputStream(path);  
			out.write(b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	      
    }

	/**
	 * 新建目录
	 * 
	 * @param a
	 */
	public static boolean createFolder(String folderPath) {
		Logger logger = Logger.getLogger(FileUtil.class.getName());
		try {
			File filePath = new File(folderPath);
			if (!filePath.exists()) {
				filePath.mkdir();
				logger.info("目录"+folderPath+"新建成功");
			}
			else{
				logger.info("目录"+folderPath+"已经存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("新建目录出错"+e.getMessage());
			return false ;
		}
		return true ;
	}

	/**
	 * 新建文件
	 * 
	 * @param a
	 */
	public static void createFile(String filePathAndName, String fileContent) {
		try {
			File filePath = new File(filePathAndName);
			if (!filePath.exists()) {
				filePath.createNewFile();
			}
			FileWriter writer = new FileWriter(filePath);
			PrintWriter pw = new PrintWriter(writer);
			pw.println(fileContent);
			writer.close();
			System.out.println("新建目录成功");
		} catch (Exception e) {
			System.out.println("新建目录出错");
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param a
	 */
	public static void deleteFile(String filePathAndName) {
		Logger logger = Logger.getLogger(FileUtil.class.getName());
		try {
			File delFile = new File(filePathAndName);
			delFile.delete();
			logger.info("删除已经解析的文件成功,文件路径:"+filePathAndName);
		} catch (Exception e) {
			logger.error("删除已经解析的文件失败,文件路径:"+filePathAndName);
			System.err.println("删除已经解析的文件失败,文件路径:"+filePathAndName);
			e.printStackTrace();
		}
	}

	/**
	 * 删除空文件夹
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath);
			File filePath = new File(folderPath);
			if (filePath.delete()) {
				System.out.println("删除文件夹" + folderPath + "操作 成功执行");
			} else {
				System.out.println("删除文件夹" + folderPath + "操作 执行失败");
			}
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件夹里的所有文件
	 * 
	 * @param a
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delFolder(path + File.separatorChar + tempList[i]);
			}
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param a
	 */
	public static boolean copyFile(String oldFile, String newFile) {
		Logger logger = Logger.getLogger(FileUtil.class.getName());
		try {
			int byteSum = 0;
			int byteRead = 0;
			File oFile = new File(oldFile);
			if (oFile.exists()) {
				InputStream inStream = new FileInputStream(oldFile);
				FileOutputStream fos = new FileOutputStream(newFile);
				byte[] buffer = new byte[1024];
				while ((byteRead = inStream.read(buffer)) != -1) {
					byteSum += byteRead;
					fos.write(buffer, 0, byteRead);
				}
				inStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("复制文件失败，文件名称："+oldFile);
			logger.error(e.getMessage());
			return false ;
		}
		logger.info("文件"+oldFile+"成功复制到"+newFile+"中");
		return true ;
	}

	/**
	 * 复制整个文件夹
	 * 
	 * @param a
	 */
	public static void copyFolder(String oldPath, String newPath) {
		try {
			createFolder(newPath);
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
					System.out.println("文件"+temp.getName()+"复制成功！");
				}
				if (temp.isDirectory()) {
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移动文件到目录
	 * 
	 * @param a
	 */
//	public static void moveFolder(String oldPath, String newPath) {
//		copyFile(oldPath, newPath);
//		deleteFile(oldPath);
//	}

}