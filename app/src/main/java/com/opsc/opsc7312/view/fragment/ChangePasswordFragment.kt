import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.R

class ChangePasswordFragment : Fragment() {

    interface ChangePasswordListener {
        fun onPasswordChanged(newPassword: String)
    }

    private var changePasswordListener: ChangePasswordListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (parentFragment is ChangePasswordListener) {
            changePasswordListener = parentFragment as ChangePasswordListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)

        val closeButton = view.findViewById<ImageButton>(R.id.btnClose)
        val changePasswordButton = view.findViewById<Button>(R.id.btnChangePassword)
        val newPasswordEditText = view.findViewById<EditText>(R.id.etNewPassword)
        val confirmPasswordEditText = view.findViewById<EditText>(R.id.etConfirmPassword)

        closeButton.setOnClickListener {
            // Dismiss the fragment or activity when close button is clicked
            parentFragmentManager.popBackStack()
        }

        changePasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (newPassword == confirmPassword) {
                    // Notify listener of the password change
                    changePasswordListener?.onPasswordChanged(newPassword)
                    // Close the dialog or fragment after successful password change
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill out both fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
