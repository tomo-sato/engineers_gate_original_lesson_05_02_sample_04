package jp.dcworks.app.addressnavigator.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.dcworks.app.addressnavigator.dto.RequestAddressSearch;
import jp.dcworks.app.addressnavigator.dto.ResponseAddress;
import jp.dcworks.app.addressnavigator.dto.YahooApiZipcodeSearch;
import jp.dcworks.app.addressnavigator.dto.YahooApiZipcodeSearch.Feature;
import jp.dcworks.app.addressnavigator.dto.YahooApiZipcodeSearch.Feature.Property.AddressElement;
import jp.dcworks.app.addressnavigator.webapi.YahooApiService;
import lombok.extern.log4j.Log4j2;

/**
 * 住所検索画面コントローラー。
 *
 * @author tomo-sato
 */
@Log4j2
@Controller
@RequestMapping("/")
public class HomeController {

	/**
	 * Yahoo API 操作関連サービスクラス。
	 */
	@Autowired
	private YahooApiService yahooApiService;

	/**
	 * [GET]住所検索画面のアクション。
	 *
	 * @param model 入力フォームのオブジェクト
	 * @return テンプレートpath
	 */
	@GetMapping(path = { "", "/" })
	public String index(Model model) {
		log.info("住所検索画面のアクションが呼ばれました。");

		if (!model.containsAttribute("requestAddressSearch")) {
			model.addAttribute("requestAddressSearch", new RequestAddressSearch());
		}
		return "index";
	}

	/**
	 * [POST]住所検索画面のアクション。
	 *
	 * @param requestAddressSearch 入力フォームの内容
	 * @param model
	 * @return
	 */
	@PostMapping("/")
	public String index(@Validated @ModelAttribute RequestAddressSearch requestAddressSearch,
			Model model) {

		String zipcode = requestAddressSearch.getZipcode();

		// 住所の検索を行う。
		ResponseEntity<YahooApiZipcodeSearch> zipcodeSearchResponse = yahooApiService.getAddress(zipcode);
		List<ResponseAddress> addressList = convertAddress(zipcodeSearchResponse);

		model.addAttribute("addressList", addressList);
		return "index";
	}

	/**
	 * 画面表示用にデータ変換。
	 * ※レスポンスのままだと扱いにくいので、画面表示用のオブジェクトにコンバート。
	 *
	 * @param zipcodeSearchResponse APIのレスポンス
	 * @return
	 */
	private static List<ResponseAddress> convertAddress(ResponseEntity<YahooApiZipcodeSearch> zipcodeSearchResponse) {

		// データが無かったらnullを返す。
		if (zipcodeSearchResponse == null
				|| zipcodeSearchResponse.getBody() == null
				|| zipcodeSearchResponse.getBody().getFeature().isEmpty()) {

			return null;
		}

		List<ResponseAddress> retList = new ArrayList<>();

		for (Feature feature : zipcodeSearchResponse.getBody().getFeature()) {
			ResponseAddress responseAddress = new ResponseAddress();
			responseAddress.setZipcode(feature.getName());
			responseAddress.setAddress(feature.getProperty().getAddress());

			for (AddressElement addressElement : feature.getProperty().getAddressElement()) {
				String Level = addressElement.getLevel();
				String name = addressElement.getName();

				switch (Level) {
					// 都道府県
					case AddressElement.LEVEL_PREFECTURE:
						responseAddress.setPrefecture(name);
						break;

					// 市区町村
					case AddressElement.LEVEL_CITY:
						responseAddress.setCity(name);
						break;
					default:
						// ※処理なし。
				}
			}
			retList.add(responseAddress);
		}

		return retList;
	}
}
