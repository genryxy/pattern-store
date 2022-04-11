package aakrasnov.diploma.service.domain;

import aakrasnov.diploma.common.TeamDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "name"})
@ToString(of = {"id", "name"})
@Document("patterns")
public class Team {
    public static ObjectId COMMON_OBJ_ID = new ObjectId("624f6f6b5bdddf7ee83350a0");

    @Id
    @NonNull
    private String id;

    @NonNull
    private String name;

    private String invitation;

    public static TeamDto toDto(Team team) {
        TeamDto dto = new TeamDto();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setInvitation(team.getInvitation());
        return dto;
    }

    public static Team fromDto(TeamDto dto) {
        Team team = new Team();
        team.setId(dto.getId());
        team.setName(dto.getName());
        team.setInvitation(dto.getInvitation());
        return team;
    }
}