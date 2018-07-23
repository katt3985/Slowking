package project.slowking.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode
public class CommandInvocation implements CommandObject {
    String command;
    Optional<String> subCommand;
    List<String> args;

}
