package github.felipeschwartz.fiber_splice_locator.file.exporter.impl;

import github.felipeschwartz.fiber_splice_locator.file.exporter.contract.FileExporter;
import github.felipeschwartz.fiber_splice_locator.model.dto.AddressDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter implements FileExporter {
    @Override
    public Resource exportFile(List<CEODTO> ceoDTOList) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        CSVFormat csvFormat = CSVFormat.Builder.create()
                   .setHeader("ID", "Box Number", "Notes", "Address", "Status")
                   .setSkipHeaderRecord(false)
                   .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {

            for (CEODTO ceoDTO : ceoDTOList) {
                csvPrinter.printRecord(
                        ceoDTO.getId(),
                        ceoDTO.getBoxNumber(),
                        ceoDTO.getNotes(),
                        formatAddress(ceoDTO.getAddress()),
                        ceoDTO.getStatus().name()
                );
            }
        }
        return new ByteArrayResource(outputStream.toByteArray());
    }

    private String formatAddress(AddressDTO address) {
        if (address == null) {
            return "";
        }
        return address.getAddressType() + ", " + address.getStreet() + ", " + address.getStreetNumber()
                + ", " + address.getNeighborhood() + ", " + address.getCity();
    }
}
