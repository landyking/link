package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;

/**
 * Created by landy on 2018/7/10.
 */
public abstract class AbstractExecution {
    public abstract void execute(DirectiveMojo mojo) throws LinkException;
}
