package aakrasnov.diploma.service.service.api;

import aakrasnov.diploma.common.DocDto;
import aakrasnov.diploma.common.Filter;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;

/**
 * Logic for interacting with documents.
 */
public interface DocService {
    /**
     * Find document with patterns by id.
     * @param id Id of the document
     * @return Found documents or empty in case of absence.
     */
    Optional<DocDto> findById(String id);

    /**
     * Add a new document to the database.
     * @param docDto Document which should be added
     * @return Added document.
     */
    DocDto addDoc(DocDto docDto);

    /**
     * Delete document by the specified id.
     * @param id Id of the document
     */
    void deleteById(String id);

    /**
     * Update document by the specified id with passed dto.
     * In order for the update to be successful author of the document for
     * update should be either in the team of the creators of the
     * document by passed id or an admin.
     * @param id Id of the document for update
     * @param updDto The document with updated info
     * @param userId Id of the user, who perform update
     * @return Result of operation (OK, BAD_REQUEST, FORBIDDEN).
     */
    HttpStatus update(String id, DocDto updDto, String userId);

    /**
     * Get collection with documents which match passed filters.
     * @param filters Filters to limit documents
     * @return List of documents which match filters.
     */
    List<DocDto> filteredDocuments(List<Filter> filters);

    /**
     * Obtains all existing documents. This operation should be available
     * only for admin users.
     * @return All documents.
     */
    List<DocDto> getAllDocs();
}
