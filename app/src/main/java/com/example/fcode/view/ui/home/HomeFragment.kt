package com.example.fcode.view.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fcode.databinding.FragmentHomeBinding
import com.example.fcode.view.Adapter.courseAdapter
import com.example.fcode.view.actCourse
import com.example.fcode.view.model.Course
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), courseAdapter.itemClick {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var listCourse: MutableList<Course>
    private lateinit var courseAdapter: courseAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initView()
        return root
    }

    private fun initView() {
        listCourse  = mutableListOf()
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(activity)
        courseAdapter = courseAdapter(listCourse,this)
        binding.homeRecyclerView.adapter = courseAdapter
        loadData()
    }

    private fun loadData() {
        val db = FirebaseDatabase.getInstance().getReference("Course")
        db.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (courseSnap in snapshot.children){
                    var course = courseSnap.getValue(Course::class.java)
                    if (course != null){
                        listCourse.add(course)
                    }
                }
                courseAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"ko thành công",Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun viewClick(document: String) {
        val intent = Intent(activity,actCourse::class.java)
        intent.putExtra("Document",document)
        startActivity(intent)
    }
}