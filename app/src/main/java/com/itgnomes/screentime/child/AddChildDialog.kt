package com.itgnomes.screentime.child

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.itgnomes.screentime.R

class AddChildDialog(
    context: Context,
    private val onChildAdded: (String, String) -> Unit
) {
    private val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_child, null)
    private val nameField: EditText = dialogView.findViewById(R.id.childNameField)
    private val emailField: EditText = dialogView.findViewById(R.id.childEmailField)

    fun show() {
        AlertDialog.Builder(dialogView.context)
            .setTitle("Add Child")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameField.text.toString()
                val email = emailField.text.toString()
                if (name.isNotEmpty() && email.isNotEmpty()) {
                    onChildAdded(name, email)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
