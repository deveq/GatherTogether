package com.soldemom.studytogether.main.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonObject
import com.soldemom.studytogether.DetailActivity
import com.soldemom.studytogether.R
import com.soldemom.studytogether.ViewModelFactory
import com.soldemom.studytogether.kakaoLocal.RetrofitHelper
import com.soldemom.studytogether.kakaoLocal.RetrofitService
import com.soldemom.studytogether.kakaoLocal.DocAddr
import com.soldemom.studytogether.main.dto.Point
import com.soldemom.studytogether.main.recycler.MainSearchListAdapter
import com.soldemom.studytogether.SplashViewModel
import kotlinx.android.synthetic.main.fragment_search.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainSearchFragment : Fragment() {

    lateinit var docAddr: DocAddr
    val db = FirebaseFirestore.getInstance()
    var studyList = mutableListOf<Point>()
    lateinit var adapter : MainSearchListAdapter
    lateinit var retrofitService: RetrofitService
    var selectedAddress: String = ""
    lateinit var viewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance())
                .get(SplashViewModel::class.java)


//        val x = 126.97794339818097
//        val y = 37.5663058756884

        val x = viewModel.longitude
        val y = viewModel.latitude


        val appKey = getString(R.string.kakao_local_app_key)
        var listView = ListView(requireContext())
        adapter = MainSearchListAdapter(::searchFragToDetailActivity, requireActivity())

        adapter.studyList = studyList
        view.search_recycler_view.adapter = adapter



        val retrofit = RetrofitHelper.getRetrofit()
        retrofitService = retrofit.create(RetrofitService::class.java)
//        retrofitService.getByGeo(appKey, x, y).enqueue(object: Callback<DocAddr>{
//            override fun onResponse(call: Call<DocAddr>, response: Response<DocAddr>) {
//                if (response.isSuccessful) {
//                    docAddr = response.body()!!
//                    val address = docAddr.documents[1].address_name
//
//                    Log.d("주소",address)
//                    db.collection("markers").whereEqualTo("address",address)
//                        .get().addOnSuccessListener {
//                            studyList = it.toObjects(Point::class.java)
//                            adapter.studyList = studyList
//                            adapter.notifyDataSetChanged()
//
//                        }
//                }
//            }
//
//            override fun onFailure(call: Call<DocAddr>, t: Throwable) {
//            }
//        })

        retrofitService.getByGeo2(appKey, x, y).enqueue(object: Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    result["documents"].asJsonArray[1].asJsonObject.apply {
                        val depth1 = this["region_1depth_name"].asString
                        val depth2 = this["region_2depth_name"].asString

                        val address = "$depth1 $depth2"
                        db.collection("markers").whereEqualTo("address",address)
                            .get().addOnSuccessListener {
                                studyList = it.toObjects(Point::class.java)
                                adapter.studyList = studyList
                                adapter.notifyDataSetChanged()
                            }
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }
        })



        view.search_button.setOnClickListener {
            val inputAddress = view.search_input.text.toString()
            Log.d("주소","내가 입력한건  $inputAddress")
            retrofitService.getByAdd(appKey,inputAddress).enqueue(object : Callback<DocAddr> {
                override fun onResponse(call: Call<DocAddr>, response: Response<DocAddr>) {
                    if (response.isSuccessful) {

                        val inputResult = response.body()!!


                        //검색된 주소가 2개 이상이라면
                        if (inputResult.documents.size >= 2) {
                            view.search_list_empty_text.visibility = View.GONE

                            for (doc in inputResult.documents) {
                                doc.changeDepth1()
                            }

                            val listView = ListView(requireContext())

                            // 다이얼로그에 들어간 listview를 꺼야야함.
                            if (listView.parent != null) {
                                (listView.parent as ViewGroup).removeView(
                                    listView
                                )
                            }

//                            val addressList = List<String>(inputResult.documents.size) {
//                                inputResult.documents[it].address_name
//                            }
                            val addressSet = hashSetOf<String>()
                            inputResult.documents.forEach {
                                addressSet.add(it.address_name)
                            }
                            val addressList = addressSet.toList()

                            listView.adapter = ArrayAdapter<String>(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                addressList
                            )


                            val dialog = AlertDialog.Builder(requireContext())
                                .setTitle("선택해주세요")
                                .setView(listView)
                                .setNegativeButton("닫기", null)
                                .create()

                            dialog.show()

                            listView.setOnItemClickListener { _, _, position, _ ->
                                selectedAddress = addressList[position]
                                Log.d("안쪽주소", "-$selectedAddress-")

                                db.collection("markers")
                                    .whereEqualTo("address", selectedAddress)
                                    .get()
                                    .addOnSuccessListener {
                                        studyList = it.toObjects(Point::class.java)
                                        adapter.studyList = studyList
                                        adapter.notifyDataSetChanged()
                                        view.search_recycler_view.visibility = View.VISIBLE

                                        dialog.cancel();
                                    }
                            }

                        }
                        else if(inputResult.documents.size == 1){
                            view.search_list_empty_text.visibility = View.GONE

                            inputResult.documents[0].changeDepth1()
                            selectedAddress = inputResult.documents[0].address_name
                            Log.d("안쪽주소", selectedAddress)
                            db.collection("markers")
                                .whereEqualTo("address",selectedAddress)
                                .get()
                                .addOnSuccessListener {
                                    studyList = it.toObjects(Point::class.java)
                                    adapter.studyList = studyList
                                    adapter.notifyDataSetChanged()
                                    view.search_recycler_view.visibility = View.VISIBLE
                                }


                        } else {
                            view.search_list_empty_text.visibility = View.VISIBLE
                            view.search_recycler_view.visibility = View.GONE
                        }
                    }
                }
                override fun onFailure(call: Call<DocAddr>, t: Throwable) {
                }
            })

            view.search_input.setText("")

        }





        return view
    }

    fun searchFragToDetailActivity(studyId: String) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("studyId",studyId)
        startActivity(intent)
    }

}