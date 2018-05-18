package person

import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream

import com.iminer.iplay.utils.excels.ExporToExcel

class PersonController {
	
	def grailsApplication;
	def personService ;
	def iFastDfs;
	
	def doUpdatePerson(){
		
		String message = personService.doUpdatePersonPassword(params.id,params.password,params.oldPassword )
		render message ;
	}
	
	def showBackList(){
		def result = personService.getPersonList(params);
		def myCount = personService.getAllPersonCount(params);
		[result:result,myCount:myCount]
	}
	
	
	
	/*def uploadFile() {
		try {
			def result = [:]
			
			CommonsMultipartFile uploadFile = params.file
			
			byte[] bs = uploadFile.getBytes();
			String fileName = uploadFile.getOriginalFilename();
			int index = fileName.lastIndexOf(".");
			String suffix = fileName.substring(index + 1);
			String[] uriArr;
			String u = "";
			String p = "";
			uriArr = FastDFSUtils.upload2group("G002",bs,suffix);

			System.out.println(uriArr);
			System.out.println(uriArr[0]+"/"+uriArr[1]);
			return uriArr[0]+"/"+uriArr[1];
		} catch (Exception e) {
			log.error(e);
			return "" ;
		}
	}*/
	
	def getExcel = {
		def filename  = "";
		filename = params.projectName;
		if(filename==null||"".equals(filename)){
			filename = System.currentTimeMillis()+"" ;
		}
		// 生成excel文件的标题和内容
		List<List<String>> list= new ArrayList<List<String>>();
		List<String> listStrs1 = ["小明","11","北京"];
		List<String> listStrs2 = ["小李","10","北京"];
		List<String> listStrs3 = ["小张","18","北京"];
		List<String> listStrs4 = ["小王","14","北京"];
		List<String> listStrs5 = ["小赵","17","北京"];
		List<String> listStrs6 = ["小钱","16","北京"];
		list.add(listStrs1);
		list.add(listStrs2);
		list.add(listStrs3);
		list.add(listStrs4);
		list.add(listStrs5);
		list.add(listStrs6);
		
		def headerList  =["姓名","年龄","住址"];
		// 创建excel并上传到fastDFS中，获得文件路径
		String result = ExporToExcel.createExcelAndUploadToDFS(iFastDfs,filename,headerList,list);
		
		println grailsApplication.config.grails.app.nginx+result ;
		
		// 根据文件的URL直接返回给前端。
		response.reset();
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "inline;filename="+new String( filename.getBytes("gb2312"), "ISO8859-1" ));
		response.setCharacterEncoding("UTF-8");
		OutputStream out = response.getOutputStream();
		URL url = new URL(grailsApplication.config.grails.app.nginx+result);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		DataInputStream input = new DataInputStream(conn.getInputStream());
		byte[] buffer = new byte[8192];
		int len;
		while ((len = input.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		out.flush();
		out.close();
		result="";
	}
	
	def getZip={
		String fileName = params.projectName;
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "inline;filename="+new String( fileName.getBytes("gb2312"), "ISO8859-1" ));
		try {
			OutputStream out = response.getOutputStream();
			ZipOutputStream zos = new ZipOutputStream(out);
			// 获得文件的路径
			def Urls = [grailsApplication.config.grails.app.nginx+"/G002/M00/BD/50/wKggJ1cHTUL9R8BrAAASAB2OdYQ513.xls?attname=疯狂动物城_票房.xls",grailsApplication.config.grails.app.nginx+"/G002/M00/BD/50/wKggKFcHTUrCoxf3AAASABrTnYE715.xls?attname=疯狂动物城_受众.xls"]; 
			// 将每个文件压缩到zip文件中
			for(int i = 0 ; i < Urls.size();i++){
				String uri = Urls.get(i);
				URL url = new URL(uri);
				// 获得每个需要打包文件的文件名 
				String zipEntryName = uri.substring(uri.indexOf("?attname=")+9)
				zos.putNextEntry(new ZipEntry(zipEntryName));
				
				// 读入需要下载的文件的内容，打包到zip文件
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				DataInputStream input = new DataInputStream(conn.getInputStream());
				byte[] buffer = new byte[8192];
				int len;
				while ((len = input.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				
				zos.closeEntry();
				input.close();
			}
			// 关闭zip输出流
			zos.close();
			out.flush();
			out.close();
			return "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
