package com.example.sagivproject.screens;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.adapters.MedicationImagesTableAdapter;
import com.example.sagivproject.adapters.diffUtils.ImageDiffCallback;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.ImageData;
import com.example.sagivproject.screens.dialogs.FullImageDialog;
import com.example.sagivproject.services.interfaces.DatabaseCallback;
import com.example.sagivproject.services.interfaces.IImageService;
import com.example.sagivproject.utils.ImageUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MedicationImagesTableActivity extends BaseActivity {
    private final List<ImageData> allImages = new ArrayList<>();
    private final List<ImageData> filteredList = new ArrayList<>();
    @Inject
    IImageService imageService;
    private MedicationImagesTableAdapter adapter;
    private TextInputEditText etSearch;
    private ActivityResultLauncher<androidx.activity.result.PickVisualMediaRequest> photoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medication_images_table);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.medicationImagesTablePage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        RecyclerView recyclerView = findViewById(R.id.recycler_MedicineImagesTablePage);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MedicationImagesTableAdapter(
                filteredList,
                new MedicationImagesTableAdapter.OnImageActionListener() {

                    @Override
                    public void onDeleteImage(ImageData image) {
                        deleteImageAndReorder(image);
                    }

                    @Override
                    public void onImageClicked(ImageData image, ImageView imageView) {
                        Drawable drawable = imageView.getDrawable();
                        if (drawable == null) return;

                        new FullImageDialog(MedicationImagesTableActivity.this, drawable).show();
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        etSearch = findViewById(R.id.edit_MedicineImagesTablePage_search);
        Button btnAdd = findViewById(R.id.btn_MedicineImagesTablePage_add);

        btnAdd.setOnClickListener(v -> photoPickerLauncher.launch(
                new androidx.activity.result.PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        ));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterImages(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        uploadImage(uri);
                    }
                }
        );

        loadImages();
    }

    private void loadImages() {
        imageService.getAllImages(new DatabaseCallback<>() {
            @Override
            public void onCompleted(List<ImageData> list) {
                allImages.clear();
                if (list != null) allImages.addAll(list);
                filterImages(Objects.requireNonNull(etSearch.getText()).toString());
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MedicationImagesTableActivity.this, "שגיאה בטעינה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterImages(String query) {
        final List<ImageData> oldList = new ArrayList<>(filteredList);
        final List<ImageData> newList = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        for (ImageData img : allImages) {
            if (img.getId() != null && img.getId().toLowerCase().contains(lowerQuery)) {
                newList.add(img);
            }
        }

        final ImageDiffCallback diffCallback = new ImageDiffCallback(oldList, newList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        filteredList.clear();
        filteredList.addAll(newList);
        diffResult.dispatchUpdatesTo(adapter);
    }

    private void uploadImage(Uri uri) {
        try {
            Bitmap bitmap;
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
            ImageView tempIv = new ImageView(this);
            tempIv.setImageBitmap(bitmap);
            String base64 = ImageUtil.convertTo64Base(tempIv);

            if (base64 != null) {
                int nextNumber = allImages.size() + 1;
                String newId = "card" + nextNumber;

                ImageData newImg = new ImageData(newId, base64);
                imageService.createImage(newImg, new DatabaseCallback<>() {
                    @Override
                    public void onCompleted(Void object) {
                        Toast.makeText(MedicationImagesTableActivity.this, "התמונה נוספה כ-" + newId, Toast.LENGTH_SHORT).show();
                        loadImages();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(MedicationImagesTableActivity.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (IOException e) {
            Toast.makeText(MedicationImagesTableActivity.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteImageAndReorder(ImageData imageToDelete) {
        allImages.remove(imageToDelete);

        for (int i = 0; i < allImages.size(); i++) {
            allImages.get(i).setId("card" + (i + 1));
        }

        imageService.updateAllImages(allImages, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(MedicationImagesTableActivity.this, "התמונה נמחקה והרשימה סודרה מחדש", Toast.LENGTH_SHORT).show();
                loadImages();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MedicationImagesTableActivity.this, "שגיאה בעדכון הרשימה", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
