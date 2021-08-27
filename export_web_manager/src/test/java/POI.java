import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class POI {

  //POI操作excel2003版  底层是二进制
  /*@Test
  public void write() throws IOException {
    //1.创建工作簿 表示整个excel表格
    HSSFWorkbook workbook = new HSSFWorkbook();
    //2.根据工作簿 创建工作表
    HSSFSheet sheet = workbook.createSheet();
    //3.根据工作表 创建第一行
    HSSFRow row = sheet.createRow(0);
    //4.创建第一行的第一列
    HSSFCell cell = row.createCell(0);
    //5.单元格设置内容
    cell.setCellValue("2021-8");

    //导出excel表格
    workbook.write(new FileOutputStream("d:/1.xls"));
    workbook.close();
  }

  @Test
  public void read() throws Exception {
    FileInputStream fileInputStream = new FileInputStream("d:/1.xls");
    //1.加载excel文件流,创建工作簿
    HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
    //2.获取工作表(第一个工作表)
    HSSFSheet sheet = workbook.getSheetAt(0);
    //3.获取第一行
    HSSFRow row = sheet.getRow(0);
    //4.获取第一行的 第一个单元格
    HSSFCell cell = row.getCell(0);
    //5.获取单元格内容
    System.out.println("第一行第一列："+cell.getStringCellValue());
    System.out.println("获取数据总行数："+sheet.getPhysicalNumberOfRows());
    System.out.println("获取总列数：" + row.getPhysicalNumberOfCells());
    workbook.close();


  }*/

  //POI操作excel2007版 底层xml
  @Test
  public void write() throws IOException {
    //1.创建工作簿 表示整个excel表格
    XSSFWorkbook workbook = new XSSFWorkbook();
    //2.根据工作簿 创建工作表
    XSSFSheet sheet = workbook.createSheet();
    //3.根据工作表 创建第一行
    XSSFRow row = sheet.createRow(0);
    //4.创建第一行的第一列
    XSSFCell cell = row.createCell(0);
    //5.单元格设置内容
    cell.setCellValue("2021-8");

    //导出excel表格
    workbook.write(new FileOutputStream("d:/1.xlsx"));
    workbook.close();
  }

  @Test
  public void read() throws Exception {
    FileInputStream fileInputStream = new FileInputStream("d:/1.xlsx");
    //1.加载excel文件流,创建工作簿
    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
    //2.获取工作表(第一个工作表)
    XSSFSheet sheet = workbook.getSheetAt(0);
    //3.获取第一行
    XSSFRow row = sheet.getRow(0);
    //4.获取第一行的 第一个单元格
    XSSFCell cell = row.getCell(0);
    //5.获取单元格内容
    System.out.println("第一行第一列："+cell.getStringCellValue());
    System.out.println("获取数据总行数："+sheet.getPhysicalNumberOfRows());
    System.out.println("获取总列数：" + row.getPhysicalNumberOfCells());
    workbook.close();


  }

}
