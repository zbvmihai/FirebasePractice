package com.zabava.firebase

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.zabava.firebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("MyUsers")

    val userList = ArrayList<Users>()
    val imageNameList = ArrayList<String>()

    lateinit var usersAdapter: UsersAdapter

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = firebaseStorage.reference

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        mainBinding.fab.setOnClickListener {

            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val id = usersAdapter.getUserId(viewHolder.adapterPosition)

                myReference.child(id).removeValue()

                val imageName = usersAdapter.getImageName(viewHolder.adapterPosition)

                val imageReference = storageReference.child("UsersImages").child(imageName)

                imageReference.delete()

                Toast.makeText(applicationContext, "The user was deleted!", Toast.LENGTH_SHORT)
                    .show()

            }

        }).attachToRecyclerView(mainBinding.rvMain)

        retrieveDataFromDatabase()

    }

    private fun retrieveDataFromDatabase() {

        myReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for (eachUser in snapshot.children) {

                    val user = eachUser.getValue(Users::class.java)

                    if (user != null) {

                        userList.add(user)
                    }

                    usersAdapter = UsersAdapter(this@MainActivity, userList)

                    mainBinding.rvMain.layoutManager = LinearLayoutManager(this@MainActivity)

                    mainBinding.rvMain.adapter = usersAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_delete_all, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.deleteAll) {

            showDialogMessage()

        }else if(item.itemId == R.id.signOut){

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showDialogMessage() {

        val dialogMessage = AlertDialog.Builder(this)
        dialogMessage.setTitle("Delete All Users?")
        dialogMessage.setMessage(
            "If you click Yes, all users will be deleted," +
                    "If you want to delete a specific user swipe left or right on !"
        )
        dialogMessage.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        dialogMessage.setPositiveButton("Yes") { _, _ ->

            myReference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (eachUser in snapshot.children) {

                        val user = eachUser.getValue(Users::class.java)

                        if (user != null) {

                            imageNameList.add(user.imageName)

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


            myReference.removeValue().addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    for(imageName in imageNameList){
                        val imageReference = storageReference.child("UsersImages").child(imageName)
                        imageReference.delete()
                    }

                    usersAdapter.notifyDataSetChanged()

                    Toast.makeText(
                        applicationContext,
                        "All users were deleted!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("Error: ", task.exception.toString())
                }
            }
        }
        dialogMessage.create().show()
    }
}