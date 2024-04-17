package jp.dcworks.app.addressnavigator.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 住所検索DTOクラス。
 *
 * @author tomo-sato
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RequestAddressSearch extends DtoBase {
	/** 郵便番号 */
	private String zipcode;
}
