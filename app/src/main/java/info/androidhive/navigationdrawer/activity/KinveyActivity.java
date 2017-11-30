package info.androidhive.navigationdrawer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kinvey.android.Client;

import info.androidhive.navigationdrawer.R;

public class KinveyActivity extends AppCompatActivity {

    /**
     * This method is used to get client details of currently logged in user
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method is used to get client details of currently logged in user
     * @return client details
     */
    public Client getClient(){
        return ((MainApplication)getApplication()).getClient();
    }
}
