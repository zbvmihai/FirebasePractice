package com.zabava.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.zabava.firebase.databinding.UsersItemBinding
import java.lang.Exception

class UsersAdapter(
    private var context: Context,
    private var userList: ArrayList<Users>
    ): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    inner class UsersViewHolder(val adapterBinding: UsersItemBinding)
        : RecyclerView.ViewHolder(adapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {

        val binding = UsersItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return UsersViewHolder(binding)

    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {

        holder.adapterBinding.tvName.text = userList[holder.adapterPosition].userName
        holder.adapterBinding.tvAge.text = userList[holder.adapterPosition].userAge.toString()
        holder.adapterBinding.tvEmail.text = userList[holder.adapterPosition].userEmail

        val imageUrl = userList[holder.adapterPosition].url

        Picasso.get().load(imageUrl).into(holder.adapterBinding.ivUser, object : Callback{
            override fun onSuccess() {
                holder.adapterBinding.pbUser.visibility = View.INVISIBLE
            }

            override fun onError(e: Exception?) {
                Toast.makeText(context, "Error loading the image.",Toast.LENGTH_SHORT).show()
                e?.localizedMessage?.let { Log.e("Error: ", it) }
            }
        })

        holder.adapterBinding.linearLayout.setOnClickListener {

            val intent = Intent(context, UpdateUserActivity::class.java)

            intent.putParcelableArrayListExtra("usersList", userList)
            intent.putExtra("position", holder.adapterPosition)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {

        return  userList.size
    }

    fun getUserId(position: Int): String {

        return userList[position].userId

    }

    fun getImageName(position: Int): String{

        return userList[position].imageName

    }
}