package github.felipeschwartz.fiber_splice_locator.file.exporter.contract;

import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileExporter {
    Resource exportFile(List<CEODTO> ceoDTOList) throws Exception;
}
