//package ad.kr.hansung.carrotmarketproject
//
//import ad.kr.hansung.carrotmarketproject.DBKey.Companion.DB_ARTICLES
//import android.Manifest
//import android.app.AlertDialog
//import android.content.Context
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.ProgressBar
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.ktx.storage
//
//
//class AddArticleActivity : AppCompatActivity() {
//    private fun startContentProvider() {
//        getContent.launch("image/*")
//    }
//
//    fun getActivityContext() : Context {
//        return this
//    }
//
//    private var selectedUri: Uri? = null
//
//    private val auth: FirebaseAuth by lazy {
//        Firebase.auth
//    }
//    private val storage: FirebaseStorage by lazy {
//        Firebase.storage
//    }
//    private val articleDB: DatabaseReference by lazy {
//        Firebase.database.reference.child(DB_ARTICLES)
//    }
//
//    // Activity Result API를 초기화합니다.
//    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        if (uri != null) {
//            findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
//            selectedUri = uri
//        } else {
//            Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.activity_add_article)
//        val imageAddButton = findViewById<Button>(R.id.imageAddButton)
//        imageAddButton.setOnClickListener {
//            // 클릭 이벤트 처리 로직을 여기에 추가합니다.
//            requestStoragePermission()
//        }
//
//
//
//        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
//            requestStoragePermission()
//        }
//
//
//
//
//        setContentView(R.layout.activity_add_article)
//        requestSinglePermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
//            when {
//                ContextCompat.checkSelfPermission(
//                    this,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE
//                ) == PackageManager.PERMISSION_GRANTED -> {
//                    startContentProvider()
//                }
//
//                else -> {
//                    requestPermissions(
//                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
//                        1010
//                    )
//                }
//
//            }
//        }
//
//        findViewById<Button>(R.id.submitButton).setOnClickListener {
//            val title = findViewById<EditText>(R.id.titleEditText).text.toString().orEmpty()
//            val price = findViewById<EditText>(R.id.priceEditText).text.toString().orEmpty()
//            val sellerId = auth.currentUser?.uid.orEmpty()
//
//            showProgress()
//
//
//
//
//            if (selectedUri != null) {
//                val photoUri = selectedUri ?: return@setOnClickListener
//                uploadPhoto(
//                    photoUri,
//                    successHandler = { uri ->
//                        uploadArticle(sellerId, title, price, uri)
//                    },
//                    errorHandler = {
//                        Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                        hideProgress()
//                    }
//                )
//            } else {
//                uploadArticle(sellerId, title, price, "")
//            }
//        }
//    }
//
//    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
//        val fileName = "${System.currentTimeMillis()}.png"
//        storage.reference.child("article/photo").child(fileName)
//            .putFile(uri)
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    storage.reference.child("article/photo").child(fileName)
//                        .downloadUrl
//                        .addOnSuccessListener { uri ->
//                            successHandler(uri.toString())
//                        }
//                        .addOnFailureListener {
//                            errorHandler()
//                        }
//                } else {
//                    errorHandler()
//                }
//            }
//    }
//
//    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl: String) {
//        val priceInt = price.toInt() // String을 Int로 변환
//        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), priceInt, imageUrl)
//        articleDB.push().setValue(model)
//
//        hideProgress()
//        finish()
//    }
//
//
//    private fun showProgress() {
//        findViewById<ProgressBar>(R.id.progressBar).isVisible = true
//    }
//
//    private fun hideProgress() {
//        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
//    }
//
//
//
//    private fun requestSinglePermission(permission: String) {
//        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
//            return
//
//        // Permission 요청 보내는 역할
//        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//            if (it == false) {
//                AlertDialog.Builder(this).apply {
//                    setTitle("Warning")
//                    setMessage("Notification Permission is Not Allowed")
//                }.show()
//            }
//        }
//
//        // 재차 확인
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//            AlertDialog.Builder(this).apply {
//                setTitle("Reason")
//                setMessage("이유(Rationale)가 있습니다")
//                setPositiveButton("Allow") { _, _ ->requestPermLauncher.launch(permission)}
//                setNegativeButton("Deny") { _, _ -> Toast.makeText(getApplicationContext(), "OK.", Toast.LENGTH_SHORT).show() }
//            }
//        }else {
//            requestPermLauncher.launch(permission)
//        }
//    }
//
//
//
//
//
//    private fun requestStoragePermission() {
//        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
//        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
//            // 이미 권한이 부여된 경우
//            startContentProvider()
//        } else {
//            // 권한이 부여되지 않은 경우 권한을 요청
//            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
//        }
//    }
//
//    // 이 부분은 다음 단계에서 사용할 권한 요청 코드입니다.
//    private val PERMISSION_REQUEST_CODE = 101
//
//
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//
//        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
//
//
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 권한이 부여된 경우
//                startContentProvider()
//            } else {
//                // 권한이 거부된 경우, 사용자에게 알림을 표시할 수 있습니다.
//                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
//
//                showPermissionDeniedMessage()
//            }
//        }
//    }
//
//    private fun showPermissionDeniedMessage() {
//        AlertDialog.Builder(this)
//            .setTitle("Permission Denied")
//            .setMessage("You need to grant permission to access external storage for image selection.")
//            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//            .show()
//    }
//
//}
//
//


package ad.kr.hansung.carrotmarketproject

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddArticleActivity : AppCompatActivity() {
    private fun startContentProvider() {
        getContent.launch("image/*")
    }

    private var selectedUri: Uri? = null

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.Companion.DB_ARTICLES)
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
            selectedUri = uri
        } else {
            Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_article)

        val imageAddButton = findViewById<Button>(R.id.imageAddButton)
        imageAddButton.setOnClickListener {
            requestStoragePermission()
        }

        findViewById<Button>(R.id.submitButton).setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString().orEmpty()
            val price = findViewById<EditText>(R.id.priceEditText).text.toString().orEmpty()
            val sellerId = auth.currentUser?.uid.orEmpty()

            showProgress()

            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(
                    photoUri,
                    successHandler = { uri ->
                        uploadArticle(sellerId, title, price, uri)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
                )
            } else {
                uploadArticle(sellerId, title, price, "")
            }
        }
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("article/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storage.reference.child("article/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }
                        .addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl: String) {
        val priceInt = price.toIntOrNull() ?: 0
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), priceInt, imageUrl)
        articleDB.push().setValue(model)

        hideProgress()
        finish()
    }

    private fun showProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true
    }

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }

    private fun requestStoragePermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            startContentProvider()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startContentProvider()
            } else {
                showPermissionDeniedMessage()
            }
        }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(this, "외부 저장소 액세스 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }
}
