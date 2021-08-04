package fuck.wozaixiaoyuan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FuckDaKa {
	/*pushplus的token给你微信推送*/
	private final static String pptoken="";
	/*我在校园登录的手机号*/
	private final static String name="";
	/*我在校园登录的密码*/
	private final static String pw="";
	
	private static StringBuilder Message=new StringBuilder();
	
	
	/*我在校园的登录URL*/
	private static String gwurl="https://gw.wozaixiaoyuan.com/basicinfo/mobile/login/username";
	
	/*打卡url*/
	private static String signurl="https://student.wozaixiaoyuan.com/health/save.json";
	
	/*登录参数*/
	private final static String param="?username=" + name + "&password=" + pw;
	
	/*新版我在校园用的JWESSION方式抛弃原版Token*/
	private static String JWESSION=null;
	
	private static HashMap<String, String> signdata=new HashMap<>();
	/*我在校园登录部分*/
	public static void Login() throws IOException {
		gwurl=gwurl+param;
		URL login=new URL(gwurl);
		URLConnection connection=login.openConnection();
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat");
		connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		connection.setRequestProperty("Content-Length", "2");
		connection.setRequestProperty("Host", "gw.wozaixiaoyuan.com");
		connection.setRequestProperty("Accept-Language", "en-us,en");
		connection.setRequestProperty("Accept", "application/json, text/plain, */*");
		connection.setDoOutput(true);
		connection.setDoInput(true);
        OutputStream out = connection.getOutputStream();
        out.write(param.getBytes());
		BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder sb=new StringBuilder();
		sb.append(in.readLine());
		System.out.println(sb.toString());
        Map headers = connection.getHeaderFields();
        JWESSION = connection.getHeaderField("JWSESSION");
        Message.append("这里是登录结果："+sb.toString()+" 如果是{\"code\":0}则为成功！\n");
	}
	/*获取打卡的位置数据*/
	private static String getSignData() {
		signdata.put("answers", "[\"0\"]");
		signdata.put("latitude", "维度");
		signdata.put("longitude", "经度");
		signdata.put("country", "中国");
		signdata.put("city", "xx市");
		signdata.put("district", "xx区");
		signdata.put("province", "xx省");
		signdata.put("township", "xx街道");
		signdata.put("street", "xx路");
		signdata.put("areacode", "区域代码");
        Set<String> keysSet = signdata.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuilder temp = new StringBuilder();
        boolean first = true;
        for (Object key : keys) {
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = signdata.get(key);
            String valueString = "";
            if (null != value) {
                valueString = String.valueOf(value);
            }
            temp.append(valueString);
        }
       return temp.toString();
    }
	/*执行打卡POST*/
	public static void Sign() throws IOException  {

		URL login=new URL(signurl);
		URLConnection connection=login.openConnection();
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", "2");
		connection.setRequestProperty("Host", "student.wozaixiaoyuan.com");
		connection.setRequestProperty("Accept-Language", "en-us,en");
		connection.setRequestProperty("Accept", "application/json, text/plain, */*");
		connection.setRequestProperty("JWSESSION", JWESSION);
		connection.setDoOutput(true);
		connection.setDoInput(true);
        String params = getSignData();
        OutputStream out = connection.getOutputStream();
        out.write(params.getBytes());
		BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
		StringBuilder sb=new StringBuilder();
		sb.append(in.readLine());
		System.out.println(sb.toString());
		Message.append("这里是打卡结果："+sb.toString()+" 如果是{\"code\":0}则为成功！\n");
	}
			
	/*微信推送*/
    public static String wxpush(String content) {
    	String url="http://www.pushplus.plus/send";
    	String params="token="+pptoken+"&title=我在校园签到&content="+content+"&template=html";
        String result = "";
        BufferedReader in = null;
        try {
            //String urlNameString = url + "?" + param;
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
    		connection.setDoOutput(true);
    		connection.setDoInput(true);
            // 建立实际的连接
            connection.connect();
            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes());
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
	public static void main(String[] args) {
		try {
			Login();//登录我在校园获取JWESSION
			Sign();//这里是打卡函数
			wxpush(Message.toString());
		} catch (IOException e) {
			wxpush("打卡异常请检查代码！");
		}
		

	}

}
