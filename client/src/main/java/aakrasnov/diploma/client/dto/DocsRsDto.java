package aakrasnov.diploma.client.dto;

import aakrasnov.diploma.common.DocDto;
import aakrasnov.diploma.common.RsBaseDto;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DocsRsDto extends RsBaseDto implements Serializable {
    private List<DocDto> docs;
}
