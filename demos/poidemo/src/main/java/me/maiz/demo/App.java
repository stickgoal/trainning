package me.maiz.demo;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try {
            List<Employment> maps = readExcel("./蜗牛学院就业信息总表（重庆-2019.10.12）.xlsx");
            System.out.println(maps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从指定路径读取Excel文件，返回类型为List<Map<String,String>>
     *
     * @param path
     */
    private static List<Employment> readExcel(String path) throws Exception {


        List<Employment> mapList = new ArrayList<>();
        File file = new File(path);
        //判断文件是否存在
        if (file.isFile() && file.exists()) {
            System.out.println(file.getPath());
            String fileName = file.getName();
            //获取文件的后缀名 \\ .是特殊字符
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            System.out.println(suffix);
            Workbook wb = getWorkbook(file, suffix);
            if (wb == null) {
                System.out.println("获取workbook出错...");
                return null;
            }

            //开始解析
            Sheet sheet = wb.getSheetAt(0);
            //第一行是列名，所以从第二行开始遍历
            int firstRowNum = sheet.getFirstRowNum() + 2;
            int lastRowNum = sheet.getLastRowNum();
            System.out.println("遍历行数，范围：" + firstRowNum + "-" + lastRowNum);
            //遍历行
            for (int rIndex = firstRowNum; rIndex <= lastRowNum; rIndex++) {
                //获取当前行的内容
                Row row = sheet.getRow(rIndex);
                System.out.print("解析第"+(rIndex+1)+"行");
                if (row != null && !isEmptyRow(row)) {
                    Employment employment = new Employment();

                    int firstCellNum = row.getFirstCellNum();
                    int lastCellNum = row.getLastCellNum();

                    for (int cIndex = firstCellNum; cIndex < lastCellNum; cIndex++) {
                        Cell cell = row.getCell(cIndex);

                        switch (cIndex) {
                            case 0:
                                employment.setIndex(Double.valueOf(cell.getNumericCellValue()).intValue());
                                break;
                            case 1:
                                employment.setStudentNo(cell.getStringCellValue());
                                break;
                            case 2:
                                employment.setName(cell.getStringCellValue());
                                break;
                            case 3:
                                employment.setGender(cell.getStringCellValue());
                                break;
                            case 4:
                                employment.setDegree(cell.getStringCellValue());
                                break;
                            case 5:
                                employment.setUniversity(cell.getStringCellValue());
                                break;
                            case 6:
                                employment.setMajor(cell.getStringCellValue());
                                break;
                            case 7:
                                employment.setGraduateTime(getDate(cell));
                                break;
                            case 8:
                                employment.setCompany(cell.getStringCellValue());
                                break;
                            case 9:
                                employment.setPosition(cell.getStringCellValue());
                                break;
                            case 10:
                                employment.setSalary(cell.getNumericCellValue());
                                break;
                            case 11:
                                employment.setCity(cell.getStringCellValue());
                                break;
                            case 12:
                                employment.setEmploymentTime(getDate(cell));
                                break;
                            case 13:
                                employment.setMemo(cell.getStringCellValue());
                                break;
                            default:
                                System.out.println("超出文件内容：" + cIndex);
                        }


                    }
                    mapList.add(employment);
                    System.out.println("结果为"+employment);

                }else{
                    System.out.println(" 空行");

                }
            }

        }

        return mapList;

    }

    private static Date getDate(Cell cell) {
        if(cell.getCellTypeEnum()==CellType.STRING){
            System.out.println(cell.getStringCellValue());
            return DateUtil.parse(cell.getStringCellValue());
        }

        return cell.getDateCellValue();
    }

    private static boolean isEmptyRow(Row row) {
        Cell cell0 = row.getCell(0);
        Cell cell1 = row.getCell(1);
        return (cell0.getCellTypeEnum() == CellType.BLANK && cell1.getCellTypeEnum() == CellType.BLANK);
    }

    private static Workbook getWorkbook(File file, String suffix) throws IOException, InvalidFormatException {
        Workbook wb;
        //根据文件后缀（xls/xlsx）进行判断
        if ("xls".equals(suffix)) {
//              //获取文件流对象
            FileInputStream inputStream = new FileInputStream(file);
            wb = new HSSFWorkbook(inputStream);
        } else if ("xlsx".equals(suffix)) {
            wb = new XSSFWorkbook(file);
        } else {
            System.out.println("文件类型错误");
            return null;
        }
        return wb;
    }
}
