package kr.twothumb.ieditor.model

enum class Tag(val value:String){
    H3_START("<h3"),
    H3_END("</h3>"),

    H3_START_EDITOR("<h3_editor"),
    H3_END_EDITOR("</h3_editor>"),

    H4_START("<h4"),
    H4_END("</h4>"),

    H4_START_EDITOR("<h4_editor"),
    H4_END_EDITOR("</h4_editor>"),

    P_START("<p"),
    P_END("</p>"),

    P_START_EDITOR("<p_editor"),
    P_END_EDITOR("</p_editor>"),

    TAG_CLOSE(">"),
    ALIGN_LEFT(" text-align:\"left\">"),
    ALIGN_CENTER(" text-align:\"center\">"),
    ALIGN_RIGHT(" text-align:\"right\">"),

    STRONG_START("<strong>"),
    STRONG_END("</strong>"),
}