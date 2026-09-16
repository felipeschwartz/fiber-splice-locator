package github.felipeschwartz.fiber_splice_locator.file.exporter.factory;

import github.felipeschwartz.fiber_splice_locator.file.exporter.MediaTypes;
import github.felipeschwartz.fiber_splice_locator.file.exporter.contract.FileExporter;
import github.felipeschwartz.fiber_splice_locator.file.exporter.impl.CsvExporter;
import github.felipeschwartz.fiber_splice_locator.file.exporter.impl.XlsxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileExporterFactory {
    private Logger log = LoggerFactory.getLogger(FileExporterFactory.class);

    @Autowired
    private ApplicationContext applicationContext;

    public FileExporter getExporter(String acceptHeader) throws Exception {
        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)) {
            return applicationContext.getBean(XlsxExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
            return applicationContext.getBean(CsvExporter.class);

        }
        throw new Exception("Unsupported file format");
    }
}
