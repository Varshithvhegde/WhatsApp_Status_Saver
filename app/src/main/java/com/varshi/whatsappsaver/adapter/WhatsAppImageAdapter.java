package com.varshi.whatsappsaver.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.varshi.whatsappsaver.ImageViewActivity;
import com.varshith.whatsappsaver.R;
import com.varshi.whatsappsaver.StatusModel;
import com.varshi.whatsappsaver.Utils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class WhatsAppImageAdapter extends RecyclerView.Adapter<WhatsAppImageAdapter.ViewHolder> {
    Context context;
    ArrayList<StatusModel> arrayList;

    public WhatsAppImageAdapter(Context context, ArrayList<StatusModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_whatsapp_pictures, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WhatsAppImageAdapter.ViewHolder holder, int position) {
        StatusModel statusModel = arrayList.get(position);
        Glide.with(context).load(statusModel.getAbsolutePath()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void copyFile(File sourceFile, File destinationFile) throws IOException {
        if (!destinationFile.getParentFile().exists()) {
            destinationFile.getParentFile().mkdirs();
        }
        if (!destinationFile.exists()) {
            destinationFile.createNewFile();
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel();
             FileChannel destination = new FileOutputStream(destinationFile).getChannel()){
            destination.transferFrom(source, 0, source.size());
            Toast.makeText(context, "Picture Saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyFileOrDirectory(String source, String destination) {
        try {
            File src = new File(source);
            File dest = new File(destination, src.getName());
            if (src.isDirectory()) {
                String files[] = src.list();
                int fileLength = files.length;
                for (int i = 0; i < fileLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dest1 = dest.getPath();
                    copyFileOrDirectory(src1, dest1);
                }
            } else {
                copyFile(src, dest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void share(String path) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        context.startActivity(Intent.createChooser(intent, "Share using"));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageButton share, download;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            share = itemView.findViewById(R.id.list_share_image);
            download = itemView.findViewById(R.id.list_save_image);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StatusModel statusModel = arrayList.get(getAdapterPosition());
                    share(statusModel.getAbsolutePath());
                }
            });

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StatusModel statusModel = arrayList.get(getAdapterPosition());
                    copyFileOrDirectory(statusModel.getAbsolutePath(), Utils.statusSaverPath);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StatusModel statusModel = arrayList.get(getAdapterPosition());
                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putExtra("image", statusModel.getAbsolutePath());
                    intent.putExtra("type", statusModel.getType());
                    intent.putExtra("aType", "1");
                    context.startActivity(intent);
                }
            });
        }
    }
}