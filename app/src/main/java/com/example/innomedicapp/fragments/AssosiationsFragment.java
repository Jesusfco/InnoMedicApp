package com.example.innomedicapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.innomedicapp.MonitoringActivity;
import com.example.innomedicapp.PrincipalNav;
import com.example.innomedicapp.R;
import com.example.innomedicapp.model.AuthUser;
import com.example.innomedicapp.model.User;
import com.example.innomedicapp.model.UserAssosiation;
import com.example.innomedicapp.util.ServerUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssosiationsFragment extends Fragment {

    public TextView nameView, emailView, phoneView, userTypeView;
    public List<UserAssosiation> assosiations = new ArrayList<>();
    public ListView contactList;
    private AuthUser auth;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_assosiations, container, false);
        this.auth = new AuthUser( getActivity() );
        context = getActivity();
        this.contactList = (ListView)view.findViewById(R.id.contactList);
        this.contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserAssosiation assosiation = assosiations.get( position );
                Intent monitoring = new Intent(context, MonitoringActivity.class);
                monitoring.putExtra("user", (Serializable) assosiation.getUser() );
                startActivity(monitoring);
            }

        });
        return view;

    }

    @Override
    public void onStart() {

        super.onStart();
        this.getAssosiations();
    }

    private void getAssosiations() {

        final AuthUser auth = new AuthUser(getActivity());

        final String url = ServerUrl.getUrlApi() + "assosiations/myAssosiations";
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        assosiations = new ArrayList<>( );

                        try {

                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                UserAssosiation assosiated = new UserAssosiation(array.getString(i));
                                assosiations.add(assosiated);
                            }


                            setListView();


                        } catch (JSONException e) {

                            e.printStackTrace();

                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error.Response: " + error.getMessage());
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("token", auth.getToken());
                return params;
            }
        };

        queue.add(postRequest);

    }

    public void setListView() {
        if(this.assosiations.isEmpty()) {
            this.contactList.setVisibility( 0 );
            return;
        }
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), R.layout.list_contacts, (ArrayList<UserAssosiation>) this.assosiations, this.auth.getUser_type() );
        this.contactList.setAdapter(customAdapter);


    }

    class CustomAdapter extends ArrayAdapter<UserAssosiation> {

        int mResource;
        private Context mContext;
        boolean isFather = true;

        public CustomAdapter(@NonNull Context context, int resource, ArrayList<UserAssosiation> objects, int user_type) {

            super( context, resource, objects );
            this.mContext = context;
            this.mResource = resource;

            if(user_type == 1) this.isFather = false;

        }

        @Override
        public int getCount() {
            return assosiations.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate( mResource, viewGroup, false );

            TextView nameView = (TextView)view.findViewById(R.id.nameList);
            TextView phoneView = (TextView)view.findViewById(R.id.phoneList);
            TextView emailView = (TextView)view.findViewById(R.id.emailList);
            TextView userTypeView = (TextView)view.findViewById(R.id.userTypeList);

            User user = new User();
            if(this.isFather)
                user = getItem(i).getUser();
            else
                user = getItem(i).getAssosiated();

            nameView.setText( user.getName());
            emailView.setText( user.getEmail());
            phoneView.setText( user.getPhone());
            userTypeView.setText(user.getUserTypeName());

            return view;
        }
    }


}
