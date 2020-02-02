package kr.twothumb.sample

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import kr.twothumb.ieditor.model.ContentType
import kr.twothumb.ieditor.model.EditorData
import kr.twothumb.ieditor.model.FontSize
import kr.twothumb.sample.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : AppCompatActivity() {
    private val bind by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this@MainActivity,
            R.layout.activity_main
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind
        bind.vm = getViewModel()
        bind.imageVm = getViewModel()
        bind.lifecycleOwner = this
        bind.btnCode.setOnClickListener {
            val i = Intent(this, SourceActivity::class.java)
            i.putExtra("source", bind.vm?.toHtml())
            startActivity(i)
        }

        init()
    }

    fun init(){
        var editorList = ArrayList<EditorData>()
        editorList.add(EditorData(ContentType.TEXT, FontSize.T1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."))
        editorList.add(EditorData(ContentType.TEXT, FontSize.T2, "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."))
        editorList.add(EditorData(ContentType.TEXT, FontSize.T2, "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. "))
        editorList.add(EditorData(ContentType.TEXT, FontSize.T2, "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))

        bind.vm?.setText(editorList)
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == bind.vm?.readExternalStorage) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bind.vm?.callImagePicker(this)
            } else {
                Toast.makeText(this, "권한에 동의하셔야 이미지 파일을 업로드 할수있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == bind.vm?.selectImage){
            if(resultCode == Activity.RESULT_OK)
                bind.vm?.addImage(Uri.parse(data?.getStringExtra("selectedImage")))
        }
    }
}
