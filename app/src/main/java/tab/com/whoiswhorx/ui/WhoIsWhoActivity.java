package tab.com.whoiswhorx.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import de.greenrobot.event.EventBus;
import tab.com.whoiswhorx.R;
import tab.com.whoiswhorx.model.TeamMember;
import tab.com.whoiswhorx.model.TeamMemberEvent;


public class WhoIsWhoActivity extends ActionBarActivity{

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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(TeamMemberEvent event){
        onTeamMemberPressed(event.teamMember);
    }

    private void onTeamMemberPressed(TeamMember teamMember) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
