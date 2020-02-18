package kr.twothumb.ieditor.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.twothumb.ieditor.model.FontSize
import kr.twothumb.ieditor.util.view.EditorText
import kr.twothumb.ieditor.ui.imagepicker.ImagePicker
import kr.twothumb.ieditor.util.NotNullMutableLiveData
import kr.twothumb.ieditor.R
import kr.twothumb.ieditor.model.*
import kr.twothumb.ieditor.util.ToHtml
import kr.twothumb.lib.logger.DevLog
import kotlin.collections.ArrayList

class HtmlEditorViewModel : ViewModel() {

    val readExternalStorage = 0x2315

    val list: NotNullMutableLiveData<ArrayList<EditorData>> = NotNullMutableLiveData(arrayListOf())
    var editorList = ArrayList<EditorData>()
    val selectImage:Int = 0x0881

    //현재 포커스
    var observeFocus = MutableLiveData<Int>()
    private var currentLineFocus:Int = 0

    var observeOptionToolbarStatus = MutableLiveData<OptionToolbarStatus>()
    private var currentOptionToolbarStatus:OptionToolbarStatus = OptionToolbarStatus.Close

    fun setText(data:ArrayList<EditorData>){
        editorList = data
        list.postValue(editorList)
    }

    fun onToolbarBtnClick(v:View){
        when (v.id) {
            R.id.btn_fontsize -> {
                currentOptionToolbarStatus = if(currentOptionToolbarStatus == OptionToolbarStatus.FontSizeLayoutOpen){
                    OptionToolbarStatus.Close
                } else{
                    OptionToolbarStatus.FontSizeLayoutOpen
                }
            }
            R.id.btn_bold-> {
                currentOptionToolbarStatus = OptionToolbarStatus.Close
                editorList[currentLineFocus].setBold()
                list.postValue(editorList)
            }
            R.id.btn_align -> {
                currentOptionToolbarStatus = if(currentOptionToolbarStatus == OptionToolbarStatus.AlignLayoutOpen){
                    OptionToolbarStatus.Close
                } else {
                    OptionToolbarStatus.AlignLayoutOpen
                }
            }
            R.id.btn_image -> {
                currentOptionToolbarStatus = OptionToolbarStatus.Close
                checkPermission(v.context as Activity)
            }
        }
        observeOptionToolbarStatus.postValue(currentOptionToolbarStatus)
    }

    fun onOptionBtnClick(v:View){
        if(currentOptionToolbarStatus == OptionToolbarStatus.AlignLayoutOpen) {
            when (v.id) {
                R.id.btn1 -> editorList[currentLineFocus].align = Align.LEFT
                R.id.btn2 -> editorList[currentLineFocus].align = Align.CENTER
                R.id.btn3 -> editorList[currentLineFocus].align = Align.RIGHT
            }
        }
        else if(currentOptionToolbarStatus == OptionToolbarStatus.FontSizeLayoutOpen) {
            when (v.id) {
                R.id.btn1 -> editorList[currentLineFocus].fontSize = FontSize.T1
                R.id.btn2 -> editorList[currentLineFocus].fontSize = FontSize.T2
                R.id.btn3 -> editorList[currentLineFocus].fontSize = FontSize.T3
            }
        }
        list.postValue(editorList)
        currentOptionToolbarStatus = OptionToolbarStatus.Close
        observeFocus.postValue(currentLineFocus)
        observeOptionToolbarStatus.postValue(currentOptionToolbarStatus )
    }

    private fun checkPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    readExternalStorage
                )
            }
            else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    readExternalStorage
                )
            }
        } else {
            callImagePicker(activity)
        }
    }

    fun callImagePicker(activity:Activity){
        val i = Intent(activity, ImagePicker::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        activity.startActivityForResult (i, selectImage)

    }

    fun onImageClick(v:View){
        if(currentLineFocus == (v.tag as Int)) {
            currentLineFocus = Int.MAX_VALUE
            observeFocus.postValue(currentLineFocus)
        }
        else{
            currentLineFocus = (v.tag as Int)
            observeFocus.postValue(currentLineFocus)
        }
        list.postValue(editorList)
    }

    fun onTextFocus(v:View){
        val edit: EditorText = v as EditorText
        edit.isFocusableInTouchMode = true
        edit.isFocusable = true

        if(!edit.hasFocus())
            edit.requestFocus()
        currentLineFocus = (edit.tag as Int)
        observeFocus.postValue(currentLineFocus)
        editorList[currentLineFocus].start = edit.selectionStart
        editorList[currentLineFocus].end = edit.selectionEnd
    }
    fun onTextClick(v:View){
        onTextFocus(v)
    }

    fun toHtml():String{
        var returnValue = ""
        for(data in editorList){
            data.styleList = ToHtml().arrangeStyle(data.styleList)
            returnValue = returnValue .plus(ToHtml().getSource(data).plus("\n"))
        }
        list.postValue(editorList)

        return returnValue
    }

    val onKeyListener: View.OnKeyListener = View.OnKeyListener{ v: View, i: Int, keyEvent: KeyEvent ->
        if (keyEvent.action == KeyEvent.ACTION_DOWN) {
            when (i){
                KeyEvent.KEYCODE_DEL -> {
                    del(v as EditText, v.getTag() as Int)
                }
                KeyEvent.KEYCODE_ENTER -> {
                    enter(v as EditText, v.getTag() as Int)
                    return@OnKeyListener true
                }

            }
        }
        return@OnKeyListener false
    }

    private fun enter(v: EditText, pos: Int) {
        currentLineFocus = pos
        if (pos == 0 && editorList[pos].type === ContentType.TEXT) {
            editorList[pos].hint = ""
        }
        val item = editorList[pos]

        //커서 마지막에서 엔터
        if (v.selectionStart == v.text.length) {
            editorList[pos].content = v.text.toString()
            editorList.add(pos + 1, EditorData(ContentType.TEXT, ""))
        }
        //커서 중간에서 엔터.... -> 현재 커서의 에디터 스타일가져오기
        else {

            val newStr = v.text.substring(v.selectionEnd, v.text.length)
            val oldStr = v.text.substring(0, v.selectionStart)

            item.content = oldStr

            val newModel = EditorData(ContentType.TEXT, newStr)
            newModel.fontSize = editorList[currentLineFocus].fontSize
            newModel.align = editorList[currentLineFocus].align
            newModel.styleList = editorList[currentLineFocus].cloneStyle()

            for(style in newModel.styleList){
                if(style.start - v.selectionEnd < 0)
                    style.start = 0
                else
                    style.start = style.start - v.selectionEnd

                if(style.end - v.selectionEnd < 0)
                    style.end = 0
                else
                    style.end = style.end - v.selectionEnd
            }

            for(style in editorList[currentLineFocus].styleList){
                if(style.end > oldStr.length )
                    style.end = oldStr.length
            }
            editorList.add(pos + 1, newModel)
        }

        list.postValue(editorList)
        currentLineFocus++
        observeFocus.postValue(currentLineFocus)
    }

    private fun del(v:EditText, pos:Int){
        if ((v).selectionStart == 0 && (v).selectionStart == (v).selectionEnd) {
            if (pos >= 1) {
                //바로위 컨텐츠가 텍스트
                if (editorList[pos - 1].type === ContentType.TEXT) {
                    val currentModel = editorList[pos]
                    for(span in currentModel.styleList){

                        span.start = span.start + editorList[pos - 1].content.length
                        span.end = span.end + editorList[pos - 1].content.length
                    }
                    editorList[pos - 1].styleList.addAll(currentModel.styleList)
                    editorList[pos - 1].currentFocus = editorList[pos - 1].content.length
                    editorList[pos - 1].content = editorList[pos - 1].content.plus(editorList[pos].content)
                    editorList.removeAt(pos)
                } else {
                    //이미지이면
                    DevLog.i("is IMAGE")
                    editorList.removeAt(pos - 1)
                }

                list.postValue(editorList)
                currentLineFocus--
                observeFocus.postValue(currentLineFocus)
            }
        }
    }

    fun addImage(uri: Uri){
        currentLineFocus++
        editorList.add(currentLineFocus, EditorData(ContentType.IMAGE, uri))

        var hasNextData = false
        for((i) in editorList.withIndex()){
            if(i >= currentLineFocus) {
                if (editorList[currentLineFocus].type == ContentType.TEXT) {
                    hasNextData = true
                    break
                } else {
                    currentLineFocus++
                }
            }
        }

        if(!hasNextData)
            editorList.add(EditorData(ContentType.TEXT, ""))
        list.postValue(editorList)
        observeFocus.postValue(currentLineFocus)
    }

    fun deleteImage() {
        editorList.removeAt(currentLineFocus)
        if(currentLineFocus != 0){
            currentLineFocus--
        }

        if(editorList.size == 0)
            editorList.add(EditorData(ContentType.TEXT, ""))
        list.postValue(editorList)
        observeFocus.postValue(currentLineFocus)
    }

    open class OnFocusChangeListener(private val vm: HtmlEditorViewModel):View.OnFocusChangeListener{
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if(hasFocus) {
                if (v != null) {
                    vm.onTextFocus(v)
                }
            }
        }
    }

    open class OnSelectionChangedListener(private val items: ArrayList<EditorData>, private val v:View): EditorText.OnSelectionChangedListener{
        private var position: Int = 0
        private var model: EditorData? = null

        fun updatePosition(position:Int){
            this.position = position
            model = items[position]
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSelectionsChanged(selStart: Int, selEnd: Int) {
            if(v.focusable == View.NOT_FOCUSABLE)
                v.focusable = View.FOCUSABLE
            if(!v.isFocusableInTouchMode)
                v.isFocusableInTouchMode = true
            model?.start = selStart
            model?.end = selEnd
        }
    }

    open class EditWatcher(private val edit: EditText, private val items: ArrayList<EditorData>): TextWatcher {
        private var position: Int = 0
        private var model: EditorData? = null
        private var isNotify: Boolean = false

        fun updatePosition(position:Int){
            this.position = position
            model = items[position]
            isNotify = true
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (!isNotify) {
                model?.currentFocus = edit.selectionEnd
            } else {
                isNotify = false
            }
        }

        override fun onTextChanged(span: CharSequence?, start: Int, count: Int, after: Int) {
            if (span is SpannableStringBuilder) {
                val spans = span.getSpans(0, span.length, StyleSpan::class.java)
                items[position].styleList.clear()
                for((i) in spans.withIndex()){
                    items[position].styleList.add(StyleData(StyleSpan(spans[i].style), span.getSpanStart(spans[i]), span.getSpanEnd(spans[i]) ))
                }
            }
            model?.content = span.toString()
            model?.start = edit.selectionStart
            model?.end = edit.selectionEnd
        }
    }
}