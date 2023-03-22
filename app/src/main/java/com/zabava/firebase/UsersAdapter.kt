package com.zabava.firebase

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zabava.firebase.databinding.UsersItemBinding

class UsersAdapter(
    var context: Context,
    var userList: ArrayList<Users>
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
}