package aakrasnov.diploma.client.api;

import aakrasnov.diploma.client.domain.User;
import aakrasnov.diploma.client.dto.AddDocRsDto;
import aakrasnov.diploma.client.dto.DocsRsDto;
import aakrasnov.diploma.client.dto.GetDocRsDto;
import aakrasnov.diploma.client.dto.UpdateDocRsDto;
import aakrasnov.diploma.client.http.AddSlash;
import aakrasnov.diploma.client.http.BasicAuthorization;
import aakrasnov.diploma.client.http.ExceptionCatcher;
import aakrasnov.diploma.client.http.RqExecution;
import aakrasnov.diploma.common.DocDto;
import aakrasnov.diploma.common.Filter;
import aakrasnov.diploma.common.RsBaseDto;
import aakrasnov.diploma.common.cache.DocValidityCheckRsDto;
import aakrasnov.diploma.common.cache.DocValidityDto;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

@Slf4j
public final class BasicClientDocApi implements ClientDocApi {

    private final HttpClient httpClient;

    private final Gson gson;

    /**
     * Base path for URL which should contain schema and host.
     * Port in general is optional.
     */
    private final String base;

    public BasicClientDocApi(final HttpClient httpClient, final String base) {
        this.httpClient = httpClient;
        this.base = new AddSlash(base).addIfAbsent();
        this.gson = new Gson();
    }

    @Override
    public GetDocRsDto getDocFromCommon(final String id) {
        HttpGet rq = new HttpGet(full(String.format("doc/%s", id)));
        GetDocRsDto res = new GetDocRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format("Failed to get document from common pool by id '%s'", id)
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setDocDto(gson.fromJson(body, DocDto.class));
            }
        }).runAndSetFail(res);
        return res;
    }

    @Override
    public DocsRsDto filterDocsFromCommon(final List<Filter> filters) {
        HttpPost rq = new HttpPost(full("docs/filtered"));
        addJsonHeaderTo(rq);
        DocsRsDto res = new DocsRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            StringEntity entity = new StringEntity(gson.toJson(filters));
            rq.setEntity(entity);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format(
                    "Failed to filter documents from common pool by filters '%s'", filters
                )
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setDocs(
                    Arrays.asList(gson.fromJson(body, DocDto[].class))
                );
            }
        }).runAndSetFail(res);
        return res;
    }

    @Override
    public DocValidityCheckRsDto checkDocValidityByTimestampFromCommon(
        final String id, final String timestamp
    ) {
        HttpPost rq = new HttpPost(full("doc/check-validity-timestamp"));
        addJsonHeaderTo(rq);
        final AtomicReference<DocValidityCheckRsDto> res;
        res = new AtomicReference<>(new DocValidityCheckRsDto());
        new ExceptionCatcher.IOCatcher<>(() -> {
            StringEntity entity = new StringEntity(
                gson.toJson(new DocValidityDto(id, timestamp))
            );
            rq.setEntity(entity);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res.get(),
                String.format("Failed to check doc '%s' validity by timestamp from common pool", id)
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.set(gson.fromJson(body, DocValidityCheckRsDto.class));
            }
        }).runAndSetFail(res.get());
        return res.get();
    }

    @Override
    public DocsRsDto getAllDocsFromCommon() {
        HttpPost rq = new HttpPost(full("docs/filtered"));
        addJsonHeaderTo(rq);
        DocsRsDto res = new DocsRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res, "Failed to get all documents from common pool"
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setDocs(
                    Arrays.asList(gson.fromJson(body, DocDto[].class))
                );
            }
        }).runAndSetFail(res);
        return res;
    }

    @Override
    public GetDocRsDto getDoc(final String id, final User user) {
        HttpGet rq = new HttpGet(full(String.format("auth/doc/%s", id)));
        GetDocRsDto res = new GetDocRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            new BasicAuthorization(rq, res).add(user);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format("Failed to get document by id '%s'", id)
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setDocDto(gson.fromJson(body, DocDto.class));
            }
        }).runAndSetFail(res);
        return res;
    }

    @Override
    public RsBaseDto deleteById(final String id, final User user) {
        HttpDelete rq = new HttpDelete(full(String.format("admin/doc/%s/delete", id)));
        RsBaseDto res = new RsBaseDto();
        new BasicAuthorization(rq, res).add(user);
        new RqExecution(httpClient, rq).execAnSetStatus(
            res,
            String.format("Failed to delete document by id '%s'", id)
        );
        if (res.getStatus() == HttpStatus.SC_OK) {
            log.info(String.format("Deleted document by id '%s'", id));
        }
        return res;
    }

    @Override
    public AddDocRsDto add(final DocDto document, final User user) {
        HttpPost rq = new HttpPost(full("auth/doc"));
        addJsonHeaderTo(rq);
        AddDocRsDto res = new AddDocRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            new BasicAuthorization(rq, res).add(user);
            StringEntity entity = new StringEntity(gson.toJson(document));
            rq.setEntity(entity);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format("Failed to add document '%s'", document),
                HttpStatus.SC_CREATED
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setDocDto(gson.fromJson(body, DocDto.class));
            }
        }).runAndSetFail(res);
        return res;
    }

    @Override
    public UpdateDocRsDto update(final String id, final DocDto docUpd, final User user) {
        HttpPost rq = new HttpPost(full(String.format("auth/doc/%s/update", id)));
        addJsonHeaderTo(rq);
        UpdateDocRsDto res = new UpdateDocRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            new BasicAuthorization(rq, res).add(user);
            StringEntity entity = new StringEntity(gson.toJson(docUpd));
            rq.setEntity(entity);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format("Failed to update doc with id '%s' with '%s'", id, docUpd.toString())
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setDocDto(gson.fromJson(body, DocDto.class));
            }
        }).runAndSetFail(res);
        return res;
    }

    @Override
    public DocValidityCheckRsDto checkDocValidityByTimestamp(
        final String id, final String timestamp, final User user
    ) {
        HttpPost rq = new HttpPost(full("auth/doc/check-validity-timestamp"));
        addJsonHeaderTo(rq);
        final AtomicReference<DocValidityCheckRsDto> res;
        res = new AtomicReference<>(new DocValidityCheckRsDto());
        new ExceptionCatcher.IOCatcher<>(() -> {
            new BasicAuthorization(rq, res.get()).add(user);
            StringEntity entity = new StringEntity(
                gson.toJson(new DocValidityDto(id, timestamp))
            );
            rq.setEntity(entity);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res.get(),
                String.format("Failed to check doc '%s' validity by timestamp", id)
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.set(gson.fromJson(body, DocValidityCheckRsDto.class));
            }
        }).runAndSetFail(res.get());
        return res.get();
    }

    @Override
    public DocsRsDto filterDocuments(final List<Filter> filters, final User user) {
        HttpPost rq = new HttpPost(full("auth/docs/filtered"));
        addJsonHeaderTo(rq);
        DocsRsDto res = new DocsRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            new BasicAuthorization(rq, res).add(user);
            StringEntity entity = new StringEntity(gson.toJson(filters));
            rq.setEntity(entity);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format("Failed to filter documents by id '%s'", filters)
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setDocs(
                    Arrays.asList(gson.fromJson(body, DocDto[].class))
                );
            }
        }).runAndSetFail(res);
        return res;
    }

    @Override
    public DocsRsDto getAllDocsForUser(final User user) {
        HttpGet rq = new HttpGet(full("auth/docs/user"));
        DocsRsDto res = new DocsRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            new BasicAuthorization(rq, res).add(user);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format("Failed to filter documents by user's teams '%s'", user.getUsername())
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setDocs(
                    Arrays.asList(gson.fromJson(body, DocDto[].class))
                );
            }
        }).runAndSetFail(res);
        return res;
    }

    @Override
    public DocsRsDto getDocsByTeamId(final String teamId, final User user) {
        HttpGet rq = new HttpGet(full(String.format("auth/docs/team/%s", teamId)));
        DocsRsDto res = new DocsRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            new BasicAuthorization(rq, res).add(user);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format("Failed to filter documents by team id '%s'", teamId)
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setDocs(
                    Arrays.asList(gson.fromJson(body, DocDto[].class))
                );
            }
        }).runAndSetFail(res);
        return res;
    }

    static void addJsonHeaderTo(HttpPost rq) {
        rq.addHeader("Content-type", "application/json");
    }

    private String full(String uri) {
        return String.format("%s%s", base, uri);
    }

}
