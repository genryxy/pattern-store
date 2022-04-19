package aakrasnov.diploma.client;

import aakrasnov.diploma.client.dto.stata.AddStataRsDto;
import aakrasnov.diploma.client.dto.stata.GetDownloadDocsRsDto;
import aakrasnov.diploma.client.dto.stata.GetStataDocRsDto;
import aakrasnov.diploma.client.dto.stata.GetStataMergedDocRsDto;
import aakrasnov.diploma.client.dto.stata.GetStataMergedPtrnsRsDto;
import aakrasnov.diploma.client.dto.stata.GetStataPtrnsRsDto;
import aakrasnov.diploma.common.StatisticDto;
import java.util.List;
import java.util.Set;

public interface ClientStatisticApi {
    /**
     * Send statistic about usage of pattern for the document.
     * @param statistics Statistic of usage of the document with patterns
     * @return Added statistic with status.
     */
    AddStataRsDto sendDocStatistic(List<StatisticDto> statistics);

    /**
     * Get statistic for patterns by ids.
     * @param patternIds Patterns ids
     * @return Statistic for patterns with status.
     */
    GetStataPtrnsRsDto getStatisticForPatterns(Set<String> patternIds);

    /**
     * Merge statistic for the patterns if they are included in several
     * database documents by summation of values.
     * @param patternIds Patterns ids
     * @return Merged statistic for patterns with status.
     */
    GetStataMergedPtrnsRsDto getStatisticMergedForPatterns(Set<String> patternIds);

    /**
     * Get statistic for the document. It can be included in
     * many entries in the database.
     * @param docId Document id
     * @return Get statistic for the specified document with status.
     */
    GetStataDocRsDto getStatisticForDoc(String docId);

    /**
     * Merge statistic for the document if it is included in several
     * database entries by summation of values for patterns.
     * @param docId Document id
     * @return Merged statistic for the document with status.
     */
    GetStataMergedDocRsDto getStatisticUsageMergedForDoc(String docId);

    /**
     * Get download count for the documents in mapping.
     * @param docIds Documents ids
     * @return Download count for the documents in mapping (e.g.{"docId_1":5})
     *  with status.
     */
    GetDownloadDocsRsDto getDownloadsCountForDocs(Set<String> docIds);
}