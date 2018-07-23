package project.slowking.annotation.processor;

import project.slowking.annotation.CommandController;
import project.slowking.service.CommandRegistrationDirectory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommandObjectProcessor implements BeanPostProcessor {

    private ConfigurableListableBeanFactory configurableBeanFactory;
    private List<Object> listeners = new ArrayList<>();

    @Autowired
    public CommandObjectProcessor(ConfigurableListableBeanFactory beanFactory) {
        this.configurableBeanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(CommandController.class))
            listeners.add(bean);

        return bean;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
       CommandRegistrationDirectory commander = configurableBeanFactory
               .getBean("commandRegistrationDirectory", CommandRegistrationDirectory.class);
       listeners.forEach(commander::register);
    }

}
