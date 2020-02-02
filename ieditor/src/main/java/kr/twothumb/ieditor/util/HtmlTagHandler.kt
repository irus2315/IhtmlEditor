package kr.twothumb.ieditor.util

import android.text.Editable
import android.text.Html
import kr.twothumb.lib.logger.DevLog
import org.xml.sax.XMLReader

class HtmlTagHandler:Html.TagHandler {


    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        when(tag){
            "html" -> return
            "body" -> return
        }
//        DevLog.i(output.)
        DevLog.e(tag, " , opening : ", opening, " , " , output)
    }
}
