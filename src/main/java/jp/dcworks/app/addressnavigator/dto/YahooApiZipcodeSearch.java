package jp.dcworks.app.addressnavigator.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Yahoo API 郵便番号検索APIレスポンスクラス。
 *	https://developer.yahoo.co.jp/webapi/map/openlocalplatform/v1/zipcodesearch.html
 *
 * @author tomo-sato
 */
@Data
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class YahooApiZipcodeSearch extends DtoBase {

	/**
	 * デフォルトコンストラクタ.
	 */
	public YahooApiZipcodeSearch() {}

	/** レスポンスのまとめ情報です。 */
	private ResultInfo resultInfo;
	/** 検索結果1件分のデータ群です。 */
	private List<Feature> feature;

	/**
	 * レスポンスのまとめ情報です。
	 */
	@Data
	@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@EqualsAndHashCode(callSuper = false)
	public static class ResultInfo extends DtoBase {
		/** レスポンス情報に含まれるデータ件数です。 */
		private Integer count;
		/** 出力されている全データ件数です。 */
		private Integer total;
		/** レスポンス情報に含まれる、全データからの取得開始位置です。 */
		private Integer start;
		/** リクエスト元に処理結果を伝えるためのコードです。正常終了の場合、200を出力します。エラー時は、下記エラー項目を参照してください。 */
		private Integer status;
		/** APIの説明文です。 */
		private Integer description;
		/** レスポンス情報を生成するのに要した時間です。 */
		private Double latency;
	}

	/**
	 * 検索結果1件分のデータ群です。
	 */
	@Data
	@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@EqualsAndHashCode(callSuper = false)
	public static class Feature extends DtoBase {
		/** 名称で郵便番号が格納されます。 */
		private String Name;
		/** 各種データを格納します。 */
		private Property property;

		@Data
		@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
		@JsonIgnoreProperties(ignoreUnknown = true)
		@EqualsAndHashCode(callSuper = false)
		public static class Property extends DtoBase {
			/** 住所文字列です。 */
			private String address;
			/** 住所構造のデータ群です。リクエストパラメータのdetailを、詳細（full）で指定すると取得できます。 */
			private List<AddressElement> addressElement;

			/**
			 * 住所構造のデータ群です。リクエストパラメータのdetailを、詳細（full）で指定すると取得できます。
			 */
			@Data
			@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
			@JsonIgnoreProperties(ignoreUnknown = true)
			@EqualsAndHashCode(callSuper = false)
			public static class AddressElement extends DtoBase {
				/** 住所構造のレベル：prefecture - 都道府県 */
				public static final String LEVEL_PREFECTURE = "prefecture";
				/** 住所構造のレベル：city - 市区町村 */
				public static final String LEVEL_CITY = "city";
				/** 住所構造のレベル：oaza - 大字 */
				public static final String LEVEL_OAZA = "oaza";
				/** 住所構造のレベル：aza - 字 */
				public static final String LEVEL_AZA = "aza";

				/** 住所構造データの住所名称です。 */
				private String name;
				/** 住所構造データの住所の読み（ひらがな）です。 */
				private String kana;
				/**
				 * 住所構造のレベルです。
				 *   prefecture - 都道府県
				 *   city - 市区町村
				 *   oaza - 大字
				 *   aza - 字
				 */
				private String level;
			}
		}
	}
}
