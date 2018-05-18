package com.iminer.iplay.utils.excels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.iminer.irpc.common.client.fastdfs.IFastDfs;

public class ExporToExcel {

	private int index = 0;  
    private String reportName;  
    private List<List<String>> result;  
    private List<String> heads = new ArrayList<String>();  
    private Map<String, CellStyle> styles = null;  
      
	/** 
     * 创建表头 
     * @param sheet 
     */  
    private void createHead(Sheet sheet){  
        CellStyle sytle = styles.get("header");  
        Row row = sheet.createRow(index++);  
        Cell cell;  
        row.setHeightInPoints(23);  
        for(int i=0;i<heads.size();i++){  
            cell = row.createCell(i);  
            cell.setCellValue(heads.get(i));  
            cell.setCellStyle(sytle);  
        }  
    }  
      
    /** 
     * 生成内容 
     * @param sheet 
     */  
    private void genContent(Sheet sheet){  
        CellStyle sytle = styles.get("content");  
        int size = result.size();  
        for(int i=0;i<size;i++){  
            Row row = sheet.createRow(index++);  
            List<String> item = result.get(i);  
            int length = heads.size();  
            for(int colum=0;colum<length;colum++){  
                Cell cell = row.createCell(colum);  
            //  cell.setCellValue(item.get(colum));  
                cell.setCellValue(String.valueOf(item.get(colum)));  
                  
                cell.setCellStyle(sytle);  
            }  
        }  
    }  
      
    /** 
     * 生成样式 
     * @param wk 
     */  
    private void createStyle(Workbook wk){  
        styles = new HashMap<String, CellStyle>();  
        // 普通字体  
        Font normalFont = wk.createFont();  
        normalFont.setFontHeightInPoints((short) 10);  
          
        // 加粗字体  
        Font boldFont = wk.createFont();  
        boldFont.setFontHeightInPoints((short) 13);  
        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);  
          
        // 表头格式  
        CellStyle headerStyle = wk.createCellStyle();  
        headerStyle.setFont(boldFont);  
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);  
        styles.put("header", headerStyle);  
          
        // 内容格式  
        CellStyle contentStyle = wk.createCellStyle();  
        contentStyle.setAlignment(CellStyle.ALIGN_CENTER);  
        contentStyle.setFont(normalFont);  
        styles.put("content", contentStyle);  
          
    }  
      
    /** 
     * 创建Excel 
     * @return 
     */  
    private Workbook genExcel(){  
        Workbook wb = new HSSFWorkbook();  
        Sheet sheet = wb.createSheet(reportName);  
//        for(int i=0;i<heads.size();i++){  
//            int size = 32 * colums.get(i);  
//            sheet.setColumnWidth(i,size);  
//        }  
        createStyle(wb);  
        createHead(sheet);  
        genContent(sheet);  
          
        return wb;  
    }  
      
    /** 
     * 生成基本的excel文件，只有一个sheet，只包含一列表头，剩下的为内容
     * @param 文件名称
     * @param 表头列表
     * @param 结果列表
     * @return 
     */  
    public static byte[] createExcelByBasic(String name,List<String> header,List<List<String>> result){  
        if(name==null || " ".equals(name)){  
            throw new IllegalArgumentException("需要导出的表名称不能为空!");  
        }  
        if(result == null){  
            throw new IllegalArgumentException("结果集合不能为空!");  
        }  
        ExporToExcel obj = new ExporToExcel();
        obj.reportName = name;
        obj.result = result;
        obj.heads = header;
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        Workbook wb = obj.genExcel();
        try {
            wb.write(out); 
            return out.toByteArray();  
        } catch (IOException e) {  
            e.printStackTrace();  
            return null;  
        }finally{  
            if(out!=null){  
                try{  
                    out.close();  
                }catch(IOException e){  
                    e.printStackTrace();  
                }
            }  
        }  
          
    }  
	
    /**
     * 生成excel，并上传到FastDFS上去，返回的的地址是相对路径，后面跟着真实名称的参数。
     * @param name
     * @param header
     * @param contentList
     * @return
     */
    public static String createExcelAndUploadToDFS(IFastDfs iFastDfs,String name,List<String> header,List<List<String>> contentList){
    	byte[] result = createExcelByBasic(name,header,contentList);
		
		String suffix = "xls";
		String[] uriArr;
		try {
			uriArr = iFastDfs.upload2group("G002",result,suffix,null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return (uriArr[0]+"/"+uriArr[1]+"?attname="+name);
    }
    
}  
