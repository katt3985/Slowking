package project.slowking.annotation.processor;

import project.slowking.annotation.EventController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

@Component
public class EventControllerProcessor implements BeanPostProcessor {

    private ConfigurableListableBeanFactory configurableBeanFactory;
    private EventDispatcher dispatcher;

    @Autowired
    public EventControllerProcessor(ConfigurableListableBeanFactory beanFactory, IDiscordClient client) {
        this.configurableBeanFactory = beanFactory;
        this.dispatcher = client.getDispatcher();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(EventController.class))
            dispatcher.registerListener(bean);

        return bean;
    }

}
