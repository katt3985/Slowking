package project.slowking.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DiscordCommand {

    String command() default "";
    String subCommand() default "";
    String[] parameters() default {};
    String help() default "no help is avalible for this command";
}
