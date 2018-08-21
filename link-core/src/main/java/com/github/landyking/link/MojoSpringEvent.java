package com.github.landyking.link;

import org.springframework.context.ApplicationEvent;

/**
 * Created by landy on 2018/8/21.
 */
public class MojoSpringEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public MojoSpringEvent(Object source) {
        super(source);
    }
}
