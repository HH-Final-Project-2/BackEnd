package com.sparta.finalpj.domain;

public enum CompanyType {
    own(0), //자사
    other(1); //타사

    private int code;


    CompanyType(int code) {
        this.code = code;
    }
}
