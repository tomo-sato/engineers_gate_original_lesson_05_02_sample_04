package jp.dcworks.app.addressnavigator.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 住所検索レスポンス用DTOクラス。
 *
 * @author tomo-sato
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseAddress extends DtoBase {
	/** 郵便番号 */
	private String zipcode;
	/** 住所 */
	private String address;
	/** 都道府県 */
	private String prefecture;
	/** 市区町村 */
	private String city;
}
