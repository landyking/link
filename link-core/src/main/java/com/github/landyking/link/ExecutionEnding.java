package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;

import java.util.Map;

/**
 * Created by landy on 2018/7/19.
 */
public interface ExecutionEnding {
    Map<String, ExecuteResult> process(DirectiveMojo mojo) throws LinkException;
}
