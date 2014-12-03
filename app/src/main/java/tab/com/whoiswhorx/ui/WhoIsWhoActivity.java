package tab.com.whoiswhorx.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import tab.com.whoiswhorx.R;
import tab.com.whoiswhorx.model.TeamMember;


public class WhoIsWhoActivity extends ActionBarActivity implements TeamMembersListFragment.TeamMembersListFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_is_who);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TeamMembersListFragment())
                    .commit();
        }

    }

    @Override
    public void onTeamMemberPressed(TeamMember teamMember) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        transaction.replace(R.id.container, TeamMemberDetailsFragment.newInstance(teamMember));
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }
}
