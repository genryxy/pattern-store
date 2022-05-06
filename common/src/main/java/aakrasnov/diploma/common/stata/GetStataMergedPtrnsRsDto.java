package aakrasnov.diploma.common.stata;

import aakrasnov.diploma.common.RsBaseDto;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetStataMergedPtrnsRsDto extends RsBaseDto {
    private Map<String, Integer> success;

    private Map<String, Integer> failure;

    private Map<String, Integer> download;
}
