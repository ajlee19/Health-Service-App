package ashleylee.rpp_demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user touches the button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, ExerciseSuggestion.class);
        startActivity(intent);
    }

    public void measureRpp(View view){
        String FileName = "";
        readFile(FileName);
    }

    /**
     * Read designated file containing SBP and HR and compute RPP
     * @param FileName file to read
     */
    private void readFile(String FileName){
        int sbpIndex = 1;
        int hrIndex = 2;
        InputStream stream = getResources().openRawResource(R.raw.rpp_info);
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();

            for (int r = 0; r<rowsCount; r++) {
                Row row = sheet.getRow(r);

                if (row != null){
                    int sbp = row.getCell(sbpIndex);
                    int hr = row.getCell(hrIndex);
                    int rpp = Math.round((sbp*hr)/10000);
                }
            }
        } catch (Exception e) {
            printlnToUser(e.toString());
        }

    }
}
