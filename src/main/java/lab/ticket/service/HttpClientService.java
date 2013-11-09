/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lab.ticket.TicketMainFrame;
import lab.ticket.model.PassengerData;
import lab.ticket.model.SingleTrainOrderVO;
import lab.ticket.model.TicketData;
import lab.ticket.model.TrainQueryInfo;
import lab.ticket.model.UserData;
import lab.ticket.util.TicketUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientService {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientService.class);

	// 获取登录提交的随机数
	public static final String POST_UTL_LOGINACTION_LOGINAYSNSUGGEST = "https://dynamic.12306.cn/otsweb/loginAction.do?method=loginAysnSuggest";
	// 登录
	public static final String POST_UTL_LOGINACTION = "https://dynamic.12306.cn/otsweb/loginAction.do?method=login";
	// 获取TOKEN URL
	public static final String GET_URL_USERTOKEN = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init";
	// 查询余票URL
	//public static final String GET_URL_QUERYTICKET_QT = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=qt";
	public static final String GET_URL_QUERYTICKET = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=queryLeftTicket";
	// 提交火车车次信息
	public static final String POST_URL_SUBMUTORDERREQUEST = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=submutOrderRequest";
	// 获取验证码(登录)
	public static final String GET_LOGINURL_PASSCODE = "https://dynamic.12306.cn/otsweb/passCodeNewAction.do?module=login&rand=sjrand";
	// 获取验证码(提交订单)
	public static final String GET_SUBMITURL_PASSCODE = "https://dynamic.12306.cn/otsweb/passCodeNewAction.do?module=passenger&rand=randp";
	// 获取联系人(解析html能获得)和提交令牌
	public static final String GET_URL_CONFIRMPASSENGER = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init";
	// 获取火车票数量
	public static final String GET_URL_GETQUEUECOUNT = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=getQueueCount";
	// 检查订单URL
	public static final String POST_URL_CHECKORDERINFO = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=checkOrderInfo&rand=";
	// 提交订单信息
	public static final String POST_URL_CONFIRMSINGLEFORQUEUE = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=confirmSingleForQueue";

	private static X509TrustManager tm = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	/**
	 * 构建HttpClient对象
	 * 
	 * @return
	 */
	public static HttpClient buildHttpClient() {
		try {
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(sslcontext);
			ClientConnectionManager ccm = new DefaultHttpClient().getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
			params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000);
			HttpClient httpclient = new DefaultHttpClient(ccm, params);
			httpclient.getParams().setParameter(HTTP.USER_AGENT,
					"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; BOIE9;ZHCN)");
			return httpclient;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	}

	private HttpResponse getHttpRequest(HttpClient httpClient, String url, List<NameValuePair> parameters,
			Map<String, String> cookieData) {
		// 创建GET请求
		try {
			logger.debug("------------------------------------------------------------------------");
			if (parameters != null && parameters.size() > 0) {
				String paramURL = URLEncodedUtils.format(parameters, HTTP.UTF_8);
				if (url.indexOf("?") > -1) {
					url = url + "&" + paramURL;
				} else {
					url = url + "?" + paramURL;
				}
			}

			logger.debug("GET URL: " + url);

			HttpGet get = new HttpGet(url);
			get.setHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; BOIE9;ZHCN)");
			if (cookieData != null) {
				boolean first = true;
				StringBuilder cookie = new StringBuilder();
				for (Map.Entry<String, String> me : cookieData.entrySet()) {
					if (first) {
						first = false;
					} else {
						cookie.append(";");
					}
					cookie.append(me.getKey() + "=" + me.getValue());
				}
				get.setHeader("Cookie", cookie.toString());
			}

			if (logger.isDebugEnabled()) {

				if (parameters != null) {
					logger.debug(" + Request parameters: ");

					for (NameValuePair param : parameters) {
						logger.debug("   - " + param.getName() + " : " + param.getValue());
					}
				}
				logger.debug(" + Request headers: ");
				for (Header header : get.getAllHeaders()) {
					logger.debug("   - " + header.getName() + " : " + header.getValue());
				}

			}

			HttpResponse response = httpClient.execute(get);
			if (logger.isDebugEnabled()) {
				logger.debug(" + Response headers: ");
				for (Header header : response.getAllHeaders()) {
					logger.debug("   - " + header.getName() + " : " + header.getValue());
				}
			}
			logger.debug("***********************************************************************");
			return response;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * 返回GET请求响应字符串
	 * @param httpClient
	 * @param url
	 * @param parameters
	 * @param cookieData
	 * @return
	 */
	private String getHttpRequestAsString(HttpClient httpClient, String url, List<NameValuePair> parameters,
			Map<String, String> cookieData) {
		try {
			HttpResponse response = getHttpRequest(httpClient, url, parameters, cookieData);
			HttpEntity entity = response.getEntity();
			String responseHTML = EntityUtils.toString(entity).trim();
			TicketMainFrame.appendMessage("GET: " + url);
			String message = null;
			if (responseHTML.length() > 300) {
				message = " + Response HTML(0-300):\n" + responseHTML.substring(0, 100);
			} else {
				message = " + Response HTML:\n" + responseHTML;
			}
			TicketMainFrame.appendMessage(message);
			logger.debug(" + Response HTML (ALL):\n" + responseHTML);
			return responseHTML;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * POST请求
	 * @param httpclient
	 * @param url
	 * @param parameters
	 * @param cookieData
	 * @return
	 */
	private HttpResponse postHttpRequest(HttpClient httpclient, String url, List<NameValuePair> parameters,
			Map<String, String> cookieData) {
		try {
			logger.debug("------------------------------------------------------------------------");
			logger.debug("POST URL: " + url);

			HttpPost post = new HttpPost(url);
			post.setHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; BOIE9;ZHCN)");
			if (cookieData != null) {
				boolean first = true;
				StringBuilder cookie = new StringBuilder();
				for (Map.Entry<String, String> me : cookieData.entrySet()) {
					if (first) {
						first = false;
					} else {
						cookie.append(";");
					}
					cookie.append(me.getKey() + "=" + me.getValue());
				}
				post.setHeader("Cookie", cookie.toString());
			}
			if (parameters != null) {
				UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
				post.setEntity(uef);
			}
			if (logger.isDebugEnabled()) {
				if (parameters != null) {
					logger.debug(" + Request parameters: ");

					for (NameValuePair param : parameters) {
						logger.debug("   - " + param.getName() + " : " + param.getValue());
					}
				}
				logger.debug(" + Request headers: ");
				for (Header header : post.getAllHeaders()) {
					logger.debug("   - " + header.getName() + " : " + header.getValue());
				}
			}
			HttpResponse response = httpclient.execute(post);
			if (logger.isDebugEnabled()) {
				logger.debug(" + Response headers: ");
				for (Header header : response.getAllHeaders()) {
					logger.debug("   - " + header.getName() + " : " + header.getValue());
				}
			}
			logger.debug("***********************************************************************");
			return response;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * 返回POST请求响应字符串
	 * @param httpClient
	 * @param url
	 * @param parameters
	 * @param cookieData
	 * @return
	 */
	private String postHttpRequestAsString(HttpClient httpClient, String url, List<NameValuePair> parameters,
			Map<String, String> cookieData) {
		try {
			HttpResponse response = postHttpRequest(httpClient, url, parameters, cookieData);
			HttpEntity entity = response.getEntity();
			String responseHTML = EntityUtils.toString(entity).trim();
			TicketMainFrame.appendMessage("GET: " + url);
			String message = null;
			if (responseHTML.length() > 300) {
				message = " + Response HTML(0-300):\n" + responseHTML.substring(0, 100);
			} else {
				message = " + Response HTML:\n" + responseHTML;
			}
			TicketMainFrame.appendMessage(message);
			logger.debug(" + Response HTML (ALL):\n" + responseHTML);
			return responseHTML;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * 初始化登录用户Cookie数据
	 * @return
	 */
	public Map<String, String> initCookie() {
		HttpClient httpClient = buildHttpClient();
		try {
			Map<String, String> cookieMap = new HashMap<String, String>();
			HttpResponse response = getHttpRequest(httpClient, "https://dynamic.12306.cn/otsweb/main.jsp", null, null);
			// 获取消息头的信息
			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				if (headers[i].getName().equals("Set-Cookie")) {
					String cookie = headers[i].getValue();
					String cookieName = cookie.split("=")[0];
					String cookieValue = cookie.split("=")[1].split(";")[0];
					cookieMap.put(cookieName, cookieValue);
				}
			}
			TicketMainFrame.appendMessage("初始化获取Cookie：" + cookieMap);
			return cookieMap;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 基于登录用户Cookie数据构建登陆验证码图片
	 * @param cookieData
	 * @return
	 */
	public File buildLoginCodeImage(Map<String, String> cookieData) {
		HttpClient httpClient = buildHttpClient();
		File file = new File(System.getProperty("java.io.tmpdir") + File.separator + cookieData.get("JSESSIONID")
				+ ".login.jpg");
		try {
			String url = "https://dynamic.12306.cn/otsweb/passCodeNewAction.do?module=login&rand=sjrand";
			HttpResponse response = getHttpRequest(httpClient, url, null, cookieData);
			HttpEntity entity = response.getEntity();
			InputStream instream = entity.getContent();
			OutputStream out = new FileOutputStream(file);
			byte[] tmp = new byte[1];
			while ((instream.read(tmp)) != -1) {
				out.write(tmp);
			}
			out.close();
			instream.close();
			TicketMainFrame.appendMessage("更新登录验证码图像:" + file.getAbsolutePath());
			return file;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 基于登录用户Cookie数据构建下单验证码图片
	 * @param cookieData
	 * @return
	 */
	public File buildSubmitCodeImage(Map<String, String> cookieData) {
		HttpClient httpClient = buildHttpClient();
		File file = new File(System.getProperty("java.io.tmpdir") + File.separator + cookieData.get("JSESSIONID")
				+ ".submit.jpg");
		try {
			String url = "https://dynamic.12306.cn/otsweb/passCodeNewAction.do?module=passenger&rand=randp";
			HttpResponse response = getHttpRequest(httpClient, url, null, cookieData);
			HttpEntity entity = response.getEntity();
			InputStream instream = entity.getContent();
			OutputStream out = new FileOutputStream(file);
			byte[] tmp = new byte[1];
			while ((instream.read(tmp)) != -1) {
				out.write(tmp);
			}
			out.close();
			instream.close();
			TicketMainFrame.appendMessage("更新下单验证码图像:" + file.getAbsolutePath());
			return file;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	private String loginAysnSuggest(String loginUser, String loginPasswd, String loginCode,
			Map<String, String> cookieData) {
		HttpClient httpClient = buildHttpClient();
		try {
			String responseHTML = postHttpRequestAsString(httpClient, POST_UTL_LOGINACTION_LOGINAYSNSUGGEST, null,
					cookieData);
			//{"loginRand":"628","randError":"Y"}
			return StringUtils.substringBetween(responseHTML, "{\"loginRand\":\"", "\",\"randError\":\"Y\"}");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/******************* 登录相关参数 **************************/
	public static final String LOGIN_LOGINRAND = "loginRand";
	public static final String LOGIN_USERNAME = "loginUser.user_name";
	public static final String LOGIN_NAMEERRORFOCUS = "nameErrorFocus";
	public static final String LOGIN_PASSWORDERRORFOCUS = "passwordErrorFocus";
	public static final String LOGIN_RANDCODE = "randCode";
	public static final String LOGIN_RANDERRORFOCUS = "randErrorFocus";
	public static final String LOGIN_REFUNDFLAG = "refundFlag";
	public static final String LOGIN_REFUNDLOGIN = "refundLogin";
	public static final String LOGIN_PASSWORD = "user.password";

	public boolean Login(String loginUser, String loginPasswd, String loginCode, Map<String, String> cookieData) {
		HttpClient httpClient = buildHttpClient();
		try {

			String loginRand = this.loginAysnSuggest(loginUser, loginPasswd, loginCode, cookieData);

			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair(LOGIN_LOGINRAND, loginRand));
			parameters.add(new BasicNameValuePair(LOGIN_USERNAME, loginUser));
			parameters.add(new BasicNameValuePair(LOGIN_NAMEERRORFOCUS, ""));
			parameters.add(new BasicNameValuePair(LOGIN_PASSWORDERRORFOCUS, ""));
			parameters.add(new BasicNameValuePair(LOGIN_RANDCODE, loginCode));
			parameters.add(new BasicNameValuePair(LOGIN_RANDERRORFOCUS, ""));
			parameters.add(new BasicNameValuePair(LOGIN_REFUNDFLAG, "Y"));
			parameters.add(new BasicNameValuePair(LOGIN_REFUNDLOGIN, "N"));
			parameters.add(new BasicNameValuePair(LOGIN_PASSWORD, loginPasswd));

			String responseHTML = postHttpRequestAsString(httpClient, POST_UTL_LOGINACTION, parameters, cookieData);
			if (responseHTML.contains("欢迎您登录中国铁路客户服务中心网站")) {
				TicketMainFrame.appendMessage("用户[" + loginUser + "]登录成功");
				return true;
			} else {
				TicketMainFrame.appendMessage("用户[" + loginUser + "]登录失败，请查看详细的日志输出");
				logger.error(" + Response ERROR HTML:\n" + responseHTML);
				return false;
			}
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/******************* 查询余票相关参数 ************************/
	// 学生票
	public static final String QUERY_INCLUDESTUDENT = "includeStudent";
	// 始发站
	public static final String QUERY_FROM_STATION_TELECODE = "orderRequest.from_station_telecode";
	// 查询时间区间段
	public static final String QUERY_START_TIME_STR = "orderRequest.start_time_str";
	// 到站
	public static final String QUERY_TO_STATION_TELECODE = "orderRequest.to_station_telecode";
	// 出发日期
	public static final String QUERY_TRAIN_DATE = "orderRequest.train_date";
	// 火车编号
	public static final String QUERY_TRAIN_NO = "orderRequest.train_no";
	// 未知
	public static final String QUERY_SEATTYPEANDNUM = "seatTypeAndNum";
	// 查询类型
	public static final String QUERY_TRAINCLASS = "trainClass";
	// 查询全部
	public static final String QUERY_TRAINPASSTYPE = "trainPassType";

	public List<TrainQueryInfo> queryTrain(TicketData ticketData, UserData userData, String trainDate) {
		HttpClient httpClient = buildHttpClient();
		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair(QUERY_TRAIN_DATE, trainDate));
			parameters.add(new BasicNameValuePair(QUERY_FROM_STATION_TELECODE, ticketData.getTrainFromCode()));
			parameters.add(new BasicNameValuePair(QUERY_TO_STATION_TELECODE, ticketData.getTrainToCode()));
			parameters.add(new BasicNameValuePair(QUERY_TRAIN_NO, ""));
			parameters.add(new BasicNameValuePair(QUERY_TRAINPASSTYPE, "QB"));
			parameters.add(new BasicNameValuePair(QUERY_TRAINCLASS, "QB#D#Z#T#K#QT#"));
			parameters.add(new BasicNameValuePair(QUERY_INCLUDESTUDENT, "00"));
			parameters.add(new BasicNameValuePair(QUERY_SEATTYPEANDNUM, ""));
			parameters.add(new BasicNameValuePair(QUERY_START_TIME_STR, "00:00--24:00"));

			String response = getHttpRequestAsString(httpClient, GET_URL_QUERYTICKET, parameters,
					userData.getCookieData());

			return TicketUtil.parserQueryInfo(response);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**************** 提交订单相关参数 *********************/
	public static final String ORDER_ARRIVE_TIME = "arrive_time";
	public static final String ORDER_FROM_STATION_NAME = "from_station_name";
	public static final String ORDER_FROM_STATION_NO = "from_station_no";
	public static final String ORDER_FROM_STATION_TELECODE = "from_station_telecode";
	public static final String ORDER_FROM_STATION_TELECODE_NAME = "from_station_telecode_name";
	public static final String ORDER_INCLUDE_STUDENT = "include_student";
	public static final String ORDER_LISHI = "lishi";
	public static final String ORDER_LOCATIONCODE = "locationCode";
	public static final String ORDER_MMSTR = "mmStr";
	public static final String ORDER_ROUND_START_TIME_STR = "round_start_time_str";
	public static final String ORDER_ROUND_TRAIN_DATE = "round_train_date";
	public static final String ORDER_SEATTYPE_NUM = "seattype_num";
	public static final String ORDER_SINGLE_ROUND_TYPE = "single_round_type";
	public static final String ORDER_START_TIME_STR = "start_time_str";
	public static final String ORDER_STATION_TRAIN_CODE = "station_train_code";
	public static final String ORDER_TO_STATION_NAME = "to_station_name";
	public static final String ORDER_TO_STATION_NO = "to_station_no";
	public static final String ORDER_TO_STATION_TELECODE = "to_station_telecode";
	public static final String ORDER_TO_STATION_TELECODE_NAME = "to_station_telecode_name";
	public static final String ORDER_TRAIN_CLASS_ARR = "train_class_arr";
	public static final String ORDER_TRAIN_DATE = "train_date";
	public static final String ORDER_TRAIN_PASS_TYPE = "train_pass_type";
	public static final String ORDER_TRAIN_START_TIME = "train_start_time";
	public static final String ORDER_TRAINNO4 = "trainno4";
	public static final String ORDER_YPINFODETAIL = "ypInfoDetail";

	public void submitOrderRequest(SingleTrainOrderVO singleTrainOrderVO) {
		String title = "[登录用户：" + singleTrainOrderVO.getLoginUser() + "] ";
		HttpClient httpClient = buildHttpClient();
		TrainQueryInfo trainQueryInfo = singleTrainOrderVO.getTrainQueryInfo();
		try {
			// 提交预定的车次 一共25个参数
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair(ORDER_ARRIVE_TIME, trainQueryInfo.getEndTime()));
			parameters.add(new BasicNameValuePair(ORDER_FROM_STATION_NAME, trainQueryInfo.getFromStation()));
			parameters.add(new BasicNameValuePair(ORDER_FROM_STATION_NO, trainQueryInfo.getFormStationNo()));
			parameters.add(new BasicNameValuePair(ORDER_FROM_STATION_TELECODE, trainQueryInfo.getFromStationCode()));
			parameters
					.add(new BasicNameValuePair(ORDER_FROM_STATION_TELECODE_NAME, trainQueryInfo.getFromStationName()));
			parameters.add(new BasicNameValuePair(ORDER_INCLUDE_STUDENT, "00"));
			parameters.add(new BasicNameValuePair(ORDER_LISHI, trainQueryInfo.getTakeTime()));
			parameters.add(new BasicNameValuePair(ORDER_LOCATIONCODE, trainQueryInfo.getLocationCode()));
			parameters.add(new BasicNameValuePair(ORDER_MMSTR, trainQueryInfo.getMmStr()));
			parameters.add(new BasicNameValuePair(ORDER_ROUND_START_TIME_STR, "00:00--24:00"));
			parameters.add(new BasicNameValuePair(ORDER_ROUND_TRAIN_DATE, singleTrainOrderVO.getTrainDate()));
			parameters.add(new BasicNameValuePair(ORDER_SEATTYPE_NUM, ""));
			parameters.add(new BasicNameValuePair(ORDER_SINGLE_ROUND_TYPE, trainQueryInfo.getSingle_round_type()));
			parameters.add(new BasicNameValuePair(ORDER_START_TIME_STR, "00:00--24:00"));
			parameters.add(new BasicNameValuePair(ORDER_STATION_TRAIN_CODE, trainQueryInfo.getTrainNo()));
			parameters.add(new BasicNameValuePair(ORDER_TO_STATION_NAME, trainQueryInfo.getToStation()));
			parameters.add(new BasicNameValuePair(ORDER_TO_STATION_NO, trainQueryInfo.getToStationNo()));
			parameters.add(new BasicNameValuePair(ORDER_TO_STATION_TELECODE, trainQueryInfo.getToStationCode()));
			parameters.add(new BasicNameValuePair(ORDER_TO_STATION_TELECODE_NAME, trainQueryInfo.getToStationName()));
			parameters.add(new BasicNameValuePair(ORDER_TRAIN_CLASS_ARR, "QB#D#Z#T#K#QT#"));
			parameters.add(new BasicNameValuePair(ORDER_TRAIN_DATE, singleTrainOrderVO.getTrainDate()));
			parameters.add(new BasicNameValuePair(ORDER_TRAIN_PASS_TYPE, "QB"));
			parameters.add(new BasicNameValuePair(ORDER_TRAIN_START_TIME, trainQueryInfo.getStartTime()));
			parameters.add(new BasicNameValuePair(ORDER_TRAINNO4, trainQueryInfo.getTrainno4()));
			parameters.add(new BasicNameValuePair(ORDER_YPINFODETAIL, trainQueryInfo.getYpInfoDetail()));

			TicketMainFrame.appendMessage(title + "提交订票请求, 车次：" + singleTrainOrderVO.getTrainNo() + ",席别："
					+ singleTrainOrderVO.getSeatType());
			HttpResponse response = postHttpRequest(httpClient, POST_URL_SUBMUTORDERREQUEST, parameters,
					singleTrainOrderVO.getCookieData());

			int statusCode = response.getStatusLine().getStatusCode();

			// 返回码 301 或 302 转发到location的新地址
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("location");
				String redirectUrl = locationHeader.getValue();

				TicketMainFrame.appendMessage(title + "订票请求响应代码：" + statusCode + ", 转向获取订票凭证数据");
				logger.debug("Response StatusCode: " + statusCode, "Redirect to URL: " + redirectUrl);
				HttpClient httpClient2 = buildHttpClient();
				String responseBody = postHttpRequestAsString(httpClient2, redirectUrl, null,
						singleTrainOrderVO.getCookieData());
				httpClient2.getConnectionManager().shutdown();
				String leftTicketStr = TicketUtil.getCredential(responseBody);
				if (StringUtils.isBlank(leftTicketStr)) {
					throw new IllegalArgumentException(title + "未取到有效的leftTicketStr数据");
				} else {
					TicketMainFrame.appendMessage(title + "成功获取到leftTicketStr凭证数据：" + leftTicketStr);
				}
				singleTrainOrderVO.setSubmitOrderRequestLeftTicketStr(leftTicketStr);
				String token = TicketUtil.getToken(responseBody);
				if (StringUtils.isBlank(leftTicketStr)) {
					throw new IllegalArgumentException(title + "未取到有效的token数据");
				} else {
					TicketMainFrame.appendMessage(title + "成功获取到Token凭证数据：" + token);
				}
				singleTrainOrderVO.setSubmitOrderRequestToken(token);
			}
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**************** 获取火车票数量 ***********************/
	public static final String GETQUEUECOUNT_FROM = "from";
	public static final String GETQUEUECOUNT_SEAT = "seat";
	public static final String GETQUEUECOUNT_STATION = "station";
	public static final String GETQUEUECOUNT_TICKET = "ticket";
	public static final String GETQUEUECOUNT_TO = "to";
	public static final String GETQUEUECOUNT_TRAIN_DATE = "train_date";
	public static final String GETQUEUECOUNT_TRAIN_NO = "train_no";

	public String getQueueCount(TicketData ticketData, SingleTrainOrderVO singleTrainOrderVO) {
		String title = "[登录用户：" + singleTrainOrderVO.getLoginUser() + "] ";
		HttpClient httpClient = buildHttpClient();
		TrainQueryInfo trainQueryInfo = singleTrainOrderVO.getTrainQueryInfo();
		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair(GETQUEUECOUNT_FROM, ticketData.getTrainFromCode()));
			parameters.add(new BasicNameValuePair(GETQUEUECOUNT_SEAT, singleTrainOrderVO.getSeatType().getValue()));
			parameters.add(new BasicNameValuePair(GETQUEUECOUNT_STATION, trainQueryInfo.getTrainNo()));
			parameters.add(new BasicNameValuePair(GETQUEUECOUNT_TICKET, singleTrainOrderVO
					.getSubmitOrderRequestLeftTicketStr()));
			parameters.add(new BasicNameValuePair(GETQUEUECOUNT_TO, ticketData.getTrainToCode()));
			parameters.add(new BasicNameValuePair(GETQUEUECOUNT_TRAIN_DATE, singleTrainOrderVO.getTrainDate()));
			parameters.add(new BasicNameValuePair(GETQUEUECOUNT_TRAIN_NO, trainQueryInfo.getTrainno4()));

			TicketMainFrame.appendMessage(title + "下单余票数查询请求, 车次：" + singleTrainOrderVO.getTrainNo() + ",席别："
					+ singleTrainOrderVO.getSeatType());
			String response = getHttpRequestAsString(httpClient, GET_URL_GETQUEUECOUNT, parameters,
					singleTrainOrderVO.getCookieData());
			return response;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/******************* 提交购票信息 **************************/
	public static final String SUBMIT_LEFTTICKETSTR = "leftTicketStr";
	// 传入参数（姓名，证件类型，证件号码）
	public static final String SUBMIT_OLDPASSENGERS = "oldPassengers";
	// 传入参数 000000000000000000000000000000
	public static final String SUBMIT_BED_LEVEL_ORDER_NUM = "orderRequest.bed_level_order_num";
	public static final String SUBMIT_CANCEL_FLAG = "orderRequest.cancel_flag";
	public static final String SUBMIT_END_TIME = "orderRequest.end_time";
	public static final String SUBMIT_FROM_STATION_NAME = "orderRequest.from_station_name";
	public static final String SUBMIT_FROM_STATION_TELECODE = "orderRequest.from_station_telecode";
	// 传入参数 Y
	public static final String SUBMIT_ID_MODE = "orderRequest.id_mode";
	public static final String SUBMIT_RESERVE_FLAG = "orderRequest.reserve_flag";
	public static final String SUBMIT_SEAT_DETAIL_TYPE_CODE = "orderRequest.seat_detail_type_code";
	public static final String SUBMIT_START_TIME = "orderRequest.start_time";
	public static final String SUBMIT_STATION_TRAIN_CODE = "orderRequest.station_train_code";
	public static final String SUBMIT_TICKET_TYPE_ORDER_NUM = "orderRequest.ticket_type_order_num";
	public static final String SUBMIT_TO_STATION_NAME = "orderRequest.to_station_name";
	public static final String SUBMIT_TO_STATION_TELECODE = "orderRequest.to_station_telecode";
	public static final String SUBMIT_TO_SEAT_TYPE_CODE = "orderRequest.seat_type_code";
	public static final String SUBMIT_TOKEN = "org.apache.struts.taglib.html.TOKEN";
	// 传入参数（seat,seat_detail,ticket,姓名,cardtype,mobileno,Y）
	public static final String SUBMIT_PASSENGERTICKETS = "passengerTickets";
	public static final String SUBMIT_RANDCODE = "randCode";
	public static final String SUBMIT_TEXTFIELD = "textfield";
	public static final String SUBMIT_TFLAG = "tFlag";

	public String confirmSingleForQueueOrder(TicketData ticketData, SingleTrainOrderVO singleTrainOrderVO,
			String randCode, boolean justCheck) {
		String title = "[登录用户：" + singleTrainOrderVO.getLoginUser() + "] ";
		HttpClient httpClient = buildHttpClient();
		TrainQueryInfo trainQueryInfo = singleTrainOrderVO.getTrainQueryInfo();
		try {

			String url = null;
			if (justCheck) {
				url = POST_URL_CHECKORDERINFO + randCode;
			} else {
				url = POST_URL_CONFIRMSINGLEFORQUEUE;
			}

			List<PassengerData> validPassengerDatas = ticketData.getValidPassengerDatas();

			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			for (int i = 0; i < validPassengerDatas.size(); i++) {
				parameters.add(new BasicNameValuePair("checkbox" + i, Integer.toString(i)));
			}
			parameters.add(new BasicNameValuePair("checkbox9", "Y"));
			parameters.add(new BasicNameValuePair("checkbox9", "Y"));
			parameters.add(new BasicNameValuePair("checkbox9", "Y"));
			parameters.add(new BasicNameValuePair("checkbox9", "Y"));
			parameters.add(new BasicNameValuePair("checkbox9", "Y"));
			parameters.add(new BasicNameValuePair(SUBMIT_LEFTTICKETSTR, singleTrainOrderVO
					.getSubmitOrderRequestLeftTicketStr()));
			for (int i = 0; i < validPassengerDatas.size(); i++) {
				parameters.add(new BasicNameValuePair(SUBMIT_OLDPASSENGERS, validPassengerDatas.get(i).getShortText()));
			}
			for (int i = 0; i < (5 - validPassengerDatas.size()); i++) {
				parameters.add(new BasicNameValuePair(SUBMIT_OLDPASSENGERS, ""));
			}
			parameters.add(new BasicNameValuePair(SUBMIT_BED_LEVEL_ORDER_NUM, "000000000000000000000000000000"));
			parameters.add(new BasicNameValuePair(SUBMIT_CANCEL_FLAG, "1"));
			parameters.add(new BasicNameValuePair(SUBMIT_END_TIME, trainQueryInfo.getEndTime()));
			parameters.add(new BasicNameValuePair(SUBMIT_FROM_STATION_NAME, trainQueryInfo.getFromStation()));
			parameters.add(new BasicNameValuePair(SUBMIT_FROM_STATION_TELECODE, trainQueryInfo.getFromStationCode()));
			parameters.add(new BasicNameValuePair(SUBMIT_ID_MODE, "Y"));
			parameters.add(new BasicNameValuePair(SUBMIT_RESERVE_FLAG, "A"));
			parameters.add(new BasicNameValuePair(SUBMIT_TO_SEAT_TYPE_CODE, ""));
			parameters.add(new BasicNameValuePair(SUBMIT_START_TIME, trainQueryInfo.getStartTime()));
			parameters.add(new BasicNameValuePair(SUBMIT_STATION_TRAIN_CODE, trainQueryInfo.getTrainNo()));
			parameters.add(new BasicNameValuePair(SUBMIT_TICKET_TYPE_ORDER_NUM, ""));
			parameters.add(new BasicNameValuePair(SUBMIT_TO_STATION_NAME, trainQueryInfo.getToStation()));
			parameters.add(new BasicNameValuePair(SUBMIT_TO_STATION_TELECODE, trainQueryInfo.getToStationCode()));
			parameters.add(new BasicNameValuePair(QUERY_TRAIN_DATE, singleTrainOrderVO.getTrainDate()));
			parameters.add(new BasicNameValuePair(QUERY_TRAIN_NO, trainQueryInfo.getTrainno4()));
			parameters.add(new BasicNameValuePair(SUBMIT_TOKEN, singleTrainOrderVO.getSubmitOrderRequestToken()));
			for (int i = 0; i < validPassengerDatas.size(); i++) {
				parameters.add(new BasicNameValuePair(SUBMIT_PASSENGERTICKETS, validPassengerDatas.get(i).getLongText(
						singleTrainOrderVO.getSeatType())));

				parameters.add(new BasicNameValuePair("passenger_" + (i + 1) + "_cardno", validPassengerDatas.get(i)
						.getCardNo()));
				parameters.add(new BasicNameValuePair("passenger_" + (i + 1) + "_cardtype", validPassengerDatas.get(i)
						.getCardType().getValue()));
				parameters.add(new BasicNameValuePair("passenger_" + (i + 1) + "_mobileno", validPassengerDatas.get(i)
						.getMobile()));
				parameters.add(new BasicNameValuePair("passenger_" + (i + 1) + "_name", validPassengerDatas.get(i)
						.getName()));
				parameters.add(new BasicNameValuePair("passenger_" + (i + 1) + "_seat", singleTrainOrderVO
						.getSeatType().getValue()));
				parameters.add(new BasicNameValuePair("passenger_" + (i + 1) + "_ticket", validPassengerDatas.get(i)
						.getTicketType().getValue()));

			}
			parameters.add(new BasicNameValuePair(SUBMIT_RANDCODE, randCode));
			// 检查订单
			if (justCheck) {
				parameters.add(new BasicNameValuePair(SUBMIT_TFLAG, "dc"));
			}
			parameters.add(new BasicNameValuePair(SUBMIT_TEXTFIELD, "中文或拼音首字母"));

			TicketMainFrame.appendMessage(title + "提交订单, 车次：" + singleTrainOrderVO.getTrainNo() + ",席别："
					+ singleTrainOrderVO.getSeatType() + ",日期：" + singleTrainOrderVO.getTrainDate());
			String responseBody = postHttpRequestAsString(httpClient, url, parameters,
					singleTrainOrderVO.getCookieData());
			return responseBody;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HttpClientService httpClientService = new HttpClientService();
		//httpClientService.initCookie();

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(GETQUEUECOUNT_FROM, "BPX"));
		parameters.add(new BasicNameValuePair(GETQUEUECOUNT_SEAT, "4"));
		parameters.add(new BasicNameValuePair(GETQUEUECOUNT_STATION, "Z3"));
		parameters.add(new BasicNameValuePair(GETQUEUECOUNT_TICKET, "40484502902027450262"));
		parameters.add(new BasicNameValuePair(GETQUEUECOUNT_TO, "JBN"));
		parameters.add(new BasicNameValuePair(GETQUEUECOUNT_TRAIN_DATE, "2013-11-21"));
		parameters.add(new BasicNameValuePair(GETQUEUECOUNT_TRAIN_NO, "24000000Z306"));

		Map<String, String> cookieData = new HashMap<String, String>();
		cookieData.put("JSESSIONID", "81BFCA3C5F2AE80241FD81E7AA23B410");
		cookieData.put("BIGipServerotsweb", "2262040842.48160.0000");

		httpClientService.getHttpRequestAsString(buildHttpClient(), GET_URL_GETQUEUECOUNT, parameters, cookieData);
	}

}
