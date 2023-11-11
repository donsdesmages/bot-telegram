package com.example.bot.telegram.util;

public class ConstRegx {
    public static final String EMAIL_REGULAR_EXPRESSION =
        "^[a-zA-Z0-9]{1,}"
        +"((\\.|\\_|-{0,1})[a-zA-Z0-9]{1,})*"
        +"@" +"[a-zA-Z0-9]{1,}"
        + "((\\.|\\_|-{0,1})[a-zA-Z0-9]{1,})*"
        + "\\.[a-zA-Z]{2,}$";


}
