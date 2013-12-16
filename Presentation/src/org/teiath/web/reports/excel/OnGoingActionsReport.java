package org.teiath.web.reports.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.teiath.data.domain.act.Event;
import org.teiath.service.act.ActionsExcelService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.reports.common.ExcelToolkit;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class OnGoingActionsReport {

	public HSSFWorkbook exportList(ActionsExcelService actionsExcelService, Date dateFrom, Date dateTo) {
		HSSFWorkbook wb = new HSSFWorkbook();
		int rowDataIndex = 1;
		int rowIndex = 1;

		HSSFSheet sheet = wb.createSheet("Λίστα Δράσεων");

		HSSFRow row = sheet.createRow((short) 0);
		row.setHeightInPoints((float) 25);

		HSSFFont font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 9);

		ExcelToolkit excelToolkit = new ExcelToolkit();

		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 0, "A/A", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 1, "Τίτλος", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 2, "Εναρξη", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 3, "Λήξη", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 4, "Κατηγορία", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 5, "Περιοχή", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);

		sheet.setColumnWidth(0, 2000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 10000);
		sheet.setColumnWidth(3, 15000);
		sheet.setColumnWidth(4, 5000);
		sheet.setColumnWidth(5, 5000);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 9);
		font.setBoldweight(Font.BOLDWEIGHT_NORMAL);

		try {
			Collection<Event> events = actionsExcelService.getOnGoingEvents(dateFrom, dateTo);

			for (Event event : events) {
				row = sheet.createRow((short) rowDataIndex++);
				excelToolkit
						.createCell(ExcelToolkit.GENERIC, wb, row, font, 0, rowIndex++ + "", HSSFCellStyle.ALIGN_CENTER,
								HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 1, event.getEventTitle(),
						HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 2,
						new SimpleDateFormat("dd/MM/yyyy").format(event.getDateFrom()), HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				if (event.getDateTo() != null) {
					excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 3,
							new SimpleDateFormat("dd/MM/yyyy").format(event.getDateTo()), HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				}
				else {
					excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 3,
							"", HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				}
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 4, event.getEventCategory().getName(),
						HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				if (event.getEventLocation() != null) {
					excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 5, event.getEventLocation(),
							HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				}
				else {
					excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 5, "",
							HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				}
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		return wb;
	}
}
