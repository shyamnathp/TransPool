package info.androidhive.navigationdrawer.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ActionMode;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.activity.AboutUsActivity;
import info.androidhive.navigationdrawer.activity.LoginActivity;
import info.androidhive.navigationdrawer.activity.MainActivity;
import info.androidhive.navigationdrawer.activity.MainApplication;
import info.androidhive.navigationdrawer.activity.MapsActivity;
import info.androidhive.navigationdrawer.adapter.CustomListAdapter;
import info.androidhive.navigationdrawer.model.Movie;
import android.support.v4.app.Fragment;
import android.view.MenuInflater;

import static android.R.attr.mode;
import static android.R.string.no;
import static android.content.Context.MODE_PRIVATE;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private OnFragmentInteractionListener mListener;
    //LISTVIEW PARAMS
    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "https://gist.githubusercontent.com/Vaish0230/2da4e69a87d839a883ed52df90c60ac7/raw/29eae008daf9db5ee52e92cbf86b00fbe40bbcb9/Project.json";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private ListView listView;
    private CustomListAdapter adapter;

    private double lat=0;
    private double lng=0;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);


        fragment.setArguments(args);
        return fragment;
    }

    private static final String MY_PREFERENCES = "my_preferences";

    public static boolean isFirst(Context context){
        final SharedPreferences reader = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        final boolean first = reader.getBoolean("is_first", true);
        if(first){
            final SharedPreferences.Editor editor = reader.edit();
            editor.putBoolean("is_first", false);
            editor.commit();
        }
        return first;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isFirst(this.getContext()))
            startActivity(new Intent(this.getContext(), LoginActivity.class));
        else
        //  startActivity(new Intent(this.getContext(), LoginActivity.class));

       //listview things
        adapter = new CustomListAdapter(this.getActivity(), movieList);

        pDialog = new ProgressDialog(this.getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        Log.d("length",Integer.toString(response.length()));

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setTitle(obj.getString("title"));
                                Log.d("title",obj.getString("title"));
                                movie.setThumbnailUrl(obj.getString("image"));
                                movie.setRating(((Number) obj.get("rating"))
                                        .doubleValue());
                                movie.setYear(obj.getInt("releaseYear"));

                                // Genre is json array
                                JSONArray genreArry = obj.getJSONArray("genre");

                                Log.d("genarray",Integer.toString(genreArry.length()));

                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                movie.setGenre(genre);
                                Log.d("genArray","completed");

                                // adding movie to movies array
                                movieList.add(movie);
                                Log.d("list size",Integer.toString(movieList.size()));

                                Toast.makeText(getContext(), "added to list", Toast.LENGTH_LONG).show();


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("list size","not coming");
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        listView.setAdapter(adapter);
                        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

                        //onitemclick
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                              //  ListEntry entry = (ListEntry) parent.getItemAtPosition(position);
                                String l="12.983380",l1="77.724152";
                                lat = Double.parseDouble(l);
                                lng = Double.parseDouble(l1);
                                Intent intent = new Intent(getActivity(), MapsActivity.class);

                                // Passing latitude and longitude to the MapActiv
                                intent.putExtra("lat", lat);
                                intent.putExtra("lng", lng);

                                startActivity(intent);
                            }
                        });

                        // Capture ListView item click
                        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                            @Override
                            public void onItemCheckedStateChanged(ActionMode mode,
                                                                  int position, long id, boolean checked) {

                                ActionBar mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                                mActionBar.hide();
                                // Capture total checked items
                                final int checkedCount = listView.getCheckedItemCount();
                                // Set the CAB title according to total checked items
                                mode.setTitle(checkedCount + " Selected");
                                // Calls toggleSelection method from ListViewAdapter Class
                                CustomListAdapter.toggleSelection(position);
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.delete:

                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                                            if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.SEND_SMS)
                                                    == PackageManager.PERMISSION_DENIED) {

                                                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                                                String[] permissions = {Manifest.permission.SEND_SMS};

                                                ActivityCompat.requestPermissions(getActivity(),permissions, PERMISSION_REQUEST_CODE);

                                            }
                                        }

                                        //Getting intent and PendingIntent instance
                                        Intent intent=new Intent(getActivity().getApplicationContext(),MainActivity.class);
                                        PendingIntent pi=PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent,0);

                                        //Get the SmsManager instance and call the sendTextMessage method to send message
                                        SmsManager sms=SmsManager.getDefault();
                                        String no="9847822351";
                                        sms.sendTextMessage(no, null,"hey wanna share a ride", pi,null);
                                        Log.d("sms","working");
                                        return true;
                                    default:
                                        return false;
                                }
                            }

                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                mode.getMenuInflater().inflate(R.menu.activity_main, menu);
                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                // TODO Auto-generated method stub
                              //  CustomListAdapter.removeSelection();
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                // TODO Auto-generated method stub
                                return false;
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        MainApplication.getInstance().addToRequestQueue(movieReq);


        setHasOptionsMenu(true);

       // adapter.notifyDataSetChanged();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // changing action bar color - changes made here
        //DO IT HERE
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inflate the layout for this fragment
        listView = (ListView) view.findViewById(R.id.list);
//Call your count down timer class///
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

}
