package khirr.parselivequery;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import khirr.parselivequery.interfaces.OnListener;

public class Subscription {

    public static final String CREATE = Constants.CREATE;
    public static final String ENTER = Constants.ENTER;
    public static final String UPDATE = Constants.UPDATE;
    public static final String LEAVE = Constants.LEAVE;
    public static final String DELETE = Constants.DELETE;
    public static final String ALL = Constants.ERROR;

    private BaseQuery mBaseQuery;
    private List<Event> mEvents = new ArrayList<>();

    private rx.Subscription mRxSubscription;

    public Subscription(BaseQuery baseQuery) {
        mBaseQuery = baseQuery;
    }

    protected void subscribe() {
        LiveQueryClient.executeQuery(mBaseQuery);
        listen();
    }

    public void unsubscribe() {
        LiveQueryClient.executeQuery(mBaseQuery.unsubscribeQueryToString());
        if (!mRxSubscription.isUnsubscribed()) {
            mRxSubscription.unsubscribe();
        }
    }

    public boolean isSubscribed() {
        return !mRxSubscription.isUnsubscribed();
    }

    public void on(@NonNull final String op, @NonNull final OnListener listener) {
        mEvents.add(new Event(op, listener));
    }

    private void listen() {
        mRxSubscription = RxBus.subscribe(new Action1<LiveQueryEvent>() {
            @Override
            public void call(final LiveQueryEvent event) {
                Log.e("E", event.toString());
                for (Event ev : mEvents) {
                    if (ev.getOp().equals(ALL)) {
                        ev.getListener().on(event.object);
                    } else if (event.op.equals(ev.getOp())) {
                        ev.getListener().on(event.object);
                    }
                }
            }
        });
    }



}
