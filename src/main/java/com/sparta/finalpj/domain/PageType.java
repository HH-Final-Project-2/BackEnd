package com.sparta.finalpj.domain;

import lombok.Getter;

@Getter
public enum PageType {
    mypages("mypages",0),
    own("own",1),
    other("other",2);


    private  String value;
    private int code;

    PageType(String value, int code) {

        this.value = value;
        this.code = code;
    }
}
