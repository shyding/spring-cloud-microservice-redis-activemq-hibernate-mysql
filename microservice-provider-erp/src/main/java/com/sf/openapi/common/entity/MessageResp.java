//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sf.openapi.common.entity;

import com.sf.aries.core.model.BaseEntity;
import com.sf.openapi.common.entity.HeadMessageResp;

public class MessageResp<T> extends BaseEntity {
    private static final long serialVersionUID = -5368272493882237580L;
    private HeadMessageResp head;
    private T body;

    public MessageResp() {
    }

    public static MessageResp<Object> getMessageRespOk() {
        MessageResp messageResp = new MessageResp();
        messageResp.setHead(HeadMessageResp.getOk());
        return messageResp;
    }

    public static MessageResp<Object> getMessageRespExc() {
        MessageResp messageResp = new MessageResp();
        messageResp.setHead(HeadMessageResp.getSecurityErr());
        return messageResp;
    }

    public HeadMessageResp getHead() {
        return this.head;
    }

    public void setHead(HeadMessageResp head) {
        this.head = head;
    }

    public T getBody() {
        return this.body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
