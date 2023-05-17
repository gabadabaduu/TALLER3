package com.example.taller3.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taller3.R
import com.example.taller3.data.model.LoggedInUser

class UserAdapter(private val userList: List<LoggedInUser>, private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(user: LoggedInUser)
    }

    inner class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val btnShowLocation: Button = itemView.findViewById(R.id.btn_show_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        // Aquí puedes cargar la imagen del usuario en el ImageView utilizando una biblioteca como Glide o Picasso
        holder.userName.text = currentUser.displayName
        holder.btnShowLocation.setOnClickListener {
            itemClickListener.onItemClick(currentUser)
        }
    }
    override fun getItemCount() = userList.size
}

