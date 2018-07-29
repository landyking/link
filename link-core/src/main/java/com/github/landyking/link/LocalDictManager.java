package com.github.landyking.link;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @author: landy
 * @date: 2018-07-29 22:44
 */
public class LocalDictManager implements ApplicationContextAware, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private String localDictFileLocation = "classpath*:/link/dict/**/*.xml";
    private ApplicationContext application;
    private LinkedHashMap<String, LocalDict> localDictMap = Maps.newLinkedHashMap();

    @Override
    public void afterPropertiesSet() throws Exception {
        reload();
    }

    private void reload() throws Exception {
        LinkedHashMap<String, LocalDict> tmp = this.localDictMap;
        this.localDictMap = load();
        for (LocalDict one : tmp.values()) {
            one.getItems().clear();
        }
        tmp.clear();
    }

    private LinkedHashMap<String, LocalDict> load() throws Exception {
        LinkedHashMap<String, LocalDict> rst = Maps.newLinkedHashMap();
        Resource[] resources = application.getResources(localDictFileLocation);
        if (resources == null || resources.length == 0) {
            logger.info("无本地字典需要加载");
        } else {
            for (Resource one : resources) {
                LinkedList<LocalDict> tmpList = null;
                try {
                    logger.info("加载本地字典文件: {}", one.toString());
                    LocalDictParser parser = new LocalDictParser(one);
                    tmpList = parser.parseDictList();
                } catch (Exception e) {
                    throw new Exception("解析本地字典文件" + one.getFile().getAbsolutePath() + "失败，请检查文件格式", e);
                }
                for (LocalDict ld : tmpList) {
                    if (rst.containsKey(ld.getName())) {
                        throw new Exception("文件" + one.getFile().getAbsolutePath() + "中的字典[" + ld.getName() + ":" + ld.getDesc() + "]重复定义");
                    } else {
                        rst.put(ld.getName(), ld);
                        logger.info("加载字典{}:{}，字典项{}个", ld.getName(), ld.getDesc(), ld.getItems().size());
                    }
                }
            }
        }
        return rst;
    }

    public String translate(String dictName, String dictItemCode) {
        if (localDictMap.containsKey(dictName)) {
            LocalDict dict = localDictMap.get(dictName);
            if (dict.getItems().containsKey(dictItemCode)) {
                return dict.getItems().get(dictItemCode).getContent();
            }
        }
        return null;
    }

    public LocalDict getLocalDict(String dictName) {
        if (localDictMap.containsKey(dictName)) {
            return localDictMap.get(dictName);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.application = applicationContext;
    }
}
