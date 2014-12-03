package tab.com.whoiswhorx.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tab.com.whoiswhorx.R;
import tab.com.whoiswhorx.WhoIsWhoApplication;
import tab.com.whoiswhorx.model.TeamMember;

import static butterknife.ButterKnife.findById;


public class TeamMemberDetailsFragment extends Fragment {

    public static TeamMemberDetailsFragment newInstance(TeamMember teamMember) {
        Bundle args = new Bundle();
        args.putParcelable(TEAM_MEMBER_KEY, teamMember);

        TeamMemberDetailsFragment teamMemberDetailsFragment = new TeamMemberDetailsFragment();
        teamMemberDetailsFragment.setArguments(args);
        return teamMemberDetailsFragment;
    }

    private static final String TEAM_MEMBER_KEY = "TeamMember";
    private TeamMember mTeamMember;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_member_details, container, false);


        if (savedInstanceState == null) {
            mTeamMember = getArguments().getParcelable(TEAM_MEMBER_KEY);
        } else {
            mTeamMember = savedInstanceState.getParcelable(TEAM_MEMBER_KEY);
        }

        TextView txtName = findById(view, R.id.txtName);
        TextView txtJobTitle = findById(view, R.id.txtJobTitle);
        TextView txtBiography = findById(view, R.id.txtBiography);
        ImageView imgPhoto = findById(view, R.id.imgPhoto);

        txtName.setText(mTeamMember.getName());
        txtJobTitle.setText(mTeamMember.getJobTitle());
        txtBiography.setText(mTeamMember.getBiography());
        imgPhoto.setTag(mTeamMember.getId());
        Picasso imageLoader = WhoIsWhoApplication.getImageLoader();
        imageLoader.load(mTeamMember.getImageURI()).placeholder(R.drawable.photo_placeholder).into(imgPhoto);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        ActionBarActivity hostActivity = (ActionBarActivity) getActivity();
        hostActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTeamMember = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(TEAM_MEMBER_KEY, mTeamMember);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            ActionBarActivity activity = (ActionBarActivity) getActivity();
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            activity.getSupportActionBar().setHomeButtonEnabled(false);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.popBackStack();
        }

        return true;
    }
}
