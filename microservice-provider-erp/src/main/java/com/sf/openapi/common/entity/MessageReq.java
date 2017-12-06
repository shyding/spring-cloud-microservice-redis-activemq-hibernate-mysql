//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sf.openapi.common.entity;

import com.sf.aries.core.model.BaseEntity;

public class MessageReq<T> extends BaseEntity {
    private static final long serialVersionUID = -2803114997756553307L;
    private HeadMessageReq head;
    private T body;

    public MessageReq() {
    }

    public HeadMessageReq getHead() {
        return this.head;
    }

    public void setHead(HeadMessageReq head) {
        this.head = head;
    }

    public T getBody() {
        return this.body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
