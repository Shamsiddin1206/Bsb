package shamsiddin.project.bsb

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shamsiddin.project.bsb.ui.theme.BsbTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private lateinit var savol:TextView

    private lateinit var op1:TextView

    private lateinit var op2:TextView

    private lateinit var op3:TextView
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BsbTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    database = FirebaseDatabase.getInstance()
                    usersRef = database.reference.child("users")
                    val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    RandomUserScreen(usersRef, getSharedPreferences("MyPreferences", Context.MODE_PRIVATE))
                }
                val testdata = Testdata(
                        "Grand Central Terminal, Park Avenue, New York is the world's",
                        "largest railway station", "highest railway station",
                        "longest railway station")
                saveUserToDatabase(testdata)
                val test2 = Testdata(
                        "Entomology is the science that studies",
                        "Behavior of human beings",
                        "Insects","The formation of rocks")
                saveUserToDatabase(test2)
                val test3 = Testdata("Where are you from?", "Asia",
                        "Africa","Europe")
                saveUserToDatabase(test3)
                val test4 = Testdata("2+2", "3",
                    "5","4")
                saveUserToDatabase(test4)
            }}}


    private fun saveUserToDatabase(user: Testdata) {
        val userRef = usersRef.child(user.savol.toString()) // Assuming username is unique

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User already exists, handle accordingly
//                    Toast.makeText(this@MainActivity, "User already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // User does not exist, save to the database
                    userRef.setValue(user)
                        .addOnSuccessListener {
                            // Data saved successfully
                            Toast.makeText(this@MainActivity, "User saved to database", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            // Handle error
                            Toast.makeText(this@MainActivity, "Failed to save user to database", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Toast.makeText(this@MainActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
            }

@Composable
fun RandomUserScreen(usersRef: DatabaseReference, sharedPreferences: SharedPreferences) {
    var randomUser by remember { mutableStateOf<Testdata?>(null) }

    LaunchedEffect(true) {
        val previousUserKey = sharedPreferences.getString("previousUserKey", null)

        getRandomUser(usersRef, previousUserKey) { user ->
            randomUser = user
            // Save the current user's key for the next run
            user?.let { newUser ->
                sharedPreferences.edit().putString("previousUserKey", newUser.savol).apply()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (randomUser != null) {
            Text("Savol: ${randomUser?.savol}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("A: ${randomUser!!.op1}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("B: ${randomUser!!.op2}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Text("C: ${randomUser!!.op3}", style = MaterialTheme.typography.bodyMedium)
            Button(
                onClick = {},
                modifier = Modifier
                    .padding(16.dp)
                    .size(100.dp)
            ) {
                Text(text = "Click me!")
            }       } else {
//            Text("No user available", style = MaterialTheme.typography.bodyMedium)
        }

    }
}

private fun getRandomUser(usersRef: DatabaseReference, previousUserKey: String?, callback: (Testdata?) -> Unit) {
    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val availableUsers = dataSnapshot.children.mapNotNull { it.getValue(Testdata::class.java) }

            // Filter out the previously displayed user
            val filteredUsers = availableUsers.filterNot { it.savol == previousUserKey }

            // Get a random user from the filtered list
            val randomUser = filteredUsers.shuffled().firstOrNull()

            callback(randomUser)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle error
            callback(null)
        }
    })
}


//
//@Composable
//fun RandomUserScreen(usersRef: DatabaseReference) {
//    var randomUser by remember { mutableStateOf<Testdata?>(null) }
//
//    LaunchedEffect(true) {
//        getRandomUser(usersRef) { user ->
//            randomUser = user
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        if (randomUser != null) {
//            Text("Savol: ${randomUser!!.savol}", style = MaterialTheme.typography.bodyMedium)
//            Spacer(modifier = Modifier.height(16.dp))
//            Text("Javob: ${randomUser!!.op1}", style = MaterialTheme.typography.bodyMedium)
//        } else {
//            Text("No user available", style = MaterialTheme.typography.bodyMedium)
//        }
//    }
//}
//
//private fun getRandomUser(usersRef: DatabaseReference, callback: (Testdata?) -> Unit) {
//    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            if (dataSnapshot.exists()) {
//                // Get a random user
//                val randomUser = dataSnapshot.children.shuffled().firstOrNull()?.getValue(Testdata::class.java)
//                callback(randomUser)
//            } else {
//                callback(null)
//            }
//        }
//
//        override fun onCancelled(databaseError: DatabaseError) {
//            // Handle error
//            callback(null)
//        }
//    })
//}



