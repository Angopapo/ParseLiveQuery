package tgio.parselivequery.queries;

import android.support.annotation.StringDef;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import tgio.parselivequery.Constants;
import tgio.parselivequery.LiveQueryClient;

/**
 * Created by pro on 16-06-21.
 */
public class BaseQuery {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            Constants.CONNECT,
            Constants.SUBSCRIBE,
            Constants.CREATE,
            Constants.ENTER,
            Constants.UPDATE,
            Constants.LEAVE,
            Constants.DELETE,
            Constants.UNSUBSCRIBE,
            Constants.ERROR
    })

    public @interface op {}
    public String className;
    public String whereKey;
    public String whereValue;
    public @op String op;
    public int requestId;
    public List<String> fields = null;


    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject where = new JSONObject();
        try {
            jo.put(Constants.OP, op);
            jo.put(Constants.REQUEST_ID, requestId);
            query.put(Constants.CLASS_NAME, className);
            where.put(whereKey, whereValue);
            query.put(Constants.WHERE, where);
            if(fields != null) {
                JSONArray fieldsArray = new JSONArray();
                for(String field : fields){
                    fieldsArray.put(field);
                }
                query.put(Constants.FIELDS, fieldsArray);
            }
            jo.put(Constants.QUERY, query);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo.toString();
    }

    private BaseQuery(@op String op, int requestId, String className) {
        this.op = op;
        this.requestId = requestId;
        this.className = className;
    }

    public static class Builder {
        BaseQuery baseQuery;


        public Builder(@op String op, String className) {
            this.baseQuery = new BaseQuery(op, LiveQueryClient.getNewRequestId(), className);
        }

        public Builder addField(String field) {
            if(baseQuery.fields == null) {
                baseQuery.fields = new ArrayList<>();
            }
            baseQuery.fields.add(field);
            return this;
        }

        public Builder where(String key, String value) {
            this.baseQuery.whereKey = key;
            this.baseQuery.whereValue = value;
            return this;
        }

        public BaseQuery build(){
            return baseQuery;
        }
    }
}
