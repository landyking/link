package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;

/**
 * Created by landy on 2018/7/19.
 */
public interface ExecutionEnding {
    ExecutionEndingData process(DirectiveMojo mojo) throws LinkException;
}
