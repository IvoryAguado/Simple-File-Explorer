package com.sds.ivor.fileexplorer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FileListAdapter extends BaseAdapter {

    public static class ViewHolder {
        public TextView resName;
        public ImageView resIcon;
        public TextView resMeta;
    }

    private static final String TAG = FileListAdapter.class.getName();

    private Context mContext;
    private List<FileListEntry> files;
    private LayoutInflater mInflater;

    public FileListAdapter(Activity context, List<FileListEntry> files) {
        super();
        mContext = context;
        this.files = files;
        mInflater = context.getLayoutInflater();

    }


    @Override
    public int getCount() {
        if (files == null) {
            return 0;
        } else {
            return files.size();
        }
    }

    @Override
    public Object getItem(int arg0) {

        if (files == null)
            return null;
        else
            return files.get(arg0);
    }

    public List<FileListEntry> getItems() {
        return files;
    }

    @Override
    public long getItemId(int position) {

        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(com.sds.ivor.fileexplorer.R.layout.explorer_item, parent, false);
            holder = new ViewHolder();
            holder.resName = (TextView) convertView.findViewById(com.sds.ivor.fileexplorer.R.id.explorer_resName);
            holder.resMeta = (TextView) convertView.findViewById(com.sds.ivor.fileexplorer.R.id.explorer_resMeta);
            holder.resIcon = (ImageView) convertView.findViewById(com.sds.ivor.fileexplorer.R.id.explorer_resIcon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final FileListEntry currentFile = files.get(position);
        holder.resName.setText(currentFile.getName());
            if (currentFile.getName().equalsIgnoreCase("..")||currentFile.getName().equalsIgnoreCase("."))
                holder.resIcon.setImageResource(R.drawable.filetype_dir);
        else
            holder.resIcon.setImageDrawable(com.sds.ivor.fileexplorer.Util.getIcon(mContext, currentFile.getPath()));
        String meta = com.sds.ivor.fileexplorer.Util.prepareMeta(currentFile, mContext);
        holder.resMeta.setText(meta);


        return convertView;
    }

}
