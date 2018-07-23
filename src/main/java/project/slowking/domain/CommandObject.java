package project.slowking.domain;

import java.util.Optional;

public interface CommandObject {

    String getCommand();
    Optional<String> getSubCommand();

    default String getFullCommandString()
    {
        if (getSubCommand().isPresent()) {
            return getCommand() + " " + getSubCommand().get();
        } else {
            return getCommand();
        }
    }

    default CommandFragment getFragment()
    {
        CommandFragment result = new CommandFragment();
        result.setCommand(getCommand());
        result.setSubCommand(getSubCommand());
        return result;
    }
}
