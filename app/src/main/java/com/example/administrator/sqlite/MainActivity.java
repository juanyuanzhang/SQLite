package com.example.administrator.sqlite;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLData;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editName, editPhone, editEmail, editQuery;
    Button btnAdd, btnAlter, btnDel, btnQuery;
    TextView tvName, tvPhone, tvEmail;
    private SQLiteDatabase db;
    private String TABLE_NAME = " myConnect";  //自訂資料庫名稱
    private String sql;
    private String newName,newPhone,newEmail;
    private int id;   //判斷用
    private String keyName;//修改用
    private AlertDialog dialog = null;
    AlertDialog.Builder builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        open();//打開資料連結
        creatTable(); //建立資料庫
    }

    private void open(){
        String path = "/data/data/" +getPackageName() +"/contacts.db";
        db = SQLiteDatabase.openOrCreateDatabase(path,null);
        Log.i("hihihi","hohoho");
    }
    private void creatTable(){  //if not exists如果沒有資料庫才建立TABLE
        sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, email TEXT);"; //最後記得分號
        db.execSQL(sql);
        Log.i("sql",sql);
    }
    private void add(){ //add新增資料
        newName = editName.getText().toString();
        newPhone = editPhone.getText().toString();
        newEmail = editEmail.getText().toString();
        sql = "INSERT INTO "+TABLE_NAME+"(name,phone,email)"+"VALUES(?,?,?)";  //sql語法-寫法1
        try{
            db.execSQL(sql, new Object[]{newName,newPhone,newEmail});
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Toast.makeText(this,"您新增一筆資料",Toast.LENGTH_LONG).show();
            editName.setText("");
            editPhone.setText("");
            editEmail.setText("");
        }
    }
    private void query(){
        keyName = editQuery.getText().toString();
        sql ="SELECT * FROM "+TABLE_NAME+" WHERE name = ? ";
        Cursor cursor = db.rawQuery(sql,new String[]{keyName});
        if(cursor.getCount()>0){
            while (cursor.moveToNext()){ //Cursor開始值為-1 ，所以需要使用moveToNext()方法，使其道下一位0
                id=cursor.getInt(0);
                editName.setText(cursor.getString(1));
                editPhone.setText(cursor.getString(2));
                editEmail.setText(cursor.getString(3));
                tvName.setText(cursor.getString(1));
                tvPhone.setText(cursor.getString(2));
                tvEmail.setText(cursor.getString(3));
            }
        }else{
            Toast.makeText(this,"查無此資料",Toast.LENGTH_LONG).show();
        }
        cursor.close(); //關閉
    }
    private void alter(){
        newName = editName.getText().toString();
        newPhone = editPhone.getText().toString();
        newEmail = editEmail.getText().toString();
        sql = "UPDATE "+TABLE_NAME+" SET name =?, phone= ?, email= ? where _id = ?" ;
        try {
            db.execSQL(sql, new String[]{newName,newPhone,newEmail,String.valueOf (id)});
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            Log.i("sql",sql);
            Toast.makeText(this,"修改成功!",Toast.LENGTH_SHORT).show();
            editName.setText("");
            editPhone.setText("");
            editEmail.setText("");
        }

    }
    private void delete(){
        sql = "DELETE FROM "+TABLE_NAME+" WHERE _id=? ";
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("訊息")
                .setMessage("確定要刪除此筆資料?")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    //設定確定按鈕
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            db.execSQL(sql, new String[]{String.valueOf(id)});
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        finally {
                            Toast.makeText(MainActivity.this,"刪除成功!",Toast.LENGTH_SHORT).show();
                            editName.setText("");
                            editPhone.setText("");
                            editEmail.setText("");
                            tvName.setText("");
                            tvPhone.setText("");
                            tvEmail.setText("");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    //設定取消按鈕
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        dialog = builder.create();
        dialog.show();

    }
    private void initView(){
        editName =findViewById(R.id.name);
        editPhone =findViewById(R.id.phone);
        editEmail =findViewById(R.id.email);
        editQuery =findViewById(R.id.search);
        btnAdd = findViewById(R.id.btnAdd);
        btnAlter =findViewById(R.id.btnEdit);
        btnDel = findViewById(R.id.btnDel);
        btnQuery = findViewById(R.id.btnQuery);
        tvName =findViewById(R.id.showName);
        tvPhone =findViewById(R.id.showPhone);
        tvEmail =findViewById(R.id.showMail);

        btnAdd.setOnClickListener(this);
        btnAlter.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAdd:
                add();
                break;
            case R.id.btnEdit:
                alter();
                break;
            case R.id.btnDel:
                delete();
                break;
            case R.id.btnQuery:
                query();
                break;
        }
    }
}
