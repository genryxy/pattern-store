package aakrasnov.diploma.client.api;

import aakrasnov.diploma.client.domain.User;
import aakrasnov.diploma.client.dto.team.AddTeamRsDto;
import aakrasnov.diploma.client.dto.team.DeleteTeamRsDto;
import aakrasnov.diploma.client.dto.team.TeamInfoRsDto;
import aakrasnov.diploma.client.dto.team.UpdateTeamRsDto;
import aakrasnov.diploma.client.http.AddSlash;
import aakrasnov.diploma.client.http.BasicAuthorization;
import aakrasnov.diploma.client.http.ExceptionCatcher;
import aakrasnov.diploma.client.http.RqExecution;
import aakrasnov.diploma.common.RsBaseDto;
import aakrasnov.diploma.common.TeamDto;
import com.google.gson.Gson;
import java.util.Optional;
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
public class ClientTeamApiImpl implements ClientTeamApi {
    private final HttpClient httpClient;

    private final Gson gson;

    /**
     * Base path for URL which should contain schema and host.
     * Port in general is optional.
     */
    private final String base;

    public ClientTeamApiImpl(final HttpClient httpClient, final String base) {
        this.httpClient = httpClient;
        this.base = new AddSlash(base).addIfAbsent();
        this.gson = new Gson();
    }

    @Override
    public RsBaseDto joinTeamByInvite(final String invitation, final User user) {
        HttpGet rq = new HttpGet(full(String.format("auth/team/join/%s", invitation)));
        RsBaseDto res = new RsBaseDto();
        new BasicAuthorization(rq, res).add(user);
        new ExceptionCatcher.IOCatcher<>(
            () -> new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format("Failed to join to the team by invite '%s'", invitation)
            )
        ).runAndSetFail(res);
        return res;
    }

    @Override
    public TeamInfoRsDto getTeamInfoByInvite(final String invite, final User user) {
        return executeHttpGet(
            full(String.format("auth/team/invite/%s", invite)),
            user,
            String.format("Failed to get team by invitation '%s'", invite)
        );
    }

    @Override
    public TeamInfoRsDto getTeamInfoById(final String id, final User user) {
        return executeHttpGet(
            full(String.format("auth/team/%s", id)),
            user,
            String.format("Failed to get team by id '%s'", id)
        );
    }

    @Override
    public AddTeamRsDto add(final TeamDto team, final User user) {
        HttpPost rq = new HttpPost(full("auth/team/create"));
        addJsonHeaderTo(rq);
        AddTeamRsDto res = new AddTeamRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            new BasicAuthorization(rq, res).add(user);
            StringEntity entity = new StringEntity(gson.toJson(team));
            rq.setEntity(entity);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res,
                String.format("Failed to add team '%s'", team),
                HttpStatus.SC_CREATED
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setTeamDto(gson.fromJson(body, TeamDto.class));
            }
        }).runAndSetFail(res);
        return res;
    }

    @Override
    public TeamInfoRsDto updateInviteCodeById(final String id, final User user) {
        return executeHttpGet(
            full(String.format("auth/team/%s/update/invite", id)),
            user,
            String.format("Failed to update team invitation by id '%s'", id)
        );
    }

    @Override
    public TeamInfoRsDto updateInviteCodeByInvite(final String invite, final User user) {
        return executeHttpGet(
            full(String.format("auth/team/invite/%s/update/invite", invite)),
            user,
            String.format("Failed to update team invitation by invite '%s'", invite)
        );
    }

    @Override
    public DeleteTeamRsDto deleteById(final String id, final User user) {
        HttpDelete rq = new HttpDelete(full(String.format("admin/team/%s/delete", id)));
        DeleteTeamRsDto res = new DeleteTeamRsDto();
        new BasicAuthorization(rq, res).add(user);
        new RqExecution(httpClient, rq).execAnSetStatus(
            res,
            String.format("Failed to delete team by id '%s'", id)
        );
        if (res.getStatus() == HttpStatus.SC_OK) {
            log.info(String.format("Deleted team by id '%s'", id));
        }
        return res;
    }

    @Override
    public UpdateTeamRsDto updateTeamById(
        final String id, final TeamDto teamUpd, final User user
    ) {
        return updateTeam(
            full(String.format("auth/team/%s/update", id)),
            teamUpd,
            user,
            String.format("Failed to update team with id '%s' with '%s'", id, teamUpd.toString())
        );
    }

    @Override
    public UpdateTeamRsDto updateTeamByInvite(
        final String invite, final TeamDto teamUpd, final User user
    ) {
        return updateTeam(
            full(String.format("auth/team/invite/%s/update", invite)),
            teamUpd,
            user,
            String.format(
                "Failed to update team with invite '%s' with '%s'", invite, teamUpd.toString()
            )
        );
    }

    static void addJsonHeaderTo(HttpPost rq) {
        rq.addHeader("Content-type", "application/json");
    }

    private String full(String uri) {
        return String.format("%s%s", base, uri);
    }

    private UpdateTeamRsDto updateTeam(
        String url, TeamDto teamUpd, User user, String errMsg
    ) {
        HttpPost rq = new HttpPost(url);
        addJsonHeaderTo(rq);
        UpdateTeamRsDto res = new UpdateTeamRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            new BasicAuthorization(rq, res).add(user);
            StringEntity entity = new StringEntity(gson.toJson(teamUpd));
            rq.setEntity(entity);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res, errMsg
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setTeamDto(gson.fromJson(body, TeamDto.class));
            }
        }).runAndSetFail(res);
        return res;
    }

    private TeamInfoRsDto executeHttpGet(String url, User user, String errMsg) {
        HttpGet rq = new HttpGet(url);
        TeamInfoRsDto res = new TeamInfoRsDto();
        new ExceptionCatcher.IOCatcher<>(() -> {
            new BasicAuthorization(rq, res).add(user);
            Optional<HttpResponse> rsp = new RqExecution(httpClient, rq).execAnSetStatus(
                res, errMsg
            );
            if (rsp.isPresent()) {
                String body = EntityUtils.toString(rsp.get().getEntity());
                log.info(body);
                res.setTeamDto(gson.fromJson(body, TeamDto.class));
            }
        }).runAndSetFail(res);
        return res;
    }
}
