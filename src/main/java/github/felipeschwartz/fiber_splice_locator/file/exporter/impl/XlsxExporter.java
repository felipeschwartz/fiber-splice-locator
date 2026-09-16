package github.felipeschwartz.fiber_splice_locator.file.exporter.impl;

import github.felipeschwartz.fiber_splice_locator.file.exporter.contract.FileExporter;
import github.felipeschwartz.fiber_splice_locator.model.dto.AddressDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class XlsxExporter implements FileExporter {

    @Override
    public Resource exportFile(List<CEODTO> ceoDTOList) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("CEOs");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Box Number", "Notes", "Address", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }

            int rowIndex = 1;
            for (CEODTO ceoDTO : ceoDTOList) {
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(ceoDTO.getId());
                dataRow.createCell(1).setCellValue(ceoDTO.getBoxNumber());
                dataRow.createCell(2).setCellValue(ceoDTO.getNotes());
                dataRow.createCell(3).setCellValue(formatAddress(ceoDTO.getAddress()));
                dataRow.createCell(4).setCellValue(ceoDTO.getStatus().name());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return new ByteArrayResource(outputStream.toByteArray());
            }
        }
    }


    private String formatAddress(AddressDTO address) {
        if (address == null) {
            return "";
        }
        return address.getAddressType() + ", " + address.getStreet() + ", " + address.getStreetNumber()
                + ", " + address.getNeighborhood() + ", " + address.getCity();
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerCellStyle;
    }
}


