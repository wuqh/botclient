package cn.gwd.rest;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BotRestClientTest {
	
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private static PoolingHttpClientConnectionManager connMgr;
	private  CloseableHttpClient httpClient;
	private static RequestConfig requestConfig;
	private static final int MAX_TIMEOUT = 10000;
	private final String BOT_CONNECTOR ="BotConnector cEdt322rVyM.cwA.HIo.iU0pvsCC9OnOkFYeVhgUnX1MLDy1guym8mQsSR5wtFU";
	private final String DIRECTLINE_URL="https://directline.botframework.com/api/conversations/";
	
	
	{
		// 设置连接池
		connMgr = new PoolingHttpClientConnectionManager();
		// 设置连接池大小
		connMgr.setMaxTotal(200);
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(MAX_TIMEOUT);
		// 设置读取超时
		configBuilder.setSocketTimeout(MAX_TIMEOUT);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		// 在提交请求之前 测试连接是否可用
		configBuilder.setStaleConnectionCheckEnabled(true);
		requestConfig = configBuilder.build();
	}
	
	protected HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			if (executionCount >= 5) {// 如果已经重试了5次，就放弃
				return false;
			}
			if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
				return true;
			}
			if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
				return false;
			}
			if (exception instanceof InterruptedIOException) {// 超时
				return false;
			}
			if (exception instanceof UnknownHostException) {// 目标服务器不可达
				return false;
			}
			if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
				return false;
			}
			if (exception instanceof SSLException) {// SSL握手异常
				return false;
			}

			HttpClientContext clientContext = HttpClientContext.adapt(context);
			HttpRequest request = clientContext.getRequest();
			// 如果请求是幂等的，就再次尝试
			if (!(request instanceof HttpEntityEnclosingRequest)) {
				return true;
			}
			return false;
		}

		
	};

	
	protected  CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig)
				.setRetryHandler(httpRequestRetryHandler).build();
	}
	

	@Test
	public void ai() {
		try {
			
			//第一次开始发对话内容
			String conversationUrl = createConversation();
			this.postMessage("from", "随便看看", conversationUrl);
			//第一次取响应
			getResponse(conversationUrl);
			
			//第二次使用原有的conversationId测试
			this.postMessage("from", "随便看看", conversationUrl);
			//第二次取响应
			getResponse(conversationUrl);
			
		} catch (IOException e) {
			logger.info("发送 POST 请求（HTTP）" + e);
		} 
	}
	
	/**
	 * 创建会话
	 * @return
	 */
	public String createConversation(){
		//第一次开始发对话内容
		String conversationUrl;
		try {
			HttpClient httpClient =this.getHttpClient();
			String httpStr = null;
			HttpPost httpPost = new HttpPost(DIRECTLINE_URL);
			httpPost.setConfig(requestConfig);
			httpPost.addHeader("Authorization", BOT_CONNECTOR);
			//1取令牌与会话id
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			logger.info("执行状态码：" + response.getStatusLine().getStatusCode());

			httpStr = EntityUtils.toString(entity, "UTF-8");
			logger.info(httpStr);
			JSONObject jsonObjectResponse = JSONObject.fromObject(httpStr);
			
			//第一次开始发对话内容
			conversationUrl = jsonObjectResponse.getString("conversationId") + "/messages/";
			
			return conversationUrl;
			
		} catch (IOException e) {
			logger.info("发送 POST 请求（HTTP）" + e);
		} 
		return null;
	}
	
	/**
	 * 发生会话
	 * @param from
	 * @param text
	 * @param conversationUrl
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private void postMessage(String from,String text,String conversationUrl) throws ClientProtocolException, IOException{
		httpClient =this.getHttpClient();
		logger.info("准备发送对话内容 : "+  DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm:ss")  );
		JSONObject jsonObject =new JSONObject();
		jsonObject.put("text", text);
		jsonObject.put("from", from);
		
		HttpPost httpPost = new HttpPost(DIRECTLINE_URL+conversationUrl);
		httpPost.setConfig(requestConfig);
		StringEntity stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");// 解决中文乱码问题
		stringEntity.setContentEncoding("UTF-8");
		stringEntity.setContentType("application/json");
		
		httpPost.addHeader("Authorization", BOT_CONNECTOR);
		httpPost.setEntity(stringEntity);
		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();
		logger.info("执行状态码：" + response.getStatusLine().getStatusCode() +" "+DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm:ss") );
		if(entity!=null){
			String httpStr = EntityUtils.toString(entity, "UTF-8");
			JSONObject jsonObjectResponse = JSONObject.fromObject(httpStr);
			if(jsonObjectResponse.containsKey("messages")){
				JSONArray array=jsonObjectResponse.getJSONArray("messages");
				if(array.size()>0){
					logger.info("响应："+array.getJSONObject(array.size()-1).getString("text"));
				}
			}
			
		}
	}
	
	/**
	 * 取响应
	 * @param conversationUrl
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private void getResponse(String conversationUrl) throws ClientProtocolException, IOException{
		HttpClient httpClient =this.getHttpClient();
		//取响应
		HttpGet httpGet = new HttpGet(DIRECTLINE_URL + conversationUrl+"?watermark=dddd");
		httpGet.addHeader("Authorization", BOT_CONNECTOR);
		HttpResponse response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		logger.info("执行状态码：" + response.getStatusLine().getStatusCode());
		String httpStr = EntityUtils.toString(entity, "UTF-8");
		JSONObject jsonObjectResponse = JSONObject.fromObject(httpStr);
		JSONArray array=jsonObjectResponse.getJSONArray("messages");
		if(array.size()>0){
			logger.info("响应："+array.getJSONObject(array.size()-1).getString("text"));
		}
		
	}

}
