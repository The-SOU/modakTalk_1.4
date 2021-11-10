package com.modak.modaktestone.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.modak.modaktestone.R
import com.modak.modaktestone.databinding.FragmentInformationBinding
import com.modak.modaktestone.databinding.ItemBestcontentBinding
import com.modak.modaktestone.navigation.model.ContentDTO
import com.modak.modaktestone.navigation.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class InformationFragment : Fragment() {
    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!
    var region: String? = null


    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        val view = binding.root

        //초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        //탭바 클릭 이벤트
        binding.informationfragmentBtnBoard.setOnClickListener {
            var fragment = BoardFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_content, fragment)?.commit()
        }
        binding.informationfragmentBtnPublicity.setOnClickListener {
            var fragment = PublicityFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_content, fragment)?.commit()
        }


        //지역 내 정보 게시판 클릭 이벤트
        binding.informationfragmentBtnSociety.setOnClickListener { v ->
            var intent = Intent(v.context, BoardContentActivity::class.java)
            intent.putExtra("destinationCategory", "지역 내 정보")
            startActivity(intent)
        }

        //지역 내 정보 어댑터와 레이아웃매니저
        binding.informationfragmentRecyclerviewSociety.adapter = SocietyRecyclerViewAdapter()
        binding.informationfragmentRecyclerviewSociety.layoutManager =
            LinearLayoutManager(this.context)

        //화제의 게시물 어댑터와 레이아웃 매니저
        binding.informationfragmentRecyclerview.adapter = HotInformationRecyclerViewAdapter()
        binding.informationfragmentRecyclerview.layoutManager = LinearLayoutManager(this.context)



        return view
    }

    inner class SocietyRecyclerViewAdapter :
        RecyclerView.Adapter<SocietyRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("users")?.document(auth?.currentUser?.uid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    var userDTO = documentSnapshot.toObject(UserDTO::class.java)
                    region = userDTO?.region

                    firestore?.collection("contents")
                        ?.whereEqualTo("contentCategory", "지역 내 정보")
                        ?.orderBy("timestamp")?.limit(3)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            contentDTOs.clear()
                            if (documentSnapshot == null) return@addSnapshotListener
                            for (snapshot in documentSnapshot.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                            }
                            notifyDataSetChanged()
                        }

                }


        }

        inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SocietyRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: SocietyRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.itemBestcontentTvTitle.text = contentDTOs[position].title

            holder.binding.itemBestcontentTvExplain.text = contentDTOs[position].explain

            holder.binding.itemBestcontentTvUsername.text = contentDTOs[position].userName

            holder.binding.itemBestcontentTvCommentcount.text =
                contentDTOs[position].commentCount.toString()

            holder.binding.itemBestcontentTvFavoritecount.text =
                contentDTOs[position].favoriteCount.toString()
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

    inner class HotInformationRecyclerViewAdapter :
        RecyclerView.Adapter<HotInformationRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("contents")?.whereEqualTo("contentCategory", "지역 내 정보")
                ?.orderBy("favoriteCount", Query.Direction.DESCENDING)?.limit(3)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    if (documentSnapshot == null) return@addSnapshotListener
                    for (snapshot in documentSnapshot.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): HotInformationRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: HotInformationRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.itemBestcontentTvTitle.text = contentDTOs[position].title

            holder.binding.itemBestcontentTvExplain.text = contentDTOs[position].explain

            holder.binding.itemBestcontentTvUsername.text = contentDTOs[position].userName

            holder.binding.itemBestcontentTvCommentcount.text =
                contentDTOs[position].commentCount.toString()

            holder.binding.itemBestcontentTvFavoritecount.text =
                contentDTOs[position].favoriteCount.toString()
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }
}