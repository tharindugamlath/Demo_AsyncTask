package asynctask.com.demo_asynctask;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {


    private TextView textView;
    private Button button;
    private ProgressDialog progressDialog;
    public static final int prograssBarType=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.readWebpage);
        textView = (TextView) findViewById(R.id.TextView01);



    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case prograssBarType:
                progressDialog=new ProgressDialog(this);
                progressDialog.setMessage("Showing content please wait...");
                progressDialog.setIndeterminate(false);
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.show();
                return progressDialog;
            default:
                return null;

        }
    }

    public class showDownloadContent extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(prograssBarType);
        }

        @Override
        protected String doInBackground(String... params) {
        String response="";
            int count=0;
            int total=0;

            for(String url:params){

                DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                URL getUrl = null;
                try {
                    getUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                URLConnection conection = null;
                try {
                    conection = getUrl.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    conection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int lenght = conection.getContentLength();

                try {
                    HttpResponse execute = defaultHttpClient.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(content));
                    String word="";
                    while ((word=bufferedReader.readLine()) !=null){
                        response +=word;
                        count++;
                        total = total+count;

                    }
                    publishProgress("" + (int) ((total * 100) / lenght));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                return response;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissDialog(prograssBarType);
            textView.setText(s);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress(Integer.parseInt(values[0]));
            Toast.makeText(getApplicationContext(),"val"+Integer.parseInt(values[0]),Toast.LENGTH_SHORT).show();
        }
    }
    public void onClick(View view){

        showDownloadContent task = new showDownloadContent();
        task.execute(new String[] { "http://www.vogella.com" });

    }

}
