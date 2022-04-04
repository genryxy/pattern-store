package aakrasnov.diploma.client;

import aakrasnov.diploma.common.DocDto;
import aakrasnov.diploma.common.Filter;
import java.util.List;
import java.util.Optional;

public interface ClientApi {

    Optional<DocDto> document(long id);

    void deleteById(long id);

    void add(DocDto document);

    void update(long id, DocDto altered);

    List<DocDto> filteredDocuments(Filter filter);

    List<DocDto> filteredDocuments(List<Filter> filters);
}
