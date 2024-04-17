package jp.dcworks.app.addressnavigator.webapi;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jp.dcworks.app.addressnavigator.dto.YahooApiZipcodeSearch;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * Yahoo API 操作関連サービスクラス。
 *
 * @author tomo-sato
 */
@Log4j2
@Service
public class YahooApiService {

	/**
	 * プロパティファイル（application.properties）の読み込み。
	 * <p>
	 * ※「yahooapiproperties.xxx」の値とマッピング。
	 * </p>
	 */
	@Data
	@Component
	@ConfigurationProperties(prefix = "yahooapiproperties")
	private static class YahooApiProperties {
		/** Client ID（アプリケーションID） */
		private String appid;

		/** ベースURL */
		private String baseurl;
		/** 郵便番号検索APIベースURL */
		private String zipcodesearchurl;
	}

	/** APIクライアント */
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	/** プロパティファイルの設定値 */
	@Autowired
	private YahooApiProperties yahooApiProperties;

	/**
	 * RestTemplateを取得する。
	 * <p>
	 * ※RestTemplateに対して何か共通の初期化処理が必要になったらこの辺で実装を想定。
	 * </p>
	 *
	 * @return RestTemplateを返す。
	 */
	private RestTemplate getRestTemplate() {
		return restTemplateBuilder.build();
	}

	/**
	 * 実行時間をトレースする為、APIリクエスト処理のラップ。
	 * <p>
	 * FIXME できればアノテーションなどでインターセプトして実行開始-終了の時間をトレースしたい。。今はベタ書き。。
	 * </p>
	 */
	private <T> ResponseEntity<T> exchange(String url, Class<T> responseType,
			HttpMethod method, MultiValueMap<String, String> queryParams, Map<String, String> postParams, String body) throws RestClientException {
		log.info(">>>>> Yahoo API 処理開始 >>>>>：endpoint=" + url + ", responseType=" + responseType.getName()
				+ ", uriVariables=" + queryParams);

		Date startTime = new Date();

		// header設定
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);
		if (method == HttpMethod.POST) {
			entity = new HttpEntity<>(body, headers);
		}

		// 必須項目「appid」をセット。
		queryParams.add("appId", yahooApiProperties.appid);

		// クエリパラメータをセット
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		String uri = builder.queryParams(queryParams).toUriString();

		// APIリクエスト。
		try {
			ResponseEntity<T> t =
					this.getRestTemplate().exchange(uri, method, entity, responseType, postParams);
			Date elapsedTime = new Date();
			long elapsed = elapsedTime.getTime() - startTime.getTime();

			log.info("<<<<< Yahoo API 処理終了 <<<<<：経過時間(" + elapsed + "ms)：endpoint=" + url + ", responseType="
					+ responseType.getName() + ", uriVariables=" + queryParams);
			return t;

		} catch (Exception e) {
			log.warn("<<<<< Yahoo API 異常終了" + " Exception=" + e);
			throw e;
		}
	}

	/**
	 * 郵便番号検索API。
	 *
	 * @param uuid クーポンUUID
	 * @return クーポン詳細を返却する。
	 */
	public ResponseEntity<YahooApiZipcodeSearch> getAddress(String zipcode) {
		String apiUrl = yahooApiProperties.baseurl + yahooApiProperties.zipcodesearchurl;

		// クエリパラメータ
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		queryParams.add("output", "json");
		queryParams.add("results", "15");
		queryParams.add("detail", "full");
		queryParams.add("query", zipcode);

		ResponseEntity<YahooApiZipcodeSearch> coupon = null;
		try {
			coupon = this.exchange(apiUrl, YahooApiZipcodeSearch.class,
					HttpMethod.GET, queryParams, new HashMap<>(), null);

		} catch (Exception e) {
			log.warn("郵便番号検索APIに失敗しました。：zipcode=" + zipcode, e);
		}

		return coupon;
	}
}
