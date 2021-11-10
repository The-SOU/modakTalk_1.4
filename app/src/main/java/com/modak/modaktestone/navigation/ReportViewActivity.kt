package com.modak.modaktestone.navigation

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.modak.modaktestone.R
import com.modak.modaktestone.databinding.ActivityReportViewBinding
import com.modak.modaktestone.databinding.ItemReportBinding
import com.modak.modaktestone.navigation.model.ReportDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class ReportViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportViewBinding
    var firestore: FirebaseFirestore? = null
    var uid: String? = null

    var targetContent: String? = null
    var targetTitle: String? = null
    var targetExplain: String? = null
    var targetComment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //초기화
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        //인텐트 값 받기
        targetContent = intent.getStringExtra("targetContent")
        targetTitle = intent.getStringExtra("targetTitle")
        targetExplain = intent.getStringExtra("targetExplain")
        targetComment = intent.getStringExtra("targetComment")

        //툴바
        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayShowCustomEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)


        //리사이클러뷰 어댑터
        binding.reportRecyclerview.adapter = ReportRecyclerViewAdapter()
        binding.reportRecyclerview.layoutManager = LinearLayoutManager(this)
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

    inner class ReportRecyclerViewAdapter :
        RecyclerView.Adapter<ReportRecyclerViewAdapter.CustomViewHolder>() {
        var reportDTO: List<String> =
            listOf(
                "- 게시판 성격에 부적절해요",
                "- 욕설과 비하가 담겨있어요",
                "- 상업광고 및 판매글이에요",
                "- 음란물 및 불건전한 내용이 있어요",
                "- 도배가 되어 있는 글이에요"
            )

        inner class CustomViewHolder(val binding: ItemReportBinding) :
            RecyclerView.ViewHolder(binding.root)


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ReportRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: ReportRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.itemReportContent.text = reportDTO[position]

            holder.binding.itemReportContent.setOnClickListener {
                when(reportDTO[position]){
                    "- 게시판 성격에 부적절해요" -> {
                        showPopup(uid, targetContent, targetComment, targetTitle, targetExplain, 0)
                    }
                    "- 욕설과 비하가 담겨있어요" -> {
                        showPopup(uid, targetContent, targetComment, targetTitle, targetExplain, 1)
                    }
                    "- 상업광고 및 판매글이에요" -> {
                        showPopup(uid, targetContent, targetComment, targetTitle, targetExplain, 2)
                    }
                    "- 음란물 및 불건전한 내용이 있어요"-> {
                        showPopup(uid, targetContent, targetComment, targetTitle, targetExplain, 3)
                    }
                    "- 도배가 되어 있는 글이에요" -> {
                        showPopup(uid, targetContent, targetComment, targetTitle, targetExplain, 4)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return reportDTO.size
        }

    }

    private fun showPopup(uid: String?, content: String?, comment: String?, title: String?, explain: String?, kind: Int?) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_custom_dialog, null)


        val alertDialog = AlertDialog.Builder(this).create()

        alertDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //신고하기 버튼 클릭할 때
        val btnReport = view.findViewById<Button>(R.id.report_dialog_btn_report)
        btnReport.setOnClickListener {
            Toast.makeText(this, "신고하였습니다", Toast.LENGTH_SHORT).show()
            var reportDTO = ReportDTO()

            reportDTO.uidWhoReported = uid
            reportDTO.targetContent = content
            targetComment = comment
            reportDTO.title = title
            reportDTO.explain = explain
            reportDTO.kind = kind
            reportDTO.timestamp = System.currentTimeMillis()
            reportDTO.time = SimpleDateFormat("MM/dd HH:mm").format(System.currentTimeMillis())
            firestore?.collection("reports")?.add(reportDTO)
                ?.addOnSuccessListener { documentReference ->
                    Log.d(
                        "TAG",
                        "DocumentSnapshot written with ID: ${documentReference.id}"
                    )
                }?.addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }
            alertDialog.dismiss()
            finish()
        }
        //취소버튼 클릭할 때
        val btnCancel = view.findViewById<Button>(R.id.report_dialog_btn_cancel)
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
            finish()
        }

        alertDialog.setView(view)
        alertDialog.show()
    }


}