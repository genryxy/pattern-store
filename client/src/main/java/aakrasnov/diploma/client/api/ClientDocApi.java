package aakrasnov.diploma.client.api;

import aakrasnov.diploma.client.domain.User;
import aakrasnov.diploma.client.dto.AddDocRsDto;
import aakrasnov.diploma.client.dto.DocsRsDto;
import aakrasnov.diploma.client.dto.GetDocRsDto;
import aakrasnov.diploma.client.dto.UpdateDocRsDto;
import aakrasnov.diploma.common.DocDto;
import aakrasnov.diploma.common.Filter;
import aakrasnov.diploma.common.RsBaseDto;
import aakrasnov.diploma.common.cache.DocValidityCheckRsDto;
import java.util.List;

public interface ClientDocApi {
    /**
     * Get document from common pool by id.
     * This method is available without authentication.
     * @param id Document id
     * @return Document and http status.
     */
    GetDocRsDto getDocFromCommon(String id);

    /**
     * Apply passed filters to the documents from common pool.
     * @param filters Filters for applying
     * @return Filtered documents from common pool and http status.
     */
    DocsRsDto filterDocsFromCommon(List<Filter> filters);

    /**
     * Check validity of the document from common pool
     * by the specified parameters.
     * @param id Document id
     * @param timestamp Document timestamp
     * @return Result of checking validity of the document by specified fields.
     */
    DocValidityCheckRsDto checkDocValidityByTimestampFromCommon(String id, String timestamp);

    /**
     * Get all documents from db from common pool.
     * @return All documents from common pool.
     */
    DocsRsDto getAllDocsFromCommon();

    /**
     * Get document by the specified id using passed user.
     * User should be authenticated and have access to the document
     * in order to get documents.
     * @param id Document id
     * @param user User identity
     * @return Document and http status.
     */
    GetDocRsDto getDoc(String id, User user);

    /**
     * Delete document by the specified id.
     * @param id Document id
     * @param user User identity
     * @return {@link org.apache.http.HttpStatus} of request and message.
     */
    RsBaseDto deleteById(String id, User user);

    /**
     * Add a new document.
     * @param document Document for addition
     * @param user User identity
     * @return Added document and http status.
     */
    AddDocRsDto add(DocDto document, User user);

    /**
     * Update existed document by the passed document for the specified id.
     * @param id Document id for update
     * @param docUpd Document with info which will be saved
     * @param user User identity
     * @return Updated document.
     */
    UpdateDocRsDto update(String id, DocDto docUpd, User user);

    /**
     * Check validity of the document by the specified parameters.
     * @param id Document id
     * @param timestamp Document timestamp
     * @param user User identity
     * @return Result of checking validity of the document by specified fields.
     */
    DocValidityCheckRsDto checkDocValidityByTimestamp(String id, String timestamp, User user);

    /**
     * Apply passed filters to the documents.
     * @param filters Filters for applying
     * @param user User identity
     * @return Filtered documents and http status.
     */
    DocsRsDto filterDocuments(List<Filter> filters, User user);

    /**
     * Get all documents available for passed user from db.
     * For admin all documents should be returned.
     * @param user User identity
     * @return All documents which are available for passed user.
     */
    DocsRsDto getAllDocsForUser(User user);

    /**
     * Get all docs which connected to the specified team.
     * The user must be a member of the team.
     * @param teamId Team id
     * @param user User identity
     * @return All docs for the specified team, which is one of user's team.
     */
    DocsRsDto getDocsByTeamId(String teamId, User user);

    class Fake implements ClientDocApi {

        @Override
        public GetDocRsDto getDocFromCommon(final String id) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public DocsRsDto filterDocsFromCommon(final List<Filter> filters) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public DocValidityCheckRsDto checkDocValidityByTimestampFromCommon(
            final String id, final String timestamp
        ) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public DocsRsDto getAllDocsFromCommon() {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public GetDocRsDto getDoc(final String id, final User user) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public RsBaseDto deleteById(final String id, final User user) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public AddDocRsDto add(final DocDto document, final User user) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public UpdateDocRsDto update(final String id, final DocDto docUpd, final User user) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public DocValidityCheckRsDto checkDocValidityByTimestamp(
            final String id, final String timestamp, final User user
        ) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public DocsRsDto filterDocuments(final List<Filter> filters, final User user) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public DocsRsDto getAllDocsForUser(final User user) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }

        @Override
        public DocsRsDto getDocsByTeamId(final String teamId, final User user) {
            throw new UnsupportedOperationException("not support in fake implementation");
        }
    }
}
