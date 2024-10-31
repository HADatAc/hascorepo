package org.hascoapi.ingestion;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import java.text.SimpleDateFormat;

public class SpreadsheetFileRecord implements Record {
    Row row;

    public SpreadsheetFileRecord(Row row) {
        this.row = row;
        //System.out.println(getColumnNameByIndex(0) + "  [" + getValueByColumnIndex(0) + "]");
        //System.out.println(getColumnNameByIndex(1) + "  [" + getValueByColumnIndex(1) + "]");
    }

    @Override
    public String getValueByColumnName(String columnName) {
        String value = "";
        try {
            Cell c = row.getCell(getColumnIndexByName(columnName));
            if (c.getHyperlink() != null) {
                value = c.getHyperlink().getAddress();
            } else {
                value = getCellValueAsString(c);
            }
        } catch (Exception e) {
            // System.out.println("row " + row.getRowNum() + ", column name " + columnName + " not found!");
        }

        return value;
    }

    @Override
    public String getValueByColumnIndex(int index) {
        String value = "";
        try {
            value = row.getCell(index).toString().trim();
        } catch (Exception e) {
            // System.out.println("row " + row.getRowNum() + ", column index " + index + " not valid!");
        }

        return value;
    }

    @Override
    public int size() {
        return row.getLastCellNum() + 1;
    }

    private int getColumnIndexByName(String columnName) {
        Sheet sheet = row.getSheet();
        Row firstRow = sheet.getRow(sheet.getFirstRowNum());

        for(int i = firstRow.getFirstCellNum(); i <= firstRow.getLastCellNum(); i++) {
            if (firstRow.getCell(i).toString().equals(columnName)) {
                return i;
            }	
        }

        return -1;
    }

    private String getColumnNameByIndex(int columnIndex) {
        Sheet sheet = row.getSheet();
        Row firstRow = sheet.getRow(sheet.getFirstRowNum());

        return firstRow.getCell(columnIndex).toString();
    }

    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        String strCellValue = "";
        switch (cell.getCellType()) {
        case STRING:
            strCellValue = cell.toString();
            break;
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                strCellValue = dateFormat.format(cell.getDateCellValue());
            } else {
                Double value = cell.getNumericCellValue();
                if (value % 1 == 0) { 
                	Long longValue = value.longValue();
                	strCellValue = longValue.toString();
                } else {
                	strCellValue = value.toString(); 
                }
            }
            break;
        case BOOLEAN:
            strCellValue = new Boolean(cell.getBooleanCellValue()).toString();
            break;
        case BLANK:
            strCellValue = "";
            break;
        }
        
        return strCellValue.trim();
    }
}
