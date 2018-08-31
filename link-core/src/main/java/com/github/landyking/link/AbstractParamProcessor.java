package com.github.landyking.link;

import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/11.
 */
public abstract class AbstractParamProcessor {
    /**
     * @param config  处理器配置信息节点
     * @param param   处理器所属参数节点
     * @param mojo    当前指令上下文
     * @param name    所属参数名称
     * @param outList 用于存放当前以及最终输出内容的列表
     */
    public abstract void processOutput(Element config, Element param, DirectiveMojo mojo, String name, List<Map<String, Object>> outList)throws Exception;

    public String tag() {
        return getClass().getSimpleName();
    }

    /**
     * @param config 处理器配置信息节点
     * @param param  处理器所属参数节点
     * @param mojo   当前指令上下文
     * @param in     输入值
     * @return
     * @throws Exception
     */
    public abstract Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception;
}
