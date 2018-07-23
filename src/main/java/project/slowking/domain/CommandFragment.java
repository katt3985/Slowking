package project.slowking.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode
public class CommandFragment implements CommandObject {
    private String command;
    private Optional<String> subCommand;
}
