package org.teiath.web.reports.common;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;

import java.util.Date;

public class ExcelToolkit {

	public static final int ONGOING_ACTIONS = 6;
	public static final int ACTIONS_BY_CATEGORY = 7;
	public static final int EVENT_INTERESTS = 8;

	public static final int GENERIC = 1;
	public static final int NUMERIC = 2;
	public static final int DATE = 3;

	public void createCell(int cellType, HSSFWorkbook wb, HSSFRow row, HSSFFont font, int column, Object value,
	                       short halign, short valign, boolean wrapText, boolean renderBorders, short fillColor) {
		CreationHelper createHelper = wb.getCreationHelper();

		HSSFCell cell = row.createCell(column);
		HSSFCellStyle cellStyle = wb.createCellStyle();

		switch (cellType) {
			case GENERIC: {
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(value != null ? (String) value : null);
				break;
			}
			case DATE: {
				cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
				try {
					cell.setCellValue(value != null ? (Date) value : null);
				} catch (NullPointerException e) {
					cell.setCellValue("");
				}
				break;
			}
			case NUMERIC: {
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
				try {
					cell.setCellValue(value != null ? (Double) value : null);
				} catch (NullPointerException e) {
					cell.setCellValue("");
				}
				break;
			}
		}
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		cellStyle.setWrapText(wrapText);

		if (renderBorders) {
			cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderRight(CellStyle.BORDER_THIN);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderTop(CellStyle.BORDER_THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		}

		cellStyle.setFillForegroundColor(fillColor);
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		cellStyle.setFont(font);

		cell.setCellStyle(cellStyle);
	}
}
