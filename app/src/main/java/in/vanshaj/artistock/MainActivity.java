package in.vanshaj.artistock;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    private long oldDataTag = 0, dataTag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TickerDataTask task = new TickerDataTask();
        task.delegate = this;
        task.execute("GOOG");

        findViewById(R.id.change_ticker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter Ticker Code");

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setSingleLine();

                FrameLayout container = new FrameLayout(MainActivity.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                input.setLayoutParams(params);
                container.addView(input);

                builder.setView(container);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tickerText = input.getText().toString();
                        TickerDataTask task = new TickerDataTask();
                        task.delegate = MainActivity.this;
                        task.execute(tickerText);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public void processFinish(boolean result) {
        if (!result) Toast.makeText(this, "An error occurred while fetching the requested data...", Toast.LENGTH_SHORT).show();
    }

    private class TickerDataTask extends AsyncTask<String, Void, Boolean> {
        AsyncResponse delegate = null;

        private double open = 0, high = 0, low = 0, close = 0, adj = 0;
        private long volume = 0;

        private String ticker, date = "2017-05-01", name;

        private List points = new ArrayList();

        @Override
        protected Boolean doInBackground(String... strings) {
            ticker = strings[0];
            name = getName(ticker);

            try {
                setUp(ticker);
            } catch (Exception ignored) {
                ignored.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            delegate.processFinish(aBoolean);
            if (aBoolean) {
                ((TextView) findViewById(R.id.ticker_name)).setText(name);
                /*((TextView) findViewById(R.id.stock_date)).setText(date);
                ((TextView) findViewById(R.id.stock_open)).setText(Double.toString(open));
                ((TextView) findViewById(R.id.stock_high)).setText(Double.toString(high));
                ((TextView) findViewById(R.id.stock_low)).setText(Double.toString(low));
                ((TextView) findViewById(R.id.stock_adj_close)).setText(Double.toString(close));
                ((TextView) findViewById(R.id.stock_volume)).setText(Double.toString(volume));
                ((TextView) findViewById(R.id.stock_adj_close)).setText(Double.toString(adj));*/

                GraphView graph = (GraphView) findViewById(R.id.close_graph);
                //graph.setBackgroundColor(getResources().getColor(R.color.white));
                graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.white));
                graph.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.white));

                graph.getGridLabelRenderer().setHorizontalAxisTitleColor(getResources().getColor(R.color.white));
                graph.getGridLabelRenderer().setVerticalAxisTitleColor(getResources().getColor(R.color.white));

                //graph.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.white));

                List<DataPoint> list = new ArrayList<>();

                for (int i = 0; i < points.size(); i += 2) {
                    int day = Integer.parseInt(((String) points.get(i)).split("-")[2]);
                    double close = (double) points.get(i+1);

                    Date dateD = null;

                    try {
                        dateD = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                    } catch (Exception ignored) {}

                    //if (dateD != null) list.add(new DataPoint(dateD, close));
                    //else
                        list.add(new DataPoint(day, close));

                    /*if (dateD != null) series.appendData(new DataPoint(dateD, close), true, 31);
                    else series.appendData(new DataPoint(day, close), true, 31);*/
                }

                Object[] arr = list.toArray();
                DataPoint[] dpArr = new DataPoint[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    dpArr[i] = (DataPoint) arr[i];
                }

                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dpArr);
                series.setBackgroundColor(getResources().getColor(R.color.white));
                series.setDataPointsRadius(5);

                graph.addSeries(series);
            }
        }

        private String getName(String ticker) {
            BufferedReader reader = null;
            try {
                URL url = new URL("http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=" + ticker + "&region=&lang=");
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder buffer = new StringBuilder();
                int read;
                char[] chars = new char[1024];
                while ((read = reader.read(chars)) != -1)
                    buffer.append(chars, 0, read);

                JSONObject json = new JSONObject(buffer.toString());
                System.out.println("GOT A JSON OBJECT");
                System.out.println(json.getJSONObject("ResultSet").getJSONArray("Result"));
                System.out.println((json.getJSONObject("ResultSet").getJSONArray("Result").get(0)));
                return ((JSONObject) json.getJSONObject("ResultSet").getJSONArray("Result").get(0)).getString("name");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (Exception ignored) {}
            }

            return null;
        }

        private void setUp(String ticker) throws Exception {
            for (int i = 1; i < 31; i++) {
                date = "2017-05-" + i;

                JSONObject jsonResult = null;

                try {
                    jsonResult = getData(ticker, date);
                } catch (Exception ignored) {
                    continue;
                }

                System.out.println("GOT A RESULT QUOTE OBJECT");
                System.out.println(jsonResult);

                open = jsonResult.getDouble("Open");
                high = jsonResult.getDouble("High");
                low = jsonResult.getDouble("Low");
                close = jsonResult.getDouble("Close");
                points.add(date);
                points.add(close);
                volume = jsonResult.getLong("Volume");
                adj = jsonResult.getDouble("Adj_Close");
            }
        }

        private JSONObject getData(String ticker, String date) throws Exception {
            String urlStr = genURL(ticker, date);

            BufferedReader reader = null;
            try {
                URL url = new URL(urlStr);
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder buffer = new StringBuilder();
                int read;
                char[] chars = new char[1024];
                while ((read = reader.read(chars)) != -1)
                    buffer.append(chars, 0, read);

                JSONObject json = new JSONObject(buffer.toString());
                System.out.println("GOT A JSON OBJECT");
                return json.getJSONObject("query").getJSONObject("results").getJSONObject("quote");
            } finally {
                if (reader != null)
                    reader.close();
            }
        }

        private String genURL(String ticker, String date) {
            String baseurl = "http://query.yahooapis.com/v1/public/yql?q=%20select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20=%20%22";
            String startdaterequest = "%22%20and%20startDate%20=%20%22";
            String enddaterequest = "%22%20and%20endDate%20=%20%22";
            String extras = "%22%20&format=json%20&diagnostics=true%20&env=store://datatables.org/alltableswithkeys%20&callback=";

            String result = baseurl + ticker + startdaterequest + date + enddaterequest + date + extras;
            System.out.println(result);
            return result;
        }
    }
}