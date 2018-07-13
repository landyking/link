package com.github.landyking.link.beetl;

import org.beetl.core.GeneralVarTagBinding;

import java.io.IOException;

/**
 * Created by landy on 2018/5/11.
 */
public class TrimTag extends GeneralVarTagBinding {
    @Override
    public void render() {
        String body = getBodyContent().getBody();
        if (body != null) {
            String original = body.trim();
            String tmp = original.toUpperCase();
            if (tmp.startsWith("AND ")) {
                original = original.substring(4);
            } else if (tmp.startsWith("OR ")) {
                original = original.substring(3);
            }
            try {
                ctx.byteWriter.writeString(original);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
