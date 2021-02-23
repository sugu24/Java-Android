package ieexp.id180441112.ieexp3_step6;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.*;

public class BasicTask6Activity extends AppCompatActivity implements ClientAdvCallback{
    /** IP addressを入力するエディットテキスト */
    protected EditText editAddress;
    /** Port numberを入力するエディットテキスト */
    protected EditText editPort;
    /** Resultを入力するエディットテキスト */
    protected EditText editAnswer;
    /** Questionを表示するエディットテキスト */
    protected TextView textQuestion;
    /** connectionを表すテキストビュー **/
    protected TextView textConnection;
    /** Resultを表示するエディットテキスト */
    protected TextView textResultShow;
    /** サーバに接続するボタン */
    protected Button buttonConnect;
    /** サーバから切断するボタン */
    protected Button buttonDisconnect;
    /** メッセージを送信するボタン */
    protected Button buttonSend;

    /** クライアントアドバンスタスク */
    private ClientAdvTask task = null;

    /** -----------------------------応用-----------------------------**/
    protected TextView textAddress;
    protected TextView textPort;
    protected TextView textStatus;
    protected TextView textAnswer;
    protected TextView textResult;
    protected Button buttonHistoryShow;
    protected Button buttonHistoryHide;
    protected Button buttonHistoryClear;
    protected TextView textHistoryShow;
    private ResultDatabase rb;
    private SQLiteDatabase readDB;
    private SQLiteDatabase writeDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_task6);

        // GUIコンポーネントを取得してインスタンス変数に設定
        editAddress = (EditText)findViewById(R.id.editAddress);
        editPort = (EditText)findViewById(R.id.editPort);
        editAnswer = (EditText)findViewById(R.id.editAnswer);
        textQuestion = (TextView)findViewById(R.id.textQuestion);
        textResultShow = (TextView)findViewById(R.id.textResultShow);
        textConnection = (TextView)findViewById(R.id.textConnection);
        buttonConnect = (Button)findViewById(R.id.buttonConnect);
        buttonDisconnect = (Button)findViewById(R.id.buttonDisconnect);
        buttonSend = (Button)findViewById(R.id.buttonSend);

        /** -----------------------------応用----------------------------- **/
        textAddress = (TextView)findViewById(R.id.textAddress);
        textPort = (TextView)findViewById(R.id.textPort);
        textStatus = (TextView)findViewById(R.id.textStatus);
        textAnswer = (TextView)findViewById(R.id.textAnswer);
        textResult = (TextView)findViewById(R.id.textResult);
        buttonHistoryShow = (Button)findViewById(R.id.buttonHistoryShow);
        buttonHistoryHide = (Button)findViewById(R.id.buttonHistoryHide);
        buttonHistoryClear = (Button)findViewById(R.id.buttonHistoryClear);
        textHistoryShow = (TextView)findViewById(R.id.textHistoryShow);
        textHistoryShow.setVisibility(View.INVISIBLE);
        if(rb == null)
            rb = new ResultDatabase(this);
        if(readDB == null)
            readDB = rb.getReadableDatabase();
        if(writeDB == null)
            writeDB = rb.getWritableDatabase();

        // 有効/無効を設定
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonSend.setEnabled(false);
        buttonHistoryHide.setEnabled(false);
        editAnswer.setVisibility(View.INVISIBLE);
        textHistoryShow.setMovementMethod(new ScrollingMovementMethod());

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

    /**
     * Connectボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
    public void handleButtonConnect(View view) {
        // サーバのIPアドレスとポート番号を取得
        String address = editAddress.getText().toString();
        String port = editPort.getText().toString();

        // エラーが起きたときのトーストの表示
        if(address.equals("") || port.equals("")) {
            Toast.makeText(this, "You see IPAddress or PortNumber again", Toast.LENGTH_SHORT).show();
        }else {
            // TCPクライアントタスクを生成してバックグラウンドで実行（非同期処理）
            task = new ClientAdvTask(address, port, this);
            task.execute();
        }
    }

    /**
     * Disconnectボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
    public void handleButtonDisconnect(View view) {
        if (task != null)
            task.leave();
        task = null;
        textConnection.setText("Disconnect");
    }

    /**
     * Sendボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
    public void handleButtonSend(View view) {
        // 入力したメッセージを取得
        String answer = editAnswer.getText().toString();
        if(answer.equals("")){
            Toast.makeText(this, "answer is enpty", Toast.LENGTH_SHORT).show();
            return;
        }else{
            for(int i = 0; i < answer.length(); i++) {
                if(Character.isDigit(answer.charAt(i))) {
                    continue;
                }else {
                    Toast.makeText(this, "You see answer again", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        /**--------------------応用--------------------**/
        String[] questionAndAnswer = task.send(answer);
        rb.insertData(writeDB, questionAndAnswer[0], answer,questionAndAnswer[1]);
        if (questionAndAnswer[1].equals("True")) {
            textResultShow.setText("correct");
        }else {
            textResultShow.setText("Bad. Input your anser again");
        }
    }

    /**--------------------応用--------------------**/
    public void handleButtonHistoryShow(View view){
        editAddress.setVisibility(View.INVISIBLE);
        editPort.setVisibility(View.INVISIBLE);
        editAnswer.setVisibility(View.INVISIBLE);
        textQuestion.setVisibility(View.INVISIBLE);
        textConnection.setVisibility(View.INVISIBLE);
        textResultShow.setVisibility(View.INVISIBLE);
        buttonConnect.setVisibility(View.INVISIBLE);
        buttonDisconnect.setVisibility(View.INVISIBLE);
        buttonSend.setVisibility(View.INVISIBLE);
        textAddress.setVisibility(View.INVISIBLE);
        textPort.setVisibility(View.INVISIBLE);
        textStatus.setVisibility(View.INVISIBLE);
        textAnswer.setVisibility(View.INVISIBLE);
        textResult.setVisibility(View.INVISIBLE);

        String showText = rb.showData(readDB);
        textHistoryShow.setText(showText);
        textHistoryShow.setVisibility(View.VISIBLE);

        buttonHistoryShow.setEnabled(false);
        buttonHistoryClear.setEnabled(false);
        buttonHistoryHide.setEnabled(true);
    }

    public void handleButtonHistoryHide(View view){
        editAddress.setVisibility(View.VISIBLE);
        editPort.setVisibility(View.VISIBLE);
        textQuestion.setVisibility(View.VISIBLE);
        textConnection.setVisibility(View.VISIBLE);
        textResultShow.setVisibility(View.VISIBLE);
        buttonConnect.setVisibility(View.VISIBLE);
        buttonDisconnect.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.VISIBLE);
        textAddress.setVisibility(View.VISIBLE);
        textPort.setVisibility(View.VISIBLE);
        textStatus.setVisibility(View.VISIBLE);
        textAnswer.setVisibility(View.VISIBLE);
        textResult.setVisibility(View.VISIBLE);
        if(task != null)
            editAnswer.setVisibility(View.VISIBLE);

        textHistoryShow.setVisibility(View.INVISIBLE);
        textHistoryShow.setText("");

        buttonHistoryShow.setEnabled(true);
        buttonHistoryClear.setEnabled(true);
        buttonHistoryHide.setEnabled(false);
    }

    public void handleButtonHistoryClear(View view){
        rb.deleteTable(writeDB);
        Toast.makeText(this, "You delete table", Toast.LENGTH_SHORT).show();
    }

    /**
     * TcpClientTask側のonPreExecute()からコールバックされるメソッド
     */
    @Override
    public void onPreExecute() {
        // ボタンの有効/無効を設定
        buttonConnect.setEnabled(false);
        buttonDisconnect.setEnabled(true);
        buttonSend.setEnabled(true);

        editAnswer.setVisibility(View.VISIBLE);
    }

    /**
     * TcpClientTask側のonProgressUpdate()からコールバックされるメソッド
     * @param values   Logに出力するメッセージ
     */
    @Override
    public void onProgressUpdate(String... values) {
        // メインアクティビティのLogにメッセージを設定または追記
        if(values[0].equals("textConnection")){
            textConnection.setText(values[1]);
        }else if(values[0].equals("textQuestion")){
            textQuestion.setText(values[1]);
        }else if(values[0].equals("editAnswer")){
            editAnswer.setText(values[1]);
        }
    }

    /**
     * TcpClientTask側のonPostExecute()からコールバックされるメソッド
     * @param aVoid doInBackground()の戻り値
     */
    @Override
    public void onPostExecute(Void aVoid) {
        // ボタンの有効/無効を設定
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonSend.setEnabled(false);
        editAnswer.setVisibility(View.INVISIBLE);

        /** textEditを初期化 **/
        textQuestion.setText("");
        textResultShow.setText("");
        textConnection.setText("");
        editAnswer.setText("");
        textConnection.setText("Disconnect now");
    }
}
