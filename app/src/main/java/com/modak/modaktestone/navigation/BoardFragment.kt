package com.modak.modaktestone.navigation

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.modak.modaktestone.R
import com.modak.modaktestone.databinding.FragmentBoardBinding
import com.modak.modaktestone.databinding.ItemReportBinding
import kotlinx.android.synthetic.main.activity_main.*

class BoardFragment : Fragment() {
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.boardfragmentRecyclerview.adapter = BoardRecyclerViewAdapter()
        binding.boardfragmentRecyclerview.layoutManager = LinearLayoutManager(this.context)

        //위의 게시판 분류 선택시 이벤트
        binding.boardfragmentBtnPublicity.setOnClickListener {
            var fragment = PublicityFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_content, fragment)?.commit()
        }
        binding.boardfragmentBtnInformation.setOnClickListener {
            var fragment = InformationFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_content, fragment)?.commit()
        }


        return view
//        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_board, container, false)
//        return super.onCreateView(inflater, container, savedInstanceState)


    }

    inner class BoardRecyclerViewAdapter :
        RecyclerView.Adapter<BoardRecyclerViewAdapter.CustomViewHolder>() {
        var busanDTO: List<String> =
            listOf("자유게시판", "건강게시판", "재취업게시판", "트로트게시판", "정보게시판", "정치게시판", "비밀게시판")

        inner class CustomViewHolder(val binding: ItemReportBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BoardRecyclerViewAdapter.CustomViewHolder {
            val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }


        override fun onBindViewHolder(
            holder: BoardRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {


            holder.binding.itemReportContent.text = busanDTO[position]

            holder.binding.itemReportContent.setTypeface(holder.binding.itemReportContent.typeface, Typeface.BOLD)

            holder.binding.layout.setOnClickListener { v ->
                var intent = Intent(v.context, BoardContentActivity::class.java)
                intent.putExtra("destinationCategory", busanDTO[position])
                startActivity(intent)
            }
//            holder.binding.boardviewitemTextviewBoard.text = busanDTO[position]
//
//            //게시판이 클릭 되었을 때
//            holder.binding.boardviewitemTextviewBoard.setOnClickListener { v ->
//                var intent = Intent(v.context, BoardContentActivity::class.java)
//                intent.putExtra("destinationCategory", busanDTO[position])
//                startActivity(intent)
//            }
        }

        override fun getItemCount(): Int {
            return busanDTO.size
        }

    }
}