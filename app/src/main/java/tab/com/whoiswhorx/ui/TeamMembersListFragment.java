package tab.com.whoiswhorx.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.observables.AndroidObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tab.com.whoiswhorx.R;
import tab.com.whoiswhorx.ddbb.DBManager;
import tab.com.whoiswhorx.exceptions.CustomException;
import tab.com.whoiswhorx.model.TeamMember;
import tab.com.whoiswhorx.parser.HtmlParser;

import static tab.com.whoiswhorx.exceptions.CustomException.ErrorType.JSOUP;
import static tab.com.whoiswhorx.exceptions.CustomException.ErrorType.NETWORK;
import static tab.com.whoiswhorx.utils.Constants.TEAM_MEMBER_LIST_URL;


/**
 * Created by juanyanezgc on 04/12/14.
 */
public class TeamMembersListFragment extends Fragment {
    private static final String TEAM_MEMBERS_KEY = "teamMembers";

    @InjectView(R.id.list_team_members)
    RecyclerView mTeamMembersList;
    @InjectView(R.id.content_switcher)
    ViewSwitcher mContentSwitcher;

    private CompositeSubscription mCompositeSubscription;
    private List<TeamMember> mTeamMembers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_members_list, container, false);
        ButterKnife.inject(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTeamMembersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTeamMembersList.addItemDecoration(new DividerDecoration(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            if (mTeamMembers == null) {
                downloadTeamMembers();
            } else {
                fillListData();
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
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_reload, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mContentSwitcher.setDisplayedChild(0);
        downloadTeamMembers();

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TEAM_MEMBERS_KEY, (ArrayList<TeamMember>) mTeamMembers);
    }

    private void downloadTeamMembers() {
        Observable<List<TeamMember>> fetchTeamMembersObservable = fetchTeamMembers()
                .map(HtmlParser::parseTeamMembers)
                .doOnNext(this::persistMembers)
                .onErrorResumeNext(this::handleError)
                .subscribeOn(Schedulers.io());

        mCompositeSubscription.add(AndroidObservable.bindFragment(this, fetchTeamMembersObservable)
                .subscribe(this::displayMembers));
    }

    private Observable<Document> fetchTeamMembers() {

        return Observable.create(subscriber -> {
            try {
                ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    Document document = Jsoup.connect(TEAM_MEMBER_LIST_URL).get();
                    subscriber.onNext(document);
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

    private void persistMembers(List<TeamMember> teamMembers) {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.saveTeamMembers(teamMembers);
    }


    private void displayMembers(List<TeamMember> teamMembers) {
        mTeamMembers = teamMembers;
        fillListData();
    }

    private void fillListData() {
        TeamMemberListAdapter adapter = new TeamMemberListAdapter(mTeamMembers);
        mContentSwitcher.setDisplayedChild(1);
        mTeamMembersList.setAdapter(adapter);
    }
}
