package org.teiath.web.reports.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.web.reports.common.ExcelToolkit;

import java.util.Collection;

public class EventInterestsReport {

	public HSSFWorkbook exportList(Collection<EventInterest> eventInterests) {
		HSSFWorkbook wb = new HSSFWorkbook();
		int rowDataIndex = 1;
		int rowIndex = 1;

		HSSFSheet sheet = wb.createSheet("Εκδηλώσεις Ενδιαφέροντος");

		HSSFRow row = sheet.createRow((short) 0);
		row.setHeightInPoints((float) 25);

		HSSFFont font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 9);

		ExcelToolkit excelToolkit = new ExcelToolkit();

		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 0, "A/A", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 1, "Ονοματεπώνυμο", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 2, "Ιδιότητα", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 3, "Email", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);

		sheet.setColumnWidth(0, 2000);
		sheet.setColumnWidth(1, 7000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 7000);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 9);
		font.setBoldweight(Font.BOLDWEIGHT_NORMAL);

		for (EventInterest eventInterest : eventInterests) {
			row = sheet.createRow((short) rowDataIndex++);
			excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 0, rowIndex++ + "", HSSFCellStyle.ALIGN_CENTER,
					HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
			excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 1, eventInterest.getUser().getFullName(),
					HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
			excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 3, eventInterest.getUser().getEmail(),
					HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);

			switch (eventInterest.getUser().getUserType()) {
				case 0:
					excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 2, "Εξωτερικός Χρήστης",
							HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
					break;
				case 1:
					excelToolkit
							.createCell(ExcelToolkit.GENERIC, wb, row, font, 2, "Φοιτητής", HSSFCellStyle.ALIGN_LEFT,
									HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
					break;
				case 2:
					excelToolkit
							.createCell(ExcelToolkit.GENERIC, wb, row, font, 2, "Καθηγητής", HSSFCellStyle.ALIGN_LEFT,
									HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
					break;
				case 3:
					excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 2, "Υπάλληλος ΤΕΙ",
							HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
					break;
			}
		}

		return wb;
	}
}
