package tab.com.whoiswhorx.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tab.com.whoiswhorx.R;
import tab.com.whoiswhorx.WhoIsWhoApplication;
import tab.com.whoiswhorx.model.TeamMember;

public class TeamMemberListAdapter extends BaseAdapter {

    private Picasso mImageLoader;
    private LayoutInflater mInflater;
    private List<TeamMember> mTeamMembers;
    private int mPhotoSize;


    public TeamMemberListAdapter(Context context, List<TeamMember> teamMembers) {
        mImageLoader = WhoIsWhoApplication.getImageLoader();
        mInflater = LayoutInflater.from(context);
        mTeamMembers = teamMembers;
        mPhotoSize = context.getResources().getDimensionPixelSize(R.dimen.photo_size);
    }

    @Override
    public int getCount() {
        return mTeamMembers.size();
    }

    @Override
    public Object getItem(int position) {
        return mTeamMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        TeamMemberViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.row_team_member_list, viewGroup, false);
            viewHolder = new TeamMemberViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (TeamMemberViewHolder) view.getTag();
        }

        TeamMember teamMember = mTeamMembers.get(position);

        viewHolder.imgPhoto.setTag(teamMember.getId());
        mImageLoader
                .load(teamMember.getImageURI())
                .placeholder(R.drawable.photo_placeholder)
                .resize(mPhotoSize, mPhotoSize)
                .into(viewHolder.imgPhoto);

        viewHolder.txtName.setText(teamMember.getName());
        viewHolder.txtJobTitle.setText(teamMember.getJobTitle());

        return view;
    }


    static class TeamMemberViewHolder {
        @InjectView(R.id.txtName)
        public TextView txtName;
        @InjectView(R.id.txtJobTitle)
        public TextView txtJobTitle;
        @InjectView(R.id.imgPhoto)
        public ImageView imgPhoto;

        public TeamMemberViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }


}
