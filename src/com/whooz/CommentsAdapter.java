package com.whooz;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentsAdapter extends ArrayAdapter<WhoozComment>{

	private ArrayList<WhoozComment> comments;
	private Context context;
	public CommentsAdapter(Context context, int resource, ArrayList<WhoozComment> comments) {
		super(context, resource, comments);
		this.context = context;
		this.comments = comments;
	}

	 private static class ViewHolder {
	        TextView comment;
	        TextView commenter;
	    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder vHold;
		if(convertView == null){
			vHold = new ViewHolder();
			LayoutInflater vi = (LayoutInflater)context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.event_fragment_item,null);
			vHold.comment = (TextView) convertView.findViewById(R.id.comment);
			vHold.commenter = (TextView) convertView.findViewById(R.id.commenter);
			convertView.setTag(vHold);
		}else{
			vHold = (ViewHolder) convertView.getTag();
		}
		
		vHold.comment.setText(comments.get(position).getComment());
		vHold.commenter.setText(comments.get(position).getUserName()+": ");
		vHold.commenter.setTextColor(Color.GRAY);
		vHold.comment.setTextColor(Color.BLACK);

		return convertView;

	}





}
