package tab.com.whoiswhorx.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import tab.com.whoiswhorx.R;
import tab.com.whoiswhorx.WhoIsWhoApplication;
import tab.com.whoiswhorx.model.TeamMember;
import tab.com.whoiswhorx.model.TeamMemberEvent;

/**
 * Created by juanyanezgc on 04/12/14.
 */
public class TeamMemberListAdapter extends RecyclerView.Adapter<TeamMemberListAdapter.ViewHolder> {

    private Picasso mImageLoader;
    private List<TeamMember> mTeamMembers;
    private int mPhotoSize;

    public TeamMemberListAdapter(List<TeamMember> teamMembers) {
        mImageLoader = WhoIsWhoApplication.getImageLoader();
        mTeamMembers = teamMembers;
        mPhotoSize = WhoIsWhoApplication.get().getResources().getDimensionPixelSize(R.dimen.photo_size);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_team_member_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TeamMember teamMember = mTeamMembers.get(position);
        viewHolder.populateView(teamMember);
    }

    @Override
    public int getItemCount() {
        return mTeamMembers == null ? 0 : mTeamMembers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.txtName)
        public TextView txtName;
        @InjectView(R.id.txtJobTitle)
        public TextView txtJobTitle;
        @InjectView(R.id.imgPhoto)
        public ImageView imgPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void populateView(TeamMember teamMember) {
            mImageLoader
                    .load(teamMember.getImageURI())
                    .placeholder(R.drawable.photo_placeholder)
                    .resize(mPhotoSize, mPhotoSize)
                    .into(imgPhoto);

            txtName.setText(teamMember.getName());
            txtJobTitle.setText(teamMember.getJobTitle());

            itemView.setOnClickListener(v -> EventBus.getDefault().post(new TeamMemberEvent(teamMember)));
        }
    }
}
