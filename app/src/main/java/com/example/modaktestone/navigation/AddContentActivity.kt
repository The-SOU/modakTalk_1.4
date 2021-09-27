package com.example.modaktestone.navigation

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ActivityAddContentBinding
import com.example.modaktestone.navigation.model.ContentDTO
import com.example.modaktestone.navigation.model.UserDTO
import com.example.modaktestone.navigation.util.ProgressDialogSecond
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class AddContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContentBinding

    var selectedCategory: String? = null

    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var anonymityDTO = ContentDTO()

    val client = Client("435FRQOQZV", "993b0e12c41c515fe18c3f75f2bdd874")
    val index: Index = client.getIndex("contents")

    private lateinit var customProgressDialog: ProgressDialogSecond

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //로딩창 객체 생성
        customProgressDialog = ProgressDialogSecond(this)
        //로딩창을 투명하게
        customProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customProgressDialog.setCanceledOnTouchOutside(false)

        //인텐트 값 받기
        if (intent.hasExtra("selectedCategory")) {
            selectedCategory = intent.getStringExtra("selectedCategory")
        } else {
            Toast.makeText(this, "전달된 이름이 없다", Toast.LENGTH_SHORT).show()
        }


        //초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //카메라 버튼 클릭했을 때 사진 선택하는 곳으로 넘어가기.
        binding.addcontentImageviewCamera.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

        binding.addcontentImageviewImage.visibility = View.INVISIBLE

        binding.addcontentBtnUpload.setOnClickListener {
            contentUpload(anonymityDTO)
        }

        //익명 버튼 클릭시
//        binding.addcontentLinearAnonymity.setOnClickListener {
//            if (anonymityDTO.anonymity.containsKey(auth?.currentUser?.uid)) {
//                anonymityDTO.anonymity.remove(auth?.currentUser?.uid)
//                binding.addcontentImageviewAnonymity.setImageResource(R.drawable.ic_unanonymity)
//                binding.addcontentTvAnonymity.setTextColor(Color.parseColor("#919191"))
//                binding.addcontentTvAnonymity.setTypeface(null, Typeface.NORMAL)
//                println("anonymity delete complete")
//            } else {
//                anonymityDTO.anonymity[auth?.currentUser?.uid!!] = true
//                binding.addcontentImageviewAnonymity.setImageResource(R.drawable.ic_anonymity)
//                binding.addcontentTvAnonymity.setTextColor(Color.BLACK)
//                binding.addcontentTvAnonymity.setTypeface(null, Typeface.BOLD)
//                println("anonymity add complete")
//            }
//        }

        //액션바 설정
        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayShowCustomEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)

        //키보드 숨기기
        binding.layout.setOnClickListener {
            hideKeyboard()
        }

        //제목 edittext 엔터키 막기
        binding.addcontentEdittextTitle.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_ENTER) true else false })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                photoUri = data?.data
                binding.addcontentImageviewImage.setImageURI(photoUri)
                binding.addcontentImageviewImage.visibility = View.VISIBLE
            } else {
                finish()
            }
        }
    }

    // ----- 펑션 모음 -----
    fun contentUpload(anonymity: ContentDTO) {
        customProgressDialog.show()
        println(anonymity.anonymity.toString())
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        var uid = auth?.currentUser?.uid
        var username: String? = null
        var region: String? = null
        var profileUrl: String? = null
        firestore?.collection("users")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var userDTO = documentSnapshot.toObject(UserDTO::class.java)
                username = userDTO?.userName
                region = userDTO?.region
                profileUrl = userDTO?.profileUrl


                //컨텐츠업로드
                //이미지 유알아이가 존재 유뮤에 따라 구분
                if (photoUri != null) {
                    //image uri가 존재할 때
//                        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
//                            return@continueWithTask storageRef.downloadUrl}?.

                    storageRef?.putFile(photoUri!!)
                        ?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                            return@continueWithTask storageRef.downloadUrl
                        }?.addOnCompleteListener {
                            storageRef.downloadUrl
                                .addOnSuccessListener(OnSuccessListener<Uri?> { uri ->
                                    var contentDTO = ContentDTO()

                                    contentDTO.imageUrl = uri.toString()

                                    contentDTO.uid = auth?.currentUser?.uid

                                    contentDTO.userName = username

                                    contentDTO.region = region

                                    contentDTO.profileUrl = profileUrl

                                    contentDTO.title =
                                        binding.addcontentEdittextTitle.text.toString()

                                    contentDTO.explain =
                                        binding.addcontentEdittextExplain.text.toString()

                                    contentDTO.contentCategory = selectedCategory

                                    contentDTO.postCount = contentDTO.postCount + 1

                                    if (anonymity.anonymity.containsKey(auth?.currentUser?.uid!!)) {
                                        contentDTO.anonymity[auth?.currentUser?.uid!!] = true
                                    }

                                    contentDTO.timestamp = System.currentTimeMillis()


//                                    //알고리아 업로드
//                                    val contentList: MutableList<JSONObject?> = ArrayList()
//                                    contentList.add(
//                                        JSONObject().put("uid", auth?.currentUser?.uid).put(
//                                            "title",
//                                            binding.addcontentEdittextTitle.text.toString()
//                                        ).put(
//                                            "explain",
//                                            binding.addcontentEdittextExplain.text.toString()
//                                        ).put(
//                                            "userName",
//                                            username
//                                        ).put(
//                                            "region",
//                                            region
//                                        ).put(
//                                            "profileUrl",
//                                            profileUrl
//                                        ).put(
//                                            "contentCategory",
//                                            selectedCategory
//                                        ).put(
//                                            "timestamp",
//                                            System.currentTimeMillis()
//                                        ).put(
//                                            "imageUrl",
//                                            uri.toString()
//                                        )
//                                    )
//                                    index.addObjectsAsync(JSONArray(contentList), null)


                                    firestore?.collection("contents")?.add(contentDTO)
                                        ?.addOnSuccessListener { documentReference ->
                                            Log.d(
                                                "TAG",
                                                "DocumentSnapshot written with ID: ${documentReference.id}"
                                            )
                                        }?.addOnFailureListener { e ->
                                            Log.w("TAG", "Error adding document", e)
                                        }
                                    setResult(Activity.RESULT_OK)

                                    finish()
                                    var intent = Intent(this, BoardContentActivity::class.java)
                                    intent.putExtra("destinationCategory", selectedCategory)
                                    startActivity(intent)

                                })

                        }

                } else {
                    //image uri가 존재하지 않을 때
                    var contentDTO = ContentDTO()

                    contentDTO.uid = auth?.currentUser?.uid

                    contentDTO.userName = username

                    contentDTO.region = region

                    contentDTO.profileUrl = profileUrl

                    contentDTO.title = binding.addcontentEdittextTitle.text.toString()

                    contentDTO.explain = binding.addcontentEdittextExplain.text.toString()

                    contentDTO.contentCategory = selectedCategory

                    if (anonymity.anonymity.containsKey(auth?.currentUser?.uid!!)) {
                        contentDTO.anonymity[auth?.currentUser?.uid!!] = true
                    }


                    contentDTO.timestamp = System.currentTimeMillis()

//                    //알고리아 업로드
//                    val contentList: MutableList<JSONObject?> = ArrayList()
//                    contentList.add(
//                        JSONObject().put("uid", auth?.currentUser?.uid).put(
//                            "title",
//                            binding.addcontentEdittextTitle.text.toString()
//                        ).put(
//                            "explain",
//                            binding.addcontentEdittextExplain.text.toString()
//                        ).put(
//                            "userName",
//                            username
//                        ).put(
//                            "region",
//                            region
//                        ).put(
//                            "profileUrl",
//                            profileUrl
//                        ).put(
//                            "contentCategory",
//                            selectedCategory
//                        ).put(
//                            "timestamp",
//                            System.currentTimeMillis()
//                        )
//                    )
//                    index.addObjectsAsync(JSONArray(contentList), null)


                    firestore?.collection("contents")?.add(contentDTO)
                        ?.addOnSuccessListener { documentReference ->
                            Log.d(
                                "TAG",
                                "DocumentSnapshot written with ID: ${documentReference.id}"
                            )
                        }?.addOnFailureListener { e ->
                            Log.w("TAG", "Error adding document", e)
                        }
                    setResult(Activity.RESULT_OK)

                    finish()
                    var intent = Intent(this, BoardContentActivity::class.java)
                    intent.putExtra("destinationCategory", selectedCategory)
                    startActivity(intent)

                }

            }
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}