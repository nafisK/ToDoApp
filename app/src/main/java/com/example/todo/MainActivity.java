package com.example.todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;


    List<String> items;
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItem;
    ItemsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem2);
        rvItem = findViewById(R.id.rvItems);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                items.remove(position);
                // Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item Removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int Position) {
                Log.d("MainActivity", "Single Click at Position " + Position);
                // Create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // Pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(Position));
                i.putExtra(KEY_ITEM_POSITION, Position);
                // Display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);

            }
        };

        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItem.setAdapter(itemsAdapter);
        rvItem.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                // Add item to model
                items.add(todoItem);
                // Notify adapter that you have inserted item
                itemsAdapter.notifyItemInserted(items.size() - 1 );
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_SHORT).show();
                saveItems();
            }

        });


    } // onCreate

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // This function will load items by reading every line of the data file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error Reading Items", e);
            items = new ArrayList<>();
        }
    }

    // This function saves items by writing them into the data file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            // Update model at the right position with new item text
            items.set(position, itemText);
            //notify the adapter
            itemsAdapter.notifyItemChanged(position);
            // persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item Updated Succesfully!" , Toast.LENGTH_SHORT).show();

        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }


    }









} // MainActivity