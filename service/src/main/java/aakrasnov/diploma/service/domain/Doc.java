package aakrasnov.diploma.service.domain;

import aakrasnov.diploma.common.DocDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@ToString(of = {"id", "lang"})
@NoArgsConstructor
@AllArgsConstructor
@Document(DocumentNames.DOCS)
public class Doc {
    @Id
    private ObjectId id;

    @NonNull
    private String lang;

    @NonNull
    private Scenario scenario;

    // TODO: set of teams
    private Team team;

    private String timestamp;

    @NonNull
    private List<Pattern> patterns;

    public static DocDto toDto(Doc doc) {
        DocDto dto = new DocDto();
        dto.setId(doc.getId().toHexString());
        if (doc.getTeam() != null) {
            dto.setTeam(Team.toDto(doc.getTeam()));
        }
        dto.setLang(doc.getLang());
        dto.setTimestamp(doc.getTimestamp());
        dto.setScenario(Scenario.toDto(doc.getScenario()));
        dto.setPatterns(
            doc.getPatterns()
                .stream().map(Pattern::toDto)
                .collect(Collectors.toList())
        );
        return dto;
    }

    public static Doc fromDto(DocDto dto) {
        Doc doc = new Doc();
        if (dto.getId() != null) {
            doc.setId(new ObjectId(dto.getId()));
        }
        if (dto.getTeam() != null) {
            doc.setTeam(Team.fromDto(dto.getTeam()));
        }
        doc.setLang(dto.getLang());
        doc.setTimestamp(dto.getTimestamp());
        doc.setScenario(Scenario.fromDto(dto.getScenario()));
        doc.setPatterns(
            dto.getPatterns()
                .stream().map(Pattern::fromDto)
                .collect(Collectors.toList())
        );
        return doc;
    }
}
