package net.jitsi.sdktest.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.UI.MessageActivity;
import net.jitsi.sdktest.adapter.ContactsAdapter;

import java.util.ArrayList;

//Fragment hiện thị Danh sách các SDT có sự dụng ứng dụng
public class ContactsFragment extends Fragment {

    private ContactsAdapter adapter;
    private ArrayList<User> listUser;               //Danh sách các User
    ListView ltvContacts;
    private ArrayList<String> numberPhone;          //Mảng chứa số diện thoại trong danh bạ điện thoại
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;           //Hằng số dùng để đánh dấu khi thwujc hiện quyền độc danh bạ điện thoại
    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ltvContacts = view.findViewById(R.id.listViewContacts);
        listUser = new ArrayList<>();
        numberPhone = new ArrayList<>();
        //Láy danh sách số điện thoại
        showContacts();

        //thêm các sdt các sự dụng app vào ListView
        addUser();

        ltvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent =new Intent(getContext(), MessageActivity.class);
                intent.putExtra("userid",listUser.get(i).getId());
                startActivity(intent);
            }
        });



        return view;
    }


    //Add User Vào arrayList ==> Sau đó hiện thị thông qưa listView
    private void addUser() {
        //Gọi đến firebase
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com").getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listUser.clear();
                    //Kiểm tra trong danh sách User trong firebase, nếu User này xuất hiện trong danh bạ điện thoại thì hiện thị lên firebase
                    for (DataSnapshot data : snapshot.getChildren()) {
                        //Log.d("AAA", "a");
                        User user = data.getValue(User.class);
                        //Kiểm tranh trong danh bạn điện thoại
                        for(int i=0;i<numberPhone.size();i++){
                            if(user.getPhone().equals(numberPhone.get(i))){
                                if (!user.getId().equals(firebaseUser.getUid())) {
                                    listUser.add(user);
                                }
                            }

                        }
                    }
//                Log.d("AAA",listUser.size()+""+listUser.get(0).getUsername());
                    adapter = new ContactsAdapter(getContext(), R.layout.item_friend_search,listUser);
                    ltvContacts.setAdapter(adapter);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Lấy danh sách danh bạ
    private void showContacts() {
        // Kiểm tra SDK có phù hợp với phiên bản không
        //Kiểm tra quyền truy cập danh bạ : READ_CONTACTS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Nếu Android Version >=6.0 thì thực hiện
            getContactNames();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.until), Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Lấy SDT trong danh bạ
    private void getContactNames(){
        ContentResolver cr = getContext().getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()) {
            // Iterate through the cursor
            do {
                // Get the contacts name
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if(Integer.parseInt(phone) >=1){
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    //Khi đã có ID và DISPLAY_NAME, ta phụ thuộc vào 2 yếu tố này để lấy NUMBER
                    while (pCur.moveToNext()) {
                        @SuppressLint("Range") String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo = phoneNo.replaceAll(" ", "");  //Xóa khoảng cách giữa các chữ số
                        char[] s = phoneNo.toCharArray();
                        if(Character.compare(s[0],'0')==0){
                            phoneNo = "+84"+ phoneNo.substring(1,phoneNo.length());           //Chuyển sang định dạng +84...
                        }
                        //Log.d("BBB"," name :" + name + " phone: "+ phoneNo + " " + phoneNo.length());
                        //Log.d("PHONE_SSS",phoneNo +" " + numberPhone.size() +"");
                        if(!numberPhone.contains(phoneNo)){
                            numberPhone.add(phoneNo);
                        }

                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }
        // Close the curosor
        cursor.close();

    }
}