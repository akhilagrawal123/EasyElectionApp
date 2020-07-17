package android.example.easyelectionapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class update_username_custom extends AppCompatDialogFragment {

    private EditText updatedUsername;
    private UpdateUsernameListner listner;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.update_username_custom,null);

        updatedUsername = view.findViewById(R.id.userNameEdit);



        builder.setView(view)
                .setTitle("Update Username")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUsername = updatedUsername.getText().toString();
                        listner.updateUsername(newUsername);


                    }
                });

        return builder.create();
    }




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listner = (UpdateUsernameListner) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "must implement UpdateUsernameListner");
        }
    }

    public interface UpdateUsernameListner
    {
        void updateUsername(String newUsername);
    }

}

