package tab.com.whoiswhorx.ui;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tab.com.whoiswhorx.R;
import tab.com.whoiswhorx.ddbb.DBManager;
import tab.com.whoiswhorx.exceptions.CustomException;
import tab.com.whoiswhorx.model.TeamMember;
import tab.com.whoiswhorx.parser.HtmlParser;
import tab.com.whoiswhorx.utils.Debug;

import static tab.com.whoiswhorx.exceptions.CustomException.ErrorType.JSOUP;
import static tab.com.whoiswhorx.exceptions.CustomException.ErrorType.NETWORK;
import static tab.com.whoiswhorx.utils.Constants.TEAM_MEMBER_LIST_URL;


public class TeamMembersListFragment extends ListFragment {

    private static final String TEAM_MEMBERS_KEY = "teamMembers";

    private List<TeamMember> mTeamMembers;
    private CompositeSubscription mCompositeSubscription;


    public interface TeamMembersListFragmentListener {
        public void onTeamMemberPressed(TeamMember teamMember);
    }

    private TeamMembersListFragmentListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();

        if (savedInstanceState == null) {
            if (mTeamMembers == null) {
                downloadTeamMembers();
            }
        } else {
            mTeamMembers = savedInstanceState.getParcelableArrayList(TEAM_MEMBERS_KEY);
            if (mTeamMembers == null) {
                downloadTeamMembers();
            } else {
                fillListData();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (TeamMembersListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TeamMembersListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_reload, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setListShown(false);
        downloadTeamMembers();

        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mListener != null) {
            mListener.onTeamMemberPressed((TeamMember) getListAdapter().getItem(position));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TEAM_MEMBERS_KEY, (ArrayList<TeamMember>) mTeamMembers);
    }

    private void downloadTeamMembers() {
        mCompositeSubscription.add(fetchTeamMembers()
                .onErrorResumeNext(this::handleError)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayMembers));
    }

    private Observable<List<TeamMember>> fetchTeamMembers() {
        return Observable.create(subscriber -> {
            try {
                ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    Debug.logInfo("[Thread: " + Thread.currentThread().getName() + "] Fetching team members");
                    Document document = Jsoup.connect(TEAM_MEMBER_LIST_URL).get();
                    List<TeamMember> teamMembers = HtmlParser.parseTeamMembers(document);
                    subscriber.onNext(teamMembers);
                    DBManager dbManager = new DBManager(getActivity());
                    dbManager.saveTeamMembers(teamMembers);
                } else {
                    subscriber.onError(new CustomException(NETWORK, "No internet connection"));
                }

            } catch (IOException e) {
                subscriber.onError(new CustomException(JSOUP, e.getMessage()));
            }
        });
    }

    private Observable<List<TeamMember>> handleError(Throwable e) {
        getActivity().runOnUiThread(() -> {
            CustomException error = (CustomException) e;
            Debug.logError("[" + Thread.currentThread().getName() + "] " + e.getMessage());
            switch (error.getErrorType()) {
                case JSOUP:
                    Toast.makeText(getActivity(), R.string.parsing_error, Toast.LENGTH_LONG).show();
                    break;
                case NETWORK:
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        return Observable.create(subscriber -> {
            DBManager dbManager = new DBManager(getActivity());
            subscriber.onNext(dbManager.getTeamMembers());
        });
    }


    private void displayMembers(List<TeamMember> teamMembers) {
        mTeamMembers = teamMembers;
        fillListData();
    }

    private void fillListData() {
        ListAdapter adapter = new TeamMemberListAdapter(getActivity(), mTeamMembers);
        setListAdapter(adapter);
        setListShown(true);
    }

}
