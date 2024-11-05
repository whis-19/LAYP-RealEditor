package Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelReader {

    private Workbook workbook;

    public ExcelReader(String filePath) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            workbook = new XSSFWorkbook(fileInputStream);
        }
    }

    public String getCellDataString(String sheetName, int rowIndex, int columnIndex) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new IllegalArgumentException("Sheet " + sheetName + " does not exist");
        }

        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return ""; // or handle this case as needed
        }

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell.toString(); // Convert the cell value to String
    }

    public void close() throws IOException {
        if (workbook != null) {
            workbook.close();
        }
    }
}