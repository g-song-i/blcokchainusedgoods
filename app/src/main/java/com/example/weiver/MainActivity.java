package com.example.weiver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private GridView main_gridView;

    Button add_item;
    Button talk;
    Button search_btn;
    EditText search_edit;

    User user;
    //메인2
    Button login_btn;
    TextView main2_user_name;
    TextView main2_user_id;
    Button bought_product;
    Button sold_product;
    Button on_sell_product;
    Button on_buy_product;

    Button category1;
    Button category2;
    Button category3;
    Button category4;
    Button category5;
    Button category6;
    Button category7;

    //이거이미지업로드테스트
    Button category8;

    boolean CASE_SEARCH = false;
    String search_text;

    ArrayList<MainGridItem> gridItems;
    ArrayList<ObjectBlock> objectBlocks = new ArrayList<>();
    MainGridAdapter gridViewAdapter;// = new MainGridAdapter();

    //String user_name = getIntent().getStringExtra("inputName");
    //버튼을 누른 시간
    private long backBtnTime = 0;

    @Override
    public void onBackPressed() {

        long curTime = System.currentTimeMillis();
        long getTime = curTime - backBtnTime ;

        if( 0 <= getTime && 1000 >= getTime) {

            android.os.Process.killProcess(android.os.Process.myPid());
            this.finish();
            super.onBackPressed();

        } else {

            backBtnTime = curTime;
            Toast.makeText(this, "\'뒤로\' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*작성자 아이디 들고오기*/
        //로그인 된 현재 유저 정보를 저장
        final PrefManager prefManager = PrefManager.getInstance(MainActivity.this);
        user = prefManager.getUser();
        //prefManager.setUserLogin(user);

        main2_user_name = (TextView)findViewById(R.id.main2_user_name);
        main2_user_id = (TextView)findViewById(R.id.main2_user_id);
        login_btn = (Button)findViewById(R.id.login_btn);

        if(prefManager.isLoggedIn()){

            main2_user_name.setText(String.valueOf(user.getUser_name()));
            main2_user_id.setText(String.valueOf(user.getUser_id()));
            login_btn.setText("로그아웃");
            login_btn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    login_btn.setText("로그인");
                    prefManager.logout();
                    finish();
                }
            });

        }else{ //로그인 안 되어있을 경우
            login_btn.setText("로그인");
            login_btn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }

        /* 검색 */
        search_edit = (EditText)findViewById(R.id.search_edit);
        search_btn = (Button)findViewById(R.id.search_btn) ;
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_text = search_edit.getText().toString();
                CASE_SEARCH = true;
                gridItems.clear();
                MainActivity.objectGrid og = new MainActivity.objectGrid("*"); //카테고리 전체라서 일단 *넣어둠
                og.execute();

            }
        });


        /*카테고리*/
        category1=(Button)findViewById(R.id.category1);
        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Category1.class));
                finish();
            }
        });
        category2=(Button)findViewById(R.id.category2);
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Category2.class));
                finish();
            }
        });
        category3=(Button)findViewById(R.id.category3);
        category3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Category3.class));
                finish();
            }
        });
        category4=(Button)findViewById(R.id.category4);
        category4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Category4.class));
                finish();
            }
        });
        category5=(Button)findViewById(R.id.category5);
        category5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Category5.class));
                finish();
            }
        });
        category6=(Button)findViewById(R.id.category6);
        category6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Category6.class));
                finish();
            }
        });
        category7=(Button)findViewById(R.id.category7);
        category7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Category7.class));
                finish();
            }
        });

       /* category8 = (Button)findViewById(R.id.category8);
        category8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestUploadImage.class));
            }
        });*/


        /* 그리드 뷰 (상품목록) */
        /*그리드 뷰 파싱해오기*/
        gridItems = new ArrayList<>();
        gridViewAdapter = new MainGridAdapter(gridItems);
        //이거 되면 카테고리 별로 다 복붙하고 여기 *대신 카테고리만 넣으면 됨
        //디비에서 카테고리에 해당하는 등록번호 다 들고오기(상태가 onSale인거)->서버에서 이미지/블록체인에서 이름,가격들고옴
        MainActivity.objectGrid og = new MainActivity.objectGrid("*"); //카테고리 전체라서 일단 *넣어둠
        og.execute();

        main_gridView = (GridView)findViewById(R.id.main_gridView);
        main_gridView.setAdapter(gridViewAdapter);


        //테스트
        /*for(int i =0;i<16;i++) {
            gridViewAdapter.addGridItem("1",ContextCompat.getDrawable(this,R.drawable.onlydog),"멍멍이","999억");
        }*/

        main_gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, BuyScreen.class);
                        intent.putExtra("register_number",gridItems.get(position).getRegister_number());
                        //Toast.makeText(getApplicationContext(), gridItems.get(position).getRegister_number(), Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }
                }
        );

        talk = (Button)findViewById(R.id.talk);
        talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefManager.isLoggedIn()){
                    startActivity(new Intent(MainActivity.this, TalkList.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "로그인 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        add_item = (Button)findViewById(R.id.add_item);
        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefManager.isLoggedIn()){
                    startActivity(new Intent(MainActivity.this, SellScreen.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "로그인 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bought_product = (Button)findViewById(R.id.bought_product);
        sold_product = (Button)findViewById(R.id.sold_product);


        bought_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BuyList.class) ;
                intent.putExtra("user_id", String.valueOf(user.getUser_id())) ;
                startActivity(intent) ;
                finish();
            }
        });

        sold_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SellList.class) ;
                intent.putExtra("user_id", String.valueOf(user.getUser_id())) ;
                startActivity(intent) ;
                finish();
            }
        });

        on_sell_product = (Button)findViewById(R.id.on_sell_product);
        on_sell_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OnDealList.class) ;
                intent.putExtra("user_id", String.valueOf(user.getUser_id())) ;
                startActivity(intent) ;
                finish();
            }
        });

        on_buy_product = (Button)findViewById(R.id.on_buy_product);
        on_buy_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OnDealList2.class) ;
                intent.putExtra("user_id", String.valueOf(user.getUser_id())) ;
                startActivity(intent) ;
                finish();
            }
        });

        //드로워
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerView = (View)findViewById(R.id.drawer_layout);

        Button btn_menu = (Button)findViewById(R.id.menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });


        drawerLayout.setDrawerListener(listner);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    DrawerLayout.DrawerListener listner = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    private class objectGrid extends AsyncTask<Void, Void, String> { //DB
        private String object_category;

        objectGrid(String object_category) {
            this.object_category = object_category;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                Log.d("tag", "doInBackground실행");

                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("object_category", object_category);

                return requestHandler.sendPostRequest(URLS.URL_GET_OBJECT_GRIDVIEW, params);


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("doInBackground 에러", "doInBackground Exception");
            }
            return null;

        }

        public void onProgressUpdate(Void... values){
            super.onProgressUpdate();
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            Log.d("onPost실행", "onPost실행");

            try {

                JSONObject object = new JSONObject(s);

                JSONArray jsonArray = object.getJSONArray("objs");
                //

                int count = 0;

                while(count < jsonArray.length()){

                    JSONObject json = jsonArray.getJSONObject(count);

                    String register_number = json.getString("register_number");
                    String object_name = json.getString("object_name");
                    String object_price = json.getString("object_cost");


                    String object_state = json.getString("object_state");
                    String object_number = json.getString("object_number");
                    String object_information = json.getString("object_information");
                    String object_owner = json.getString("object_owner");
                    String register_time = json.getString("register_time");


                    MainActivity.getImg gi = new MainActivity.getImg(register_number, object_name, object_price, "0");
                    gi.execute();


                    count++;
                }
                //gridViewAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "서버가 비활성화된 상태입니다.", Toast.LENGTH_SHORT).show();

            }

        }
    }

    private class getImg extends AsyncTask<String, Integer, Bitmap> { //서버
        private String register_number;
        String object_name;
        String object_price;
        String img_cnt;
        Bitmap bitmapImg = null;

        getImg(String register_number, String object_name, String object_price, String img_cnt) {
            this.register_number = register_number;
            this.object_name = object_name;
            this.object_price = object_price;
            this.img_cnt = img_cnt;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            /*try {
                URL myFileUrl = new URL(URLS.URL_GETIMG);
                HttpURLConnection connection = (HttpURLConnection) myFileUrl.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();*/


            HashMap<String, String> params = new HashMap<>();
            params.put("register_number", register_number);
            params.put("img_cnt", img_cnt);


                /*OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(getPostDataString(params));

                Log.d("request 입력", getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();



                int responseCode = connection.getResponseCode();

                Log.d("이미지리스폰스",Integer.toString(responseCode));



                InputStream is = ThisSendPostRequest(URLS.URL_GETIMG, params);


                //InputStream is = conn.getInputStream();


                bitmapImg = BitmapFactory.decodeStream(is);
            }catch (IOException e) {
                e.printStackTrace();
            }*/

            InputStream is = ThisSendPostRequest(URLS.URL_GETIMGMAIN, params);
            //Log.d("is",is.toString());

            bitmapImg = BitmapFactory.decodeStream(is);
            return bitmapImg;

        }

        protected void onPostExecute (Bitmap img){

            //bitmapImageList.add(img);
            //bs_test1.setImageBitmap(bitmapImageList.get(0));
            if(CASE_SEARCH){
                if(object_name.equals(search_text)){
                    MainGridItem inform = new MainGridItem(register_number, object_name, object_price,img);
                    gridItems.add(inform);

                }
            }
            else {
                MainGridItem inform = new MainGridItem(register_number, object_name, object_price, img);
                gridItems.add(inform);


            }

            gridViewAdapter.notifyDataSetChanged();




        }


        public void onProgressUpdate(Void... values){
            super.onProgressUpdate();
        }

    }


    InputStream ThisSendPostRequest(String requestURL, HashMap<String, String> postDataParams) {

        URL url;
        StringBuilder sb = new StringBuilder();
        InputStream is = null;

        try{
            Log.d("url",requestURL);
            Log.d("params",postDataParams.toString());
            url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();//

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write(getPostDataString(postDataParams));

            Log.d("request 입력", getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();



            int responseCode = connection.getResponseCode();

            Log.d("이미지리스폰스",Integer.toString(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();

                /*BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                sb = new StringBuilder();

                String response;

                while ((response = br.readLine()) != null) {

                    sb.append(response);
                }*/
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("asdf", "ThisSendPostRequest: E11111111");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("asdf", "ThisSendPostRequest: E222222222");
        }

        return is;

    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {

            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));

            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        Log.d("getPostData", result.toString());
        return result.toString();
    }

    /*블록체인*/
    /*private class objectGridBlock extends AsyncTask<Void, Void, String> {
        private String registerNumber;

        objectGridBlock(String registerNumber) {
            this.registerNumber = registerNumber;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                Log.d("tag", "doInBackground실행");

                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("register_number", registerNumber);

                return requestHandler.sendPostRequest(URLS.URL_GET_OBJECT_BLOCK, params);


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("doInBackground 에러", "doInBackground Exception");
            }
            return null;

        }

        public void onProgressUpdate(Void... values){
            super.onProgressUpdate();
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            Log.d("onPost실행", "onPost실행");

            try {

                JSONObject object = new JSONObject(s);

                //JSONArray jsonArray = object.getJSONArray("obj");
                //Toast.makeText(getApplicationContext(), object.getString("reviews"), Toast.LENGTH_SHORT).show();

                int count = 0;

                //while(count < jsonArray.length()){

                    JSONObject json = object.getJSONObject("obj");

                    String register_number = json.getString("registerNumber");
                    String object_name = json.getString("objectName");
                    String object_price = json.getString("objectCost"); /// 여기 이름 잘보기!

                    String object_number = json.getString("originObjectNumber");
                    String object_information = json.getString("objectInformation");
                    String object_owner = json.getString("objectOwner");
                    String register_time = json.getString("registerTime");


                if(CASE_SEARCH){
                    if(object_name.equals(search_text)){
                    MainGridItem inform = new MainGridItem(register_number, object_name, object_price);
                    gridItems.add(inform);

                    //ObjectBlock informm = new ObjectBlock(registerNumber,object_number,object_name,object_information,object_price, object_owner,register_time);
                    //objectBlocks.add(informm);
                    }
                }
                else {
                    MainGridItem inform = new MainGridItem(register_number, object_name, object_price);
                    gridItems.add(inform);

                    //ObjectBlock informm = new ObjectBlock(registerNumber,object_number,object_name,object_information,object_price, object_owner,register_time);
                    //objectBlocks.add(informm);
                }

                    //count++;
                //}
                gridViewAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "서버가 꺼져있어요^^", Toast.LENGTH_SHORT).show();

            }

        }
    }*/

}
