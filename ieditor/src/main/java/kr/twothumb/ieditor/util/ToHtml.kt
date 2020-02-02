package kr.twothumb.ieditor.util

import android.graphics.Typeface
import android.text.Html
import kr.twothumb.ieditor.model.*
import kr.twothumb.lib.logger.DevLog

class ToHtml {

//    private val strongStart = "<strong>"
//    private val strongStart = "<strong>"
//    private val strongEnd = "</strong>"
//    private val strongEnd = "</strong>"

    fun getSource(data: EditorData):String{

        val currentAlign = when(data.align){
            Align.LEFT -> Tag.ALIGN_LEFT.value
            Align.CENTER -> Tag.ALIGN_CENTER.value
            Align.RIGHT -> Tag.ALIGN_RIGHT.value
            else -> Tag.TAG_CLOSE.value
        }

        val buffer = StringBuffer(data.content)
        for(styleData in data.styleList) {
            if(styleData.style.style == Typeface.BOLD) {
                buffer.insert(styleData.end, Tag.STRONG_END.value)
                buffer.insert(styleData.start, Tag.STRONG_START.value)
            }
        }
        data.content = buffer.toString()

        data.content = when(data.fontSize){
            FontSize.T1 -> Tag.H3_START.value.plus(currentAlign).plus(data.content).plus(Tag.H3_END.value)
            FontSize.T2 -> Tag.H4_START.value.plus(currentAlign).plus(data.content).plus(Tag.H4_END.value)
            FontSize.T3 -> Tag.P_START.value.plus(currentAlign).plus(data.content).plus(Tag.P_END.value)
        }

        for(styleData in data.styleList) {
            DevLog.i(styleData.start, " , ", styleData.end)
        }

        return data.content
    }

    fun arrangeStyle(styleList:ArrayList<StyleData>):ArrayList<StyleData>{
        val sortedList = styleList.sortedWith(compareBy { it.start })
        return ArrayList(sortedList.reversed())
    }

    fun distinctStyle(styleList:ArrayList<StyleData>):ArrayList<StyleData>{
        val sortedList = styleList.distinctBy { it.start }
        return ArrayList(sortedList.reversed())
    }

    fun toHtml(html:String):String{
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString().replace("\n", "")
    }

    private fun replaceEditorTag(html:String):String{
        if(html.isEmpty()){
            return ""
        }
        return html.replace(Tag.H3_START.value, Tag.H3_START_EDITOR.value)
    }
}
