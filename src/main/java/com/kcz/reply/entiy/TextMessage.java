package com.kcz.reply.entiy;

import lombok.Data;

@Data
public class TextMessage extends BaseWechatMessage {
    private String Content;
}
