package ieexp.id180441112.ieexp3_step6;

import android.os.AsyncTask;

import java.io.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ClientAdvTask extends AsyncTask<Void, String, Void> {
    //ソケット宣言
    private Socket socket = null;
    //ストリーム宣言
    private InputStream is = null;
    private DataInputStream dis = null;
    private OutputStream os = null;
    private DataOutputStream dos = null;

    private String ipAddress;
    private String portNumber;
    private boolean flag = true;
    private boolean result = true;
    private boolean isLoop;
    private String question;

    private ClientAdvCallback callback;

    public ClientAdvTask(String ipAddress, String portNumber, ClientAdvCallback callback) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.callback = callback;
        isLoop = true;
    }

    /**
     * バックグラウンド処理
     * （アクティビティのUI操作は不可能）
     * @param params    未使用（Void型の配列）
     * @return  null
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            //ポート番号の取得
            int port = Integer.parseInt(portNumber);
            // サーバへ接続（3秒でタイムアウト）
            socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, port),3000);
            try {
                // コネクションが確立したらソケットの入出力ストリームにバッファ付ストリームと
                // データ入出力ストリームを連結
                dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                // UIを操作したい場合はpublishProgress()を実行してonProgressUpdate()をコールバック
                publishProgress("textConnection", "connection with "
                        + socket.getInetAddress().toString() + ":" + socket.getPort() + "!");


                // サーバからの応答受信ループ（ノンブロッキング）
                while (isLoop) {
                    if (result) {
                        //受け取った文を表示する
                        question = dis.readUTF();
                        publishProgress("textQuestion",question);
                    }
                    flag = true;
                    result = dis.readBoolean();
                    flag = false;
                    publishProgress("editAnswer","");
                }
            } catch(SocketException e) {
                publishProgress("textConnection","You see IPAddress or PortNumber again");
            }catch(SocketTimeoutException e) {
                publishProgress("textConnection","You see IPAddress or PortNumber again");
            }catch(UnknownHostException e) {
                publishProgress("textConnection","You see IPAddress again");
            }catch(IOException e){
                publishProgress("textConnection","[ERROR] " + e.getMessage());
            }
        } catch(SocketTimeoutException e){
            publishProgress("textConnection","You see IPAddress or PortNumber again");
        }
        catch (Exception e) {
            publishProgress("textConnection","ERROR " + e.getMessage());
        }

        return null;
    }


    public void leave() {
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
                publishProgress("socket is closed.");
            } catch (IOException e) {}
        }
        //ストリームを閉じる
        if(dos != null) {
            try {
                dos.close();
            }catch(IOException e) {}
        }
        if(dis != null) {
            try {
                dis.close();
            }catch(IOException e) {}
        }
    }

    public String[] send(String answer) {
        String[] returnS = {"",""};
        returnS[0] = question;
        try {
            dos.writeInt(Integer.parseInt(answer));
            dos.flush();
            while(flag) {}
            if(result)
                returnS[1] = "True";
            else if(!result)
                returnS[1] = "False";
            return returnS;
        }catch(IOException e) {e.printStackTrace();}
        returnS[1] = "False";
        return returnS;
    }

    /**
     * バックグラウンド処理を行う前の事前処理
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null)
            callback.onPreExecute();
    }

    /**
     * doInBackground()の処理が終了したときに呼び出されるメソッド
     * @param aVoid doInBackground()の戻り値
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (callback != null)
            callback.onPostExecute(aVoid);
    }

    /**
     * doInBackground()内でpublishProgress()が呼ばれたときに呼び出されるメソッド
     * @param values   Logに出力するメッセージ
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (callback != null)
            callback.onProgressUpdate(values);
    }

}
