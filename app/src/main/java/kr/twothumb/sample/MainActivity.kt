package kr.twothumb.sample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import kr.twothumb.ieditor.model.ContentType
import kr.twothumb.ieditor.model.EditorData
import kr.twothumb.ieditor.model.FontSize
import kr.twothumb.lib.logger.DevLog
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

    private fun init(){
        val editorList = ArrayList<EditorData>()
        editorList.add(EditorData(ContentType.TEXT, FontSize.T3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))

        bind.vm?.setText(editorList)
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == bind.vm?.readExternalStorage) {
            DevLog.d(grantResults.size)
            DevLog.i(grantResults[0],  " :" , grantResults.isNotEmpty())
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bind.vm?.callImagePicker(this)
            }
            else{
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
