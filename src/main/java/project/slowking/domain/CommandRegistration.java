package project.slowking.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;


@Data
@EqualsAndHashCode
public class CommandRegistration implements CommandObject {
    private String command;
    private Optional<String> subCommand;
    private Object component;
    private Method method;
    private String help;
    private List<String> parameters;

}

