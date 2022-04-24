package aakrasnov.diploma.client.api;

import aakrasnov.diploma.client.dto.stata.GetStataDocRs;
import aakrasnov.diploma.client.dto.stata.GetStataPtrnsRs;
import aakrasnov.diploma.client.http.AddSlash;
import aakrasnov.diploma.client.http.RqExecution;
import aakrasnov.diploma.common.stata.AddStataRsDto;
import aakrasnov.diploma.common.stata.GetDownloadDocsRsDto;
import aakrasnov.diploma.common.stata.GetStataMergedDocRsDto;
import aakrasnov.diploma.common.stata.GetStataMergedPtrnsRsDto;
import aakrasnov.diploma.common.stata.StatisticDto;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

@Slf4j
public class ClientStatisticApiImpl implements ClientStatisticApi {
    private final HttpClient httpClient;

    private final Gson gson;

    /**
     * Base path for URL which should contain schema and host.
     * Port in general is optional.
     */
    private final String base;

    public ClientStatisticApiImpl(final HttpClient httpClient, final String base) {
        this.httpClient = httpClient;
        this.base = new AddSlash(base).addIfAbsent();
        this.gson = new Gson();
    }

    @Override
    public AddStataRsDto sendDocStatistic(final List<StatisticDto> statistics) {
        HttpPost rq = new HttpPost(full("statistic"));
        BasicClientDocApi.addJsonHeaderTo(rq);
        AddStataRsDto res = new AddStataRsDto();
        try {
            StringEntity entity = new StringEntity(gson.toJson(statistics));
            rq.setEntity(entity);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format("Failed to upload statistic '%s'", statistics),
                HttpStatus.SC_CREATED
            );
            if (rsp.isPresent()) {
                res.setStatisticDocs(
                    gson.fromJson(
                        EntityUtils.toString(rsp.get().getEntity()),
                        AddStataRsDto.class
                    ).getStatisticDocs()
                );
            }
        } catch (IOException exc) {
            log.error("Failed to convert entity", exc);
            res.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
        return res;
    }

    @Override
    public GetStataPtrnsRs getStatisticForPatterns(final Set<String> patternIds) {
        return null;
    }

    @Override
    public GetStataMergedPtrnsRsDto getStatisticMergedForPatterns(final Set<String> patternIds) {
        return null;
    }

    @Override
    public GetStataDocRs getStatisticForDoc(final String docId) {
        return null;
    }

    @Override
    public GetStataMergedDocRsDto getStatisticUsageMergedForDoc(final String docId) {
        return null;
    }

    @Override
    public GetDownloadDocsRsDto getDownloadsCountForDocs(final Set<String> docIds) {
        return null;
    }

    private String full(String uri) {
        return String.format("%s%s", base, uri);
    }

}
